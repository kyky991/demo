package com.zing.test.netty.frame;

import io.netty.channel.*;

@ChannelHandler.Sharable
public class FrameServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("Server received: " + msg);
        ctx.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("echo success");
                } else {
                    System.out.println("echo failed");
                }
            }
        });
    }

}
