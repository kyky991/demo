package com.zing.common.trace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TraceWebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TraceHandlerInterceptor traceHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(traceHandlerInterceptor).addPathPatterns("/**");
    }

}
