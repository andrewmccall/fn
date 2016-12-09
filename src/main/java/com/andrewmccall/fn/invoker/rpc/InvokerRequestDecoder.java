package com.andrewmccall.fn.invoker.rpc;

import com.andrewmccall.fn.invoker.InvokerRequest;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

import static com.andrewmccall.fn.invoker.rpc.JacksonRpc.getObjectMapper;

/**
 * Created by andrewmccall on 29/11/2016.
 */
public class InvokerRequestDecoder<T> extends ByteToMessageDecoder {


    private static final Logger log = LogManager.getLogger(InvokerRequestDecoder.class);

    /**
     * Gets the ObjectMapper from the synchronized method when the class is created so we don't enter the synchronized
     * block for every call.
     */
    private static final ObjectMapper objectMapper = getObjectMapper();

    private JavaType parameterizedType;

    public InvokerRequestDecoder(Class<T> clazz) {
        parameterizedType = objectMapper.getTypeFactory().constructParametrizedType(InvokerRequest.class, InvokerRequest.class, clazz);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        log.trace("Decoding... ");

        ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);

        log.trace("{} bytes available", byteBufInputStream.available());

        try {
            InvokerRequest<T> r = objectMapper.readValue((InputStream) byteBufInputStream, parameterizedType);
            log.trace("Request {}", r);

            out.add(r);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            byteBufInputStream.reset();
            while ((length = byteBufInputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            log.error("Failed to process message: {}", result.toString("UTF-8"), e);
        }


    }

}