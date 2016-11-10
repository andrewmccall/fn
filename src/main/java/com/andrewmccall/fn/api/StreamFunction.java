package com.andrewmccall.fn.api;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by andrewmccall on 30/08/2016.
 */
public interface StreamFunction {

    void execute(InputStream is, OutputStream os, RequestContext context);
}
