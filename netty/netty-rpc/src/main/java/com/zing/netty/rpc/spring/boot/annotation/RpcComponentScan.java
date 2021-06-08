package com.zing.netty.rpc.spring.boot.annotation;

import java.lang.annotation.*;

/**
 * @author Zing
 * @date 2021-01-04
 */
@Target(ElementType.TYPE)
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcComponentScan {

    String[] value() default {};

    String[] basePackages() default {};

    Class<?>[] basePackageClasses() default {};

}
