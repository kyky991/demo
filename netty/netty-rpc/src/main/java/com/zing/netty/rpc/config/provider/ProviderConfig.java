package com.zing.netty.rpc.config.provider;

import com.zing.netty.rpc.config.AbstractRpcConfig;

/**
 * @author Zing
 * @date 2021-01-02
 */
public class ProviderConfig extends AbstractRpcConfig {

    protected Object ref;

    public Object getRef() {
        return ref;
    }

    public void setRef(Object ref) {
        this.ref = ref;
    }
}
