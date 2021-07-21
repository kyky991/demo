package com.zing.common.task.support.redis;

import com.zing.common.task.support.AbstractDelayHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Slf4j
public class RedissonDelayHandler extends AbstractDelayHandler implements InitializingBean {

    private static final String DEFAULT_KEY = "delay:task:redisson";

    private RedissonClient redissonClient;

    public RedissonDelayHandler(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(redissonClient, "redisson client must not be null.");
        init();
    }

    @Override
    protected Object take() throws InterruptedException {
        RBlockingQueue<Object> queue = getBlockingQueue();
        return queue.take();
    }

    @Override
    public boolean offer(Object task, long delay, TimeUnit timeUnit) {
        RDelayedQueue<Object> delayedQueue = getDelayedQueue();
        delayedQueue.remove(task);
        delayedQueue.offer(task, delay, timeUnit);
        return true;
    }

    @Override
    public boolean remove(Object task) {
        RDelayedQueue<Object> delayedQueue = getDelayedQueue();
        return delayedQueue.remove(task);
    }

    @Override
    public boolean removeIf(Predicate<Object> filter) {
        Objects.requireNonNull(filter);

        boolean removed = false;
        RDelayedQueue<Object> delayedQueue = getDelayedQueue();
        final Iterator<Object> each = delayedQueue.iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }

        return removed;
    }

    private RBlockingQueue<Object> getBlockingQueue() {
        return redissonClient.getBlockingQueue(getName());
    }

    private RDelayedQueue<Object> getDelayedQueue() {
        return redissonClient.getDelayedQueue(getBlockingQueue());
    }

    private String getName() {
        String key = taskProperties.getRedisson().getKey();
        return StringUtils.isNotBlank(key) ? key : DEFAULT_KEY;
    }

}
