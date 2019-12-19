package com.zing.rabbitmq.api.exchange.direct;

import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.RabbitMQFactory;

/**
 * @author Zing
 * @date 2019-12-01
 */
public class DirectExchangeProducer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        String exchangeName = "test_direct_exchange";
        String routingKey = "test.direct";

        String msg = "Hello World! Direct Exchange ...";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
    }
}
