package com.zing.springcloudalibaba.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author Zing
 * @date 2020-07-12
 */
@Slf4j
@RestController
public class TestController {

    @Value("${server.port}")
    private int port;

    @GetMapping("/")
    public String health() {
        return "UP";
    }

    @GetMapping("/first")
    public String first(Integer timeout) {
        if (timeout == null) {
            timeout = ThreadLocalRandom.current().nextInt(100);
        }
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String s = String.format("port: %d %d", port, timeout);
        log.info(s);
        return s;
    }

}
