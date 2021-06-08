package com.zing.netty.rpc.config;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Zing
 * @date 2021-01-02
 */
public class AbstractRpcConfig {

    private AtomicInteger generator = new AtomicInteger(0);

    protected String id;

    protected String interfaceClass = null;

    protected Class<?> proxyClass = null;

    public String getId() {
        if (StringUtils.isEmpty(id)) {
            id = "cfg-" + generator.getAndIncrement();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInterface() {
        return interfaceClass;
    }

    public void setInterface(String interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

}
