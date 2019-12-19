package com.zing.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author Zing
 * @date 2019-12-02
 */
public class RabbitMQFactory {

    public static ConnectionFactory createConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("hadooooop");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }

    public static Connection createConnection() throws Exception {
        return createConnectionFactory().newConnection();
    }

    public static Channel createChannel() throws Exception {
        return createConnection().createChannel();
    }
}
