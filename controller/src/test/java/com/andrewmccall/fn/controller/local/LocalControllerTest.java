package com.andrewmccall.fn.controller.local;

import com.andrewmccall.fn.api.ExecutionContext;
import com.andrewmccall.fn.api.ImmutableExecutionContext;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by andrewmccall on 16/04/2017.
 */
public class LocalControllerTest {


    @Test
    public void testRegister() {

        ExecutionContext ctx = null;
        Path executable = Paths.get("");

        ImmutableLocalFunctionDescriptor.Builder builder = ImmutableLocalFunctionDescriptor.builder();
        builder.executableUrl(executable.normalize().toString());

        ImmutableExecutionContext.Builder ctxBuilder =  ImmutableExecutionContext.builder();
        ctxBuilder.applicationId("TestApplication");
        ctxBuilder.functionVersion("a");

        LocalController lc = new LocalController();

        //FunctionDescriptor fd = ImmutableLocalFunctionDescriptor.builder().

        //lc.register(ctx, executable);





    }
}
