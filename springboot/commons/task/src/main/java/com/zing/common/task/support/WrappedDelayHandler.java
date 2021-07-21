package com.zing.common.task.support;

import com.zing.common.task.props.TaskProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Slf4j
public class WrappedDelayHandler extends AbstractDelayHandler {

    protected AbstractDelayHandler handler;

    public WrappedDelayHandler(AbstractDelayHandler handler) {
        this.handler = Objects.requireNonNull(handler);
    }

    @Override
    public void setTaskProperties(TaskProperties taskProperties) {
        super.setTaskProperties(taskProperties);
        handler.setTaskProperties(taskProperties);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public boolean offer(Object task, long delay, TimeUnit timeUnit) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null.");
        }
        checkTask(unwrapTask(task));
        return handler.offer(wrapTask(task), delay, timeUnit);
    }

    @Override
    public boolean remove(Object task) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null.");
        }
        return handler.remove(wrapTask(task));
    }

    @Override
    public boolean removeIf(Predicate<Object> filter) {
        if (filter == null) {
            throw new IllegalArgumentException("filter must not be null");
        }
        return handler.removeIf(t -> filter.test(unwrapTask(t)));
    }

    @Override
    protected Object take() throws InterruptedException {
        Object task = handler.take();
        return unwrapTask(task);
    }

    private WrappedTask wrapTask(Object task) {
        if (task instanceof WrappedTask) {
            return (WrappedTask) task;
        } else {
            return new WrappedTask(task);
        }
    }

    private Object unwrapTask(Object task) {
        if (task instanceof WrappedTask) {
            WrappedTask wrappedTask = (WrappedTask) task;
            return wrappedTask.getSource();
        }
        return task;
    }

}
