package com.zing.springcloudalibaba.rocketmq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author Zing
 * @date 2020-07-13
 */
public interface MySink {

    String INPUT = "my-input";

    @Input(INPUT)
    SubscribableChannel input();
}
