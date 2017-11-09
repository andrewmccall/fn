package com.andrewmccall.fn.controller;

import com.andrewmccall.fn.api.ExecutionContext;

/**
 * Parent interface for functions that are deployed within the cluster.
 */
public interface FunctionDescriptor {

    State getState();

    ExecutionContext getExecutionContext();

    String getExecutableUrl();

}
