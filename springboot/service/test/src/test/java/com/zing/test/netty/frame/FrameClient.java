package com.zing.test.netty.frame;

import com.zing.test.netty.Client;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import static com.zing.test.netty.Constants.HOST;
import static com.zing.test.netty.Constants.PORT;

public class FrameClient {

    public static void main(String[] args) throws Exception {
        new Client()
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new FixedLengthFrameDecoder(5));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new FrameClientHandler());
                    }
                })
                .host(HOST)
                .port(PORT)
                .start();
    }

}
