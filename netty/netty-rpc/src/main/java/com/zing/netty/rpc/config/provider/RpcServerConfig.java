package com.zing.netty.rpc.config.provider;

import com.zing.netty.rpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Zing
 * @date 2021-01-03
 */
@Slf4j
public class RpcServerConfig {

    private final String host = "127.0.0.1";

    private int port;

    private List<ProviderConfig> providerConfigs;

    private RpcServer rpcServer = null;

    public RpcServerConfig(List<ProviderConfig> providerConfigs) {
        this.providerConfigs = providerConfigs;
    }

    public void export() {
        if (rpcServer == null) {
            try {
                rpcServer = new RpcServer(host + ":" + port);
            } catch (Exception e) {
                log.error("export exception", e);
            }

            for (ProviderConfig providerConfig : providerConfigs) {
                rpcServer.registerProcessor(providerConfig);
            }
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<ProviderConfig> getProviderConfigs() {
        return providerConfigs;
    }

    public void setProviderConfigs(List<ProviderConfig> providerConfigs) {
        this.providerConfigs = providerConfigs;
    }
}
