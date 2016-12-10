package com.andrewmccall.fn.invoker.rpc;

import com.andrewmccall.fn.invoker.InvokerRequest;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutput;

import static com.andrewmccall.fn.json.JacksonConfig.getObjectMapper;

/**
 * Created by andrewmccall on 29/11/2016.
 */
public class InvokerRequestEncoder<T> extends MessageToByteEncoder<InvokerRequest<T>> {


    private static final Logger log = LogManager.getLogger(InvokerRequestEncoder.class);

    /**
     * Gets the ObjectMapper from the synchronized method when the class is created so we don't enter the synchronized
     * block for every call.
     */
    private static final ObjectMapper objectMapper = getObjectMapper();

    private JavaType parameterizedType;

    public InvokerRequestEncoder(Class<T> clazz) {
        parameterizedType = objectMapper.getTypeFactory().constructParametrizedType(InvokerRequest.class, InvokerRequest.class, clazz);
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, InvokerRequest<T> msg, ByteBuf out) throws Exception {
        ByteBufOutputStream os = new ByteBufOutputStream(out);
        objectMapper.writeValue((DataOutput) os, msg);
    }
}
