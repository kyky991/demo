package com.zing.test.dubbo;

import lombok.Data;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Data
public class DubboProvider {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("provider.xml");
        context.start();
        System.in.read();
    }

}
