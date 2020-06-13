package com.atguigu.gmall.cart.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.service.CartAsyncService;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: dpc
 * @data: 2020/6/12,10:00
 */
@Component
public class CartListener {
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String PRICE_PREFIX = "price:info:";
    private static final String KEY_PREFIX = "cart:info:";
    @Autowired
    private CartAsyncService cartAsyncService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "cart_item_queue", durable = "true"),
            exchange = @Exchange(value = "GMALL_ITEM_EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.update"}
    ))
    public void listener(Long spuId, Channel channel, Message message) {
        //同步所有 spu下面的所有sku的价格
        List<SkuEntity> skuEntities = pmsClient.querySkusBySpuId(spuId).getData();
        if (!CollectionUtils.isEmpty(skuEntities)) {
            skuEntities.forEach(skuEntity -> {
                String s = this.redisTemplate.opsForValue().get(PRICE_PREFIX + skuEntity.getId());
                if (!StringUtils.isEmpty(s)) {
                    this.redisTemplate.opsForValue().set(PRICE_PREFIX + skuEntity.getId(), skuEntity.getPrice().toString());
                }
            });
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    this.rabbitTemplate.convertAndSend("ORDER-EXCHANGE", "cart.delete", JSON.toJSONString(map));
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ORDER_CART_QUEUE", durable = "true"),
            exchange = @Exchange(value = "ORDER-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = "cart.delete"
    ))
    public void deleteCart(Map<String, Object> map, Channel channel, Message message) {
        String userId = map.get("userId").toString();
        String arrayList = map.get("skuIds").toString();
        List<Long> skuIds = JSON.parseArray(arrayList, Long.class);
        //删除redis和mysql中的购物车
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(KEY_PREFIX + userId);
        hashOps.delete(skuIds.stream().map(skuId -> skuId.toString()).collect(Collectors.toList()).toArray());
        this.cartAsyncService.batchDeleteCart(userId, skuIds);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
