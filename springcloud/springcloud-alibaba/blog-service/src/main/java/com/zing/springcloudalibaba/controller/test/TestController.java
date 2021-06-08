package com.zing.springcloudalibaba.controller.test;

import com.zing.springcloudalibaba.constant.RocketMQConstant;
import com.zing.springcloudalibaba.domain.dto.UserDTO;
import com.zing.springcloudalibaba.domain.messaging.MessageDTO;
import com.zing.springcloudalibaba.domain.query.LoginBody;
import com.zing.springcloudalibaba.domain.query.TimeQuery;
import com.zing.springcloudalibaba.feign.UrlFeign;
import com.zing.springcloudalibaba.feign.UserFeign;
import com.zing.springcloudalibaba.rocketmq.MySource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Zing
 * @date 2020-07-11
 */
@Slf4j
@RefreshScope
@RestController
public class TestController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient client;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private UrlFeign urlFeign;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${config.test}")
    private String test;

    @GetMapping("/instance")
    public List<ServiceInstance> instance() {
        List<ServiceInstance> list = discoveryClient.getInstances("user-service");
        log.info(list.toString());
        log.info(test);
        return list;
    }

    @GetMapping("/first")
    public String first() {
        System.out.println(client);
        ServiceInstance choose = client.choose("user-service");
        System.out.println(choose);
        return restTemplate.getForObject("http://user-service/first?timeout={1}", String.class, 0);
    }

    @GetMapping("/feign")
    public String feign() {
        return userFeign.first(10);
    }

    @GetMapping("/multiParamGet")
    public String multiParamGet() {
        return userFeign.first(new TimeQuery(0));
    }

    @GetMapping("/login")
    public UserDTO login(String username) {
        return userFeign.login(new LoginBody(username, "xxx"));
    }

    @GetMapping("/urlFeign")
    public String urlFeign() {
        return urlFeign.first(10);
    }

    @GetMapping("/user")
    public UserDTO user() {
        return userFeign.user(ThreadLocalRandom.current().nextInt(5));
    }

    @GetMapping("/send")
    public String send(String msg) {
        rocketMQTemplate.convertAndSend(
                RocketMQConstant.TEST_TOPIC,
                MessageDTO.builder().id(UUID.randomUUID().toString()).msg(msg).build()
        );
        return "SUCCESS";
    }

    @Autowired
    private Source source;

    @GetMapping("/stream")
    public String stream(String msg) {
        source.output().send(
                MessageBuilder.withPayload("消息体：" + msg).build()
        );
        return "SUCCESS";
    }

    @Autowired
    private MySource mySource;

    @GetMapping("/stream2")
    public String stream2(String msg) {
        mySource.output().send(
                MessageBuilder.withPayload("消息体2：" + msg).build()
        );
        return "SUCCESS";
    }
}
