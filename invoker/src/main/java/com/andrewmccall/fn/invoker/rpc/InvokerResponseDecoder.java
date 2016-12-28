package com.andrewmccall.fn.invoker.rpc;

import com.andrewmccall.fn.invoker.InvokerResponse;
import com.andrewmccall.fn.json.JacksonConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.List;

import static com.andrewmccall.fn.json.JacksonConfig.getObjectMapper;

/**
 * Created by andrewmccall on 29/11/2016.
 */
public class InvokerResponseDecoder<T> extends ByteToMessageDecoder {


    private static final Logger log = LogManager.getLogger(InvokerResponseDecoder.class);

    /**
     * Gets the ObjectMapper from the synchronized method when the class is created so we don't enter the synchronized
     * block for every call.
     */
    private static final ObjectMapper objectMapper = JacksonConfig.getObjectMapper();

    private JavaType parameterizedType;

    public InvokerResponseDecoder(Class<T> clazz) {
        parameterizedType = objectMapper.getTypeFactory().constructParametrizedType(InvokerResponse.class, InvokerResponse.class, clazz);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        log.trace("Decoding... ");

        int readIdx = in.readerIndex();
        int writeIdx = in.writerIndex();

        ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);

        log.trace("{} bytes available", byteBufInputStream.available());

        try {
            InvokerResponse<T> r = objectMapper.readValue((InputStream) byteBufInputStream, parameterizedType);
            log.trace("Request {}", r);

            out.add(r);
        } catch (Exception e) {
            log.warn("Failed to process message.", e);
            log.debug("Contents: {}", in.toString(CharsetUtil.UTF_8));
        }



    }

}
