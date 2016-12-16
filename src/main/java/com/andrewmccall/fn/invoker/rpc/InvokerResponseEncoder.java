package com.andrewmccall.fn.invoker.rpc;

import com.andrewmccall.fn.invoker.InvokerResponse;
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
public class InvokerResponseEncoder<T> extends MessageToByteEncoder<InvokerResponse<T>> {


    private static final Logger log = LogManager.getLogger(InvokerResponseEncoder.class);

    /**
     * Gets the ObjectMapper from the synchronized method when the class is created so we don't enter the synchronized
     * block for every call.
     */
    private static final ObjectMapper objectMapper = getObjectMapper();

    private JavaType parameterizedType;

    public InvokerResponseEncoder(Class<T> clazz) {
        parameterizedType = objectMapper.getTypeFactory().constructParametrizedType(InvokerResponse.class, InvokerResponse.class, clazz);
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, InvokerResponse<T> msg, ByteBuf out) throws Exception {
        log.trace("Writing at pos {} of {} value {}", out.writerIndex(), out, msg);
        out.writeBytes(objectMapper.writeValueAsBytes(msg));
    }
}
