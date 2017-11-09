package com.andrewmccall.fn.discovery.standalone;

import com.andrewmccall.fn.discovery.ServiceInstance;
import com.andrewmccall.fn.discovery.ServiceRegistry;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;


/**
 * Created by andrewmccall on 28/12/2016.
 */
public class StandalonRegistryClient implements ServiceRegistry {

    private final ResponseHandler handler = new ResponseHandler();

    private static final InternalClassResolver resolver = new InternalClassResolver();

    private Channel channel;

    public StandalonRegistryClient(String host, int port) {

        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup(1));
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(

                        new ObjectEncoder(),
                        new ObjectDecoder(resolver),
                        handler);

            }
        });

        ChannelFuture f = b.connect(host, port);

        f.awaitUninterruptibly();
        assert f.isDone();

        channel = f.channel();

    }

    @Override
    public void register(ServiceInstance serviceInstance) {
        channel.write(serviceInstance);
    }

    @Override
    public Collection<ServiceInstance> getServiceInstances(String serviceId) {
        GetAll get = new GetAll();
        get.setServiceId(serviceId);
        Future<Collection<ServiceInstance>> future = sendGetAll(get);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ServiceInstance getServiceInstance(String serviceId, String instanceId) {
        Get get = new Get();
        get.setServiceId(serviceId);
        get.setInstanceId(instanceId);
        Future<ServiceInstance> future = sendGet(get);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    private CompletableFuture<Collection<ServiceInstance>> sendGetAll(GetAll getAll) {
        final CompletableFuture<Collection<ServiceInstance>> future = new CompletableFuture<>();
        handler.registerCallback(getAll.hashCode(), (value) -> {

            future.complete((Collection<ServiceInstance>) value);

        });
        channel.write(getAll);
        return future;
    }

    private CompletableFuture<ServiceInstance> sendGet(Get get) {
        final CompletableFuture<ServiceInstance> future = new CompletableFuture<>();
        handler.registerCallback(get.hashCode(), (value) -> {

            future.complete((ServiceInstance) value);

        });
        channel.write(get);
        return future;
    }

    private class ResponseHandler extends ChannelInboundHandlerAdapter {

        Map<Object, Consumer> consumerMap = new HashMap();

        public void registerCallback(Object request, Consumer c) {
            consumerMap.put(request, c);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            if (msg instanceof Response) {
                Response response = (Response) msg;
                if (consumerMap.containsKey(response.getRequest())) {
                    consumerMap.get(response.getRequest()).accept(msg);
                    consumerMap.remove(response.getRequest());
                }
            }
        }
    }

}
