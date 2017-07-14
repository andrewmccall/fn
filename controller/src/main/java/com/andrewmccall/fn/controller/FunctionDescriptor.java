package com.andrewmccall.fn.controller;

import com.andrewmccall.fn.api.ExecutionContext;
import org.immutables.value.Value;

/**
 * Parent interface for functions that are deployed within the cluster.
 */
public interface FunctionDescriptor {

    State getState();

    ExecutionContext getExecutionContext();

    String getExecutableUrl();

}
