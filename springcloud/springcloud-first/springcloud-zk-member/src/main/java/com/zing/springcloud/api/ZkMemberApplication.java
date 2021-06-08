package com.zing.springcloud.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Zing
 * @date 2019-11-12
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ZkMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZkMemberApplication.class, args);
    }

}
