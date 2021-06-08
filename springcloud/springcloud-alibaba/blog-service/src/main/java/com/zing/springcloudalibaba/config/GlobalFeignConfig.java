package com.zing.springcloudalibaba.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * feign 全局配置类 或 在yml文件中使用 feign.client.config.default 配置
 *
 * @author Zing
 * @date 2020-07-11
 */
public class GlobalFeignConfig {

    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }

}
