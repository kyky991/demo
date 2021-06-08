package com.zing.springcloudalibaba.gateway.config;

import com.zing.springcloudalibaba.gateway.filter.TraceTimeFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;

/**
 * 1. Order越小越靠前执行
 * 2. 局部过滤器工厂的Order按配置顺序从1开始递增
 * 3. 如果配置了默认过滤器，则先执行相同Order的默认过滤器
 *
 * @author Zing
 * @date 2020-07-14
 */
@Slf4j
@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/user/**")
                        .filters(f -> f.filter(new TraceTimeFilter())
                                .addResponseHeader("X-Response-Foo", "Bar")
                        )
                        .uri("lb://user-service")
                        .order(0)
                        .id("user-route-1")
                )
                .build();
    }

    @Bean
    @Order(-1)
    public GlobalFilter a() {
        return (exchange, chain) -> {
            log.info("first pre filter");
            return chain.filter(exchange).then(
                    Mono.fromRunnable(() ->
                            log.info("third post filter")
                    )
            );
        };
    }

    @Bean
    @Order(0)
    public GlobalFilter b() {
        return (exchange, chain) -> {
            log.info("second pre filter");
            return chain.filter(exchange).then(
                    Mono.fromRunnable(() ->
                            log.info("second post filter")
                    )
            );
        };
    }

    @Bean
    @Order(0)
    public GlobalFilter c() {
        return (exchange, chain) -> {
            log.info("third pre filter");
            return chain.filter(exchange).then(
                    Mono.fromRunnable(() ->
                            log.info("first post filter")
                    )
            );
        };
    }
}
