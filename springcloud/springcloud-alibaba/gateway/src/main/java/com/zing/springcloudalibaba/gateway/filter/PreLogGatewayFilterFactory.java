package com.zing.springcloudalibaba.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author Zing
 * @date 2020-07-14
 */
@Slf4j
@Component
public class PreLogGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {

    @Override
    public GatewayFilter apply(NameValueConfig config) {
        return (exchange, chain) -> {
            log.info("请求... {} = {}", config.getName(), config.getValue());

            ServerHttpRequest request = exchange
                    .getRequest()
                    .mutate()
                    .build();
            ServerWebExchange ex = exchange
                    .mutate()
                    .request(request)
                    .build();
            return chain.filter(ex);
        };
    }
}
