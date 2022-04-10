package com.zing.test.service;

import com.zing.test.annotation.Caching;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TestService {

    @Autowired
    private RedissonClient redissonClient;

    @Async
    @Caching
    public void asyncTest() {
        try {
            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RBucket<Integer> bucket = redissonClient.getBucket("test");
            bucket.set(1);
            bucket.expire(5, TimeUnit.SECONDS);
        }
    }

    @Caching(mode = Caching.Mode.CHECK)
    public int asyncTestCheck() {
        return 0;
    }

}
