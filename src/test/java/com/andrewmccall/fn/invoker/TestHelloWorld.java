package com.andrewmccall.fn.invoker;

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


        Invoker<TestRequest, TestResponse> invoker = new Invoker<>(new HelloWorldFunction(), TestRequest.class, TestResponse.class);

        String key = "key";
        String value = "world!";


        TestRequest request = new TestRequest();
        request.setKey(key);
        request.setValue(value);


        InvokerRequest<TestRequest> invokerRequest = new InvokerRequest<>(request, new InvokerRequest.SerializedRequestContext() {
            {
                this.setParameters(Collections.emptyMap());
                this.setRequestId(UUID.randomUUID().toString());
            }

        });

        TestResponse response = invoker.execute(invokerRequest).getPayload();

        assertEquals(request.getKey(), response.getKey());
        assertEquals("Hello " + request.getValue(), response.getValue());

        invoker.shutdown();

    }

    @Test
    public void testRemoteCall() throws InterruptedException, IOException {

        Invoker<TestRequest, TestResponse> invoker = new Invoker<>(new HelloWorldFunction(), TestRequest.class, TestResponse.class);

        String key = "key";
        String value = "world!";


        TestRequest request = new TestRequest();
        request.setKey(key);
        request.setValue(value);


        Thread.sleep(1000);

        Socket clientSocket = new Socket("localhost", 9999);



        log.debug("Socket connected: {}", clientSocket.isConnected());



        OutputStream os = clientSocket.getOutputStream();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(os, new InvokerRequest<>(request, new InvokerRequest.SerializedRequestContext() {
            {
                this.setParameters(Collections.emptyMap());
                this.setRequestId(UUID.randomUUID().toString());
            }

        }));

        os.flush();
        os.close();

        //InvokerResponse response = mapper.readValue(clientSocket.getInputStream(), InvokerResponse.class);

        clientSocket.close();
        invoker.shutdown();

    }



    public static class TestObject {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestObject that = (TestObject) o;

            if (key != null ? !key.equals(that.key) : that.key != null) return false;
            return value != null ? value.equals(that.value) : that.value == null;

        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "TestObject{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static final class TestRequest extends TestObject {}

    public static final class TestResponse extends TestObject {}

}
