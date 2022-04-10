package com.zing.test.aspect;

import com.zing.test.annotation.Caching;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

@Slf4j
@Aspect
@Configuration
public class CachingAspect {

    private static final String REDIS_KEY_METHOD_RET = "REDIS_KEY_METHOD_RET";

    @Autowired
    private RedissonClient redissonClient;

    @Pointcut("@annotation(com.zing.test.annotation.Caching)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        Caching caching = method.getAnnotation(Caching.class);
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();

        RMap<String, Integer> rMap = redissonClient.getMap(REDIS_KEY_METHOD_RET);
        if (caching.mode() == Caching.Mode.CACHE) {
            Object obj = null;
            try {
                obj = pjp.proceed();
                rMap.put(methodName, 0);
            } catch (Throwable e) {
                rMap.put(methodName, -1);
            }
            rMap.expireAt(endOfToday());
            return obj;
        } else {
            String cacheKey = methodName.substring(0, methodName.lastIndexOf(caching.suffix()));
            Integer ret = rMap.get(cacheKey);
            return ret == null ? -2 : ret;
        }
    }

    public static Date endOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

}
