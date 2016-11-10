package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.Function;
import com.andrewmccall.fn.api.RequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A sample Function that echos the received String.
 *
 * @author andrewmccall, @date 8/22/16 9:05 AM
 */
public class EchoFunction implements Function<String,String> {


    private static final Logger log = LogManager.getLogger(EchoFunction.class.getName());

    @Override
    public String execute(String in, RequestContext context) {

        log.warn("Echo function called with input {}", in);

        return in;
    }
}
