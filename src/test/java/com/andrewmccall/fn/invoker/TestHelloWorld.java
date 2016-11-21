package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.rpc.JsonCodec;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import static junit.framework.TestCase.assertEquals;

/**
 * Created by andrewmccall on 24/10/2016.
 */
public class TestHelloWorld implements JsonCodec {

    private static final Logger log = LogManager.getLogger(TestHelloWorld.class);

    @Test
    public void testHelloWorldFunction() {


        Invoker<HelloWorldFunction.TestRequest, HelloWorldFunction.TestResponse> invoker = new Invoker<>(new HelloWorldFunction(), HelloWorldFunction.TestRequest.class, HelloWorldFunction.TestResponse.class);

        String key = "key";
        String value = "world!";


        HelloWorldFunction.TestRequest request = new HelloWorldFunction.TestRequest();
        request.setKey(key);
        request.setValue(value);


        InvokerRequest<HelloWorldFunction.TestRequest> invokerRequest = new InvokerRequest<>(request, new SerializedRequestContext() {
            {
                this.setParameters(Collections.emptyMap());
                this.setRequestId(UUID.randomUUID().toString());
            }

        });

        HelloWorldFunction.TestResponse response = invoker.execute(invokerRequest).getPayload();

        assertEquals(request.getKey(), response.getKey());
        assertEquals("Hello " + request.getValue(), response.getValue());

        invoker.shutdown();

    }

    @Test
    public void testRemoteCall() throws InterruptedException, IOException {

        Invoker<HelloWorldFunction.TestRequest, HelloWorldFunction.TestResponse> invoker = new Invoker<>(new HelloWorldFunction(), HelloWorldFunction.TestRequest.class, HelloWorldFunction.TestResponse.class);

        String key = "key";
        String value = "world!";


        HelloWorldFunction.TestRequest request = new HelloWorldFunction.TestRequest();
        request.setKey(key);
        request.setValue(value);

        Socket clientSocket = new Socket("localhost", 9999);


        log.debug("Socket connected: {}", clientSocket.isConnected());


        OutputStream os = clientSocket.getOutputStream();
        InputStream is = clientSocket.getInputStream();


        getObjectMapper().writeValue(os, new InvokerRequest<>(request, new SerializedRequestContext() {
            {
                this.setParameters(Collections.emptyMap());
                this.setRequestId(UUID.randomUUID().toString());
            }

        }));

        os.flush();
        InvokerResponse response = getObjectMapper().readValue(is, getObjectMapper().getTypeFactory().constructParametrizedType(InvokerResponse.class, InvokerResponse.class, request.getClass()));


        log.info("Response from invoker was {}", response);


        clientSocket.close();
        invoker.shutdown();

    }

    @Test
    public void testRemoteSpeed() throws InterruptedException, IOException {

        Invoker<HelloWorldFunction.TestRequest, HelloWorldFunction.TestResponse> invoker = new Invoker<>(new HelloWorldFunction(), HelloWorldFunction.TestRequest.class, HelloWorldFunction.TestResponse.class);


        int threads = 10;
        int loops = 1000000;
        int loopsDone = loops;


        String key = "key";
        String value = "world!";

        ExecutorService pool = Executors.newFixedThreadPool(threads + 1);

        BlockingQueue<HelloWorldFunction.TestRequest> queue = new ArrayBlockingQueue<>();
        pool.submit(() -> {
           for (;loopsDone > 0; loopsDone--)  {

           }
        });

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {

                try {
                    Socket clientSocket = new Socket("localhost", 9999);
                    OutputStream os = clientSocket.getOutputStream();

                    while (true) {
                        getObjectMapper().writeValue(os, new InvokerRequest<>(queue.poll(), new SerializedRequestContext() {
                            {
                                this.setParameters(Collections.emptyMap());
                                this.setRequestId(UUID.randomUUID().toString());
                            }

                        }));
                        os.flush();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        }

        while (loopsDone > 0 && !queue.isEmpty()) {
            Thread.sleep(200);
            System.out.println(loopsDone + " remain, queue depth " + queue.size());
        }

        System.out.println("Processed " + loops + " in " + time + "ms");

    }

    private void executeRandomRequest(OutputStream os) {




        try {

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
