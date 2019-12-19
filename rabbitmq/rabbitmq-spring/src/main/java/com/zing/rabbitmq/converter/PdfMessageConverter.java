package com.zing.rabbitmq.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author Zing
 * @date 2019-12-07
 */
public class PdfMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        throw new MessageConversionException("Error");
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        byte[] body = message.getBody();
        String fileName = UUID.randomUUID().toString();
        String path = String.format("file/%s.pdf", fileName);
        try {
            Files.copy(new ByteArrayInputStream(body), Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
