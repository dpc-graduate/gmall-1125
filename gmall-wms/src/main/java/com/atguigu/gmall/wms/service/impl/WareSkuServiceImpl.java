package com.atguigu.gmall.wms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.wms.entity.vo.SkuLockVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.wms.mapper.WareSkuMapper;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuMapper, WareSkuEntity> implements WareSkuService {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final String KEY_PREFIX = "wms:stock";

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<WareSkuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional
    public List<SkuLockVo> checkAndLock(List<SkuLockVo> lockVos, String OrderToken) {
        if (CollectionUtils.isEmpty(lockVos)) {
            return null;
        }
        //不能全锁，要在遍历每个商品的时候才加分布式锁
        lockVos.forEach(skuLockVo -> {
            checkLock(skuLockVo);
        });
        //如果有商品锁定失败，所有锁定成功的商品要解锁库，并响应锁定情况
        boolean flag = lockVos.stream().anyMatch(skuLockVo -> !skuLockVo.getLock());
        if (flag) {
            //锁定成功的
            List<SkuLockVo> lockSuccess = lockVos.stream().filter(SkuLockVo::getLock).collect(Collectors.toList());
            //解锁
            lockSuccess.forEach(skuLockVo -> {
                this.baseMapper.unlock(skuLockVo.getWareSkuId(), skuLockVo.getCount());
            });
            return lockVos;
        }
        //把库存的锁定信息保存到redis中
        this.redisTemplate.opsForValue().set(KEY_PREFIX + OrderToken, JSON.toJSONString(lockVos),5, TimeUnit.MINUTES);
this.rabbitTemplate.convertAndSend("ORDER_EXCHANGE","stock.ttl",OrderToken);
        return null;
    }

    /**
     * 锁定库存
     *
     * @param skuLockVo
     */
    private void checkLock(SkuLockVo skuLockVo) {
        //只锁当前的商品
        RLock fairLock = this.redissonClient.getFairLock("lock" + skuLockVo.getSkuId());
        fairLock.lock();
        //验库存
        List<WareSkuEntity> wareSkuEntities = this.baseMapper.check(skuLockVo.getSkuId(), skuLockVo.getCount());
        //如果没有仓库满足购买需求 直接锁定失败，释放锁
        if (CollectionUtils.isEmpty(wareSkuEntities)) {
            //设置锁定失败
            skuLockVo.setLock(false);
            fairLock.unlock();
            return;
        }
        //锁库存，我们这里默认选择第一个仓库
        WareSkuEntity entity = wareSkuEntities.get(0);
        int lock = this.baseMapper.lock(entity.getId(), skuLockVo.getCount());
        if (lock == 1) {
            skuLockVo.setLock(true);
            skuLockVo.setWareSkuId(entity.getId());
        }
        //释放锁
        fairLock.unlock();
    }
}