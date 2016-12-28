package com.andrewmccall.fn.gateway;

import com.andrewmccall.fn.config.ConfigurationProvider;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Created by andrewmccall on 09/12/2016.
 */
public class GatewayInitializer extends ChannelInitializer<SocketChannel> {

    private final ConfigurationProvider configurationProvider;

    public GatewayInitializer(ConfigurationProvider configurationProvider) {
        this.configurationProvider = configurationProvider;
    }


    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();

        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseEncoder());
        // Remove the following line if you don't want automatic content compression.
        //p.addLast(new HttpContentCompressor());
        p.addLast(new GatewayServerHandler(configurationProvider));
    }
}
