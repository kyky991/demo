package com.zing.rabbitmq.api.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.RabbitMQFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zing
 * @date 2019-12-02
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        String exchangeName = "test_ack_exchange";
        String routingKey = "test.ack";

        String msg = "Hello World! ACK Message";
        for (int i = 0; i < 5; i++) {
            Map<String, Object> headers = new HashMap<>();
            headers.put("num", i);

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .headers(headers)
                    .build();
            channel.basicPublish(exchangeName, routingKey, true, properties, msg.getBytes());
        }
    }

}
