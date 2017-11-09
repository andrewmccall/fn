package com.andrewmccall.fn.discovery.standalone;

import io.netty.handler.codec.serialization.ClassResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andrewmccall on 28/12/2016.
 */
class InternalClassResolver implements ClassResolver {

    private final Map<String, Class> cache = new HashMap<>();

    @Override
    public Class<?> resolve(String className) throws ClassNotFoundException {
        return cache.containsKey(className) ? cache.get(className) : loadClass(className);
    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        Class<?> clazz = this.getClass().getClassLoader().loadClass(className);
        cache.put(className, clazz);
        return clazz;
    }
}
