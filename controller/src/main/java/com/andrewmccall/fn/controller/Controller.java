package com.andrewmccall.fn.controller;

import com.andrewmccall.fn.api.ExecutionContext;

import java.util.Collection;


/**
 * A Controller runs the cluster, managing the starting and stopping of Functions.
 */
public interface Controller {

    /**
     * Registers a new Function.
     * @param context The ExecutionContext of the new Function
     * @param executable the path to the executable.
     */
    void register(FunctionDescriptor executable) ;

    Collection<String> getFunctions();

    Collection<FunctionDescriptor> getDescriptors(String applicationId);

    void remove(ExecutionContext context);

}
