package com.andrewmccall.fn.gateway;


import com.andrewmccall.fn.ServerLifecycle;
import com.andrewmccall.fn.config.ConfigurationProvider;
import com.andrewmccall.fn.config.LocalConfigurationProvider;
import com.andrewmccall.fn.invoker.Invoker;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Gateway provides a stateless HTTP conduit to Functions executing on the cluster.
 */
public class Gateway extends ServerLifecycle {

    static final int PORT = 8080;

    private static final Logger log = LogManager.getLogger(Invoker.class.getName());


    private final ConfigurationProvider configurationProvider;

    Gateway(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;

        log.info(STARTUP, "Created a new Gateway running on port {}", PORT);
    }

    @Override
    public void start() {

        ChannelFuture f;
        try {
            f = startServer(PORT);
        } catch (InterruptedException e) {
            log.warn(STARTUP, "Failed to start.", e);
            return;
        }

        //Channel ch = f.channel();

        System.err.println("Open your web browser and navigate to ://127.0.0.1:" + PORT + '/');

        //ch.closeFuture().sync();
    }

    public static void main(String[] args) throws Exception {
        Gateway gateway = new Gateway(new LocalConfigurationProvider());
        gateway.start();
    }

    @Override
    protected ServerBootstrap getServerBootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(getAcceptorGroup(), getHandlerGroup())
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new GatewayInitializer(new LocalConfigurationProvider()));
        return b;
    }
}
