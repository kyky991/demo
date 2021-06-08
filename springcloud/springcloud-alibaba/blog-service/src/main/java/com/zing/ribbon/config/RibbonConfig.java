package com.zing.ribbon.config;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.loadbalancer.RandomRule;
import com.zing.ribbon.annotation.ExcludeAnnotation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用此规则不可放在扫描的路径下, 如果非要放置, 需要加自定义注解
 * {@code @ComponentScan(excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = {ExcludeAnnotation.class})})}
 *
 * @author Zing
 * @date 2020-07-11
 */
@Configuration
@ExcludeAnnotation
public class RibbonConfig {

    @Bean
    public IRule ribbonRule() {
        return new RandomRule();
//        return new NacosWeightedRule();
//        return new NacosSameClusterWeightedRule();
//        return new NacosFinalRule();
    }

    @Bean
    public IPing ribbonPing() {
        return new PingUrl();
    }

}
