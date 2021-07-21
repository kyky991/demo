package com.zing.common.task.support;

import com.zing.common.task.props.TaskProperties;
import com.zing.common.task.DelayHandler;
import com.zing.common.task.common.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class AbstractDelayHandler implements DelayHandler {

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            600L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2500),
            new NamedThreadFactory("delay-handler"),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    private final AtomicBoolean init = new AtomicBoolean(false);

    protected TaskManager taskManager;
    protected TaskProperties taskProperties;

    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void setTaskProperties(TaskProperties taskProperties) {
        this.taskProperties = taskProperties;
    }

    protected void init() {
        Assert.notNull(taskManager, "task manager must not be null.");
        Assert.notNull(taskProperties, "task properties must not be null.");

        if (init.compareAndSet(false, true)) {
            @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Object task = take();
                        EXECUTOR.submit(() -> {
                            try {
                                boolean ret = taskManager.handleTask(task);
                                log.info("handle task [{}] [{}]", ret, task);
                            } catch (Exception e) {
                                taskManager.handleException(task, e);
                                log.warn("handle task exception [" + task + "]", e);
                            }
                        });
                    } catch (Exception e) {
                        log.warn("take task exception", e);
                    }
                }
            }, "DelayHandler");
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * 检查任务
     *
     * @param task 任务
     */
    protected void checkTask(Object task) {
        if (!taskManager.checkTask(task)) {
            if (log.isWarnEnabled()) {
                log.warn("task is not acceptable [{}]", task);
            }
            throw new IllegalArgumentException("task is not acceptable [" + task + "]");
        }
    }

    /**
     * 获取任务
     *
     * @return 任务
     * @throws InterruptedException 异常
     */
    protected abstract Object take() throws InterruptedException;

}
