package com.zing.springcloudalibaba.controller.sentinel;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.zing.springcloudalibaba.controller.sentinel.handler.SentinelBlockHandler;
import com.zing.springcloudalibaba.controller.sentinel.handler.SentinelFallback;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zing
 * @date 2020-07-12
 */
@RestController
public class SentinelController {

    @SentinelResource(value = "sentinel-resource",
            blockHandler = "block", blockHandlerClass = SentinelBlockHandler.class,
            fallback = "fallback", fallbackClass = SentinelFallback.class
    )
    @GetMapping("/sentinel-api")
    public String api(String s) {
        if (StringUtils.isEmpty(s)) {
            throw new IllegalArgumentException("empty...");
        }
        return "param: " + s;
    }

}
