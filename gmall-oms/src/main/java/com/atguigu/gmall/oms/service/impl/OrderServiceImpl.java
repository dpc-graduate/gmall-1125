package com.atguigu.gmall.oms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.oms.entity.OrderItemEntity;
import com.atguigu.gmall.oms.exception.OrderException;
import com.atguigu.gmall.oms.feign.GmallPmsClient;
import com.atguigu.gmall.oms.feign.GmallUmsClient;
import com.atguigu.gmall.oms.mapper.OrderItemMapper;
import com.atguigu.gmall.oms.vo.OrderItemVo;
import com.atguigu.gmall.oms.vo.OrderSubmitVo;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.entity.SpuDescEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigu.gmall.pms.vo.SaleAttrValueVo;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import com.atguigu.gmall.ums.entity.UserEntity;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.oms.mapper.OrderMapper;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements OrderService {
    @Autowired
    private GmallUmsClient umsClient;
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private OrderItemMapper itemMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<OrderEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<OrderEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * ???????????? ??????order??????order_item ???
     *
     * @param submitVo
     * @return
     */
    @Override
    @Transactional
    public OrderEntity saveOrder(OrderSubmitVo submitVo) {
        List<OrderItemVo> items = submitVo.getItems();
        if (CollectionUtils.isEmpty(items)) {
            throw new OrderException("????????????????????????");
        }
        OrderEntity entity = new OrderEntity();
        entity.setUserId(submitVo.getUserId());
        entity.setOrderSn(submitVo.getOrderToken());
        entity.setCreateTime(new Date());
        UserEntity userEntity = umsClient.queryUserById(submitVo.getUserId()).getData();
        if (userEntity != null) {
            entity.setUsername(userEntity.getUsername());
        }
        entity.setTotalAmount(submitVo.getTotalPrice());
        entity.setPayAmount(submitVo.getTotalPrice().subtract(new BigDecimal(submitVo.getBounds() / 100)));
        entity.setPayType(submitVo.getPayType());
        entity.setSourceType(0);
        entity.setStatus(0);
        entity.setDeliveryCompany(submitVo.getDeliveryCompany());
        //TODO??? ??????????????????????????? ??????sku ???????????????
        //todo:  ????????????
        UserAddressEntity address = submitVo.getAddress();
        if (address != null) {
            entity.setReceiverName(address.getName());
            entity.setReceiverAddress(address.getAddress());
            entity.setReceiverRegion(address.getRegion());
            entity.setReceiverProvince(address.getProvince());
            entity.setReceiverPostCode(address.getPostCode());
            entity.setReceiverPhone(address.getPhone());
            entity.setReceiverCity(address.getCity());
        }
        entity.setDeleteStatus(0);
        entity.setUseIntegration(submitVo.getBounds());
        entity.setIntegrationAmount(new BigDecimal(submitVo.getBounds() / 100));
        this.save(entity);
        //TODO:????????????
        /*
            ??????order_item ??? ??????????????????
         */
        items.forEach(item -> {
            OrderItemEntity itemEntity = new OrderItemEntity();
            itemEntity.setOrderId(entity.getId());
            itemEntity.setOrderSn(submitVo.getOrderToken());
            SkuEntity skuEntity = this.pmsClient.querySkuById(item.getSkuId()).getData();
            if (skuEntity != null) {
                //??????sku??????
                itemEntity.setSkuId(skuEntity.getId());
                itemEntity.setSkuQuantity(item.getCount().intValue());
                itemEntity.setSkuPrice(skuEntity.getPrice());
                itemEntity.setSkuPic(skuEntity.getDefaultImage());
                itemEntity.setSkuName(skuEntity.getName());
                itemEntity.setCategoryId(skuEntity.getCatagoryId());
            }
            //??????spu??????
            SpuEntity spuEntity = this.pmsClient.querySpuById(skuEntity.getSpuId()).getData();
            if (spuEntity != null) {
                BrandEntity brandEntity = this.pmsClient.queryBrandById(spuEntity.getBrandId()).getData();
                if (brandEntity != null) {
                    itemEntity.setSpuBrand(brandEntity.getName());
                }
                itemEntity.setSpuId(spuEntity.getId());
                itemEntity.setSpuName(spuEntity.getName());
                SpuDescEntity descEntity = this.pmsClient.querySpuDescById(spuEntity.getId()).getData();
                if (descEntity != null) {
                    itemEntity.setSpuPic(descEntity.getDecript());
                }
                List<SaleAttrValueVo> attrValueVos = this.pmsClient.querySaleAttrBySpuId(spuEntity.getId()).getData();
                if (!CollectionUtils.isEmpty(attrValueVos)) {
                    itemEntity.setSkuAttrsVals(JSON.toJSONString(attrValueVos));
                }
                //TODO:????????????????????????
                this.itemMapper.insert(itemEntity);
            }
        });
        //?????????????????????????????????????????? ??????????????????????????????90s???????????????????????? ????????????
        this.rabbitTemplate.convertAndSend("ORDER_EXCHANGE", "order.ttl", submitVo.getOrderToken());
        return entity;
    }

}