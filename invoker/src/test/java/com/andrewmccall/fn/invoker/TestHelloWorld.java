package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.ImmutableExecutionContext;
import com.andrewmccall.fn.api.ImmutableRequestContext;
import com.andrewmccall.fn.config.ConfigurationProvider;
import com.andrewmccall.fn.config.LocalConfigurationProvider;
import com.andrewmccall.fn.discovery.ServiceInstance;
import com.andrewmccall.fn.discovery.ServiceRegistry;
import com.andrewmccall.fn.invoker.rpc.InvokerRequestEncoder;
import com.andrewmccall.fn.invoker.rpc.InvokerResponseDecoder;
import com.andrewmccall.fn.invoker.rpc.JsonObjectDecoder;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by andrewmccall on 24/10/2016.
 */
public class TestHelloWorld {

    private static final Logger log = LogManager.getLogger(TestHelloWorld.class);


    private ObjectMapper objectMapper = new ObjectMapper(new JsonFactory().configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)).registerModule(new Jdk8Module());

    private ConfigurationProvider configurationProvider = new LocalConfigurationProvider();

    @Test
    public void testHelloWorldFunction() {


        InvokerRequestHandler<HelloWorldFunction.TestRequest, HelloWorldFunction.TestResponse> invoker = new InvokerRequestHandler<>(new HelloWorldFunction());

        String key = "key";
        String value = "world!";


        HelloWorldFunction.TestRequest request = new HelloWorldFunction.TestRequest(key, value);
        request.setKey(key);
        request.setValue(value);


        InvokerRequest<HelloWorldFunction.TestRequest> invokerRequest = new InvokerRequest<>(request, ImmutableRequestContext.builder().requestId(UUID.randomUUID().toString()).parameters(Collections.emptyMap()).build());

        HelloWorldFunction.TestResponse response = invoker.execute(invokerRequest).getPayload();

        assertEquals(request.getKey(), response.getKey());
        assertEquals("Hello " + request.getValue(), response.getValue());

    }

    @Test
    public void testRemoteCall() throws InterruptedException, IOException {

        // Register the instance we're about to start.
        ServiceRegistry registry = configurationProvider.getClusterConfig().getServiceRegistry();

        ServiceInstance instance = new ServiceInstance();
        ImmutableExecutionContext ctx = ImmutableExecutionContext.builder().applicationId("hello-world").functionVersion("1").build();
        instance.setExecutionContext(ctx);
        instance.setInstanceId("testRemoteCall");
        instance.setStatus(ServiceInstance.Status.REQUESTED);

        registry.register(instance);

        Invoker<HelloWorldFunction.TestRequest, HelloWorldFunction.TestResponse> invoker = new Invoker<>("hello-world", "testRemoteCall", new HelloWorldFunction(), HelloWorldFunction.TestRequest.class, HelloWorldFunction.TestResponse.class, configurationProvider);
        invoker.start();

        String key = "key";
        String value = "world!";


        HelloWorldFunction.TestRequest request = new HelloWorldFunction.TestRequest(key, value);

        Socket clientSocket = new Socket("localhost", 9999);


        log.debug("Socket connected: {}", clientSocket.isConnected());


        OutputStream os = clientSocket.getOutputStream();
        InputStream is = clientSocket.getInputStream();


        InvokerRequest<HelloWorldFunction.TestRequest> invokerRequest = new InvokerRequest<>(request, ImmutableRequestContext.builder().requestId(UUID.randomUUID().toString()).parameters(Collections.emptyMap()).build());

        objectMapper.writeValue(os, invokerRequest);

        log.trace("Sent request, calling flush.");
        os.flush();

        InvokerResponse response = objectMapper.readValue(is, objectMapper.getTypeFactory().constructParametrizedType(InvokerResponse.class, InvokerResponse.class, request.getClass()));


        log.info("Response from invoker was {}", response);


        clientSocket.close();
        invoker.stop();

    }

    @Test
    public void testMultipleRemoteCalls() throws InterruptedException, IOException {

        // Register the instance we're about to start.
        ServiceRegistry registry = configurationProvider.getClusterConfig().getServiceRegistry();

        ServiceInstance instance = new ServiceInstance();
        ImmutableExecutionContext ctx = ImmutableExecutionContext.builder().applicationId("hello-world").functionVersion("1").build();
        instance.setExecutionContext(ctx);
        instance.setInstanceId("testRemoteCall");
        instance.setStatus(ServiceInstance.Status.REQUESTED);

        registry.register(instance);

        Invoker<HelloWorldFunction.TestRequest, HelloWorldFunction.TestResponse> invoker = new Invoker<>("hello-world", "testRemoteCall", new HelloWorldFunction(), HelloWorldFunction.TestRequest.class, HelloWorldFunction.TestResponse.class, configurationProvider);
        invoker.start();

        String key = "key";
        String value = "world!";


        HelloWorldFunction.TestRequest request = new HelloWorldFunction.TestRequest(key, value);

        Socket clientSocket = new Socket("localhost", 9999);


        log.debug("Socket connected: {}", clientSocket.isConnected());


        OutputStream os = clientSocket.getOutputStream();
        InputStream is = clientSocket.getInputStream();


        InvokerRequest<HelloWorldFunction.TestRequest> invokerRequest = new InvokerRequest<>(request, ImmutableRequestContext.builder().requestId(UUID.randomUUID().toString()).parameters(Collections.emptyMap()).build());

        objectMapper.writeValue(os, invokerRequest);

        log.trace("Sent request, calling flush.");
        os.flush();

        objectMapper.writeValue(os, invokerRequest);

        log.trace("Sent request, calling flush.");
        os.flush();

        objectMapper.writeValue(os, invokerRequest);

        log.trace("Sent request, calling flush.");
        os.flush();

        objectMapper.writeValue(os, invokerRequest);

        log.trace("Sent request, calling flush.");
        os.flush();


        InvokerResponse response = objectMapper.readValue(is, objectMapper.getTypeFactory().constructParametrizedType(InvokerResponse.class, InvokerResponse.class, request.getClass()));


        log.info("Response from invoker was {}", response);

        /*response = objectMapper.readValue(is, objectMapper.getTypeFactory().constructParametrizedType(InvokerResponse.class, InvokerResponse.class, request.getClass()));


        log.info("Response from invoker was {}", response);
        response = objectMapper.readValue(is, objectMapper.getTypeFactory().constructParametrizedType(InvokerResponse.class, InvokerResponse.class, request.getClass()));


        log.info("Response from invoker was {}", response);
        response = objectMapper.readValue(is, objectMapper.getTypeFactory().constructParametrizedType(InvokerResponse.class, InvokerResponse.class, request.getClass()));


        log.info("Response from invoker was {}", response);
    */

        clientSocket.close();
        invoker.stop();

    }


    @Test
    public void testRemoteSpeed() throws InterruptedException, IOException {

        // Register the instance we're about to start.
        ServiceRegistry registry = configurationProvider.getClusterConfig().getServiceRegistry();

        ServiceInstance instance = new ServiceInstance();
        ImmutableExecutionContext ctx = ImmutableExecutionContext.builder().applicationId("hello-world").functionVersion("1").build();
        instance.setExecutionContext(ctx);
        instance.setInstanceId("testRemoteSpeed");
        instance.setStatus(ServiceInstance.Status.REQUESTED);

        registry.register(instance);

        Invoker<HelloWorldFunction.TestRequest, HelloWorldFunction.TestResponse> invoker = new Invoker<>("hello-world", "testRemoteSpeed", new HelloWorldFunction(), HelloWorldFunction.TestRequest.class, HelloWorldFunction.TestResponse.class, configurationProvider);
        invoker.start();

        int threads = 1;
        final int loops = 1000;
        RequestHandler handler = new RequestHandler(loops);


        final AtomicInteger loopsDone = new AtomicInteger(loops);

        final long start = System.currentTimeMillis();

        String value = "world!";

        ExecutorService pool = Executors.newFixedThreadPool(threads + 1);

        for (int i = loops; i > 0; i--) {
            handler.add(new HelloWorldFunction.TestRequest("" + i, value));
            loopsDone.decrementAndGet();
        }

        List<ChannelFuture> channels = new ArrayList<>();

        EventLoopGroup workerGroup = new NioEventLoopGroup(threads);

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new InvokerRequestEncoder<HelloWorldFunction.TestRequest>(), new JsonObjectDecoder(), new InvokerResponseDecoder<>(HelloWorldFunction.TestResponse.class), handler.getHander());
                }
            });



            for (int i = 0; i < threads; i++) {
                // Start the client.
                ChannelFuture f = b.connect("localhost", 9999).sync(); // (5)

                channels.add(f);

                final int p = i;
                pool.submit(() -> {


                    try {

                        while (true) {

                            InvokerRequest request = new InvokerRequest<>(handler.take(), ImmutableRequestContext.builder().requestId(UUID.randomUUID().toString()).parameters(Collections.emptyMap()).build());
                            log.debug("Sending {}", request);

                            Channel c = channels.get(p).channel();
                            c.writeAndFlush(request);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }

            int prev = -1;
            int count = 0;

            while (!handler.allProcessed()) {
                Thread.sleep(200);
                System.out.println(loopsDone + " remain, queue depth " + handler.size() + " outstanding: " + handler.outstanding());

                if (prev == handler.outstanding()) {
                    count++;
                } else
                    count = 0;
                prev= handler.outstanding();
                if (count == 50) {
                    log.error("I appear to be stuck...");
                    break;
                }
            }

            //Thread.sleep(20000);

            long time = System.currentTimeMillis() - start;

            log.warn("Processed {} in {} ms", loops , time);
            if (handler.outstanding() > 0 ) {
                handler.unprocessed().forEach((i) -> {
                    log.warn("Failed to process {}", i+1);
                });
                fail("Not all messages processed.");

            }
        } finally {
            workerGroup.shutdownGracefully();
            invoker.stop();
        }


        System.out.println(":::::: SHUTDOWN ::::::");

    }


    private static class RequestHandler {

        private final int loops;

        private final BitSet requests;

        private final BlockingQueue<HelloWorldFunction.TestRequest> queue;

        public RequestHandler(final int loops) {
            log.warn("Creating new Handler with {} loops.", loops);
            this.loops = loops;
            requests = new BitSet(loops);
            queue = new ArrayBlockingQueue<>(loops);
            outstanding();
        }

        public int getLoops() {
            return loops;
        }

        public void add(HelloWorldFunction.TestRequest request) {
            queue.add(request);
        }

        public HelloWorldFunction.TestRequest take() throws InterruptedException {
            return queue.take();
        }

        public int size() {
            return queue.size();
        }

        public ChannelInboundHandlerAdapter getHander() {
            return new ResponseHandler();
        }

        public boolean allProcessed() {

            if (!queue.isEmpty()) return false;
            if (requests.length() < loops) return false;
            return requests.length() == requests.cardinality();

        }

        public int outstanding() {

            if (log.isWarnEnabled()) {
                log.warn("Length {}, Cardinality {}", requests.length(), requests.cardinality());
            }

            return loops - requests.cardinality();
        }

        public IntStream unprocessed() {
            requests.flip(0, requests.size());
            return requests.stream();
        }


        private class ResponseHandler extends ChannelInboundHandlerAdapter {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                InvokerResponse<HelloWorldFunction.TestResponse> response = (InvokerResponse) msg;

                log.debug("Got response {}", response);
                int key = Integer.parseInt(response.getPayload().getKey()) - 1;
                if (requests.get(key)) log.warn("Bit already set for key {}!", key);
                requests.set(key);
            }
        }


    }


}
