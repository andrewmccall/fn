package com.andrewmccall.fn.discovery.standalone;

import com.andrewmccall.fn.ServerLifecycle;
import com.andrewmccall.fn.discovery.LocalRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * A lightweight standalone ServiceRegistry server.
 */
public class StandaloneRegistryServer extends ServerLifecycle {

    private static final LocalRegistry registry = new LocalRegistry();

    /**
     * Starts the service registry. Unimplemented.
     */
    @Override
    public void start() {
    }

    @Override
    protected ServerBootstrap getServerBootstrap() {


        ServerBootstrap b = new ServerBootstrap();
        b.group(getAcceptorGroup(), getHandlerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new StandaloneRegistryInitializer(registry))
                .option(ChannelOption.SO_BACKLOG, 120)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        return b;

    }

}
