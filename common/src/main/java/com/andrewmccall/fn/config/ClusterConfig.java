package com.andrewmccall.fn.config;

import com.andrewmccall.fn.discovery.ServiceRegistry;
import org.immutables.value.Value;

import java.io.Serializable;

/**
 * Provides access to the cluster config.
 */
@Value.Immutable
public interface ClusterConfig extends Serializable {

    ServiceRegistry getServiceRegistry();

}
