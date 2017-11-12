package com.andrewmccall.fn.invoker.rpc;

import com.andrewmccall.fn.invoker.InvokerRequest;
import com.andrewmccall.fn.json.JacksonConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutput;

/**
 * Created by andrewmccall on 29/11/2016.
 */
public class InvokerRequestEncoder<T> extends MessageToByteEncoder<InvokerRequest<T>> {

    /**
     * Gets the ObjectMapper from the synchronized method when the class is created so we don't enter the synchronized
     * block for every call.
     */
    private static final ObjectMapper objectMapper = JacksonConfig.getObjectMapper();

    public InvokerRequestEncoder() {
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, InvokerRequest<T> msg, ByteBuf out) throws Exception {
        ByteBufOutputStream os = new ByteBufOutputStream(out);
        objectMapper.writeValue((DataOutput) os, msg);
    }
}
