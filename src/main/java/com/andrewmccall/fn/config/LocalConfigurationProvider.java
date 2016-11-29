package com.andrewmccall.fn.config;

import com.andrewmccall.fn.discovery.LocalRegistry;

/**
 * Created by andrewmccall on 24/11/2016.
 */
public class LocalConfigurationProvider implements ConfigurationProvider {


    private static final ClusterConfig clusterConfig;

    static {
        clusterConfig = ImmutableClusterConfig.builder().serviceRegistry(new LocalRegistry()).build();
    }


    @Override
    public ClusterConfig getClusterConfig() {
        return clusterConfig;
    }
}
