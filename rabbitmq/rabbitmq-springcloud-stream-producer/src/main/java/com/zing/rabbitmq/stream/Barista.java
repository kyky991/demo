package com.zing.rabbitmq.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author Zing
 * @date 2019-12-07
 */
public interface Barista {

    String OUTPUT_CHANNEL = "output_channel";

    @Output(Barista.OUTPUT_CHANNEL)
    MessageChannel output();

}
