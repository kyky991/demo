package com.zing.test.netty.echo;

import com.zing.test.netty.Server;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;

import static com.zing.test.netty.Constants.PORT;

public class EchoServer {

    public static void main(String[] args) throws Exception {
        EchoServerHandler serverHandler = new EchoServerHandler();

        new Server()
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(serverHandler);
                    }
                })
                .port(PORT)
                .start();
    }

}
