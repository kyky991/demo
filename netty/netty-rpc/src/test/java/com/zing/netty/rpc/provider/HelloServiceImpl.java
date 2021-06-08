package com.zing.netty.rpc.provider;

import com.zing.netty.rpc.api.IHelloService;
import com.zing.netty.rpc.api.User;

/**
 * @author Zing
 * @date 2021-01-03
 */
public class HelloServiceImpl implements IHelloService {
    @Override
    public String echo(String name) {
        return "echo " + name;
    }

    @Override
    public String echo(User user) {
        return "echo " + user;
    }
}
