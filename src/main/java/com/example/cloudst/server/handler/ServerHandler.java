package com.example.cloudst.server.handler;

import io.netty.channel.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf totalBuf;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Подключился клиент " + ctx.channel().remoteAddress());
        totalBuf = ctx.alloc().buffer();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) {
        ByteBuf inboundMsgBuf = (ByteBuf) msg;
        if (inboundMsgBuf.isReadable()) inboundMsgBuf.writeBytes(totalBuf);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}
