package com.andrewmccall.fn;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Interface that provides common methods for managing component lifecycles.
 */
public interface Lifecycle {


    public static final Marker STARTUP = MarkerManager.getMarker("STARTUP");
    public static final Marker SHUTDOWN = MarkerManager.getMarker("SHUTDOWN");

    /**
     * Starts this component
     */
    public void start();

    /**
     * Stops this component.
     */
    public void stop();

}
