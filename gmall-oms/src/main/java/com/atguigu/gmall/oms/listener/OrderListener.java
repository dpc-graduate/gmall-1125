package com.atguigu.gmall.oms.listener;

import com.atguigu.gmall.oms.mapper.OrderMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author: dpc
 * @data: 2020/6/14,14:44
 */
@Component
public class OrderListener {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {"ORDER_DEAD_QUEUE"})
    public void closeOrder(String orderToken, Channel channel, Message message) throws IOException {
        if (this.orderMapper.closeOrder(orderToken) == 1) {
            //解锁库存
            this.rabbitTemplate.convertAndSend("ORDER_EXCHANGE", "stock.unlock", orderToken);
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            if (message.getMessageProperties().getRedelivered()) {

                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
            e.printStackTrace();
        }
    }
}
