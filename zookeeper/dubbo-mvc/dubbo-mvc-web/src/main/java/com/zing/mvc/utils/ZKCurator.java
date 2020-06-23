package com.zing.mvc.utils;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKCurator {

    private final static Logger log = LoggerFactory.getLogger(ZKCurator.class);

    private CuratorFramework client = null;     //zk客户端

    public ZKCurator(CuratorFramework client) {
        this.client = client;
    }

    /**
     * 初始化
     */
    public void init() {
        //使用命名空间
        client = client.usingNamespace("zk-curator-connector");
    }

    /**
     * 判断zk是否连接
     */
    public boolean isZKAlive() {
        return (client.getState() == CuratorFrameworkState.STARTED);
    }
}
