package com.zing.springcloudalibaba;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.zing.springcloudalibaba.rocketmq.MySource;
import com.zing.springcloudalibaba.rocketmq.PointsSource;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * 实现feign全局配置 @EnableFeignClients(defaultConfiguration = GlobalFeignConfig.class)
 *
 * @author Zing
 * @date 2020-07-11
 */
@EnableBinding({Source.class, MySource.class, PointsSource.class})
@EnableFeignClients//(defaultConfiguration = GlobalFeignConfig.class)
@MapperScan("com.zing.springcloudalibaba.mapper")
@SpringCloudApplication
public class BlogApplication {

    public static void main(String[] args) {
//        System.setProperty(TransportConfig.HEARTBEAT_CLIENT_IP, "clouuuuud");
        SpringApplication.run(BlogApplication.class, args);
    }

    @Bean
    @LoadBalanced
    @SentinelRestTemplate
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
