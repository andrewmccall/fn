package com.andrewmccall.fn;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides common Lifecycle management for Server components.
 */
public abstract class ServerLifecycle implements Lifecycle {

    private static final Logger log = LogManager.getLogger(ServerLifecycle.class.getName());

    private transient NioEventLoopGroup acceptorGroup = new NioEventLoopGroup(1);
    private transient NioEventLoopGroup handlerGroup = new NioEventLoopGroup(1);


    /**
     * Gets the ServerBootstrap for this component.
     * @return the ServerBootstrap
     */
    protected abstract ServerBootstrap getServerBootstrap();

    public NioEventLoopGroup getAcceptorGroup() {
        return acceptorGroup;
    }

    public NioEventLoopGroup getHandlerGroup() {
        return handlerGroup;
    }

    public ChannelFuture startServer(int port) throws InterruptedException {
        return getServerBootstrap().localAddress(port).bind().sync();
    }

    @Override
    public void stop() {

        // Mark our instance as shutting down in the registry.

        log.info(SHUTDOWN, "Shutting down.");
        Future af = acceptorGroup.shutdownGracefully();
        Future hf = handlerGroup.shutdownGracefully();
        log.debug(SHUTDOWN, "Waiting for thread group stop.");
        af.awaitUninterruptibly();
        hf.awaitUninterruptibly();
        log.info(SHUTDOWN, "Shutdown complete");

        // Mark the instance as stop in the registry -- do we clean up here or keep it as a 'frozen' instance?

    }
}
