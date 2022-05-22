package com.zing.common.trace;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class TraceHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, String> attachments = request.getParameterMap().entrySet().stream()
                .filter(e -> e.getKey().startsWith(TraceContext.PREFIX) && e.getValue().length > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
        attachments.forEach((key, value) -> TraceContext.getContext().setAttachment(key, value));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TraceContext.removeContext();
    }

}
