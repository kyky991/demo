package com.zing.test.domain;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
public class AspectJTest {

    @Pointcut("execution(* *..*.test(..))")
    public void test() {

    }

    @Before("test()")
    public void before() {
        System.out.println("before");
    }

    @After("test()")
    public void after() {
        System.out.println("after");
    }

    @Around("test()")
    public Object around(ProceedingJoinPoint pjp) {
        System.out.println("before around");
        Object o = null;
        try {
            o = pjp.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("after around");
        return o;
    }

}
