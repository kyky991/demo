package com.zing.springcloudalibaba.rocketmq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author Zing
 * @date 2020-07-13
 */
public interface MySource {

    String OUTPUT = "my-output";

    @Output(OUTPUT)
    MessageChannel output();
}
