package com.andrewmccall.fn.invoker;

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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SerializedRequestContext{");
        sb.append("executionContext=").append(executionContext);
        sb.append(", requestId='").append(requestId).append('\'');
        sb.append(", parameters=").append(parameters);
        sb.append('}');
        return sb.toString();
    }
}
