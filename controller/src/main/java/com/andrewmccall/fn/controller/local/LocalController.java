package com.andrewmccall.fn.controller.local;

import com.andrewmccall.fn.api.ExecutionContext;
import com.andrewmccall.fn.api.Function;
import com.andrewmccall.fn.controller.Controller;
import com.andrewmccall.fn.controller.FunctionDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A Controler implementation that runs locally. Useful for testing not really production.
 */
public class LocalController implements Controller {


    private static final Logger log = LogManager.getLogger(LocalController.class);

    private Map<String, Map<ExecutionContext, LocalFunctionDescriptor>> functions;

    @Override
    public void register(FunctionDescriptor executable) {

        // register the descriptor.


    }

    @Override
    public Collection<String> getFunctions() {
        return null;
    }

    @Override
    public List<FunctionDescriptor> getDescriptors(String applicationId) {
        return null;
    }

    @Override
    public void remove(ExecutionContext context) {

    }




}
