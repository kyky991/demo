package com.zing.netty.test;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author Zing
 * @date 2020-12-03
 */
public class InBoundHandlerA extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(getClass().getSimpleName() + "\t" + msg);
        ctx.fireChannelRead(msg);
    }


}
