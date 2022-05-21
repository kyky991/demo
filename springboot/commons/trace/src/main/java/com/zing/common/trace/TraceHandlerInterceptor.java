package com.zing.common.trace;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class TraceHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.getParameterMap().entrySet().stream()
                .filter(e -> e.getKey().startsWith(TraceContext.PREFIX) && e.getValue().length > 0)
                .forEach(e -> TraceContext.getContext().setAttachment(e.getKey(), e.getValue()[0]));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TraceContext.removeContext();
    }

}
