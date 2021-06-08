package com.zing.springcloud.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zing
 * @date 2019-11-12
 */
@RestController
public class MemberApiController {

    @Value("${server.port}")
    String serverPort;

    @RequestMapping("/member")
    public String member() {
        return "zk-member:" + serverPort;
    }

}
