package com.atguigu.gmall.order.service;

import com.atguigu.gmall.cart.entity.Cart;
import com.atguigu.gmall.order.exception.OrderException;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.vo.OrderConfirmVo;
import com.atguigu.gmall.order.vo.OrderItemVo;
import com.atguigu.gmall.order.vo.UserInfo;
import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.vo.ItemSaleVo;
import com.atguigu.gmall.ums.entity.UserAddressEntity;
import com.atguigu.gmall.ums.entity.UserEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.spel.ast.OpOr;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
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
    private StringRedisTemplate redisTemplate;
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
        redisTemplate.opsForValue().set(KEY_PREFIX + orderToken, orderToken,3, TimeUnit.HOURS);
        confirmVo.setOrderToken(orderToken);
        return confirmVo;
        //TODO :使用异步编排来优化
    }
}
