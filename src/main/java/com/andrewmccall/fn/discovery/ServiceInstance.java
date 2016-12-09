package com.andrewmccall.fn.discovery;

import com.andrewmccall.fn.api.ExecutionContext;

import java.util.Date;

/**
 * A ServiceInstance holds the content of something registered in the Service discovery framework a Leases are stored as
 * hirearchical key values.
 */
public class ServiceInstance {

    public enum Status {
        REQUESTED,
        STARTING,
        STARTUP_FAILED,
        RUNNING,
        SHUTTING_DOWN,
        STOPPED
    }

    /**
     * The key for this ServiceInstance
     */
    String instanceId;

    String host;

    int port;
    /**
     * The Date this ServiceInstance was registered.
     */
    Date registrationDate;

    ExecutionContext executionContext;

    Status status;

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public void setExecutionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServiceInstance{");
        sb.append("instanceId='").append(instanceId).append('\'');
        sb.append(", host='").append(host).append('\'');
        sb.append(", port=").append(port);
        sb.append(", registrationDate=").append(registrationDate);
        sb.append(", executionContext=").append(executionContext);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
