package com.zing.rabbitmq;

import com.zing.rabbitmq.stream.RabbitSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Zing
 * @date 2019-12-08
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTest {

    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void testSend() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put("num", UUID.randomUUID().toString());
        rabbitSender.send("Hello World Cloud Stream Rabbit...", properties);
    }

}