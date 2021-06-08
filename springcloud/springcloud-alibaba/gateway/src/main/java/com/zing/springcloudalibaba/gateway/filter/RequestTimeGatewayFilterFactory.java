package com.zing.springcloudalibaba.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * @author Zing
 * @date 2020-07-14
 */
@Slf4j
@Component
public class RequestTimeGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestTimeGatewayFilterFactory.Config> {

    private static final String REQUEST_TIME = "requestTime";

    public RequestTimeGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            exchange.getAttributes().put(REQUEST_TIME, System.currentTimeMillis());
            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        Long requestTime = exchange.getAttribute(REQUEST_TIME);
                        if (requestTime != null) {
                            log.info("request {} {} -> {} ms",
                                    exchange.getRequest().getURI().getRawPath(),
                                    exchange.getRequest().getQueryParams(),
                                    System.currentTimeMillis() - requestTime);
                        }
                    })
            );
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("withParams");
    }

    @Data
    public static class Config {
        private Boolean withParams;
    }

}
