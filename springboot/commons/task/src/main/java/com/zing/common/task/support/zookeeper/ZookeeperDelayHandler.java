package com.zing.common.task.support.zookeeper;

import com.zing.common.task.common.Entry;
import com.zing.common.task.support.AbstractDefaultDelayHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Slf4j
public class ZookeeperDelayHandler extends AbstractDefaultDelayHandler implements InitializingBean {

    /**
     * 默认任务队列 zookeeper path
     */
    private static final String PATH = "/delay/task";

    private CuratorFramework client;

    public ZookeeperDelayHandler(CuratorFramework client) {
        this.client = client;
    }

    public void setCuratorFramework(CuratorFramework client) {
        this.client = client;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(client, "curator framework must not be null.");
        init();
        log.info("delay task zookeeper path [{}]", getPath());
    }

    @Override
    protected Object peek() {
        return null;
    }

    @Override
    protected Entry<Object> peekEntry() {
        return null;
    }

    @Override
    protected boolean offer0(Object task, long delay, TimeUnit timeUnit) {
        return false;
    }

    @Override
    protected boolean remove0(Object task) {
        return false;
    }

    @Override
    protected boolean removeIf0(Predicate<Object> filter) {
        return false;
    }

    private String getPath() {
        String path = taskProperties.getZookeeper().getPath();
        return StringUtils.isNotBlank(path) ? path : PATH;
    }

}
