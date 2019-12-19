package com.zing.rabbitmq.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

/**
 * @author Zing
 * @date 2019-12-07
 */
public class ImageMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        throw new MessageConversionException("Error");
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        Object name = message.getMessageProperties().getHeaders().get("extName");
        String extName = name == null ? "jpg" : name.toString();

        byte[] body = message.getBody();
        String fileName = UUID.randomUUID().toString();
        String path = String.format("file/%s.%s", fileName, extName);
        File f = new File(path);
        try {
            Files.copy(new ByteArrayInputStream(body), f.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }
}
