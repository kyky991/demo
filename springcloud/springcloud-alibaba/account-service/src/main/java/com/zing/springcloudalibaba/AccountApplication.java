package com.zing.springcloudalibaba;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 可不写 @EnableDiscoveryClient 默认开启
 *
 * @author Zing
 */
@MapperScan("com.zing.springcloudalibaba.mapper")
@SpringBootApplication(exclude = {DruidDataSourceAutoConfigure.class})
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

}