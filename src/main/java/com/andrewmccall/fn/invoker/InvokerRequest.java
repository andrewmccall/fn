package com.andrewmccall.fn.invoker;

import com.amazonaws.Request;
import com.andrewmccall.fn.api.ExecutionContext;
import com.andrewmccall.fn.api.RequestContext;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Map;

public class InvokerRequest<T> {

    private T payload;
    private RequestContext context;

    @JsonCreator
    public InvokerRequest(@JsonProperty("payload") T result, @JsonProperty("context") SerializedRequestContext context) {
        this.payload = result;
        this.context = context;
    }
    public T getPayload() {
        return payload;
    }

    public RequestContext getContext() {
        return context;
    }


}
