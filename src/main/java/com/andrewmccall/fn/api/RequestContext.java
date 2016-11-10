package com.andrewmccall.fn.api;

import java.util.Map;

/**
 * The RequestContext holds additional context information surrouding a request.
 */
public interface RequestContext {

    /**
     * Each request is provided with a unique ID.
     * @return
     */
    String getRequestId();

    /**
     * Map that holds parameters specific to this RequestContext. Eg. HTTP headers.
     * returns a map
     */
    Map<String, String> getParameters();

    /**
     * Returned the current ExectionContext for this function.
     * @return
     */
    ExecutionContext getExecutionContext();

}
