package com.zing.rabbitmq;

import com.rabbitmq.client.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConsumerTest {

    ConnectionFactory connectionFactory = null;
    Connection connection = null;
    Channel channel = null;

    @Before
    public void setUp() throws Exception {
        //1 创建一个ConnectionFactory, 并进行配置
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("rabbit.zing.com");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");

        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(3000);

        //2 通过连接工厂创建连接
        connection = connectionFactory.newConnection();

        //3 通过connection创建一个Channel
        channel = connection.createChannel();
    }

    @After
    public void tearDown() throws Exception {
        // 关闭相关的连接
        channel.close();
        connection.close();
    }

    @Test
    public void testFirst() throws Exception {
        //4 声明（创建）一个队列
        String queueName = "test001";
        channel.queueDeclare(queueName, true, false, false, null);

        //5 创建消费者
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        //6 设置Channel
        channel.basicConsume(queueName, true, queueingConsumer);

        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.err.println("消费端: " + msg);
        }
    }

    @Test
    public void testDirectExchange() throws Exception {
        String exchangeName = "test_direct_exchange";
        String exchangeType = "direct";

        String queueName = "test_direct_queue";

        String routingKey = "test.direct.key";

        //表示声明了一个交换机
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, null);
        //表示声明了一个队列
        channel.queueDeclare(queueName, true, false, false, null);
        //建立一个绑定关系:
        channel.queueBind(queueName, exchangeName, routingKey);

        //durable 是否持久化消息
        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, true, queueingConsumer);

        while (true) {
            //获取消息，如果没有消息，这一步将会一直阻塞
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("收到消息：" + msg);
        }
    }

    @Test
    public void testTopicExchange() throws Exception {
        String exchange = "test_topic_exchange";
        String exchangeType = "topic";

        String queueName = "test_topic_queue";

        String routingKey = "user.*";
//        String routingKey = "user.#";

        channel.exchangeDeclare(exchange, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchange, routingKey);

        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        channel.basicConsume(queueName, true, queueingConsumer);

        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("收到消息：" + msg);
        }
    }

    @Test
    public void testFanoutExchange() throws Exception {
        String exchange = "test_fanout_exchange";
        String exchangeType = "fanout";

        String queueName = "test_fanout_queue_3";

        String routingKey = "";

        channel.exchangeDeclare(exchange, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchange, routingKey);

        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        channel.basicConsume(queueName, true, queueingConsumer);

        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("收到消息：" + msg);
        }
    }

    @Test
    public void testConfirm() throws Exception {
        String exchangeName = "test_confirm_exchange";
        String exchangeType = "topic";
        String routingKey = "confirm.save";

        String queueName = "test_confirm_queue";

        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        channel.basicConsume(queueName, true, queueingConsumer);

        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("收到消息：" + msg);
        }
    }

    @Test
    public void testReturn() throws Exception {
        String exchangeName = "test_return_exchange";
        String exchangeType = "topic";
        String routingKey = "return.#";

        String queueName = "test_return_queue";

        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);

        channel.basicConsume(queueName, true, queueingConsumer);

        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            String msg = new String(delivery.getBody());
            System.out.println("收到消息：" + msg);
        }
    }

    @Test
    public void testCustomConsumer() throws Exception {
        String exchangeName = "test_custom_consumer_exchange";
        String exchangeType = "topic";
        String routingKey = "consumer.#";

        String queueName = "test_custom_consumer_queue";

        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        channel.basicConsume(queueName, true, "", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.err.println("----------------handle custom consumer----------------");
                System.err.println("consumerTag: " + consumerTag);
                System.err.println("envelope: " + envelope);
                System.err.println("properties: " + properties);
                System.err.println("body: " + new String(body));
            }
        });

        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQos() throws Exception {
        String exchangeName = "test_qos_exchange";
        String exchangeType = "topic";
        String routingKey = "qos.#";

        String queueName = "test_qos_queue";

        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        channel.basicQos(0, 3, false);

        channel.basicConsume(queueName, false, "", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.err.println("----------------handle qos consumer----------------");
                System.err.println("consumerTag: " + consumerTag);
                System.err.println("envelope: " + envelope);
                System.err.println("properties: " + properties);
                System.err.println("body: " + new String(body));

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });

        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAck() throws Exception {
        String exchangeName = "test_ack_exchange";
        String exchangeType = "topic";
        String routingKey = "ack.#";

        String queueName = "test_ack_queue";

        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);

        channel.basicConsume(queueName, false, "", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.err.println("----------------handle ack consumer---------------");
                System.err.println("body: " + new String(body));

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if ((Integer)properties.getHeaders().get("num") == 0) {
                    channel.basicNack(envelope.getDeliveryTag(), false, true);
                } else {
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        });

        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTTL() throws Exception {
        String exchangeName = "test_ttl_exchange";
        String exchangeType = "topic";
        String routingKey = "ttl.#";

        String queueName = "test_ttl_queue";

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-max-length", 3000);
        arguments.put("x-message-ttl", 10000);
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, false, false, false, arguments);
        channel.queueBind(queueName, exchangeName, routingKey);

        channel.basicConsume(queueName, true, "", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.err.println("----------------handle ttl consumer----------------");
                System.err.println("consumerTag: " + consumerTag);
                System.err.println("envelope: " + envelope);
                System.err.println("properties: " + properties);
                System.err.println("body: " + new String(body));
            }
        });

        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDlx() throws Exception {
        String exchangeName = "test_dlx_exchange";
        String exchangeType = "topic";
        String routingKey = "dlx.#";

        String queueName = "test_dlx_queue";


        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "dlx.exchange");
        channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
        channel.queueDeclare(queueName, true, false, false, arguments);
        channel.queueBind(queueName, exchangeName, routingKey);


        // 进行死信队列的声明
        channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
        channel.queueDeclare("dlx.queue", true, false, false, null);
        channel.queueBind("dlx.queue", "dlx.exchange", "#");


        channel.basicConsume(queueName, true, "", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.err.println("----------------handle dlx consumer----------------");
                System.err.println("consumerTag: " + consumerTag);
                System.err.println("envelope: " + envelope);
                System.err.println("properties: " + properties);
                System.err.println("body: " + new String(body));
            }
        });

        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}