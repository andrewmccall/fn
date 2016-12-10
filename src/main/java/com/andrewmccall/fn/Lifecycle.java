package com.andrewmccall.fn;

/**
 * Interface that provides common methods for managing component lifecycles.
 */
public interface Lifecycle {

    /**
     * Starts this component
     */
    public void start();

    /**
     * Stops this component.
     */
    public void stop();

}
