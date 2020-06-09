package com.atguigu.gmall.sms.service.impl;


import com.atguigu.gmall.pms.vo.ItemSaleVo;
import com.atguigu.gmall.sms.entity.SmsSkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SmsSkuLadderEntity;
import com.atguigu.gmall.sms.entity.vo.SkuSaleVo;
import com.atguigu.gmall.sms.service.SmsSkuLadderService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.sms.mapper.SmsSkuBoundsMapper;
import com.atguigu.gmall.sms.entity.SmsSkuBoundsEntity;
import com.atguigu.gmall.sms.service.SmsSkuBoundsService;
import org.springframework.transaction.annotation.Transactional;


@Service("smsSkuBoundsService")
public class SmsSkuBoundsServiceImpl extends ServiceImpl<SmsSkuBoundsMapper, SmsSkuBoundsEntity> implements SmsSkuBoundsService {
    @Autowired
    private SmsSkuFullReductionServiceImpl reductionService;
    @Autowired
    private SmsSkuLadderService smsSkuLadderService;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SmsSkuBoundsEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SmsSkuBoundsEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    @Transactional
    public void saveSkuSales(SkuSaleVo skuSaleVo) {
        //保存sku的营销信息
        //保存积分优惠
        SmsSkuBoundsEntity entity = new SmsSkuBoundsEntity();
        entity.setSkuId(skuSaleVo.getSkuId());
        entity.setGrowBounds(skuSaleVo.getGrowBounds());
        entity.setBuyBounds(skuSaleVo.getBuyBounds());
        List<Integer> works = skuSaleVo.getWork();
        //设置一种规则去保存 1111 这种二进制数据，在读取的时候再通过某种规则转换过来
        entity.setWork(works.get(3) * 8 + works.get(2) * 4 + works.get(1) * 2 + works.get(0));
        this.save(entity);
        //保存满减信息
        SmsSkuFullReductionEntity reductionEntity = new SmsSkuFullReductionEntity();
        reductionEntity.setSkuId(skuSaleVo.getSkuId());
        reductionEntity.setFullPrice(skuSaleVo.getFullPrice());
        reductionEntity.setReducePrice(skuSaleVo.getReducePrice());
        reductionEntity.setAddOther(skuSaleVo.getFullAddOther());
        this.reductionService.save(reductionEntity);
        //保存打折信息
        SmsSkuLadderEntity smsSkuLadderEntity = new SmsSkuLadderEntity();
        smsSkuLadderEntity.setSkuId(skuSaleVo.getSkuId());
        smsSkuLadderEntity.setFullCount(skuSaleVo.getFullCount());
        smsSkuLadderEntity.setDiscount(skuSaleVo.getDiscount());
        smsSkuLadderEntity.setAddOther(skuSaleVo.getFullAddOther());
        this.smsSkuLadderService.save(smsSkuLadderEntity);
    }

    @Override
    public List<ItemSaleVo> querySaleVoBySkuId(Long skuId) {
        List<ItemSaleVo> itemSaleVos = new ArrayList<>();

        // 积分
        SmsSkuBoundsEntity boundsEntity = this.getOne(new QueryWrapper< SmsSkuBoundsEntity>().eq("sku_id", skuId));
        if (boundsEntity != null) {
            ItemSaleVo boundSaleVo = new ItemSaleVo();
            boundSaleVo.setType("积分");
            boundSaleVo.setDesc("送" + boundsEntity.getGrowBounds() + "成长积分，送" + boundsEntity.getBuyBounds() + "购物积分");
            itemSaleVos.add(boundSaleVo);
        }

        // 满减
        SmsSkuFullReductionEntity reductionEntity = this.reductionService.getOne(new QueryWrapper< SmsSkuFullReductionEntity>().eq("sku_id", skuId));
        if (reductionEntity != null) {
            ItemSaleVo reductionSaleVo = new ItemSaleVo();
            reductionSaleVo.setType("满减");
            reductionSaleVo.setDesc("满" + reductionEntity.getFullPrice() + "减" + reductionEntity.getReducePrice());
            itemSaleVos.add(reductionSaleVo);
        }

        // 打折
        SmsSkuLadderEntity ladderEntity = this.smsSkuLadderService.getOne(new QueryWrapper< SmsSkuLadderEntity>().eq("sku_id", skuId));
        if (ladderEntity != null) {
            ItemSaleVo ladderSaleVo = new ItemSaleVo();
            ladderSaleVo.setType("打折");
            ladderSaleVo.setDesc("满" + ladderEntity.getFullCount() + "件打" + ladderEntity.getDiscount().divide(new BigDecimal(10)) + "折");
            itemSaleVos.add(ladderSaleVo);
        }

        return itemSaleVos;
    }

}