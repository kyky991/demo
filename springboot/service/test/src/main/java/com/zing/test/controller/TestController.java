package com.zing.test.controller;

import com.zing.test.demo.DemoService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Reference
    private DemoService demoService;

    @GetMapping("/test")
    public String test(String msg) {
        return demoService.echo(msg);
    }

}
