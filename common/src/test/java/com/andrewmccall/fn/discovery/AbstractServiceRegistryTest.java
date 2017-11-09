package com.andrewmccall.fn.discovery;

/**
 * All ServiceRegistries are more or less going to have the same core methods that need testing. This class provides
 * tests for all the interface methods.
 */
public class AbstractServiceRegistryTest {

    private final ServiceRegistry registry;

    protected AbstractServiceRegistryTest(ServiceRegistry registry) {
        this.registry = registry;
    }

}
