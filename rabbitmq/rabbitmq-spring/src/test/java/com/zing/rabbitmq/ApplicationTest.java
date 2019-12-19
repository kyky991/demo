package com.zing.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zing.rabbitmq.entity.Order;
import com.zing.rabbitmq.entity.Packaged;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Zing
 * @date 2019-12-04
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testAdmin() {
        rabbitAdmin.declareExchange(new DirectExchange("test.direct.exchange", false, false));
        rabbitAdmin.declareExchange(new TopicExchange("test.topic.exchange", false, false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout.exchange", false, false));

        rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));

        rabbitAdmin.declareBinding(new Binding("test.direct.queue", Binding.DestinationType.QUEUE,
                "test.direct.exchange", "direct", null));

        rabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue("test.topic.queue", false))
                        .to(new TopicExchange("test.topic.exchange", false, false))
                        .with("topic.#"));

        rabbitAdmin.declareBinding(
                BindingBuilder.bind(new Queue("test.fanout.queue", false))
                        .to(new FanoutExchange("test.fanout.exchange", false, false)));

        // 清空队列
        rabbitAdmin.purgeQueue("test.topic.exchange", false);
    }

    @Test
    public void testSendMessage() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "消息描述");
        messageProperties.getHeaders().put("type", "消息类型");

        Message message = new Message("Hello Rabbit Template...".getBytes(), messageProperties);
        rabbitTemplate.convertAndSend("topic.exchange001", "spring.amqp", message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                System.err.println("postProcessMessage");
                message.getMessageProperties().getHeaders().put("desc", "额外消息描述");
                message.getMessageProperties().getHeaders().put("attr", "额外属性");
                return message;
            }
        });
    }

    @Test
    public void testSendMessage2() {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().put("desc", "消息描述");
        messageProperties.getHeaders().put("type", "消息类型");
        messageProperties.setContentType("text/plain");

        Message message = new Message("Hello Rabbit Template...".getBytes(), messageProperties);
        rabbitTemplate.send("topic.exchange001", "spring.rabbit", message);

        rabbitTemplate.convertAndSend("topic.exchange001", "spring.amqp", "send object...");
        rabbitTemplate.convertAndSend("topic.exchange002", "rabbit.client", "send object...");
    }

    @Test
    public void testSendJsonMessage() throws Exception {
        Order order = new Order(1L, "订单", "内容");
        String json = MAPPER.writeValueAsString(order);
        System.err.println("order: " + json);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");

        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.send("topic.exchange001", "spring.order", message);
    }

    @Test
    public void testSendJavaMessage() throws Exception {
        Order order = new Order(1L, "订单", "内容");
        String json = MAPPER.writeValueAsString(order);
        System.err.println("order: " + json);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        messageProperties.getHeaders().put("__TypeId__", "com.zing.rabbitmq.entity.Order");

        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.send("topic.exchange001", "spring.order", message);
    }

    @Test
    public void testSendMappingMessage() throws Exception {
        Order order = new Order(1L, "订单", "内容");
        String json = MAPPER.writeValueAsString(order);
        System.err.println("order: " + json);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        messageProperties.getHeaders().put("__TypeId__", "order");

        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.send("topic.exchange001", "spring.order", message);

        Packaged pack = new Packaged(2L, "包裹", "描述");
        String json2 = MAPPER.writeValueAsString(pack);
        System.err.println("pack: " + json2);

        MessageProperties messageProperties2 = new MessageProperties();
        messageProperties2.setContentType("application/json");
        messageProperties2.getHeaders().put("__TypeId__", "pack");

        Message message2 = new Message(json2.getBytes(), messageProperties2);
        rabbitTemplate.send("topic.exchange001", "spring.pack", message2);
    }

    @Test
    public void testSendImageMessage() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get("file", "tmp.png"));

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("image/png");
        messageProperties.getHeaders().put("extName", "png");

        Message message = new Message(bytes, messageProperties);
        rabbitTemplate.send("", "queue.image", message);
    }

    @Test
    public void testSendPdfMessage() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get("file", "tmp.pdf"));

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/pdf");

        Message message = new Message(bytes, messageProperties);
        rabbitTemplate.send("", "queue.pdf", message);
    }
}