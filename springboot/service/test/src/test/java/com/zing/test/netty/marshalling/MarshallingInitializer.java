package com.zing.test.netty.marshalling;

import com.zing.test.netty.domain.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.io.Serializable;

public class MarshallingInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
        ch.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
        ch.pipeline().addLast(new ObjectHandler());
    }

    public static class ObjectHandler extends SimpleChannelInboundHandler<Serializable> {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(new User(RandomUtils.nextLong(), RandomStringUtils.randomAlphabetic(12)));
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Serializable msg) throws Exception {
            System.out.println(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println(cause);
        }
    }

}
