package com.zing.springcloudalibaba.rule;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Zing
 * @date 2020-07-11
 */
@Slf4j
public class NacosWeightedRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
        // 读取配置文件，初始化
    }

    @Override
    public Server choose(Object key) {
        try {
            BaseLoadBalancer lb = (BaseLoadBalancer) getLoadBalancer();

            // 请求的微服务的名称
            String name = lb.getName();

            // 服务发现相关API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

            // nacos client 自动通过基于权重的负载均衡算法，选择一个实例
            Instance instance = namingService.selectOneHealthyInstance(name);

            log.info("port = {}, instance = {}", instance.getPort(), instance);

            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("rule choose error", e);
        }
        return null;
    }
}

