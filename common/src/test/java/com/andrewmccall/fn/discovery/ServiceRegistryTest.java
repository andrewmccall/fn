package com.andrewmccall.fn.discovery;

import com.andrewmccall.fn.api.ExecutionContext;
import com.andrewmccall.fn.api.ImmutableExecutionContext;
import org.junit.Test;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Provides tests of the default methods in the ServiceRegistry class.
 */
public class ServiceRegistryTest {

    private static int id = 0;

    /**
     * This method tests a method that uses random IDs.
     */
    @Test
    public void testGetServiceInstance() throws SocketException {

        Collection<ServiceInstance> instances = new ArrayList<>();

        ServiceRegistry reg = new ServiceRegistry() {
            @Override
            public void register(ServiceInstance serviceInstance) {
            }

            @Override
            public Collection<ServiceInstance> getServiceInstances(String serviceId) {
                return instances;
            }

            @Override
            public ServiceInstance getServiceInstance(String serviceId, String instanceId) {
                return null;
            }
        };

        // initially there are no instances. We should return null.
        assertTrue("No instances should return null", reg.getServiceInstance("anything") == null);

        ServiceInstance instance = createTestServiceInstance();
        instances.add(instance);

        // when there is one it should always return the signle instance.
        for (int i = 0; i < 10; i++)
            assertEquals(instance, reg.getServiceInstance("anything"));

        // add a couple more.
        instances.add(createTestServiceInstance());
        instances.add(createTestServiceInstance());

        // we should always return one of the instances - but we're never sure which.
        for (int i = 0; i < 10; i++)
            assertTrue(instances.contains(reg.getServiceInstance("anythingelse")));
    }

    private ServiceInstance createTestServiceInstance() throws SocketException {
        ServiceInstance si = new ServiceInstance();
        si.setInstanceId("INSTANCE-" + id++);
        ExecutionContext ctx = ImmutableExecutionContext.builder()
                .applicationId(this.getClass().getName())
                .functionVersion("1.0.0")
                .build();
        si.setExecutionContext(ctx);
        return si;
    }

}
