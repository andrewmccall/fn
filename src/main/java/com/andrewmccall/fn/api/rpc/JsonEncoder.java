package com.andrewmccall.fn.api.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JsonEncoder extends MessageToByteEncoder implements JsonCodec {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        ByteBufOutputStream os = new ByteBufOutputStream(out);
        getObjectMapper().writeValue(os, msg);
    }
}