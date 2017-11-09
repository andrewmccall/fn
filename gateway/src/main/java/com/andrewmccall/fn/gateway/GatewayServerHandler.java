package com.andrewmccall.fn.gateway;

import com.andrewmccall.fn.api.ImmutableRequestContext;
import com.andrewmccall.fn.config.ConfigurationProvider;
import com.andrewmccall.fn.discovery.ServiceInstance;
import com.andrewmccall.fn.invoker.InvokerRequest;
import com.andrewmccall.fn.invoker.InvokerResponse;
import com.andrewmccall.fn.json.JacksonConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.JsonObject;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.UUID;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by andrewmccall on 09/12/2016.
 */
public class GatewayServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LogManager.getLogger(GatewayServerHandler.class);

    private static final ObjectMapper objectMapper = JacksonConfig.getObjectMapper();

    private static final PathParser parser = new PathParser();

    private final ConfigurationProvider configurationProvider;

    public GatewayServerHandler(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {


        ImmutableRequestContext.Builder builder = ImmutableRequestContext.builder();

        HttpRequest request = null;

        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;

            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }


            //builder.parameters(Collections.unmodifiableMap(request.headers()));


            String functionId =parser.extractFunctionId(request.uri());
            log.warn("FunctionId is: {}", functionId);

        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            JsonObject obj = objectMapper.readValue((InputStream) new ByteBufInputStream(content), JsonObject.class);

            // find the Execution context to get us the Invoker.
            ServiceInstance serviceInstance = configurationProvider.getClusterConfig().getServiceRegistry().getServiceInstance("test");

            // get the Invoker and open a connection.
            String host = serviceInstance.getHost();
            int port = serviceInstance.getPort();

            Socket clientSocket = new Socket(host, port);


            log.debug("Socket connected: {}", clientSocket.isConnected());


            OutputStream os = clientSocket.getOutputStream();
            InputStream is = clientSocket.getInputStream();


            InvokerRequest<JsonObject> invokerRequest = new InvokerRequest<>(obj, ImmutableRequestContext.builder().requestId(UUID.randomUUID().toString()).parameters(Collections.emptyMap()).build());

            objectMapper.writeValue(os, invokerRequest);

            log.trace("Sent request, calling flush.");
            os.flush();

            InvokerResponse invokerResponse = objectMapper.readValue(is, objectMapper.getTypeFactory().constructParametrizedType(InvokerResponse.class, InvokerResponse.class, JsonObject.class));

            log.info("Response from invoker was {}", invokerResponse);


            clientSocket.close();




                /*
                boolean keepAlive = false;
                if (request != null)
                keepAlive = HttpUtil.isKeepAlive(request);
                // Build the response object.
                 */

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HTTP_1_1, httpContent.decoderResult().isSuccess() ? OK : BAD_REQUEST,
                    content);


            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                /*
                if (keepAlive) {
                    // Add 'Content-Length' header only for a keep-alive connection.
                    response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                    // Add keep alive header as per:
                    // - http://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01.html#Connection
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }
                */

            // Write the response.
            ctx.write(response);

                /*
                if (!keepAlive) {
                    // If keep-alive is off, close the connection once the content is fully written.
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
                */

        }
    }


    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


}