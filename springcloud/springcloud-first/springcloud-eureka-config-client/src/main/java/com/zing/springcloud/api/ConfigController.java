package com.zing.springcloud.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zing
 * @date 2019-11-21
 */
@RefreshScope
@RestController
public class ConfigController {

    @Value("${profile}")
    private String profile;

    @RequestMapping("/profile")
    public String profile() {
        return profile;
    }

}
