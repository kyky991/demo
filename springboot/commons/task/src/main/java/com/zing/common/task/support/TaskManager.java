package com.zing.common.task.support;

import com.zing.common.task.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.ResolvableType;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class TaskManager implements BeanPostProcessor {

    private static final Map<Class<?>, TaskHandler<?>> HANDLERS = new HashMap<>(16);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskHandler) {
            TaskHandler<?> handler = (TaskHandler<?>) bean;
            Class<?> clazz = resolveGenericClass(handler.getClass());
            if (HANDLERS.containsKey(clazz)) {
                throw new IllegalStateException("duplicate handler for " + clazz);
            }
            HANDLERS.put(clazz, handler);

            if (log.isInfoEnabled()) {
                log.info("found handler [{}] [{}]", handler.getClass().getSimpleName(), clazz.getSimpleName());
            }
            return null;
        }
        return bean;
    }

    public boolean checkTask(Object task) {
        if (task != null) {
            return HANDLERS.containsKey(task.getClass());
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public boolean handleTask(Object task) {
        if (task != null) {
            TaskHandler handler = HANDLERS.get(task.getClass());
            if (handler != null) {
                return handler.handle(task);
            } else {
                log.info("not found handler [{}]", task);
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void handleException(Object task, Throwable e) {
        if (task != null) {
            TaskHandler handler = HANDLERS.get(task.getClass());
            if (handler != null) {
                handler.exceptionCaught(task, e);
            } else {
                log.info("not found handler [{}]", task);
            }
        }
    }

    private Class<?> resolveGenericClass(Class<?> clazz) {
        ResolvableType rt = ResolvableType.forClass(clazz);
        ResolvableType grt = Arrays.stream(rt.getInterfaces())
                .filter(t -> Objects.equals(t.resolve(), TaskHandler.class))
                .findFirst()
                .orElse(null);
        Objects.requireNonNull(grt, clazz.getName() + " must implements " + TaskHandler.class.getName() + ".");

        Class<?> generic = Objects.requireNonNull(grt.getGeneric(0).resolve(), grt.resolve().getName() + " must have generic.");

        Method equals = null;
        Method hashCode = null;
        try {
            equals = generic.getMethod("equals", Object.class);
            hashCode = generic.getMethod("hashCode");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        if (!generic.equals(equals.getDeclaringClass()) || !generic.equals(hashCode.getDeclaringClass())) {
            throw new IllegalArgumentException("should overwrite 'equals' and 'hashCode' method");
        }

        return generic;
    }

}
