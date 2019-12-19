package com.zing.rabbitmq.api.ack;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * @author Zing
 * @date 2019-12-02
 */
public class MyConsumer extends DefaultConsumer {

    public MyConsumer(Channel channel) {
        super(channel);
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println("body:" + new String(body));
        System.out.println("headers:" + properties.getHeaders());

        if ((Integer) properties.getHeaders().get("num") == 0) {
            getChannel().basicNack(envelope.getDeliveryTag(), false, true);
        } else {
            getChannel().basicAck(envelope.getDeliveryTag(), false);
        }
    }
}
