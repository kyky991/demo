package com.zing.springcloudalibaba;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 可不写 @EnableDiscoveryClient 默认开启
 *
 * @author Zing
 */
@EnableFeignClients
@MapperScan("com.zing.springcloudalibaba.mapper")
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}