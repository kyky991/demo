package com.zing.rabbitmq.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Zing
 * @date 2019-12-07
 */
public interface Barista {

    String INPUT_CHANNEL = "input_channel";

    @Output(Barista.INPUT_CHANNEL)
    SubscribableChannel input();

}
