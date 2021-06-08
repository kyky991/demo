package com.zing.springcloud.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Zing
 * @date 2019-11-12
 */
@RestController
public class OrderApiController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/order")
    public String order() {
        String url = "http://zk-member/member";
        return "zk-order -> " + restTemplate.getForObject(url, String.class);
    }

}
