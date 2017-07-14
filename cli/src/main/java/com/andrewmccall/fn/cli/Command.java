package com.andrewmccall.fn.cli;

import com.andrewmccall.fn.api.ExecutionContext;

import javax.imageio.spi.ServiceRegistry;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class Command {

    public ServiceRegistry serviceRegistry;


    /**
     *
     * @param context
     * @param executable
     */
    public void register(ExecutionContext context, Path executable) {
        // registration steps.
        // * Find the correct cluster config.
        // * Load up the ServiceRegistry.
        // * Upload the code.
        // * Create the function in service discovery
        // * trigger the underlying resource scheduler to start the function.
    }

    /**
     * Returns a list of functions currently running in the cluster.
     * @return
     */
    public List<String> getFunctions() {
        return null;
    }

    public Descriptor getDescriptor(String applicationId) {
        return null;
    }


    public void remove (String pplicationId) {

    }


    /**
     * A Descriptor describes the current state of an application.
     */
    public static class Descriptor {

        String applicationId;

        Collection<VersionDescriptor> versions;

        String uri;
    }

    public static class VersionDescriptor {

    }
}
