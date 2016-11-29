package com.andrewmccall.fn.config;

import java.io.Serializable;

/**
 * Created by andrewmccall on 24/11/2016.
 */
public interface ConfigurationProvider extends Serializable {

    public ClusterConfig getClusterConfig();
}
