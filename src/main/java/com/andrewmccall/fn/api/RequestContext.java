package com.andrewmccall.fn.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Map;

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
    @JsonIgnore
    ExecutionContext getExecutionContext();

}
