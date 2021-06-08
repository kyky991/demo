package com.zing.springcloudalibaba.rule;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Zing
 * @date 2020-07-11
 */
@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object key) {
        try {
            String clusterName = nacosDiscoveryProperties.getClusterName();

            BaseLoadBalancer lb = (BaseLoadBalancer) getLoadBalancer();

            // 请求的微服务的名称
            String name = lb.getName();

            // 服务发现相关API
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();

            List<Instance> instances = namingService.selectInstances(name, true);
            List<Instance> sameClusterInstances = instances.stream()
                    .filter(instance -> Objects.equals(instance.getClusterName(), clusterName))
                    .collect(Collectors.toList());

            List<Instance> instancesToBeChosen = new ArrayList<>();
            if (CollectionUtils.isEmpty(sameClusterInstances)) {
                instancesToBeChosen = instances;
                log.warn("跨集群调用，name = {}, clusterName = {}, instances = {}", name, clusterName, instances);
            } else {
                instancesToBeChosen = sameClusterInstances;
            }

            Instance instance = ExtendBalancer.getHostByRandomWeightEx(instancesToBeChosen);

            log.info("port = {}, clusterName = {}, instance = {}", instance.getPort(), clusterName, instance);

            return new NacosServer(instance);
        } catch (NacosException e) {
            log.error("rule choose error", e);
        }
        return null;
    }

    static class ExtendBalancer extends Balancer {
        public static Instance getHostByRandomWeightEx(List<Instance> hosts) {
            return getHostByRandomWeight(hosts);
        }
    }
}

