package com.zing.rabbitmq.api.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.zing.rabbitmq.RabbitMQFactory;

import java.util.Map;

/**
 * @author Zing
 * @date 2019-11-21
 */
public class Consumer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        String queueName = "test001";
        channel.queueDeclare(queueName, true, false, false, null);

        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        channel.basicConsume(queueName, true, queueingConsumer);

        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String body = new String(delivery.getBody());
            System.out.println(body);

            Map<String, Object> headers = delivery.getProperties().getHeaders();
            System.out.println(headers);
        }
    }

}
