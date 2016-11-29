package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.RequestContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvokerRequest<T> {

    private T payload;
    private RequestContext context;

    @JsonCreator
    public InvokerRequest(@JsonProperty("payload") T result, @JsonProperty("context") RequestContext context) {
        this.payload = result;
        this.context = context;
    }
    public T getPayload() {
        return payload;
    }

    public RequestContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "InvokerRequest{" +
                "payload=" + payload +
                ", context=" + context +
                '}';
    }
}
