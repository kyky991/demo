package com.zing.rabbitmq.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Zing
 * @date 2019-12-07
 */
@EnableBinding(Barista.class)
@Component
public class RabbitSender {

    @Autowired
    private Barista barista;

    public void send(Object message, Map<String, Object> properties) throws Exception {
        MessageHeaders msh = new MessageHeaders(properties);
        Message msg = MessageBuilder.createMessage(message, msh);
        boolean status = barista.output().send(msg);
        System.err.println("status:" + status);
    }

}
