package com.andrewmccall.fn.discovery;

import com.andrewmccall.fn.config.ClusterConfig;
import com.google.common.collect.Lists;
import org.apache.curator.x.discovery.LocalIpFilter;
import org.apache.helix.api.Cluster;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The ServiceRegistry provides an abstraction layer providing service discovery of Functions and cluster services.
 */
public interface ServiceRegistry {

    /**
     * Registers a new ServiceInstance.
     *
     * @param serviceInstance the ServiceInstance to register
     */
    void register(ServiceInstance serviceInstance);

    /**
     * Gets a ServiceInstance implementations are free to determine which instance to send. The default implmentation
     * send a random instance.
     *
     * @param serviceId
     * @return
     */
    default ServiceInstance getServiceInstance(String serviceId) {
        Collection<ServiceInstance> instances = getServiceInstances(serviceId);
        if (instances.isEmpty()) return null;
        if (instances.size() == 1) return instances.iterator().next();
        int index = ThreadLocalRandom.current().nextInt(instances.size());
        Iterator<ServiceInstance> it = instances.iterator();
        for (int i = 0 ; i <= index; i ++) {
            if (i == index) return it.next();
            it.next();
        }
        return null;
    }

    /**
     * Gets a Collection of Leases for a serviceId.
     *
     * @param serviceId the Id of the service.
     * @return the Collection if ServiceInstance objects for the given service Id.
     */
    Collection<ServiceInstance> getServiceInstances(String serviceId);

    /**
     * Gets the instance of a ServiceInstance for a given service and instance Id.
     *
     * @param serviceId  the service Id.
     * @param instanceId the instance Id.
     * @return the ServiceInstance if it exists, null if none can be found.
     */
    ServiceInstance getServiceInstance(String serviceId, String instanceId);

    /**
     * Helper method that returns a host.
     *
     * @return
     */
    static String getFirstHost() throws SocketException {
        return getAllLocalIPs().iterator().next().getHostAddress();
    }


    /**
     * based on http://pastebin.com/5X073pUc & org.apache.curator.x.discovery.ServiceInstanceBuilder.
     * <p>
     * <p>
     * Returns all available IP addresses.
     * <p>
     * In error case or if no network connection is established, we return
     * an empty list here.
     * <p>
     * Loopback addresses are excluded - so 127.0.0.1 will not be never
     * returned.
     * <p>
     * The "primary" IP might not be the first one in the returned list.
     *
     * @return Returns all IP addresses (can be an empty list in error case
     * or if network connection is missing).
     * @throws SocketException errors
     * @since 0.1.0
     */
    static Collection<InetAddress> getAllLocalIPs() throws SocketException {
        List<InetAddress> listAdr = Lists.newArrayList();
        Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
        if (nifs == null) return listAdr;

        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();
            // We ignore subinterfaces - as not yet needed.

            Enumeration<InetAddress> adrs = nif.getInetAddresses();
            while (adrs.hasMoreElements()) {
                InetAddress adr = adrs.nextElement();
                if ((adr != null) && !adr.isLoopbackAddress() && (nif.isPointToPoint() || !adr.isLinkLocalAddress())) {
                    listAdr.add(adr);
                }
            }
        }
        return listAdr;
    }

}