package com.andrewmccall.fn.invoker.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by andrewmccall on 12/12/2016.
 */
public class JsonMessageDecoder extends JsonObjectDecoder {

    private static final Logger log = LogManager.getLogger(JsonMessageDecoder.class);

    public JsonMessageDecoder() {
        super (16 * 1024 * 1024);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel inactive called for {}", ctx.hashCode());
        super.channelInactive(ctx);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        log.trace("Decoding JSON ByteBuf({}) [{}] {} ", ctx.hashCode(), System.identityHashCode(in), in);
        //log.trace("Decoding JSON message at pos {} from ByteBuf {}", in.readerIndex(), in);


        log.trace("The Buffer contains: {}", in.toString(CharsetUtil.UTF_8));

        try {
            super.decode(ctx, in, out);
        } catch (CorruptedFrameException e) {
            log.error("Whoops!", e);
            log.error("Content: {}", in.toString(CharsetUtil.UTF_8));
        }

       log.trace("Current JSON ByteBuf [{}] {}", System.identityHashCode(in), in);

        log.trace ("Values {}: {}", out.size(), out);
    }


}
