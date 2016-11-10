package com.andrewmccall.fn.invoker;

/**
 * Created by andrewmccall on 10/11/2016.
 */

import com.andrewmccall.fn.api.ExecutionContext;
import com.andrewmccall.fn.api.RequestContext;

import java.io.Serializable;
import java.util.Map;

public class SerializedRequestContext implements RequestContext, Serializable {

    private transient ExecutionContext executionContext;

    private String requestId;
    private Map<String, String> parameters;

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public void setExecutionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
