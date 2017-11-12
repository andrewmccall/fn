package com.andrewmccall.fn.discovery.standalone;

import java.io.Serializable;

/**
 * Created by andrewmccall on 28/12/2016.
 */
public class Get implements Serializable{

    private String serviceId;
    private String instanceId;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Get get = (Get) o;

        return (serviceId != null ? serviceId.equals(get.serviceId) : get.serviceId == null) && (instanceId != null ? instanceId.equals(get.instanceId) : get.instanceId == null);
    }

    @Override
    public int hashCode() {
        int result = serviceId != null ? serviceId.hashCode() : 0;
        result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
        return result;
    }
}
