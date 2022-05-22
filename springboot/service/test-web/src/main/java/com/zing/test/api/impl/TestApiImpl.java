package com.zing.test.api.impl;

import com.zing.test.api.ITestApi;
import com.zing.test.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@DubboService
public class TestApiImpl implements ITestApi {

    @Autowired
    private TestService testService;

    @Override
    public int poll(String msg) {
        testService.async(msg);
        return 2;
    }

}
