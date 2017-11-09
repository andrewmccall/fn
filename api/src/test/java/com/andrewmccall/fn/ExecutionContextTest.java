package com.andrewmccall.fn;

import com.andrewmccall.fn.api.ExecutionContext;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExecutionContextTest {

    @Test
    public void testDefaultMethods() {


        ExecutionContext testCtx = new ExecutionContext() {
            @Override
            public String getApplicationId() {
                return null;
            }

            @Override
            public String getFunctionVersion() {
                return null;
            }
        };

        assertTrue("The properties of the default value should be empty.", testCtx.getProperties().isEmpty());
        assertTrue("The test value should not be in the property..", testCtx.getProperty("test") == null);

        testCtx = new ExecutionContext() {

            Map<String, String> map = new HashMap<String, String>() {{
                put("test", "value");
            }};

            @Override
            public String getApplicationId() {
                return null;
            }

            @Override
            public String getFunctionVersion() {
                return null;
            }

            @Override
            public Map<String, String> getProperties() {
                return map;
            }
        };

        assertFalse("The properties of the default value should not be empty.", testCtx.getProperties().isEmpty());
        assertEquals("The property 'test' shoudl return value 'value'", "value", testCtx.getProperty("test"));


    }
}
