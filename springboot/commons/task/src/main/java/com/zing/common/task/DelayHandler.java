package com.zing.common.task;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public interface DelayHandler {

    /**
     * 添加任务
     * overrides previous delay if it has been already added.
     *
     * @param task     任务
     * @param delay    延时
     * @param timeUnit 粒度
     * @return <code>true</code> if element has added and <code>false</code> if not.
     */
    boolean offer(Object task, long delay, TimeUnit timeUnit);

    /**
     * 移除任务
     *
     * @param task 任务
     * @return <code>true</code> if an element was removed as a result of this call.
     */
    boolean remove(Object task);

    /**
     * 移除任务
     *
     * @param filter 任务
     * @return <code>true</code> if any elements were removed.
     */
    boolean removeIf(Predicate<Object> filter);

}
