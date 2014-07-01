/*
 * Copyright (C) 2012~2014 dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dinstone.rpc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.rpc.Configuration;
import com.dinstone.rpc.Constants;
import com.dinstone.rpc.RpcException;
import com.dinstone.rpc.Server;
import com.dinstone.rpc.netty.RpcProtocolDecoder;
import com.dinstone.rpc.netty.RpcProtocolEncoder;
import com.dinstone.rpc.server.AbstractServer;
import com.dinstone.rpc.service.DefaultServiceHandler;

/**
 * @author guojinfei
 * @version 1.0.0.2014-6-25
 */
public class NettyServer extends AbstractServer implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    /**
     * @param config
     */
    public NettyServer(Configuration config) {
        super(config, new DefaultServiceHandler());
    }

    public void start() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RpcProtocolDecoder(true));
                        ch.pipeline().addLast(new RpcProtocolEncoder(true));
                        ch.pipeline().addLast(new NettyServerHandler(handler));
                    }
                });
            boot.option(ChannelOption.SO_BACKLOG, 128);
            boot.childOption(ChannelOption.SO_KEEPALIVE, true);

            int port = config.getInt(Constants.SERVICE_PORT, Constants.DEFAULT_SERVICE_PORT);
            InetSocketAddress localAddress = new InetSocketAddress(port);
            String host = config.get(Constants.SERVICE_HOST);
            if (host != null) {
                localAddress = new InetSocketAddress(host, port);
            }
            LOG.info("RPC service works on " + localAddress);

            boot.bind(localAddress).sync();
        } catch (InterruptedException e) {
            throw new RpcException(500, "Server can't bind to the specified local address ", e);
        }
    }

    public void stop() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}
