package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.ImmutableRequestContext;
import com.andrewmccall.fn.config.ConfigurationProvider;
import com.andrewmccall.fn.config.LocalConfigurationProvider;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TestEchoFunctionRequestHandler {

    private final ConfigurationProvider configurationProvider = new LocalConfigurationProvider();

    @Test
    public void testEchoAgent() {
        InvokerRequestHandler<String, String> echoHandler = new InvokerRequestHandler<>(new EchoFunction());
        String test = "Hello world!";

        InvokerRequest<String> request = new InvokerRequest<>(test, ImmutableRequestContext.builder().requestId(UUID.randomUUID().toString()).parameters(Collections.emptyMap()).build());

        assertEquals(test, echoHandler.execute(request).getPayload());

    }

}
