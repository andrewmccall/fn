package com.andrewmccall.fn.config;

import com.andrewmccall.fn.discovery.LocalRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by andrewmccall on 24/11/2016.
 */
public class LocalConfigurationProvider implements ConfigurationProvider {

    private static final Logger log = LogManager.getLogger(LocalConfigurationProvider.class);
    private static final ClusterConfig clusterConfig;

    static {
        clusterConfig = ImmutableClusterConfig.builder().serviceRegistry(new LocalRegistry()).build();
    }


    @Override
    public ClusterConfig getClusterConfig() {

        log.debug("Requested clusterConfig, returning {}", clusterConfig);
        return clusterConfig;
    }
}
