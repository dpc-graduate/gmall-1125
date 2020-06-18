package com.atguigu.gmall.wms.listener;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.wms.entity.vo.SkuLockVo;
import com.atguigu.gmall.wms.mapper.WareSkuMapper;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * @author: dpc
 * @data: 2020/6/13,15:38
 */
@Component
public class StockListener {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "wms:stock";
    @Autowired
    private WareSkuMapper wareSkuMapper;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ORDER.STOCK.QUEUE", durable = "true"),
            exchange = @Exchange(value = "ORDER_EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = "stock.unlock"
    ))
    public void unlock(String orderToken, Channel channel, Message message) throws IOException {
        String lockSku = this.redisTemplate.opsForValue().get(KEY_PREFIX + orderToken);
        if (!StringUtils.isEmpty(lockSku)) {
            List<SkuLockVo> lockVos = JSON.parseArray(lockSku, SkuLockVo.class);
            lockVos.forEach(skuLockVo -> {
                wareSkuMapper.unlock(skuLockVo.getSkuId(), skuLockVo.getCount());
            });

            //防止重复解锁库存,把redis中的删掉
            this.redisTemplate.delete(KEY_PREFIX + orderToken);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

    }
}
