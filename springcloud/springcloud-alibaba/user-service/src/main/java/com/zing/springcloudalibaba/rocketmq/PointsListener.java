package com.zing.springcloudalibaba.rocketmq;

import com.zing.springcloudalibaba.constant.RocketMQConstant;
import com.zing.springcloudalibaba.domain.messaging.BlogPointsDTO;
import com.zing.springcloudalibaba.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Zing
 * @date 2020-07-13
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = RocketMQConstant.POINTS_CONSUMER_GROUP,
        topic = RocketMQConstant.POINTS_TOPIC)
public class PointsListener implements RocketMQListener<BlogPointsDTO> {

    @Autowired
    private UserService userService;

    @Override
    public void onMessage(BlogPointsDTO message) {
        userService.collectPoints(message);
        log.info("listener 消费积分");
    }
}
