package com.zing.rabbitmq.api.dlx;

import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.RabbitMQFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zing
 * @date 2019-12-02
 */
public class Consumer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        String exchangeName = "test_dlx_exchange";
        String routingKey = "#.dlx";
        String queueName = "test_dlx_queue";

        channel.exchangeDeclare(exchangeName, "topic", true);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "dlx.exchange");
        channel.queueDeclare(queueName, true, false, false, arguments);
        channel.queueBind(queueName, exchangeName, routingKey);

        // 进行死信队列声明
        channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
        channel.queueDeclare("dlx.queue", true, false, false, null);
        channel.queueBind("dlx.queue", "dlx.exchange", "#");

        channel.basicConsume(queueName, true, new MyConsumer(channel));
    }

}
