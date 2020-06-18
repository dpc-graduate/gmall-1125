package com.atguigu.gmall.wms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sun.rmi.runtime.Log;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * @author: dpc
 * @data: 2020/6/15,8:53
 */
@Configuration
@Slf4j
public class RabbitMQConfig implements RabbitTemplate.ReturnCallback, RabbitTemplate.ConfirmCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostConstruct
    public void init(){
        this.rabbitTemplate.setConfirmCallback(this);
        this.rabbitTemplate.setReturnCallback(this);
    }
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack){
            log.info("消息成功到达交换机");
        }else {
            log.error("消息发送到交换机失败:{}", cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息没有到达队列,交换机:{},路由键:{},消息内容:{}", exchange, routingKey, message.toString());
    }
    //1 业务交换及机 使用已有的ORDER_EXCHANGE
    //2 延迟队列 ，延迟时间 死信交换机 死信 rk 不能有消费者 超时就会到交换机
    @Bean
    public Queue ttlQueue(){
        HashMap<String, Object> map = new HashMap<>();
        //设置延迟时间
        map.put("x-message-ttl",120000);
        //设置死信交换机
        map.put("x-dead-letter-exchange","ORDER_EXCHANGE");
        //之前已经有一个解锁库存的消息队列了，这边不再声明新的死信队列。直接把以前的解锁库存当成是这个延迟队列的死信队列，再指定一下路由键,和声明解锁库存时的路由键一样
        map.put("x-dead-letter-routing-key","stock.unlock");
        return new Queue("WMS_TTL_QUEUE",true,false,false,map);
    }
    //3 延迟队列绑定到业务交换机
    @Bean
    public Binding ttlBind(){
        return new Binding("WMS_TTL_QUEUE", Binding.DestinationType.QUEUE,"ORDER_EXCHANGE","stock.ttl",null);
    }
//    //4 死信交换机
//    //5 死信队列 本质就是普通队列，放的是死信消息
//    @Bean
//    public Queue deadQueue(){
//        return new Queue("WMS_DEAD_QUEUE",true,false,false);
//    }
//    //6 死信队列绑定到死信交换机
//    @Bean
//    public Binding deadBind(){
//        return new Binding("WMS_DEAD_QUEUE", Binding.DestinationType.QUEUE,"ORDER_EXCHANGE","stock.dead",null);
//    }
}
