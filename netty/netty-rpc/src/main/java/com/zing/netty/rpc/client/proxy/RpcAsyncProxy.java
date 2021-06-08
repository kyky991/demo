package com.zing.netty.rpc.client.proxy;

import com.zing.netty.rpc.client.RpcFuture;

/**
 * @author Zing
 * @date 2021-01-03
 */
public interface RpcAsyncProxy {

    RpcFuture call(String methodName, Object... args);

}
