package com.andrewmccall.fn.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

/**
 * The RequestContext holds additional context information surrouding a request.
 */
@Value.Immutable
@JsonDeserialize(builder=ImmutableRequestContext.Builder.class)
public interface RequestContext extends Serializable {

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
    Optional<ExecutionContext> getExecutionContext();

}
