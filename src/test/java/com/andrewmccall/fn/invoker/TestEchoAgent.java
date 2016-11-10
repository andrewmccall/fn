package com.andrewmccall.fn.invoker;

import org.junit.Test;

import java.util.Collections;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TestEchoAgent {

    @Test
    public void testEchoAgent() {
        Invoker<String, String> echoInvoker = new Invoker<>(new EchoFunction(), String.class, String.class);
        String test = "Hello world!";

        InvokerRequest<String> request = new InvokerRequest<>(test, new SerializedRequestContext() {
            {
                this.setParameters(Collections.emptyMap());
                this.setRequestId(UUID.randomUUID().toString());
            }

        });

        assertEquals(test, echoInvoker.execute(request).getPayload());

        echoInvoker.shutdown();
    }

}
