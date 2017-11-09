package com.andrewmccall.fn.discovery.standalone;

import com.andrewmccall.fn.discovery.ServiceRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by andrewmccall on 28/12/2016.
 */
public class GetAllHandler extends ChannelInboundHandlerAdapter {

    private final ServiceRegistry serviceRegistry;
    private static final Logger log = LoggerFactory.getLogger(GetHandler.class);

    public GetAllHandler(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof GetAll) {
            handleGetAll(ctx, (GetAll) msg);
        } else {
            super.channelRead(ctx, msg);
        }
    }

    private void handleGetAll(ChannelHandlerContext ctx, GetAll get) {
        if (get.getServiceId() == null) {
            log.warn("Service ID is null. request dropped.");
            return;
        }

        GetAllResponse response = new GetAllResponse(get, new ArrayList<>(serviceRegistry.getServiceInstances(get.getServiceId())));

        ctx.write(response);

    }


}
