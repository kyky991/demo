package com.zing.common.task.support;

import com.zing.common.task.common.Entry;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

@Slf4j
public abstract class AbstractDefaultDelayHandler extends AbstractDelayHandler {

    private static final int SLEEP_MILLIS = 1000;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final transient ReentrantLock lock = new ReentrantLock();
    private final Condition available = lock.newCondition();

    @Override
    public boolean offer(Object task, long delay, TimeUnit timeUnit) {
        lock.lock();

        try {
            boolean offered = offer0(task, delay, timeUnit);

            Object first = peek();
            boolean isFirst = task.equals(first);
            if (isFirst) {
                available.signal();
            }

            if (log.isInfoEnabled()) {
                log.info("offer task [offer:{}] [first:{}] [{}]", offered, isFirst, task);
            }

            return offered;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean remove(Object task) {
        lock.lock();

        try {
            return remove0(task);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeIf(Predicate<Object> filter) {
        lock.lock();

        try {
            return removeIf0(filter);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected Object take() throws InterruptedException {
        lock.lockInterruptibly();

        try {
            for (; ; ) {
                Entry<Object> first = peekEntry();
                if (first == null) {
                    if (log.isInfoEnabled()) {
                        log.info("wait task");
                    }
                    available.await();
                } else {
                    long delay = TimeUnit.MILLISECONDS.convert(first.getScore() - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
                    if (delay <= 0) {
                        if (remove(first.getValue())) {
                            if (log.isInfoEnabled()) {
                                log.info("take task [{}]", first.getValue());
                            }
                            return first.getValue();
                        } else {
                            if (log.isInfoEnabled()) {
                                log.warn("take task failed [{}]", first.getValue());
                            }
                            available.await(1000, TimeUnit.MILLISECONDS);
                            continue;
                        }
                    }

                    if (log.isInfoEnabled()) {
                        log.info("wait task timeout [{}] [{}]", FORMATTER.format(LocalDateTime.now().plusNanos(delay)), first);
                    }
                    available.awaitNanos(delay);
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

    protected abstract Object peek();

    protected abstract Entry<Object> peekEntry();

    protected abstract boolean offer0(Object task, long delay, TimeUnit timeUnit);

    protected abstract boolean remove0(Object task);

    protected abstract boolean removeIf0(Predicate<Object> filter);

}
