package com.zing.common.task.support;

import com.zing.common.redis.codec.MixedFastjsonCodec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

@Slf4j
@Order
public class DefaultDelayHandler extends AbstractDelayHandler implements InitializingBean {

    private static final String KEY = "delay:task";
    private static final long SLEEP_MILLIS = 1000;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final transient ReentrantLock lock = new ReentrantLock();
    private final Condition available = lock.newCondition();

    private RedissonClient redissonClient;

    public DefaultDelayHandler(RedissonClient redissonClient) {
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
    public boolean offer(Object task, long delay, TimeUnit timeUnit) {
        lock.lock();

        try {
            boolean ret = offer0(task, delay, timeUnit);

            Object first = peek();
            boolean isFirst = task.equals(first);
            if (isFirst) {
                available.signal();
            }

            log.info("offer task [{}] first [{}] [{}]", ret, isFirst, task);

            return ret;
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected Object take() throws InterruptedException {
        lock.lockInterruptibly();

        try {
            for (; ; ) {
                // 修改获取任务的方式。
                // 使用lua脚本
                final long timestamp = System.currentTimeMillis();
                final ScoredEntry<Object> first = peekAndRemIfExpire(timestamp);
                if (first == null) {
                    if (log.isInfoEnabled()) {
                        log.info("wait task");
                    }
                    available.await();
                } else {
                    final long score = first.getScore().longValue();
                    final Object task = first.getValue();
                    final long delayMs = score - timestamp;
                    if (delayMs <= 0) {
                        if (log.isInfoEnabled()) {
                            log.info("take task [{}] [{}]", score, task);
                        }
                        return task;
                    }

                    if (log.isInfoEnabled()) {
                        log.info("wait task timeout [{}] [{}]", FORMATTER.format(LocalDateTime.now().plus(delayMs, ChronoUnit.MILLIS)), task);
                    }
                    available.await(delayMs + 5, TimeUnit.MILLISECONDS);
                }
            }
        } catch (Throwable e) {
            available.await(SLEEP_MILLIS, TimeUnit.MILLISECONDS);
            throw e;
        } finally {
            if (peek() != null) {
                available.signal();
            }
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object task) {
        lock.lock();

        try {
            RScoredSortedSet<Object> set = getSet();
            boolean removed = set.remove(task);

            if (log.isInfoEnabled()) {
                log.info("remove task [{}] [{}]", removed, task);
            }

            return removed;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeIf(Predicate<Object> filter) {
        lock.lock();

        try {
            boolean removed = false;
            final RScoredSortedSet<Object> set = getSet();
            final Iterator<Object> each = set.iterator();
            while (each.hasNext()) {
                if (filter.test(each.next())) {
                    each.remove();
                    removed = true;
                }
            }

            if (log.isInfoEnabled()) {
                log.info("removeIf task [{}]", removed);
            }

            return removed;
        } finally {
            lock.unlock();
        }
    }

    protected Object peek() {
        final RScoredSortedSet<Object> set = getSet();
        return set.first();
    }

    protected boolean offer0(Object task, long delay, TimeUnit timeUnit) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay can't be negative");
        }

        long delayInMs = timeUnit.toMillis(delay);
        long timeout = System.currentTimeMillis() + delayInMs;

        final RScoredSortedSet<Object> set = getSet();
        boolean added = set.add(timeout, task);
        return added;
    }

    protected <T> ScoredEntry<T> peekAndRemIfExpire(long timestamp) {
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
                RScript.ReturnType.MULTI, Collections.singletonList(getKey()), timestamp);

        List<ScoredEntry<T>> entries = decode(parts);
        return !entries.isEmpty() ? entries.get(0) : null;
    }

    protected <T> ScoredEntry<T> pollFirstEntry() {
        List<ScoredEntry<T>> entries = pollFirstEntry(1);
        if (!entries.isEmpty()) {
            return entries.get(0);
        }
        return null;
    }

    protected <T> List<ScoredEntry<T>> pollFirstEntry(int count) {
        if (count < 0) {
            return Collections.emptyList();
        }
        return pollEntry(0, count - 1);
    }

    protected <T> List<ScoredEntry<T>> pollLastEntry(int count) {
        if (count < 0) {
            return Collections.emptyList();
        }
        return pollEntry(-count, -1);
    }

    protected <T> List<ScoredEntry<T>> pollEntry(int from, int to) {
        final RScript script = redissonClient.getScript(MixedFastjsonCodec.INSTANCE);
        List<Object> parts = script.eval(RScript.Mode.READ_WRITE,
                "local v = redis.call('zrange', KEYS[1], ARGV[1], ARGV[2], ARGV[3]); "
                        + "if #v > 0 then "
                        + "redis.call('zremrangebyrank', KEYS[1], ARGV[1], ARGV[2]); "
                        + "return v; "
                        + "end "
                        + "return v;",
                RScript.ReturnType.MULTI, Collections.singletonList(getKey()), from, to, "WITHSCORES");

        return decode(parts);
    }

    @SuppressWarnings("unchecked")
    protected <T> List<ScoredEntry<T>> decode(List<Object> parts) {
        List<ScoredEntry<T>> result = new ArrayList<>();
        for (int i = 0; i < parts.size(); i += 2) {
            result.add(new ScoredEntry<>(((Number) parts.get(i + 1)).doubleValue(), (T) parts.get(i)));
        }
        return result;
    }

    private RScoredSortedSet<Object> getSet() {
        return redissonClient.getScoredSortedSet(getKey());
    }

    private String getKey() {
        String key = taskProperties.getRedis().getKey();
        return StringUtils.isNotBlank(key) ? key : KEY;
    }

}
