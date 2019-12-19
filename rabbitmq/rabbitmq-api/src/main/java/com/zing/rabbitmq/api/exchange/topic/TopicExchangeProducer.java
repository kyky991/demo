package com.zing.rabbitmq.api.exchange.topic;

import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.RabbitMQFactory;

/**
 * @author Zing
 * @date 2019-12-01
 */
public class TopicExchangeProducer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        String exchangeName = "test_topic_exchange";
        String routingKey = "user.save";
        String routingKey2 = "user.update";
        String routingKey3 = "user.delete.abc";

        String msg = "Hello World! Topic Exchange ...";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
        channel.basicPublish(exchangeName, routingKey2, null, msg.getBytes());
        channel.basicPublish(exchangeName, routingKey3, null, msg.getBytes());
    }
}
