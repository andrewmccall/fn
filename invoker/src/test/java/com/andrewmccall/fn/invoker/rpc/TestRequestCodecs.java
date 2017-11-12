package com.andrewmccall.fn.invoker.rpc;

import com.andrewmccall.fn.api.ImmutableRequestContext;
import com.andrewmccall.fn.invoker.HelloWorldFunction;
import com.andrewmccall.fn.invoker.InvokerRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;

/**
 * Created by andrewmccall on 06/12/2016.
 */
public class TestRequestCodecs {


    @Test
    public void testEncodeDecode() throws Exception {

        TestRequest request = new TestRequest("key", "value");

        InvokerRequest<TestRequest> invokerRequest = new InvokerRequest<>(request, ImmutableRequestContext.builder().requestId(UUID.randomUUID().toString()).parameters(Collections.emptyMap()).build());

        ByteBuf buf = new UnpooledByteBufAllocator(false).buffer();
        InvokerRequestEncoder<TestRequest> encoder = new InvokerRequestEncoder<>();
        encoder.encode(null,invokerRequest, buf);

        InvokerRequestDecoder<TestRequest> decoder = new InvokerRequestDecoder<>(TestRequest.class);

        List<Object> out = new ArrayList<>();
        decoder.decode(null, buf, out);

        assertFalse(out.isEmpty());

        InvokerRequest<TestRequest> result = (InvokerRequest<TestRequest>) out.get(0);


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

    public static final class TestRequest extends HelloWorldFunction.TestObject {

        public TestRequest() {

        }

        public TestRequest(String key, String value) {
            this.setKey(key);
            this.setValue(value);
        }
    }

    public static final class TestResponse extends HelloWorldFunction.TestObject {}

}
