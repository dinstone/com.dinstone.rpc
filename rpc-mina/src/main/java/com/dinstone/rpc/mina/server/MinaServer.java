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

package com.dinstone.rpc.mina.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.rpc.RpcConfiguration;
import com.dinstone.rpc.RpcException;
import com.dinstone.rpc.Server;
import com.dinstone.rpc.mina.RpcProtocolDecoder;
import com.dinstone.rpc.mina.RpcProtocolEncoder;
import com.dinstone.rpc.protocol.HeartbeatPing;
import com.dinstone.rpc.protocol.HeartbeatPong;
import com.dinstone.rpc.protocol.Pong;
import com.dinstone.rpc.server.AbstractServer;
import com.dinstone.rpc.service.DefaultServiceHandler;

/**
 * @author guojinfei
 * @version 1.0.0.2014-7-29
 */
public class MinaServer extends AbstractServer implements Server {

    private final class PassiveKeepAliveMessageFactory implements KeepAliveMessageFactory {

        public boolean isResponse(IoSession session, Object message) {
            if (message instanceof HeartbeatPong) {
                return true;
            }
            return false;
        }

        public boolean isRequest(IoSession session, Object message) {
            if (message instanceof HeartbeatPing) {
                return true;
            }
            return false;
        }

        public Object getResponse(IoSession session, Object request) {
            HeartbeatPing pingMessage = (HeartbeatPing) request;
            return new HeartbeatPong(pingMessage.getMessageId(), new Pong());
        }

        public Object getRequest(IoSession session) {
            return null;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(MinaServer.class);

    private SocketAcceptor acceptor;

    private ExecutorService executorService;

    /**
     * @param config
     */
    public MinaServer(RpcConfiguration config) {
        super(config, new DefaultServiceHandler());
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.Server#start()
     */
    public synchronized void start() {
        // This socket acceptor will handle incoming connections
        acceptor = new NioSocketAcceptor();

        SocketSessionConfig sessionConfig = acceptor.getSessionConfig();
        sessionConfig.setKeepAlive(true);
        LOG.debug("KeepAlive is {}", sessionConfig.isKeepAlive());

        // set read buffer size
        sessionConfig.setReadBufferSize(8 * 1024);
        LOG.debug("ReadBufferSize is {}", sessionConfig.getReadBufferSize());
        LOG.debug("SendBufferSize is {}", sessionConfig.getSendBufferSize());

        // get filter chain builder
        DefaultIoFilterChainBuilder chainBuilder = acceptor.getFilterChain();

        // add message codec filter
        final RpcProtocolEncoder encoder = new RpcProtocolEncoder();
        final RpcProtocolDecoder decoder = new RpcProtocolDecoder();
        int maxLen = config.getMessageMaxSize();
        LOG.debug("Server property [rpc.protocol.maxlength = {}]", maxLen);
        encoder.setMaxObjectSize(maxLen);
        decoder.setMaxObjectSize(maxLen);
        chainBuilder.addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactory() {

            public ProtocolEncoder getEncoder(IoSession session) throws Exception {
                return encoder;
            }

            public ProtocolDecoder getDecoder(IoSession session) throws Exception {
                return decoder;
            }
        }));

        executorService = Executors.newCachedThreadPool();
        chainBuilder.addLast("threadPool", new ExecutorFilter(executorService, IoEventType.MESSAGE_RECEIVED));

        // add keep alive filter
        KeepAliveFilter kaFilter = new KeepAliveFilter(new PassiveKeepAliveMessageFactory(), IdleStatus.BOTH_IDLE);
        kaFilter.setForwardEvent(true);
        chainBuilder.addLast("keepAlive", kaFilter);

        // add business handler
        acceptor.setHandler(new MinaServerHandler(handler));

        int port = config.getServicePort();
        InetSocketAddress localAddress = new InetSocketAddress(port);
        try {
            String host = config.getServiceHost();
            if (host != null) {
                localAddress = new InetSocketAddress(host, port);
            }
            acceptor.bind(localAddress);
            LOG.info("RPC service works on " + localAddress);
        } catch (IOException e) {
            LOG.error("Server can't bind to the specified local address " + localAddress, e);
            throw new RpcException(500, "Server can't bind to the specified local address " + localAddress, e);
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.Server#stop()
     */
    public synchronized void stop() {
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                executorService.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                break;
            }
        }

        acceptor.dispose();
    }
}
