package com.zing.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestSubService {

    @Async
    public void asyncNest(String msg) {
        log.warn(msg + "-nest async..................................");
    }

}
