package com.zing.rabbitmq.api.exchange.fanout;

import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.RabbitMQFactory;

/**
 * @author Zing
 * @date 2019-12-01
 */
public class FanoutExchangeProducer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        String exchangeName = "test_fanout_exchange";
        String routingKey = "";

        String msg = "Hello World! Fanout Exchange ...";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
    }
}
