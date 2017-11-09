package com.andrewmccall.fn.api;

import java.io.Serializable;

/*
 * Functions are the main unit of execution for the serveless systems. A function must be stateless and
 *
 * @author andrewmccall, @date 20/07/16 20:28
 */
public interface Function<I, O> extends Serializable {

    /**
     * The method all functions implement, providing an input of type T and the RequestContext the function returns an
     * output of type O
     *
     * @param in the input parameter
     * @param context the Request context
     * @return an output value
     */
    O execute(I in, RequestContext context );

}
