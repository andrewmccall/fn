package com.andrewmccall.fn.discovery;

import com.andrewmccall.fn.discovery.ServiceInstance;
import com.andrewmccall.fn.discovery.ServiceRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A local, in memory Registry.
 */
public class LocalRegistry implements ServiceRegistry {

    Map<String, Map<String, ServiceInstance>> leaseMap = new HashMap<>();

    /**
     * Registers or updates a ServiceInstance in this registry.
     *
     * @param serviceInstance the ServiceInstance to register
     */
    @Override
    public void register(ServiceInstance serviceInstance) {

        if (!leaseMap.containsKey(serviceInstance.getExecutionContext().getApplicationId())) {
            leaseMap.put(serviceInstance.getExecutionContext().getApplicationId(), new HashMap<>());
        }
        leaseMap.get(serviceInstance.getExecutionContext().getApplicationId()).put(serviceInstance.getInstanceId(), serviceInstance);
    }

    @Override
    public Collection<ServiceInstance> getServiceInstances(String serviceId) {
        if (leaseMap.containsKey(serviceId))
            return Collections.emptyList();
        return leaseMap.get(serviceId).values();
    }

    /**
     * Gets a ServiceInstance for a service and instance ID.
     * @param serviceId the service Id.
     * @param instanceId the instance Id.
     * @return the ServiceInstance or null if none exists.
     */
    @Override
    public ServiceInstance getServiceInstance(String serviceId, String instanceId) {
        if (!leaseMap.containsKey(serviceId))
            return null;
        return leaseMap.get(serviceId).get(instanceId);
    }

}
