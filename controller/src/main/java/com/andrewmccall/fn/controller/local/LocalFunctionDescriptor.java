package com.andrewmccall.fn.controller.local;

import com.andrewmccall.fn.controller.FunctionDescriptor;
import org.immutables.value.Value;

/**
 * Created by andrewmccall on 25/03/2017.
 */
@Value.Immutable
public interface LocalFunctionDescriptor extends FunctionDescriptor {

    /**
     * gets the Running process for this Function.
     * @return the Process that is running the Function.
     */
    Process getProcess();

}
