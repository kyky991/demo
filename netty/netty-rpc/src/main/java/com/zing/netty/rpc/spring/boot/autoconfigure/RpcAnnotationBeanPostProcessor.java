package com.zing.netty.rpc.spring.boot.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Zing
 * @date 2021-01-04
 */
public class RpcAnnotationBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, BeanClassLoaderAware, ResourceLoaderAware, EnvironmentAware {

    private final Set<String> packages;

    private ClassLoader classLoader;

    private ResourceLoader resourceLoader;

    private Environment environment;

    public RpcAnnotationBeanPostProcessor(String... packages) {
        this(Arrays.asList(packages));
    }

    public RpcAnnotationBeanPostProcessor(Collection<String> packages) {
        this(new LinkedHashSet<>(packages));
    }

    public RpcAnnotationBeanPostProcessor(Set<String> packages) {
        this.packages = packages;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<String> resolvePackages = resolvePackages(packages);
        if (CollectionUtils.isEmpty(resolvePackages)) {
//            registerServiceBeans(resolvePackages, registry);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    private Set<String> resolvePackages(Set<String> packages) {
        Set<String> resolvedPackages = new LinkedHashSet<>(packages.size());
        for (String pkg : packages) {
            if (StringUtils.hasText(pkg)) {
                resolvedPackages.add(environment.resolvePlaceholders(pkg));
            }
        }
        return resolvedPackages;
    }
}
