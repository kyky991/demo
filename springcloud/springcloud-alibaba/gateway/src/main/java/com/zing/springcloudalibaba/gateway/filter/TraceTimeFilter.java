package com.zing.springcloudalibaba.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author Zing
 * @date 2020-07-14
 */
@Slf4j
public class TraceTimeFilter implements GatewayFilter, Ordered {

    private static final String REQUEST_TIME = "requestTime2";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put(REQUEST_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    Long requestTime = exchange.getAttribute(REQUEST_TIME);
                    if (requestTime != null) {
                        log.info("trace {} -> {} ms", exchange.getRequest().getURI().getRawPath(),
                                System.currentTimeMillis() - requestTime);
                    }
                })
        );
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
