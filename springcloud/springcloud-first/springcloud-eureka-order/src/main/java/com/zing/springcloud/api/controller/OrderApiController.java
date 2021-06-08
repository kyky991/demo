package com.zing.springcloud.api.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zing.springcloud.api.feign.MemberApiFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    private MemberApiFeign memberApiFeign;

    @RequestMapping("/order/member/rest/{ms}")
    public String memberRest(@PathVariable Integer ms) {
        String url = "http://app-member/member/" + ms;
        return "app-order -> " + restTemplate.getForObject(url, String.class);
    }

    @RequestMapping("/order/member/feign/{ms}")
    public String memberFeign(@PathVariable Integer ms) {
        return "app-order -> feign:" + memberApiFeign.member(ms);
    }

    @HystrixCommand(fallbackMethod = "hystrixFallbackMethod")
    @RequestMapping("/order/member/hystrix/method/{ms}")
    public String hystrixMethod(@PathVariable Integer ms) {
        return "app-order -> hystrix method:" + memberApiFeign.member(ms);
    }

    private String hystrixFallbackMethod(Integer ms) {
        return "hystrix fallback method:" + ms;
    }

    @RequestMapping("/order/member/hystrix/class/{ms}")
    public String hystrixClass(@PathVariable Integer ms) {
        return "app-order -> hystrix class:" + memberApiFeign.member(ms);
    }
}
