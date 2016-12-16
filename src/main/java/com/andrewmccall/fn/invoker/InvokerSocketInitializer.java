package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.Function;
import com.andrewmccall.fn.invoker.rpc.InvokerRequestDecoder;
import com.andrewmccall.fn.invoker.rpc.InvokerResponseEncoder;
import com.andrewmccall.fn.invoker.rpc.JsonMessageDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.json.JsonObjectDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by andrewmccall on 10/12/2016.
 */
public class InvokerSocketInitializer<I, O> extends ChannelInitializer<SocketChannel> {

    private static final Logger log = LogManager.getLogger(InvokerSocketInitializer.class);

    private final Function<? super I, ? extends O> function;
    private final Class<I> in;
    private final Class<O> out;

    InvokerSocketInitializer(Function<? super I, ? extends O> function, Class<I> in, Class<O> out) {
        this.function = function;
        this.in = in;
        this.out = out;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {


        log.trace("Initializing channel {}:{}", ch.localAddress().getAddress(), ch.localAddress().getPort());

        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(
                new InvokerResponseEncoder<>(out),
                new JsonMessageDecoder(),
                new InvokerRequestDecoder<>(in),
                new InvokerRequestHandler<I,O>(function));

        log.trace("Configured.");
    }
}
