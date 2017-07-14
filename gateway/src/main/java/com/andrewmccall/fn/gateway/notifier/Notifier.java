package com.andrewmccall.fn.gateway.notifier;

import com.andrewmccall.fn.gateway.GatewayDescriptor;

/**
 * A Notifier is called when a Gateway starts. This allows dynamic starting of gateways and notification of external
 * systems eg. Firewalls/Load balancers when a new one is available.
 */
public interface Notifier {

    void notifyStart(GatewayDescriptor descriptor);

    void notifyStop(GatewayDescriptor descriptor);

}
