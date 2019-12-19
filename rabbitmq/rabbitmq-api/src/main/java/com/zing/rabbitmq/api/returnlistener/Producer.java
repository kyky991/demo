package com.zing.rabbitmq.api.returnlistener;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ReturnListener;
import com.zing.rabbitmq.RabbitMQFactory;

import java.io.IOException;

/**
 * @author Zing
 * @date 2019-12-02
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("handleReturn...");
                System.out.println("replyCode:" + replyCode);
                System.out.println("replyText:" + replyText);
                System.out.println("exchange:" + exchange);
                System.out.println("routingKey:" + routingKey);
                System.out.println("properties:" + properties);
                System.out.println("body:" + new String(body));
            }
        });

        String exchangeName = "test_return_exchange";
        String routingKey = "test.return";
        String routingKeyError = "test.error";

        String msg = "Hello World! Return Message";
        channel.basicPublish(exchangeName, routingKey, true, null, msg.getBytes());
//        channel.basicPublish(exchangeName, routingKeyError, true, null, msg.getBytes());
    }

}
