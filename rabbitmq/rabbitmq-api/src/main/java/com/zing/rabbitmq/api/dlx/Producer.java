package com.zing.rabbitmq.api.dlx;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.RabbitMQFactory;

/**
 * @author Zing
 * @date 2019-12-02
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        String exchangeName = "test_dlx_exchange";
        String routingKey = "test.dlx";

        String msg = "Hello World! DLX Message";

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .expiration("10000")
                .build();
        channel.basicPublish(exchangeName, routingKey, true, properties, msg.getBytes());
    }

}
