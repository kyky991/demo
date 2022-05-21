package com.zing.test.redis.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Caching {

    Mode mode() default Mode.CACHE;

    String suffix() default "Check";

    /**
     * 模式
     */
    enum Mode {
        CACHE,
        CHECK,
        ;
    }

}
