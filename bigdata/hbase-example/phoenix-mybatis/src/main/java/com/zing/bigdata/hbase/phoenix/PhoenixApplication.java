package com.zing.bigdata.hbase.phoenix;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = "com.zing.bigdata.hbase.phoenix.mapper", sqlSessionFactoryRef = "phoenixSqlSessionFactory")
public class PhoenixApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoenixApplication.class, args);
    }

}
