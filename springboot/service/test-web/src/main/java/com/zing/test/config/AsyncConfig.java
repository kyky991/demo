package com.zing.test.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
public class AsyncConfig extends AsyncConfigurerSupport {

    @Override
    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        threadPool.setCorePoolSize(8);
        threadPool.setMaxPoolSize(20);
        threadPool.setQueueCapacity(2000);
        threadPool.setKeepAliveSeconds(60);
        threadPool.setWaitForTasksToCompleteOnShutdown(true);
        threadPool.setThreadNamePrefix("async-thread-pool-");
        threadPool.initialize();
        return threadPool;
    }

}
