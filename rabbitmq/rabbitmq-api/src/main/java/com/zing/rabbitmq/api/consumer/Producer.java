package com.zing.rabbitmq.api.consumer;

import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.RabbitMQFactory;

/**
 * @author Zing
 * @date 2019-12-02
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        String exchangeName = "test_consumer_exchange";
        String routingKey = "test.consumer";

        String msg = "Hello World! Consumer Message";
        channel.basicPublish(exchangeName, routingKey, true, null, msg.getBytes());
    }

}
