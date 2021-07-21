package com.zing.test.dubbo;

import com.zing.test.demo.DemoService;
import lombok.Data;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Data
public class DubboConsumer {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("consumer.xml");
        context.start();

        DemoService demoService = context.getBean(DemoService.class);

        for (int i = 0; i < 1; i++) {
            String msg = demoService.echo("hello");
            System.out.println(msg);
        }
    }

}
