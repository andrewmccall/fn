package com.andrewmccall.fn.api;

import java.util.Collections;
import java.util.Map;

/**
 * An ExecutionContext is passed to a function on startup allowing properties to be passed to a function.
 */
public interface ExecutionContext {

    String getApplicationId();

    String getFunctionVersion();

    /**
     * Properties are user-defined values that may be configured on the API Gateway.
     * @param property
     * @return
     */
    default String getProperty(String property)  {
        return getProperties().get(property);
    }

    default Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    //ExecutorRegistry getExecutionRegistry();

}
