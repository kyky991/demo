package com.zing.test.dubbo.comsumer;

import com.zing.test.dubbo.service.IGreetingService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.RpcContext;

public class ApiConsumer {

    public static void main(String[] args) throws Exception {
        ReferenceConfig<IGreetingService> referenceConfig = new ReferenceConfig<>();

        referenceConfig.setApplication(new ApplicationConfig("dubbo-consumer"));
        referenceConfig.setRegistry(new RegistryConfig("zookeeper://zookeeper:2181"));
        referenceConfig.setInterface(IGreetingService.class);
        referenceConfig.setTimeout(5000);

        referenceConfig.setVersion("1.0.0");
        referenceConfig.setGroup("dubbo");

        referenceConfig.setAsync(true);

        IGreetingService greetingService = referenceConfig.get();

        RpcContext.getContext().setAttachment("company", "balabala");

        System.out.println(greetingService.echo("hello"));

        System.out.println(RpcContext.getContext().getFuture().get());

        RpcContext.getContext().getCompletableFuture().whenComplete((o, throwable) -> {
            System.out.println(o);
        });

        Thread.currentThread().join();
    }

}
