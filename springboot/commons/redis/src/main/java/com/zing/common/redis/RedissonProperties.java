package com.zing.common.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {

    private String address;
    private String password;

    private int timeout = 3000;
    private int connectPoolSize = 64;
    private int connectMinimumIdleSize = 10;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getConnectPoolSize() {
        return connectPoolSize;
    }

    public void setConnectPoolSize(int connectPoolSize) {
        this.connectPoolSize = connectPoolSize;
    }

    public int getConnectMinimumIdleSize() {
        return connectMinimumIdleSize;
    }

    public void setConnectMinimumIdleSize(int connectMinimumIdleSize) {
        this.connectMinimumIdleSize = connectMinimumIdleSize;
    }
}
