package com.zing.goods.config;

import com.zing.goods.props.GoodsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GoodsProperties.class})
public class AppConfiguration {

}
