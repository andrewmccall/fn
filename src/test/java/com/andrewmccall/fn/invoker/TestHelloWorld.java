package com.andrewmccall.fn.invoker;

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

import static junit.framework.TestCase.assertEquals;

/**
 * Created by andrewmccall on 24/10/2016.
 */
public class TestHelloWorld {

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


        Thread.sleep(1000);

        Socket clientSocket = new Socket("localhost", 9999);



        log.debug("Socket connected: {}", clientSocket.isConnected());



        OutputStream os = clientSocket.getOutputStream();
        InputStream is = clientSocket.getInputStream();

        ObjectMapper mapper = new ObjectMapper(new JsonFactory().configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false));
        mapper.writeValue(os, new InvokerRequest<>(request, new SerializedRequestContext() {
            {
                this.setParameters(Collections.emptyMap());
                this.setRequestId(UUID.randomUUID().toString());
            }

        }));

        os.flush();
        InvokerResponse response = mapper.readValue(is, mapper.getTypeFactory().constructParametrizedType(InvokerResponse.class, InvokerResponse.class, request.getClass()));


        log.info("Response from invoker was {}", response);


        clientSocket.close();
        invoker.shutdown();

    }







}
