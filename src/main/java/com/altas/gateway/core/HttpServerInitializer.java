package com.altas.gateway.core;

import com.altas.gateway.loader.GlobalConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

public class HttpServerInitializer  extends ChannelInitializer<SocketChannel> {

    @Override
    public void initChannel(SocketChannel ch) throws CertificateException,SSLException {
        ChannelPipeline p = ch.pipeline();

        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1024 * 1024));
        p.addLast(new HttpServerInboundHandler());

        p.addFirst("ping", new IdleStateHandler(GlobalConfig.instance().getIdleTimeout(), 15, 10, TimeUnit.SECONDS));

    }
}
