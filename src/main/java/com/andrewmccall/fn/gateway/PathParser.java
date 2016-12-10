package com.andrewmccall.fn.gateway;

import com.google.common.base.Preconditions;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class PathParser {


    String extractFunctionId(String uri) {

        Preconditions.checkNotNull(uri, "Invalid function call without a URI!");

        QueryStringDecoder decoder = new QueryStringDecoder(uri);
        String path = StringUtils.trimToNull(decoder.path());

        Preconditions.checkNotNull(path, "Path may not be null.");

        char[] c = path.toCharArray();
        int index = c[0] == '/' ? 1:0;
        for(int i = index ;i<c.length;i++) {
            if(c[i] == '/') {
                return path.substring(index,i);
            }
        }
        return path.substring(index, path.length());
    }

}
