package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.Function;
import com.andrewmccall.fn.api.ImmutableExecutionContext;
import com.andrewmccall.fn.invoker.rpc.InvokerRequestCodec;
import com.andrewmccall.fn.config.ConfigurationProvider;
import com.andrewmccall.fn.config.LocalConfigurationProvider;
import com.andrewmccall.fn.discovery.ServiceInstance;
import com.andrewmccall.fn.discovery.ServiceRegistry;
import com.andrewmccall.fn.invoker.rpc.InvokerResponseCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.net.SocketException;
import java.util.UUID;


/**
 * An Invoker executes a function with a given input and output type.
 */
public class Invoker<I, O> {

    private static final Logger log = LogManager.getLogger(Invoker.class.getName());
    private static final Marker STARTUP = MarkerManager.getMarker("STARTUP");
    private static final Marker SHUTDOWN = MarkerManager.getMarker("SHUTDOWN");

    private static final int ACCEPTOR_THREADS = 2;
    private static final int HANDLER_THREADS = 10;
    private static final int PORT = 9999;

    private final Function<? super I, ? extends O> function;

    private final Class<I> in;
    private final Class<O> out;

    private final String functionId;
    private final String instanceId;

    private transient NioEventLoopGroup acceptorGroup = new NioEventLoopGroup(ACCEPTOR_THREADS); // 2 threads
    private transient NioEventLoopGroup handlerGroup = new NioEventLoopGroup(HANDLER_THREADS); // 10 thread

    private final ConfigurationProvider configurationProvider;

    private transient ServiceRegistry registry;

    public Invoker(String functionId, String instanceId, Function<? super I, ? extends O> function, Class<I> in, Class<O> out, ConfigurationProvider configurationProvider) {

        this.functionId = functionId;
        this.instanceId = instanceId;

        this.function = function;
        this.in = in;
        this.out = out;

        this.configurationProvider = configurationProvider;

        log.info(STARTUP, "Starting Invoker for function {} with in-class {} and out-class {}", function.getClass().getName(), in.getName(), out.getName());

    }

    public void startup() {

        registry = configurationProvider.getClusterConfig().getServiceRegistry();

        ServiceInstance instance = registry.getServiceInstance(functionId, instanceId);

        log.debug(STARTUP, "Found instance for self, {}", instance);

        if (instance == null) {
            instance = new ServiceInstance();
        }

        if (instance.getStatus() != ServiceInstance.Status.REQUESTED)  {
            log.warn("Instance does not have the expected status..."); // do something?
        }

        instance.setStatus(ServiceInstance.Status.STARTING);
        registry.register(instance);

        ServerBootstrap b = new ServerBootstrap();
        b.group(acceptorGroup, handlerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new InvokerSocketInitialiser())
                .option(ChannelOption.SO_BACKLOG, 120)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            Future f = b.localAddress(PORT).bind().sync();
            log.info(STARTUP, "Waiting for Socket startup.");
            f.awaitUninterruptibly();

            if (!f.isSuccess()) {
                log.error(STARTUP, "Startup failed, socket not running.");
                instance.setStatus(ServiceInstance.Status.STARTUP_FAILED);
                registry.register(instance);
                shutdown();
                return;
            }

        } catch (InterruptedException e) {
            log.error(STARTUP, "Failed to bind to port {}", PORT);
        }
        log.info(STARTUP, "Started on port {}", PORT);

        instance.setPort(PORT);
        // default to the first host.
        try {
            instance.setHost(ServiceRegistry.getFirstHost());
        } catch (SocketException e) {
            log.error(STARTUP, "Failed to start.", e);
            shutdown();
            return;
        }

        // once we've started, let's update our instance as available.
        instance.setStatus(ServiceInstance.Status.RUNNING);
        registry.register(instance);

    }

    public void shutdown() {

        // Mark our instance as shutting down in the registry.

        log.info(SHUTDOWN, "Shutting down.");
        Future af = acceptorGroup.shutdownGracefully();
        Future hf = handlerGroup.shutdownGracefully();
        log.debug(SHUTDOWN, "Waiting for thread group shutdown.");
        af.awaitUninterruptibly();
        hf.awaitUninterruptibly();
        log.info(SHUTDOWN, "Shutdown complete");

        // Mark the instance as shutdown in the registry -- do we clean up here or keep it as a 'frozen' instance?
    }

    public InvokerResponse<O> execute(InvokerRequest<I> request) {
        log.debug("Calling function with payload {}");
        InvokerResponse<O> response = new InvokerResponse<>(function.execute(request.getPayload(), request.getContext()), request.getContext());
        log.debug("Returning {}");
        return response;
    }

    class InvokerRequestHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

            log.info("Registered...{}", ctx.channel().id());
            super.channelRegistered(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            if (! (msg instanceof InvokerRequest)) return;

            log.debug("Got message type: {} value: [{}]", msg);

            super.channelRead(ctx, msg);
            InvokerRequest<I> request = (InvokerRequest<I>) msg;
            InvokerResponse<O> response = execute(request);
            log.debug("Function returned {}", response);

            ctx.write(response);

        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }
    }

    /**
     * Performs the initial set up of sockets as they connect to Netty.
     * Registers the pipeline of handlers that received messages are passed through
     */
    public class InvokerSocketInitialiser extends ChannelInitializer<SocketChannel> {

        @Override
        public void initChannel(SocketChannel ch) throws Exception {


            log.trace("Initializing channel {}:{}", ch.localAddress().getAddress(), ch.localAddress().getPort());

            ChannelPipeline pipeline = ch.pipeline();

            pipeline.addLast(   new InvokerResponseCodec<>(out),
                                new InvokerRequestCodec<>(in),
                                new InvokerRequestHandler());

            log.trace("Configured.");
        }
    }


    private static Invoker i;

    public static void main(String[] args) {

        String functionId = UUID.randomUUID().toString();
        String instanceId = UUID.randomUUID().toString();

        String functionClassName = args[0];
        String requestClassName = args[1];
        String responseClassName = args[2];

        ConfigurationProvider configurationProvider = new LocalConfigurationProvider();

        ServiceRegistry registry = configurationProvider.getClusterConfig().getServiceRegistry();
        ServiceInstance instance = new ServiceInstance();
        instance.setInstanceId(UUID.randomUUID().toString());

        ImmutableExecutionContext ctx = ImmutableExecutionContext.builder().applicationId(functionId).functionVersion("1").build();
        instance.setExecutionContext(ctx);
        instance.setStatus(ServiceInstance.Status.REQUESTED);
        registry.register(instance);


        Function function = null;
        try {
            function = (Function) Class.forName(functionClassName).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.fatal(STARTUP, "Could not instantiate function class {} ", functionClassName, e);
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            log.fatal(STARTUP, "Could not find request class {} ", functionClassName, e);
            System.exit(-1);
        }
        Class requestClass = null;
        try {
            requestClass = Class.forName(requestClassName);
        } catch (ClassNotFoundException e) {
            log.fatal(STARTUP, "Could not find request class {} ", requestClassName);
            System.exit(-1);
        }

        Class responseClass = null;
        try {
            responseClass = Class.forName(responseClassName);
        } catch (ClassNotFoundException e) {
            log.fatal(STARTUP, "Could not find response class {} ", requestClassName);
            System.exit(-1);
        }

        i = new Invoker(functionId, instanceId, function, requestClass, responseClass, configurationProvider);
        i.startup();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // do nothing
            }
        }


    }
}
