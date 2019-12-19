package com.zing.rabbitmq.stream;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author Zing
 * @date 2019-12-07
 */
@EnableBinding(Barista.class)
@Component
public class RabbitReceiver {

    @StreamListener(Barista.INPUT_CHANNEL)
    public void receiver(Message message,
                         @Header(AmqpHeaders.CHANNEL) Channel channel,
                         @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) throws Exception {
        System.err.println("message:" + message);
        channel.basicAck(deliveryTag, false);
    }

}
