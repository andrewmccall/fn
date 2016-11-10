package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.Function;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.concurrent.Future;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;


/**
 * An Invoker executes a function with a given input and output type.
 */
public class Invoker<I, O> {

    private static final Logger log = LogManager.getLogger(Invoker.class.getName());

    private ObjectMapper mapper;

    private static final int ACCEPTOR_THREADS = 2;
    private static final int HANDLER_THREADS = 10;
    private static final int PORT = 9999;

    private final Function<? super I, ? extends O> function;

    private final Class<I> in;
    private final Class<O> out;

    private transient NioEventLoopGroup acceptorGroup = new NioEventLoopGroup(ACCEPTOR_THREADS); // 2 threads
    private transient NioEventLoopGroup handlerGroup = new NioEventLoopGroup(HANDLER_THREADS); // 10 thread

    public Invoker(Function<? super I, ? extends O> function, Class<I> in, Class<O> out) {

        this.function = function;
        this.in = in;
        this.out = out;

        mapper = new ObjectMapper();

        Marker marker = MarkerManager.getMarker("STARTUP");

        log.info(marker, "Starting Invoker for function {} with in-class {} and out-class {}", function.getClass().getName(), in.getName(), out.getName());

        ServerBootstrap b = new ServerBootstrap();
        b.group(acceptorGroup, handlerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new InvokerSocketInitialiser())
                .option(ChannelOption.SO_BACKLOG, 120)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            Future f = b.localAddress(PORT).bind().sync();
            log.info("Waiting for Socket startup.");
            f.awaitUninterruptibly();
        } catch (InterruptedException e) {
            log.error(marker, "Failed to bind to port {}", PORT);
        }
        log.info(marker, "Started on port {}", PORT);


    }

    public void shutdown() {
        log.info("Shutting down.");
        Future af = acceptorGroup.shutdownGracefully();
        Future hf = handlerGroup.shutdownGracefully();
        af.awaitUninterruptibly();
        hf.awaitUninterruptibly();
    }

    public InvokerResponse<O> execute(InvokerRequest<I> request) {
        return new InvokerResponse<>(function.execute(request.getPayload(), request.getContext()), request.getContext());
    }

    class InvokerRequestHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

            log.info("Registered...{}", ctx.channel().id());
            super.channelRegistered(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

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

            pipeline.addLast(   new JsonEncoder(),
                                new JsonDecoder(in),
                                new InvokerRequestHandler());

            log.trace("Configured.");
        }
    }

    public class JsonDecoder extends io.netty.handler.codec.ByteToMessageDecoder {



        Class clazz;

        public JsonDecoder(Class clazz) {
            this.clazz = clazz;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {


            ByteBufInputStream byteBufInputStream = new ByteBufInputStream(in);
            out.add(mapper.readValue(byteBufInputStream, mapper.getTypeFactory().constructParametrizedType(InvokerRequest.class, InvokerRequest.class, clazz)));
        }
    }

    public class JsonEncoder extends MessageToByteEncoder {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            ByteBufOutputStream os = new ByteBufOutputStream(out);
            mapper.writeValue(os, msg);
        }
    }

    private static Invoker i;

    public static void main(String[] args) {
        String functionClassName = args[0];
        String requestClassName = args[1];
        String responseClassName = args[2];


        Marker marker = MarkerManager.getMarker("STARTUP");


        Function function = null;
        try {
            function = (Function) Class.forName(functionClassName).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            log.fatal(marker, "Could not instantiate function class {} ", functionClassName, e);
            System.exit(-1);
        } catch (ClassNotFoundException e) {
            log.fatal(marker, "Could not find request class {} ", functionClassName, e);
            System.exit(-1);
        }
        Class requestClass = null;
        try {
            requestClass = Class.forName(requestClassName);
        } catch (ClassNotFoundException e) {
            log.fatal(marker, "Could not find request class {} ", requestClassName);
            System.exit(-1);
        }

        Class responseClass = null;
        try {
            responseClass = Class.forName(responseClassName);
        } catch (ClassNotFoundException e) {
            log.fatal(marker, "Could not find response class {} ", requestClassName);
            System.exit(-1);
        }

        i = new Invoker(function, requestClass, responseClass);

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // do nothing
            }
        }


    }
}
