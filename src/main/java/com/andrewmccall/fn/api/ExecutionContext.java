package com.andrewmccall.fn.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

/**
 * An ExecutionContext is passed during execution and defines values set at the Function level.
 */
@Value.Immutable
@JsonDeserialize(builder=ImmutableExecutionContext.Builder.class)
public interface ExecutionContext extends Serializable {

    /**
     * Gets the applicationId for this function. Functions are deployed with an ApplicationID new versions will use the
     * same applicaiton Id.
     * @return the application ID.
     */
    String getApplicationId();

    /**
     * Gets the version of the function, deploying new code or changing the properties of an ExecutionContext will
     * alter the version of the function.
     *
     * @return the version of this function
     */
    String getFunctionVersion();

    /**
     * Gets a single property by key. Properties are user-defined values that may be configured when the Function is
     * created. ExecutionContext properties are defined at the Function level and are passed to all executions of a
     * Function.
     * @param property the property name
     * @return the property value
     */
    default String getProperty(String property)  {
        return getProperties().get(property);
    }

    /**
     * Gets the full property set. Properties are user-defined values that may be configured when the Function is
     * created. ExecutionContext properties are defined at the Function level and are passed to all executions of a
     * Function.
     * @return all properties
     */
    default Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

    //ExecutorRegistry getExecutionRegistry();

}
