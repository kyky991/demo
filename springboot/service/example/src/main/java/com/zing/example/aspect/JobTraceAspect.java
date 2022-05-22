package com.zing.example.aspect;

import com.xxl.job.core.handler.annotation.XxlJob;
import com.zing.common.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Configuration
public class JobTraceAspect {

    @Around("@annotation(xxlJob)")
    public Object around(ProceedingJoinPoint pjp, XxlJob xxlJob) throws Throwable {
        Map<String, String> data = new HashMap<>(8);
        data.put(TraceContext.PREFIX + "job", xxlJob.value());

        TraceContext.getContext().setAttachments(data);

        Object obj = null;
        try {
            obj = pjp.proceed();
        } finally {
            TraceContext.removeContext();
        }
        return obj;
    }

}
