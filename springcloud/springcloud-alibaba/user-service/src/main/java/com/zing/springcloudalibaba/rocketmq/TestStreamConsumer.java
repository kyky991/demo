package com.zing.springcloudalibaba.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Component;

/**
 * @author Zing
 * @date 2020-07-13
 */
@Slf4j
@Component
public class TestStreamConsumer {

    @StreamListener(Sink.INPUT)
    public void receive(String message) {
        log.info("stream rocketmq message = {}", message);
    }

    @StreamListener(MySink.INPUT)
    public void receive2(String message) {
        log.info("自定义接口 stream rocketmq message = {}", message);

//        throw new IllegalArgumentException("ex...");
    }

    /**
     * 全局异常处理
     *
     * @param message 异常消息
     */
    @StreamListener("errorChannel")
    public void error(Message<?> message) {
        ErrorMessage errorMessage = (ErrorMessage) message;
        log.warn("异常 errorMessage = {}", errorMessage);
    }
}
