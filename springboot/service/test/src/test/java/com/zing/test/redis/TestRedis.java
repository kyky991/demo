package com.zing.test.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zing.common.redis.codec.MixedFastjsonCodec;
import com.zing.test.entity.Foo;
import com.zing.test.redis.pkg2.People;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.*;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRedis {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void test() {
        RLock lock = redissonClient.getLock("lock.test");
        lock.lock(1, TimeUnit.MINUTES);

        RScoredSortedSet<Object> set = redissonClient.getScoredSortedSet("set.test");
        for (int i = 0; i < 10; i++) {
            set.add(i, i);
        }
        set.expire(1000, TimeUnit.MINUTES);

        Object first = set.first();
        Object last = set.last();
        System.out.println(first);
        System.out.println(last);
    }

    @Test
    public void test0() {
        RScoredSortedSet<Object> set = redissonClient.getScoredSortedSet("set.test");
        set.remove(1);
    }

    @Test
    public void test2() {
        RBucket<String> bucket = redissonClient.getBucket("test.exp");
        bucket.set("11111111111");
        bucket.expire(1000, TimeUnit.SECONDS);

        bucket.set("2222222222222222222", bucket.remainTimeToLive(), TimeUnit.MILLISECONDS);
    }

    @Test
    public void test3() {

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        list.sort((o1, o2) -> o2.compareTo(o1));
        System.out.println(list);

        try {
            throw new RuntimeException();
        } finally {
            System.out.println("11111111111111111111111111");
        }
    }

    @Test
    public void test4() {
        long id = 1L;
        RBucket<Object> bucket = redissonClient.getBucket("test.goods:" + id);
        bucket.set(new Foo("foo", "yyyyyyyyyyy"), 10, TimeUnit.MINUTES);
    }

    @Test
    public void test5() {
        RScoredSortedSet<com.zing.test.redis.pkg1.People> bucket = redissonClient.getScoredSortedSet("test.people");
        bucket.add(1, new com.zing.test.redis.pkg1.People(1L));

        RScoredSortedSet<People> bucket2 = redissonClient.getScoredSortedSet("test.people");
        Object first = bucket2.first();
        System.out.println(first.getClass().getName());
        System.out.println(first);
    }

    @Test
    public void test6() {
        RScoredSortedSet<Task> bucket = redissonClient.getScoredSortedSet("test.task");
        for (int i = 0; i < 5; i++) {
            Task task = new Task(i);
            task.setSource(new com.zing.test.redis.pkg1.People((long) i));
            boolean ret = bucket.add(i * 2 + 1, task);
            System.out.println(ret);

            System.out.println(JSON.toJSONString(task, SerializerFeature.WriteClassName));
        }
    }

    @Test
    public void test7() throws InterruptedException {
        Task task = new Task();
        task.setSource(new People(1L));

        RBlockingQueue<Task> queue = redissonClient.getBlockingQueue("test.queue");
        RDelayedQueue<Task> delayedQueue = redissonClient.getDelayedQueue(queue);
        delayedQueue.offer(task, 10, TimeUnit.SECONDS);

        long s = System.currentTimeMillis();

        Task t = queue.take();
        System.out.println(t);
        System.out.println(System.currentTimeMillis() - s);
    }

    @Test
    public void test8() {
        List<ScoredEntry<Object>> result = pollEntry("test.task", 0, 0);
        System.out.println(result);
    }

    protected <T> List<ScoredEntry<T>> pollEntry(String key, int from, int to) {
        final RScript script = redissonClient.getScript(MixedFastjsonCodec.INSTANCE);
        List<Object> parts = script.eval(RScript.Mode.READ_WRITE,
                "local v = redis.call('zrange', KEYS[1], ARGV[1], ARGV[2], ARGV[3]); "
                        + "if #v > 0 then "
                        + "  redis.call('zremrangebyrank', KEYS[1], ARGV[1], ARGV[2]); "
                        + "  return v; "
                        + "end "
                        + "return v;",
                RScript.ReturnType.MULTI, Collections.singletonList(key), from, to, "WITHSCORES");

        return decode(parts);
    }

    @Test
    public void test9() {
        ScoredEntry<Object> entry = pollEntry("test.task", System.currentTimeMillis());
        if (entry != null) {
            System.out.println(entry.getScore());
            System.out.println(entry.getValue());
        }
    }

    protected <T> ScoredEntry<T> pollEntry(String key, long timestamp) {
        final RScript script = redissonClient.getScript(MixedFastjsonCodec.INSTANCE);
        List<Object> parts = script.eval(RScript.Mode.READ_WRITE,
                "local v = redis.call('zrange', KEYS[1], 0, 0, 'WITHSCORES'); "
                        + "local ts = tonumber(ARGV[1]); "
                        + "if #v > 0 then "
                        + "  if tonumber(v[2]) <= ts then "
                        + "    redis.call('zremrangebyrank', KEYS[1], 0, 0); "
                        + "    return v; "
                        + "  end "
                        + "end "
                        + "return v;",
                RScript.ReturnType.MULTI, Collections.singletonList(key), timestamp);

        List<ScoredEntry<T>> entries = decode(parts);
        return !entries.isEmpty() ? entries.get(0) : null;
    }

    protected <T> List<ScoredEntry<T>> decode(List<Object> parts) {
        List<ScoredEntry<T>> result = new ArrayList<>();
        for (int i = 0; i < parts.size(); i += 2) {
            result.add(new ScoredEntry<>(((Number) parts.get(i + 1)).doubleValue(), (T) parts.get(i)));
        }
        return result;
    }

}
