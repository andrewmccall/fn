package com.andrewmccall.fn.discovery.standalone;

/**
 * Created by andrewmccall on 28/12/2016.
 */
public class GetAll {

    String serviceId;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetAll getAll = (GetAll) o;

        return serviceId != null ? serviceId.equals(getAll.serviceId) : getAll.serviceId == null;
    }

    @Override
    public int hashCode() {
        return serviceId != null ? serviceId.hashCode() : 0;
    }
}
