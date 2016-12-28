package com.andrewmccall.fn.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;

/**
 * Created by andrewmccall on 29/11/2016.
 */
public class JacksonConfig {

    private static ObjectMapper objectMapper;

    private static Object lock = new Object();

    public static ObjectMapper getObjectMapper() {

        synchronized (lock) {
            initMapper();
        }
        return objectMapper;
    }

    private static void initMapper() {
        if (objectMapper != null) return;
        objectMapper = new ObjectMapper(
                new JsonFactory()
                        .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)

        );
        objectMapper.registerModule(new Jdk8Module());
       // objectMapper.registerModule(new JSR353Module());
    }

}
