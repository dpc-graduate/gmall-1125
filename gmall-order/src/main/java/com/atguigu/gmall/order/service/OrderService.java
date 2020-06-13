package com.atguigu.gmall.order.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.entity.Cart;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderItemVo;
import com.atguigu.gmall.oms.exception.OrderException;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.UserInfo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.vo.ItemSaleVo;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.entity.vo.SkuLockVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author: dpc
 * @data: 2020/6/12,11:09
 */
@Service
public class OrderService {
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private GmallWmsClient wmsClient;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private GmallUmsClient umsClient;
    @Autowired
    private GmallCartClient cartClient;
    @Autowired
    private GmallOmsClient omsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public static final String KEY_PREFIX = "order:token:";


    public OrderConfirmVo confirm() {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        //获取登录用户id
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        Long userId = userInfo.getUserId();
        List<Cart> carts = this.cartClient.queryCheckedCartsByUserId(userId).getData();
        if (CollectionUtils.isEmpty(carts)) {
            throw new OrderException("未选中购物车信息");
        }
        /**
         *
         *  实时查询商品列表信息
         */
        //获取购物车中选中的商品信息
        List<OrderItemVo> OrderItemVo = carts.stream().map(cart -> {
            OrderItemVo itemVo = new OrderItemVo();
            //下面两个从购物车获取
            itemVo.setSkuId(cart.getSkuId());
            itemVo.setCount(cart.getCount());
            //实时查询数据库中的sku信息
            SkuEntity skuEntity = pmsClient.querySkuById(cart.getSkuId()).getData();
            if (skuEntity != null) {
                itemVo.setTitle(skuEntity.getTitle());
                itemVo.setDefaultImage(skuEntity.getDefaultImage());
                itemVo.setPrice(skuEntity.getPrice());
                itemVo.setWeight(new BigDecimal(skuEntity.getWeight()));
            }
            //销售属性
            List<SkuAttrValueEntity> skuAttrValueEntities = this.pmsClient.querySaleAttrValueBySpuId(cart.getSkuId()).getData();
            if (!CollectionUtils.isEmpty(skuAttrValueEntities)) {
                itemVo.setSaleAttrs(skuAttrValueEntities);
            }
            //营销信息
            List<ItemSaleVo> itemSaleVos = this.smsClient.querySaleVosBySkuId(cart.getSkuId()).getData();
            if (!CollectionUtils.isEmpty(itemSaleVos)) {
                itemVo.setSales(itemSaleVos);
            }
            List<WareSkuEntity> wareSkuEntityList = this.wmsClient.queryWareSkusBySkuId(cart.getSkuId()).getData();
            if (!CollectionUtils.isEmpty(wareSkuEntityList)) {
                itemVo.setStore(wareSkuEntityList.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0));

            }

            return itemVo;

        }).collect(Collectors.toList());
        //设置商品信息
        confirmVo.setOrderItems(OrderItemVo);
        //查询用户的收货地址
        List<UserAddressEntity> addressEntities = this.umsClient.queryAddressByUserId(userId).getData();
        if (!CollectionUtils.isEmpty(addressEntities)) {
            confirmVo.setAddresses(addressEntities);
        }
        //查询用户积分信息
        UserEntity userEntity = this.umsClient.queryUserById(userId).getData();
        if (userEntity != null) {
            confirmVo.setBounds(userEntity.getIntegration());
        }
        //生成防重的唯一标识，redis中存一份，vo中设置一份
        //每秒钟生成26万个id
        String orderToken = IdWorker.getTimeId();
        redisTemplate.opsForValue().set(KEY_PREFIX + orderToken, orderToken, 3, TimeUnit.HOURS);
        confirmVo.setOrderToken(orderToken);
        return confirmVo;
        //TODO :使用异步编排来优化
    }

    /**
     * 提交订单
     *
     * @param submitVo
     */
    public void submit(OrderSubmitVo submitVo) {
        // 防重 查询redis是否包含当前页面提交的token，包含? 先删除 再放行。不包含，抛出异常
        String orderToken = submitVo.getOrderToken();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]" +
                " then return redis.call('del', KEYS[1]) " +
                "else return 0 end";
        //指定lua脚本执行的返回值
        Boolean execute = this.redisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(KEY_PREFIX + orderToken), orderToken);
        if (!execute) {
            throw new OrderException("订单已提交，请不要重复提交");
        }
        // 验价
        //获取页面总价格
        BigDecimal totalPrice = submitVo.getTotalPrice();
        List<OrderItemVo> items = submitVo.getItems();
        if (CollectionUtils.isEmpty(items)) {
            throw new OrderException("请选择要购买的商品");
        }
        BigDecimal currentTotalPrice = items.stream().map(item -> {
            SkuEntity skuEntity = this.pmsClient.querySkuById(item.getSkuId()).getData();
            if (skuEntity != null) {
                return skuEntity.getPrice().multiply(item.getCount());
            }
            return new BigDecimal(0);
        }).reduce((a, b) -> a.add(b)).get();
        if (currentTotalPrice.compareTo(totalPrice) != 0) {
            throw new OrderException("页面已经过期，请刷新后重试");
        }
        // 检验库存并锁定库存
        List<SkuLockVo> skuLockVos = items.stream().map(item -> {
            SkuLockVo skuLockVo = new SkuLockVo();
            skuLockVo.setSkuId(item.getSkuId());
            skuLockVo.setCount(item.getCount().intValue());
            return skuLockVo;
        }).collect(Collectors.toList());
        List<SkuLockVo> lockVos = this.wmsClient.checkAndLock(skuLockVos, orderToken).getData();
        if (!CollectionUtils.isEmpty(lockVos)) {
            throw new OrderException("订单锁定失败" + JSON.toJSONString(skuLockVos));
        }
        Long userId = null;
        // 新增订单
        try {
            userId = LoginInterceptor.getUserInfo().getUserId();
            submitVo.setUserId(userId);
            OrderEntity orderEntity = this.omsClient.saveOrder(submitVo).getData();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO:
            //订单创建失败，立马解锁库存
        }
        // 删除购物车,对后续业务没影响，可以使用异步删除MQ
        Map<String, Object> map = new HashMap<String, Object>();
        List<Long> skuIdList = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
        map.put("userId", userId);
        map.put("skuIds",JSON.toJSONString(skuIdList));
        this.rabbitTemplate.convertAndSend("ORDER-EXCHANGE", "cart.delete", map);


    }
}
