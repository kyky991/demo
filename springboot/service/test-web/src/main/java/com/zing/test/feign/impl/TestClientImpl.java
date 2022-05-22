package com.zing.test.feign.impl;

import com.zing.test.feign.TestClient;
import com.zing.test.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestClientImpl implements TestClient {

    @Autowired
    private TestService testService;

    @Override
    public int poll(String msg) {
        testService.async(msg);
        return 1;
    }

}
