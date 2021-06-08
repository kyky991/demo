package com.zing.netty.rpc.provider;

import com.zing.netty.rpc.api.IHelloService;
import com.zing.netty.rpc.config.provider.ProviderConfig;
import com.zing.netty.rpc.config.provider.RpcServerConfig;

import java.util.Arrays;

/**
 * @author Zing
 * @date 2021-01-03
 */
public class ServerTest {

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                ProviderConfig providerConfig = new ProviderConfig();
                providerConfig.setInterface(IHelloService.class.getName());
                providerConfig.setRef(HelloServiceImpl.class.newInstance());

                RpcServerConfig serverConfig = new RpcServerConfig(Arrays.asList(providerConfig));
                serverConfig.setPort(9999);
                serverConfig.export();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
