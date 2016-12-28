package com.andrewmccall.fn.api;

import java.io.Serializable;

/*
 * Functions are the main unit of execution for the serveless systems. A function must be stateless and
 *
 * @author andrewmccall, @date 20/07/16 20:28
 */
public interface Function<I, O> extends Serializable {

    O execute(I in, RequestContext context );

}