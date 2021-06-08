package com.zing.springcloudalibaba.gateway.predicate;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Zing
 * @date 2020-07-13
 */
@Slf4j
@Component
public class TimeBetweenRoutePredicateFactory extends AbstractRoutePredicateFactory<TimeBetweenRoutePredicateFactory.Config> {

    public TimeBetweenRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        LocalTime n = LocalTime.now();
        LocalTime start = config.getStart();
        LocalTime end = config.getEnd();

        // gateway内部解析时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        log.warn(formatter.format(n));

        log.info("now = {}, start = {}, end = {}", n, start, end);
        log.info("{}", n.isAfter(start) && n.isBefore(end));

        return exchange -> {
            LocalTime now = LocalTime.now();
            return now.isAfter(start) && now.isBefore(end);
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("start", "end");
    }

    @Data
    public static class Config {
        private LocalTime start;
        private LocalTime end;
    }
}
