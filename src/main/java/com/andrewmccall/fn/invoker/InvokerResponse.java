package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.RequestContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvokerResponse<T> {


    private T payload;
    private RequestContext context;

    @JsonCreator
    public InvokerResponse(@JsonProperty("payload") T result, @JsonProperty("context") SerializedRequestContext context) {
        this.payload = result;
        this.context = context;
    }

    public InvokerResponse (T result, RequestContext context) {
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
        return "InvokerResponse{" +
                "payload=" + payload +
                ", context=" + context +
                '}';
    }
}
