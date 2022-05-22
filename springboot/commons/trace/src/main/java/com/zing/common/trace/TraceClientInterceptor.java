package com.zing.common.trace;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConditionalOnClass({RequestInterceptor.class})
public class TraceClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Map<String, String> attachments = TraceContext.getContext().getAttachments();
        attachments.forEach(requestTemplate::query);
    }

}
