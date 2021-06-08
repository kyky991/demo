package com.zing.springcloudalibaba;

import com.zing.springcloudalibaba.rocketmq.MySink;
import com.zing.springcloudalibaba.rocketmq.PointsSink;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 可不写 @EnableDiscoveryClient 默认开启
 *
 * @author Zing
 * @date 2020-07-11
 */
@EnableBinding({Sink.class, MySink.class, PointsSink.class})
@MapperScan("com.zing.springcloudalibaba.mapper")
@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

}