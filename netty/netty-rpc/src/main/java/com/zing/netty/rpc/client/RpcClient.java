package com.zing.netty.rpc.client;

import com.zing.netty.rpc.client.proxy.RpcAsyncProxy;
import com.zing.netty.rpc.client.proxy.RpcProxyImpl;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zing
 * @date 2020-12-31
 */
public class RpcClient {

    private String serverAddr;

    private long timeout;

    private final Map<Class<?>, Object> syncProxyInstanceMap = new ConcurrentHashMap<>(16);
    private final Map<Class<?>, Object> asyncProxyInstanceMap = new ConcurrentHashMap<>(16);

    public void init(String serverAddr, long timeout) {
        this.serverAddr = serverAddr;
        this.timeout = timeout;
        connect();
    }

    private void connect() {
        RpcConnectManager.getInstance().connect(serverAddr);
    }

    public void stop() {
        RpcConnectManager.getInstance().stop();
    }

    /**
     * 同步调用
     */
    public <T> T invokeSync(Class<T> interfaceClass) {
        if (syncProxyInstanceMap.containsKey(interfaceClass)) {
            return (T) syncProxyInstanceMap.get(interfaceClass);
        } else {
            Object proxy = Proxy.newProxyInstance(
                    interfaceClass.getClassLoader(),
                    new Class[]{interfaceClass},
                    new RpcProxyImpl<>(interfaceClass, timeout)
            );
            syncProxyInstanceMap.put(interfaceClass, proxy);
            return (T) proxy;
        }
    }

    /**
     * 异步调用
     */
    public <T> RpcAsyncProxy invokeAsync(Class<T> interfaceClass) {
        if (asyncProxyInstanceMap.containsKey(interfaceClass)) {
            return (RpcAsyncProxy) asyncProxyInstanceMap.get(interfaceClass);
        } else {
            RpcAsyncProxy proxy = new RpcProxyImpl<>(interfaceClass, timeout);
            asyncProxyInstanceMap.put(interfaceClass, proxy);
            return proxy;
        }
    }

}
