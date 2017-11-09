package com.andrewmccall.fn.discovery.standalone;

import com.andrewmccall.fn.discovery.ServiceInstance;
import com.andrewmccall.fn.discovery.ServiceRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by andrewmccall on 28/12/2016.
 */
public class GetHandler extends ChannelInboundHandlerAdapter {

    private final ServiceRegistry serviceRegistry;
    private static final Logger log = LoggerFactory.getLogger(GetHandler.class);

    public GetHandler(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Get) {
            handleGet(ctx, (Get) msg);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    private void handleGet(ChannelHandlerContext ctx,Get get) {
        if (get.getServiceId() == null) {
            log.warn("Service ID is null. request dropped.");
            return;
        }

        ServiceInstance instance;
        if (get.getInstanceId() != null) {
            instance = serviceRegistry.getServiceInstance(get.getServiceId(), get.getInstanceId());

        } else {
            instance = serviceRegistry.getServiceInstance(get.getServiceId());
        }

        GetResponse response = new GetResponse(get, instance);

        ctx.write(response);
    }
}
