package com.zing.springcloudalibaba.rocketmq;

import com.zing.springcloudalibaba.constant.RocketMQConstant;
import com.zing.springcloudalibaba.domain.messaging.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author Zing
 * @date 2020-07-12
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "consumer-group", topic = RocketMQConstant.TEST_TOPIC)
public class ConsumeListener implements RocketMQListener<MessageDTO> {

    @Override
    public void onMessage(MessageDTO message) {
        log.info("消费消息：{}", message);
    }
}
