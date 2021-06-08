package com.zing.netty.rpc.client;

/**
 * @author Zing
 * @date 2021-01-02
 */
public interface RpcCallback {

    void success(Object result);

    void failure(Throwable throwable);

}
