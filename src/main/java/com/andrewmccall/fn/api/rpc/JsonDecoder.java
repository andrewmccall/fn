package com.andrewmccall.fn.api.rpc;

import com.andrewmccall.fn.invoker.InvokerRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class JsonDecoder extends io.netty.handler.codec.ByteToMessageDecoder implements JsonCodec {

    Class clazz;

    public JsonDecoder(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {


        ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);
        out.add(getObjectMapper().readValue(byteBufInputStream, getObjectMapper().getTypeFactory().constructParametrizedType(InvokerRequest.class, InvokerRequest.class, clazz)));


    }

}


