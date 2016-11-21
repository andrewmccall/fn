package com.andrewmccall.fn.api.rpc;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by andrewmccall on 13/11/2016.
 */
public interface JsonCodec {

    ObjectMapper objectMapper = new ObjectMapper(
            new JsonFactory()
                    .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false));

    default ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}
