package com.zing.rabbitmq.api.message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.RabbitMQFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zing
 * @date 2019-11-21
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        Map<String, Object> headers = new HashMap<>();
        headers.put("my1", "111");
        headers.put("my2", "222");

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .expiration("10000")
                .headers(headers)
                .build();

        String msg = "Hello World!";
        for (int i = 0; i < 5; i++) {
            channel.basicPublish("", "test001", properties, msg.getBytes());
        }
    }
}
