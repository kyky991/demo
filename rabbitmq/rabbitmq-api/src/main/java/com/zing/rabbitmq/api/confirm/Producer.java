package com.zing.rabbitmq.api.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.zing.rabbitmq.RabbitMQFactory;

import java.io.IOException;

/**
 * @author Zing
 * @date 2019-12-01
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        //1 创建一个ConnectionFactory, 并进行配置
        ConnectionFactory connectionFactory = RabbitMQFactory.createConnectionFactory();

        //2 通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        //3 创建Channel
        Channel channel = connection.createChannel();

        //4 指定消息投递模式：消息的确认模式
        channel.confirmSelect();

        //5 发送数据
        String exchangeName = "test_confirm_exchange";
        String routingKey = "test.confirm";

        String msg = "Hello World! Confirm Message";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());

        //6 添加一个确认监听
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("handleAck...");
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("handleNack...");
            }
        });
    }

}
