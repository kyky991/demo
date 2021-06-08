package com.zing.netty.easychat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.zing.netty.easychat.mapper"})
@ComponentScan(basePackages = {"com.zing.netty.easychat", "org.n3r.idworker"})
public class Application {

    @Bean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
