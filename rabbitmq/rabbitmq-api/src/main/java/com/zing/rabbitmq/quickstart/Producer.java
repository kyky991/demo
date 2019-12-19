package com.zing.rabbitmq.quickstart;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.zing.rabbitmq.RabbitMQFactory;

/**
 * @author Zing
 * @date 2019-11-21
 */
public class Producer {

    public static void main(String[] args) throws Exception {
        //1 创建一个ConnectionFactory, 并进行配置
        ConnectionFactory connectionFactory = RabbitMQFactory.createConnectionFactory();

        //2 通过连接工厂创建连接
        Connection connection = connectionFactory.newConnection();

        //3 创建Channel
        Channel channel = connection.createChannel();

        //4 通过Channel发送数据
        String msg = "Hello World!";
        for (int i = 0; i < 5; i++) {
            channel.basicPublish("", "test001", null, msg.getBytes());
        }

        //5 关闭相关的连接
        channel.close();
        connection.close();
    }
}
