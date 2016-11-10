package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.Function;
import com.andrewmccall.fn.api.RequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by andrewmccall on 24/10/2016.
 */
public class HelloWorldFunction implements Function<TestHelloWorld.TestRequest, TestHelloWorld.TestResponse> {

    private static final Logger log = LogManager.getLogger(HelloWorldFunction.class);

    @Override
    public TestHelloWorld.TestResponse execute(TestHelloWorld.TestRequest in, RequestContext context) {

        log.info("Request {}", in);


        TestHelloWorld.TestResponse response = new TestHelloWorld.TestResponse();
        response.setKey(in.getKey());
        response.setValue("Hello " + in.getValue());

        log.info("Response {}", response);
        return response;
    }

}
