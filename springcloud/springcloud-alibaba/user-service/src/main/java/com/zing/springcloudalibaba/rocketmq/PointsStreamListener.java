package com.zing.springcloudalibaba.rocketmq;

import com.zing.springcloudalibaba.domain.messaging.BlogPointsDTO;
import com.zing.springcloudalibaba.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

/**
 * @author Zing
 * @date 2020-07-13
 */
@Slf4j
@Component
public class PointsStreamListener {

    @Autowired
    private UserService userService;

    @StreamListener(PointsSink.INPUT)
    public void collect(BlogPointsDTO message) {
        userService.collectPoints(message);
        log.info("stream 消费积分");
    }
}
