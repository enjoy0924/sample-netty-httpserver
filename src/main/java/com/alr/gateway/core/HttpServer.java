package com.alr.gateway.core;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class HttpServer {

    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private int port = 8080;
    private int bossCount = 4;
    private int workerCount = 200;
    private boolean keepAlive = true;
    private boolean reuseAddress = true;
    private int linger = 3000;
    private int backlog = 128;

    public HttpServer(int port) {
        this.port = port;
    }

    public HttpServer(int port, int bossCount,int workerCount, int backlog, int linger, boolean keepAlive, boolean reuseAddress) {
        this.port = port;
        this.bossCount = bossCount;
        this.workerCount = workerCount;
        this.backlog = backlog;
        this.linger = linger;
        this.keepAlive = keepAlive;
        this.reuseAddress = reuseAddress;
    }


    public boolean start() {

        bossGroup = new NioEventLoopGroup(bossCount);
        workerGroup = new NioEventLoopGroup(workerCount);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer()).option(ChannelOption.SO_BACKLOG, backlog)
                    .childOption(ChannelOption.SO_KEEPALIVE, keepAlive)
                    .childOption(ChannelOption.SO_REUSEADDR, reuseAddress)
                    .childOption(ChannelOption.SO_LINGER, linger);

            ChannelFuture f = b.bind(port).sync();
            channel = f.channel();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            return false;
        }
    }

    public boolean stop() throws Exception {
        try {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            channel.closeFuture().sync();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}