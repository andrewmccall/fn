package com.andrewmccall.fn.invoker;

import com.andrewmccall.fn.api.Function;
import com.andrewmccall.fn.api.RequestContext;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by andrewmccall on 24/10/2016.
 */
public class HelloWorldFunction implements Function<HelloWorldFunction.TestRequest, HelloWorldFunction.TestResponse> {

    private static final Logger log = LogManager.getLogger(HelloWorldFunction.class);

    @Override
    public TestResponse execute(TestRequest in, RequestContext context) {

        log.info("Request {}", in);


        TestResponse response = new TestResponse();
        response.setKey(in.getKey());
        response.setValue("Hello " + in.getValue());

        log.info("Response {}", response);
        return response;
    }

    public static class TestObject {
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestObject that = (TestObject) o;

            return (key != null ? key.equals(that.key) : that.key == null) && (value != null ? value.equals(that.value) : that.value == null);

        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "TestObject{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static final class TestRequest extends TestObject {

        public TestRequest() {

        }

        public TestRequest(String key, String value) {
            this.setKey(key);
            this.setValue(value);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .appendSuper(super.toString())
                    .toString();
        }

    }

    public static final class TestResponse extends TestObject {

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .appendSuper(super.toString())
                    .toString();
        }
    }

}
