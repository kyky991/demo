package com.zing.test.dubbo.provider;

import com.zing.test.dubbo.service.IGreetingService;
import com.zing.test.dubbo.service.impl.GreetingServiceImpl;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

public class ApiProvider {

    public static void main(String[] args) throws Exception {
        ServiceConfig<IGreetingService> serviceConfig = new ServiceConfig<>();

        serviceConfig.setApplication(new ApplicationConfig("dubbo-provider"));
        serviceConfig.setRegistry(new RegistryConfig("zookeeper://zookeeper:2181"));
        serviceConfig.setInterface(IGreetingService.class);
        serviceConfig.setRef(new GreetingServiceImpl());

        serviceConfig.setVersion("1.0.0");
        serviceConfig.setGroup("dubbo");

        serviceConfig.export();

        System.out.println("server is started");
        System.in.read();
    }

}
