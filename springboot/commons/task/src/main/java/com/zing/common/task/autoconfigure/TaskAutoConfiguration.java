package com.zing.common.task.autoconfigure;

import com.zing.common.task.Constant;
import com.zing.common.task.DelayHandler;
import com.zing.common.task.props.TaskProperties;
import com.zing.common.task.support.DefaultDelayHandler;
import com.zing.common.task.support.TaskManager;
import com.zing.common.task.support.WrappedDelayHandler;
import com.zing.common.task.support.redis.RedissonDelayHandler;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({TaskProperties.class})
@ConditionalOnProperty(name = Constant.TASK_ENABLED, havingValue = "true", matchIfMissing = true)
public class TaskAutoConfiguration {

    @Bean
    public TaskManager taskManager() {
        return new TaskManager();
    }

    @Bean
    @ConditionalOnBean({RedissonClient.class})
    @ConditionalOnProperty(name = Constant.TASK_REDIS_ENABLED, havingValue = "true", matchIfMissing = true)
    public DelayHandler defaultDelayHandler(TaskManager taskManager, TaskProperties taskProperties, RedissonClient redissonClient) {
        WrappedDelayHandler delayHandler = new WrappedDelayHandler(new DefaultDelayHandler(redissonClient));
        delayHandler.setTaskManager(taskManager);
        delayHandler.setTaskProperties(taskProperties);
        delayHandler.init();
        return delayHandler;
    }

    @Bean
    @ConditionalOnBean({RedissonClient.class})
    @ConditionalOnProperty(name = Constant.TASK_REDISSON_ENABLED, havingValue = "true", matchIfMissing = true)
    public DelayHandler redissonDelayHandler(TaskManager taskManager, TaskProperties taskProperties, RedissonClient redissonClient) {
        WrappedDelayHandler delayHandler = new WrappedDelayHandler(new RedissonDelayHandler(redissonClient));
        delayHandler.setTaskManager(taskManager);
        delayHandler.setTaskProperties(taskProperties);
        delayHandler.init();
        return delayHandler;
    }

}
