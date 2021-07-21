package com.zing.test.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class TestEmbeddedChannel {

    static class StringPrinter extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println("StringPrinter ---> " + msg);
        }
    }

    public static void main(String[] args) throws Exception {
        final LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(
                1024, 2, 4, 4, 10);
        ChannelInitializer<Channel> initializer = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(decoder);
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringPrinter());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(initializer);

//        for (int i = 0; i < 10; i++) {
//            byte[] content = ("测试测试..." + i).getBytes(CharsetUtil.UTF_8);
//
//            ByteBuf buf = Unpooled.buffer();
//            buf.writeChar(0x0001);
//            buf.writeInt(content.length);
//            buf.writeInt(0xCAFEBABE);
//            buf.writeBytes(content);
//            channel.writeInbound(buf);
//        }


        byte[] content = ("测试测试...100").getBytes(CharsetUtil.UTF_8);
        byte[] content2 = ("测试测试...1000").getBytes(CharsetUtil.UTF_8);
        ByteBuf buf = Unpooled.buffer();
        buf.writeChar(0x0001);
        buf.writeInt(content.length);
        buf.writeInt(0xCAFEBABE);
        buf.writeBytes(content);
        buf.writeChar(0x0001);
        buf.writeInt(content2.length);
        channel.writeInbound(buf);

        buf = Unpooled.buffer();
        buf.writeInt(0xCAFEBABE);
        buf.writeBytes(content2);
        channel.writeInbound(buf);

        TimeUnit.SECONDS.sleep(5);
    }

}
