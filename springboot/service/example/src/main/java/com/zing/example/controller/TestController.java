package com.zing.example.controller;

import com.zing.test.api.ITestApi;
import com.zing.test.feign.TestClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private TestClient testClient;

    @DubboReference
    private ITestApi testApi;

    @GetMapping("/echo")
    public Object echo() {
        testApi.poll("dubbo");
        testClient.poll("feign");
        return RandomStringUtils.randomAlphanumeric(10);
    }

}
