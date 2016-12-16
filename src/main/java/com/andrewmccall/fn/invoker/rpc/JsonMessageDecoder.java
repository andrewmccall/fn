package com.andrewmccall.fn.invoker.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by andrewmccall on 12/12/2016.
 */
public class JsonMessageDecoder extends JsonObjectDecoder {

    private static final Logger log = LogManager.getLogger(JsonMessageDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        log.trace("Decoding JSON message at pos {} from ByteBuf {}", in.readerIndex(), in);

        try {
            super.decode(ctx, in, out);
        } catch (CorruptedFrameException e) {
            log.error("Whoops!");
        }

        log.trace ("Values {}", out);
    }
}
