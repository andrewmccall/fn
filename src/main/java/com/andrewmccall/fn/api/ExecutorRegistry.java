package com.andrewmccall.fn.api;

import java.net.InetAddress;
import java.util.Collection;

/**
 * Provides access to reExecutors
 */
public interface ExecutorRegistry {

    /**
     * gets the most current version of a Endpoint.
     *
     * @param functionId the ID of a function
     * @return
     */
    Endpoint getRegisteredFunction(String functionId);

    void registerEndpoint(ExecutionContext executionContext, InetAddress host, int port);

    void removeEndpoint(ExecutionContext executionContext, InetAddress host, int port);

    Collection<Endpoint> getEndpoint(String functionId);

    interface Endpoint {

        ExecutionContext getExecutionContext();

        /**
         * Returns a collection of Hosts where a function is executing.
         * @return
         */
        Collection<Host> getHosts();

        interface Host {
            String getAddress();
            int getPort();
        }

    }

}
