package com.zing.springcloudalibaba.controller.sentinel.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Zing
 * @date 2020-07-12
 */
@Slf4j
public class SentinelFallback {

    public static String fallback(String s, Throwable e) {
        log.warn(e.toString());
        return "降级... fallback";
    }

}
