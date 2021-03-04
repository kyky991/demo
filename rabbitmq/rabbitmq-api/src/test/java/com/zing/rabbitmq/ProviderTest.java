package com.zing.rabbitmq;

import com.rabbitmq.client.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.DoubleStream;

public class ProviderTest {

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

        //2 通过连接工厂创建连接
        connection = connectionFactory.newConnection();

        //3 通过connection创建一个Channel
        channel = connection.createChannel();
    }

    @After
    public void tearDown() throws Exception {
        //5 关闭相关的连接
        channel.close();
        connection.close();
    }

    @Test
    public void testFirst() throws Exception {
        //4 通过Channel发送数据
        String msg = "Hello Rabbitmq";
        channel.basicPublish("", "test001", null, msg.getBytes());
    }

    @Test
    public void testDirectExchange() throws Exception {
        String exchangeName = "test_direct_exchange";
        String routingKey = "test.direct";

        String msg = "direct exchange test msg";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
    }

    @Test
    public void testTopicExchange() throws Exception {
        String exchangeName = "test_topic_exchange";

        String routingKey1 = "user.save";
        String routingKey2 = "user.update";
        String routingKey3 = "user.delete.abc";

        String msg = "topic exchange test msg";
        channel.basicPublish(exchangeName, routingKey1, null, msg.getBytes());
        channel.basicPublish(exchangeName, routingKey2, null, msg.getBytes());
        channel.basicPublish(exchangeName, routingKey3, null, msg.getBytes());
    }

    @Test
    public void testFanoutExchange() throws Exception {
        String exchangeName = "test_fanout_exchange";

        String msg = "fanout exchange test msg";
        for (int i = 0; i < 5; ++i) {
            channel.basicPublish(exchangeName, "vfvdf", null, msg.getBytes());
        }
    }

    @Test
    public void testMessage() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put("test01", "111");
        headers.put("test02", "222");

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .expiration("10000")
                .headers(headers)
                .build();

        String msg = "test message";
        for (int i = 0; i < 5; ++i) {
            channel.basicPublish("", "test001", properties, msg.getBytes());
        }
    }

    @Test
    public void testConfirm() throws Exception {
        channel.confirmSelect();

        String exchangeName = "test_confirm_exchange";
        String routingKey = "confirm.save";

        String msg = "confirm test msg";
        channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());

        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("----------------ack----------------");
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("-------------no ack----------------");
            }
        });

        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReturn() throws Exception {
        String exchangeName = "test_return_exchange";
        String routingKey = "return.save";
        String routingKeyError = "error.save";

        String msg = "return test msg";
        channel.basicPublish(exchangeName, routingKeyError, false, null, msg.getBytes());

        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.err.println("----------------handle return----------------");
                System.err.println("replyCode: " + replyCode);
                System.err.println("replyText: " + replyText);
                System.err.println("exchange: " + exchange);
                System.err.println("routingKey: " + routingKey);
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
    public void testCustomConsumer() throws Exception {
        String exchangeName = "test_custom_consumer_exchange";
        String routingKey = "consumer.save";

        String msg = "custom consumer test msg";
        for (int i = 0; i < 5; ++i) {
            channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
        }
    }

    @Test
    public void testQos() throws Exception {
        String exchangeName = "test_qos_exchange";
        String routingKey = "qos.save";

        String msg = "qos test msg";
        for (int i = 0; i < 5; ++i) {
            channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
        }
    }

    @Test
    public void testAck() throws Exception {
        String exchangeName = "test_ack_exchange";
        String routingKey = "ack.save";

        for (int i = 0; i < 5; ++i) {
            Map<String, Object> headers = new HashMap<>();
            headers.put("num", i);

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .headers(headers)
                    .build();

            String msg = "ack test msg " + i;
            channel.basicPublish(exchangeName, routingKey, properties, msg.getBytes());
        }
    }

    @Test
    public void testTTL() throws Exception {
        String exchangeName = "test_ttl_exchange";
        String routingKey = "ttl.save";

        String msg = "ttl test msg";
        for (int i = 0; i < 5; ++i) {
            channel.basicPublish(exchangeName, routingKey, null, msg.getBytes());
        }
    }

    @Test
    public void testDlx() throws Exception {
        String exchangeName = "test_dlx_exchange";
        String routingKey = "dlx.save";

        String msg = "dlx test msg";
        for (int i = 0; i < 5; ++i) {

            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .deliveryMode(2)
                    .contentEncoding("UTF-8")
                    .expiration("10000")
                    .build();

            channel.basicPublish(exchangeName, routingKey, properties, msg.getBytes());
        }
    }
}
