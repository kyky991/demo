package com.zing.netty.rpc.api;

import com.zing.netty.rpc.api.User;

/**
 * @author Zing
 * @date 2021-01-03
 */
public interface IHelloService {

    String echo(String name);

    String  echo(User user);

}
