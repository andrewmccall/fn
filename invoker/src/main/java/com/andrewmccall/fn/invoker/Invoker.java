package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.ServerLifecycle;
import com.andrewmccall.fn.api.Function;
import com.andrewmccall.fn.api.ImmutableExecutionContext;
import com.andrewmccall.fn.config.ConfigurationProvider;
import com.andrewmccall.fn.config.LocalConfigurationProvider;
import com.andrewmccall.fn.discovery.ServiceInstance;
import com.andrewmccall.fn.discovery.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketException;
import java.util.UUID;


/**
 * An Invoker executes a function with a given input and output type.
 */
public class Invoker<I, O> extends ServerLifecycle {

    private static final Logger log = LogManager.getLogger(Invoker.class.getName());

    private static final int PORT = 9999;

    private final Function<? super I, ? extends O> function;

    private final Class<I> in;

    private final String functionId;
    private final String instanceId;

    private final ConfigurationProvider configurationProvider;

    public Invoker(String functionId, String instanceId, Function<? super I, ? extends O> function, Class<I> in, ConfigurationProvider configurationProvider) {

        this.functionId = functionId;
        this.instanceId = instanceId;

        this.function = function;
        this.in = in;

        this.configurationProvider = configurationProvider;

        log.info(STARTUP, "Created Invoker for function {} with in-class {} and out-class {}", function.getClass().getName(), in.getName());

    }

    @Override
    protected ServerBootstrap getServerBootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        b.group(getAcceptorGroup(), getHandlerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new InvokerSocketInitializer<>(function, in))
                .option(ChannelOption.SO_BACKLOG, 120)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        return b;
    }

    public void start() {

        ServiceRegistry registry = configurationProvider.getClusterConfig().getServiceRegistry();

        ServiceInstance instance = registry.getServiceInstance(functionId, instanceId);

        log.debug(STARTUP, "Found instance for self, {}", instance);

        if (instance == null) {
            instance = new ServiceInstance();
        }

        if (instance.getStatus() != ServiceInstance.Status.REQUESTED) {
            log.warn("Instance does not have the expected status..."); // do something?
        }

        instance.setStatus(ServiceInstance.Status.STARTING);
        registry.register(instance);



        try {
            Future f = startServer(PORT);
            log.info(STARTUP, "Waiting for Socket start.");
            f.awaitUninterruptibly();

            if (!f.isSuccess()) {
                log.error(STARTUP, "Startup failed, socket not running.");
                instance.setStatus(ServiceInstance.Status.STARTUP_FAILED);
                registry.register(instance);
                stop();
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
            stop();
            return;
        }

        // once we've started, let's update our instance as available.
        instance.setStatus(ServiceInstance.Status.RUNNING);
        registry.register(instance);

        log.debug("Registered self as {}", instance);

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

        i = new Invoker(functionId, instanceId, function, requestClass, configurationProvider);
        i.start();

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // do nothing
            }
        }


    }
}
