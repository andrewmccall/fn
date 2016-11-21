package com.andrewmccall.fn.discovery.zk;

import com.andrewmccall.fn.api.ExecutionContext;
import com.andrewmccall.fn.discovery.ServiceInstance;
import com.andrewmccall.fn.discovery.ServiceRegistry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Lightweight wrapper around the Curator service discover extension.
 */
public class CuratorServiceRegistry implements ServiceRegistry{


    private ServiceDiscovery<ExecutionContext> discovery =
            ServiceDiscoveryBuilder.builder(ExecutionContext.class)
                    .basePath("functions")
                    .client(CuratorHelper.getCuratorFramework()).serializer(new JsonInstanceSerializer<>(ExecutionContext.class))
                    .build();

    @Override
    public void register(ServiceInstance serviceInstance) {
        try {
            discovery.registerService(toCuratorServiceInstance(serviceInstance));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<ServiceInstance> getServiceInstances(String serviceId) {
        try {

            return discovery.queryForInstances(serviceId).stream()
                    .map(CuratorServiceRegistry::fromCuratorServiceIntance).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public ServiceInstance getServiceInstance(String serviceId, String instanceId) {
        try {
            return fromCuratorServiceIntance(discovery.queryForInstance(serviceId, instanceId));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static org.apache.curator.x.discovery.ServiceInstance<ExecutionContext> toCuratorServiceInstance(ServiceInstance serviceInstance) {

        try {
            return org.apache.curator.x.discovery.ServiceInstance.<ExecutionContext>builder()
                    .name(serviceInstance.getExecutionContext().getApplicationId())
                    .id(serviceInstance.getInstanceId())
                    .registrationTimeUTC(serviceInstance.getRegistrationDate().getTime())
                    .port(serviceInstance.getPort())
                    .payload(serviceInstance.getExecutionContext())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    private static ServiceInstance fromCuratorServiceIntance(org.apache.curator.x.discovery.ServiceInstance<ExecutionContext> curator) {

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setHost(curator.getAddress());
        serviceInstance.setPort(curator.getPort());
        serviceInstance.setInstanceId(curator.getId());
        serviceInstance.setExecutionContext(curator.getPayload());
        serviceInstance.setRegistrationDate(new Date(curator.getRegistrationTimeUTC()));
        return serviceInstance;
    }
}
