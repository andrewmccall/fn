package com.andrewmccall.fn.discovery.standalone;

import com.andrewmccall.fn.discovery.ServiceInstance;
import com.andrewmccall.fn.discovery.ServiceRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by andrewmccall on 28/12/2016.
 */
public class PutHandler extends ChannelInboundHandlerAdapter {

    private final ServiceRegistry registry;

    public PutHandler(ServiceRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ServiceInstance) {
            handlePut((ServiceInstance) msg);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    private void handlePut(ServiceInstance put) {
        registry.register(put);
    }

}
