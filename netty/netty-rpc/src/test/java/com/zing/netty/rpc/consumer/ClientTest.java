package com.zing.netty.rpc.consumer;

import com.zing.netty.rpc.api.IHelloService;
import com.zing.netty.rpc.api.User;
import com.zing.netty.rpc.client.RpcClient;
import com.zing.netty.rpc.client.RpcFuture;
import com.zing.netty.rpc.client.proxy.RpcAsyncProxy;

/**
 * @author Zing
 * @date 2021-01-03
 */
public class ClientTest {

    public static void main(String[] args) throws Exception {
        RpcClient client = new RpcClient();
        client.init("127.0.0.1:9999", 3000);

        IHelloService helloService = client.invokeSync(IHelloService.class);
        String hello = helloService.echo("hello");
        System.out.println(hello);

        RpcAsyncProxy proxy = client.invokeAsync(IHelloService.class);
        RpcFuture future1 = proxy.call("echo", "async hello");
        RpcFuture future2 = proxy.call("echo", new User("async name"));

        Object result1 = future1.get();
        Object result2 = future2.get();

        System.out.println(result1);
        System.out.println(result2);
    }

}
