package com.zing.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.entity.Order;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Zing
 * @date 2019-12-07
 */
@Component
public class RabbitReceiver {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "topic.queue.boot", durable = "true"),
            exchange = @Exchange(value = "topic.exchange.boot", type = "topic", durable = "true", ignoreDeclarationExceptions = "true"),
            key = "springboot.*")
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        System.err.println("Payload Message:" + message.getPayload());

        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        // 手动ACK
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}",
                    durable = "${spring.rabbitmq.listener.order.queue.durable}"),
            exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}",
                    type = "${spring.rabbitmq.listener.order.exchange.type}",
                    durable = "${spring.rabbitmq.listener.order.exchange.durable}",
                    ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignore-declaration-exceptions}"),
            key = "${spring.rabbitmq.listener.order.key}")
    )
    @RabbitHandler
    public void onMessage(@Payload Order order, @Headers Map<String, Object> headers, Channel channel) throws Exception {
        System.err.println("Payload Order:" + order);

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        // 手动ACK
        channel.basicAck(deliveryTag, false);
    }

}
