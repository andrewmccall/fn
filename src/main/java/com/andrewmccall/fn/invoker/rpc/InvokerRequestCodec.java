package com.andrewmccall.fn.invoker.rpc;

import com.andrewmccall.fn.invoker.InvokerRequest;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutput;
import java.io.InputStream;
import java.util.List;

/**
 * Created by andrewmccall on 29/11/2016.
 */
public class InvokerRequestCodec<T> extends ByteToMessageCodec<InvokerRequest<T>> {

    private static final ObjectMapper objectMapper = new ObjectMapper(
            new JsonFactory()
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
    );

    private static final Logger log = LogManager.getLogger(InvokerRequestCodec.class);

    private JavaType parameterizedType;

    public InvokerRequestCodec(Class<T> clazz) {
        parameterizedType = objectMapper.getTypeFactory().constructParametrizedType(InvokerRequest.class, InvokerRequest.class, clazz);
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, InvokerRequest<T> msg, ByteBuf out) throws Exception {
        ByteBufOutputStream os = new ByteBufOutputStream(out);
        objectMapper.writeValue((DataOutput) os, msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        log.trace("Decoding... ");

        ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);

        log.trace("{} bytes available", byteBufInputStream.available());

        InvokerRequest<T> r = objectMapper.readValue((InputStream) byteBufInputStream, parameterizedType);

        log.trace("Request {}", r);

        out.add(r);

    }
}
