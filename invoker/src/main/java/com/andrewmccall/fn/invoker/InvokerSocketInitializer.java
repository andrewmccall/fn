package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.Function;
import com.andrewmccall.fn.invoker.rpc.InvokerRequestDecoder;
import com.andrewmccall.fn.invoker.rpc.InvokerResponseEncoder;
import com.andrewmccall.fn.invoker.rpc.JsonObjectDecoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by andrewmccall on 10/12/2016.
 */
public class InvokerSocketInitializer<I, O> extends ChannelInitializer<SocketChannel> {

    private static final Logger log = LogManager.getLogger(InvokerSocketInitializer.class);

    private final Function<? super I, ? extends O> function;
    private final Class<I> in;

    InvokerSocketInitializer(Function<? super I, ? extends O> function, Class<I> in) {
        this.function = function;
        this.in = in;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {


        log.trace("Initializing channel {}:{}", ch.localAddress().getAddress(), ch.localAddress().getPort());

        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(
                new InvokerResponseEncoder<>(),
                new JsonObjectDecoder(),
                new InvokerRequestDecoder<>(in),
                new InvokerRequestHandler<I,O>(function));

        log.trace("Configured.");
    }
}
