package com.zing.test.aspect;

import com.zing.common.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Configuration
public class AsyncLogAspect {

    @Around("@annotation(async)")
    public Object around(ProceedingJoinPoint pjp, Async async) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();

        Map<String, String> attachments = new HashMap<>(TraceContext.getContext().getAttachments());
        attachments.put("method", methodName);

        Object obj = null;
        try {
            log.warn("start {}", attachments);
            obj = pjp.proceed();
        } catch (Throwable e) {
            log.error("ex {}", attachments);
            throw e;
        } finally {
            TraceContext.removeContext();
        }
        return obj;
    }

}
