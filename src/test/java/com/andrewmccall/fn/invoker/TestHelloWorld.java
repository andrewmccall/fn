package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.ImmutableExecutionContext;
import com.andrewmccall.fn.api.ImmutableRequestContext;
import com.andrewmccall.fn.config.ConfigurationProvider;
import com.andrewmccall.fn.config.LocalConfigurationProvider;

import com.andrewmccall.fn.discovery.ServiceInstance;
import com.andrewmccall.fn.discovery.ServiceRegistry;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by andrewmccall on 24/10/2016.
 */
public class TestHelloWorld  {

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

        Thread.sleep(10000);


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
        final int loops = 300;

        final AtomicInteger loopsDone = new AtomicInteger(loops);

        final long start = System.currentTimeMillis();

        String key = "key";
        String value = "world!";

        ExecutorService pool = Executors.newFixedThreadPool(threads + 1);

        BlockingQueue<HelloWorldFunction.TestRequest> queue = new ArrayBlockingQueue<>(loops);
        pool.submit(() -> {

           for (int i = loops;i > 0; i--)  {
               queue.add(new HelloWorldFunction.TestRequest(key + " " + i, value));
               loopsDone.decrementAndGet();
           }

        });

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {

                try {
                    Socket clientSocket = new Socket("localhost", 9999);
                    OutputStream os = clientSocket.getOutputStream();

                    while (true) {

                        InvokerRequest request = new InvokerRequest<>(queue.take(), ImmutableRequestContext.builder().requestId(UUID.randomUUID().toString()).parameters(Collections.emptyMap()).build());
                        log.debug("Sending {}", request);
                        objectMapper.writeValue(os, request);
                        os.flush();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

        while (!queue.isEmpty()) {
            Thread.sleep(200);
            System.out.println(loopsDone + " remain, queue depth " + queue.size());
        }

        Thread.sleep(20000);

        long time = System.currentTimeMillis() - start;

        System.out.println("Processed " + loops + " in " + time + "ms");

        invoker.stop();

    }


}
