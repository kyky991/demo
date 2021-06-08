package com.zing.springcloudalibaba.controller.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Zing
 * @date 2020-07-12
 */
@Slf4j
public class SentinelBlockHandler {

    public static String block(String s, BlockException e) {
        log.warn(e.toString());
        return "限流/降级... block";
    }

}
