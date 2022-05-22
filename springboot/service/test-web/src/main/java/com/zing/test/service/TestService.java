package com.zing.test.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestService {

    @Autowired
    private TestSubService testSubService;

    @Async
    public void async(String msg) {
        log.warn(msg + "-async..................................");
        testSubService.asyncNest(msg);
    }

}
