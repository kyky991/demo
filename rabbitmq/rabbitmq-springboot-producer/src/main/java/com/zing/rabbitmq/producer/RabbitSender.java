package com.zing.rabbitmq.producer;

import com.zing.rabbitmq.entity.Order;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author Zing
 * @date 2019-12-07
 */
@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //回调函数: confirm确认
    final RabbitTemplate.ConfirmCallback confirmCallback = (correlationData, ack, cause) -> {
        System.err.println("correlationData:" + correlationData);
        System.err.println("ack:" + ack);
        System.err.println("cause:" + cause);
        if (!ack) {
            System.err.println("异常处理....");
        }
    };

    //回调函数: return返回
    final RabbitTemplate.ReturnCallback returnCallback = (message, replyCode, replyText, exchange, routingKey) -> {
        System.err.println("message:" + message);
        System.err.println("replyCode:" + replyCode);
        System.err.println("replyText:" + replyText);
        System.err.println("exchange:" + exchange);
        System.err.println("routingKey:" + routingKey);
    };

    // 发送消息方法调用: 构建Message消息
    public void send(Object message, Map<String, Object> properties) throws Exception {
        MessageHeaders msh = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(message, msh);
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        CorrelationData correlationData = new CorrelationData();
        // id + 时间戳 全局唯一
        correlationData.setId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("topic.exchange.boot", "springboot.rabbit", msg, correlationData);
    }

    //发送消息方法调用: 构建自定义对象消息
    public void sendOrder(Order order) throws Exception {
        rabbitTemplate.setConfirmCallback(confirmCallback);
        rabbitTemplate.setReturnCallback(returnCallback);
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("topic.exchange.order", "springboot.order", order, correlationData);
    }

}
