package com.zing.rabbitmq.api.limit;

import com.rabbitmq.client.Channel;
import com.zing.rabbitmq.RabbitMQFactory;

/**
 * @author Zing
 * @date 2019-12-02
 */
public class Consumer {

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMQFactory.createChannel();

        String exchangeName = "test_qos_exchange";
        String routingKey = "#.qos";
        String queueName = "test_qos_queue";

        channel.exchangeDeclare(exchangeName, "topic", true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        channel.basicQos(0, 3, false);

        //1 限流方式 autoAck设置为false
        channel.basicConsume(queueName, false, new MyConsumer(channel));
    }

}
