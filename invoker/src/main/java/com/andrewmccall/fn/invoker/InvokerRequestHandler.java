package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.Function;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by andrewmccall on 10/12/2016.
 */
public class InvokerRequestHandler<I, O>  extends ChannelInboundHandlerAdapter {

    private static final Logger log = LogManager.getLogger(InvokerRequestHandler.class);

    private final Function<? super I, ? extends O> function;

    InvokerRequestHandler(Function<? super I, ? extends O> function) {
        this.function = function;
    }

    public InvokerResponse<O> execute(InvokerRequest<I> request) {
        log.debug("Calling function with payload {}", request);
        InvokerResponse<O> response = new InvokerResponse<>(function.execute(request.getPayload(), request.getContext()), request.getContext());
        log.debug("Returning {}", response);
        return response;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        log.info("Registered...{}", ctx.channel().id());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof InvokerRequest)) return;

        log.debug("Got message type: {} value: [{}]", msg);

        //super.channelRead(ctx, msg);
        InvokerRequest<I> request = (InvokerRequest<I>) msg;
        InvokerResponse<O> response = execute(request);
        log.debug("Function returned {}", response);

        ctx.writeAndFlush(response);


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
