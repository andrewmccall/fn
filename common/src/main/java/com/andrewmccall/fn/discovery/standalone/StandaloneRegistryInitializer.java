package com.andrewmccall.fn.discovery.standalone;

import com.andrewmccall.fn.discovery.LocalRegistry;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Initializes the StandaloneRegistryChannel.
 */
public class StandaloneRegistryInitializer extends ChannelInitializer<SocketChannel> {

    private static final InternalClassResolver resolver = new InternalClassResolver();

    private  final LocalRegistry registry;


    public StandaloneRegistryInitializer(LocalRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(
                new ObjectEncoder(),
                new ObjectDecoder(resolver),
                new PutHandler(registry),
                new GetHandler(registry),
                new GetAllHandler(registry));
    }


}
