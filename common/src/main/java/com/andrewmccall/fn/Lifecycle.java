package com.andrewmccall.fn;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Interface that provides common methods for managing component lifecycles.
 */
public interface Lifecycle {


    Marker STARTUP = MarkerManager.getMarker("STARTUP");
    Marker SHUTDOWN = MarkerManager.getMarker("SHUTDOWN");

    /**
     * Starts this component
     */
    default void start() {};

    /**
     * Stops this component.
     */
    default void stop() {};

}
