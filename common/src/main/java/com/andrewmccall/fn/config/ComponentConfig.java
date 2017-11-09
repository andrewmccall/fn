package com.andrewmccall.fn.config;

/**
 * Abstract class that provides the component config.
 */
public interface ComponentConfig {

    String getComponentKey();

    /**
     * Gets the classname for this component.
     * @return
     */
    String getClassname();

    /**
     * Gets the port this component is running on.
     * @return
     */
    int getPort();

}
