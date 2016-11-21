package com.andrewmccall.fn.discovery.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;

/**
 * Created by andrewmccall on 14/11/2016.
 */
public final class CuratorHelper {

    private static final CuratorFramework curator;

    static {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        builder.connectString("localhost");
        builder.namespace("/com/andrewmccall/fn/");
        curator = builder.build();
    }

    public static CuratorFramework getCuratorFramework() {
        return curator;
    }
}
