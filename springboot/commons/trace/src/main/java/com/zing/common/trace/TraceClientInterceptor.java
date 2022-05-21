package com.zing.common.trace;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({RequestInterceptor.class})
public class TraceClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        TraceContext.getContext().getAttachments().forEach(requestTemplate::query);
    }

}
