package com.andrewmccall.fn.discovery.standalone;

import com.andrewmccall.fn.discovery.ServiceInstance;

/**
 * Created by andrewmccall on 28/12/2016.
 */
public class GetResponse implements Response<Get> {

    private Get request;
    private ServiceInstance instance;

    public GetResponse() {
    }

    public GetResponse(Get request, ServiceInstance instance) {
        this.request = request;
        this.instance = instance;
    }

    @Override
    public Get getRequest() {
        return request;
    }

    public void setRequest(Get request) {
        this.request = request;
    }

    public ServiceInstance getInstance() {
        return instance;
    }

    public void setInstance(ServiceInstance instance) {
        this.instance = instance;
    }
}
