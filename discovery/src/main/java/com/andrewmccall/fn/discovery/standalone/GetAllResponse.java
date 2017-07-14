package com.andrewmccall.fn.discovery.standalone;

import com.andrewmccall.fn.discovery.ServiceInstance;

import java.util.ArrayList;

/**
 * Created by andrewmccall on 28/12/2016.
 */
public class GetAllResponse implements Response<GetAll> {

    GetAll request;
    ArrayList<ServiceInstance> instances;

    public GetAllResponse() {
    }

    public GetAllResponse(GetAll request, ArrayList<ServiceInstance> instances) {
        this.request = request;
        this.instances = instances;
    }

    @Override
    public GetAll getRequest() {
        return request;
    }

    public void setRequest(GetAll request) {
        this.request = request;
    }

    public ArrayList<ServiceInstance> getInstances() {
        return instances;
    }

    public void setInstances(ArrayList<ServiceInstance> instances) {
        this.instances = instances;
    }
}
