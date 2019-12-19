package com.zing.rabbitmq;

import com.zing.rabbitmq.entity.Order;
import com.zing.rabbitmq.producer.RabbitSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zing
 * @date 2019-12-07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void testSend() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("num", "11111");
        properties.put("now", new Date());
        rabbitSender.send("Hello Word SpringBoot Rabbit", properties);
    }

    @Test
    public void testSendOrder() throws Exception {
        Order order = new Order(1L, "订单", "内容");
        rabbitSender.sendOrder(order);
    }
}