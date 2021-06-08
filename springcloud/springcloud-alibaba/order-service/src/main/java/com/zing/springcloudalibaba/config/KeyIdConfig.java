package com.zing.springcloudalibaba.config;

import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Zing
 */
@Configuration
public class KeyIdConfig {

    @Bean("orderKeyGenerator")
    public SnowflakeShardingKeyGenerator orderKeyGenerator() {
        return new SnowflakeShardingKeyGenerator();
    }
}