package com.zing.springcloudalibaba.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * 添加  @Configuration 后此配置会作用于所有的feign （涉及到父子上下文的问题）
 * 如果加了 @Configuration，就必须将此类挪到 @ComponentScan 能扫描的包以外
 *
 * @author Zing
 * @date 2020-07-11
 */
public class UserFeignConfig {

    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }

}
