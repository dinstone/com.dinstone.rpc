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

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.rpc.Configuration;
import com.dinstone.rpc.Constants;
import com.dinstone.rpc.RpcException;
import com.dinstone.rpc.Server;
import com.dinstone.rpc.mina.RpcProtocolDecoder;
import com.dinstone.rpc.mina.RpcProtocolEncoder;
import com.dinstone.rpc.server.AbstractServer;
import com.dinstone.rpc.service.DefaultServiceHandler;

/**
 * @author guojinfei
 * @version 1.0.0.2014-7-29
 */
public class MinaServer extends AbstractServer implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(MinaServer.class);

    private SocketAcceptor acceptor;

    /**
     * @param config
     */
    public MinaServer(Configuration config) {
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
        // acceptor.setReuseAddress(true);

        SocketSessionConfig sessionConfig = acceptor.getSessionConfig();
        LOG.debug("KeepAlive is {}", sessionConfig.isKeepAlive());
        sessionConfig.setReadBufferSize(64 * 1024);
        LOG.debug("ReadBufferSize is {}", sessionConfig.getReadBufferSize());
        LOG.debug("SendBufferSize is {}", sessionConfig.getSendBufferSize());

        int maxLen = config.getInt(Constants.MAX_LENGTH, Integer.MAX_VALUE);
        LOG.debug("Server property [rpc.protocol.maxlength = {}]", maxLen);

        // get a reference to the filter chain from the acceptor
        DefaultIoFilterChainBuilder chainBuilder = acceptor.getFilterChain();

        final RpcProtocolEncoder encoder = new RpcProtocolEncoder();
        final RpcProtocolDecoder decoder = new RpcProtocolDecoder();
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

        // chainBuilder.addLast("keepAlive", new KeepAliveFilter(new
        // KeepAliveMessageFactory() {
        //
        // private final IoBuffer KAMSG_REQ = IoBuffer.wrap(new byte[] { -1 });
        //
        // private final IoBuffer KAMSG_REP = IoBuffer.wrap(new byte[] { -2 });
        //
        // public boolean isResponse(IoSession session, Object message) {
        // if (!(message instanceof IoBuffer)) {
        // return false;
        // }
        // IoBuffer realMessage = (IoBuffer) message;
        // if (realMessage.limit() != 1) {
        // return false;
        // }
        //
        // boolean result = (realMessage.get() == -2);
        // realMessage.rewind();
        // return result;
        // }
        //
        // public boolean isRequest(IoSession session, Object message) {
        // if (!(message instanceof IoBuffer)) {
        // return false;
        // }
        // IoBuffer realMessage = (IoBuffer) message;
        // if (realMessage.limit() != 1) {
        // return false;
        // }
        //
        // boolean result = (realMessage.get() == -1);
        // realMessage.rewind();
        // return result;
        // }
        //
        // public Object getResponse(IoSession session, Object request) {
        // return KAMSG_REP.duplicate();
        // }
        //
        // public Object getRequest(IoSession session) {
        // return KAMSG_REQ.duplicate();
        // }
        // }));

        ExecutorService threadPool = Executors.newCachedThreadPool();
        chainBuilder.addLast("threadPool", new ExecutorFilter(threadPool, IoEventType.MESSAGE_RECEIVED));

        acceptor.setHandler(new MinaServerHandler(handler));

        int port = config.getInt(Constants.SERVICE_PORT, Constants.DEFAULT_SERVICE_PORT);
        InetSocketAddress localAddress = new InetSocketAddress(port);
        try {
            String host = config.get(Constants.SERVICE_HOST);
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
        acceptor.dispose();
    }
}
