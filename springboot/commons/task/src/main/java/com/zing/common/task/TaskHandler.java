package com.zing.common.task;

public interface TaskHandler<T> {

    /**
     * 处理任务
     *
     * @param task 任务
     * @return 结果
     */
    boolean handle(T task);

    /**
     * 处理异常
     *
     * @param task 任务
     * @param e    移除
     */
    default void exceptionCaught(T task, Throwable e) {
    }

}
