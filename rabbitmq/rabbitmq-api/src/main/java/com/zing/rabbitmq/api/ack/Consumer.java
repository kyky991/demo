package com.zing.rabbitmq.api.ack;

import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.RabbitMQFactory;

/**
 * @author Zing
 * @date 2019-12-02
 */
public class Consumer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        String exchangeName = "test_ack_exchange";
        String routingKey = "#.ack";
        String queueName = "test_ack_queue";

        channel.exchangeDeclare(exchangeName, "topic", true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        //手工签收 autoAck为false
        channel.basicConsume(queueName, false, new MyConsumer(channel));
    }

}
