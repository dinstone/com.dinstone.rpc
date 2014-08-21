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

package com.dinstone.rpc.mina.client;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.rpc.Configuration;
import com.dinstone.rpc.Constants;
import com.dinstone.rpc.mina.RpcProtocolDecoder;
import com.dinstone.rpc.mina.RpcProtocolEncoder;

/**
 * @author guojf
 * @version 1.0.0.2013-4-11
 */
public class MinaConnector {

    private static final Logger LOG = LoggerFactory.getLogger(MinaConnector.class);

    private NioSocketConnector ioConnector;

    private int refCount;

    /**
     * @param config
     * @param ioConnector
     */
    public MinaConnector(Configuration config) {
        initConnector(config);
    }

    /**
     * @param config
     */
    private void initConnector(Configuration config) {
        // create connector
        ioConnector = new NioSocketConnector();
        SocketSessionConfig sessionConfig = ioConnector.getSessionConfig();
        LOG.debug("KeepAlive is {}", sessionConfig.isKeepAlive());
        LOG.debug("ReadBufferSize is {}", sessionConfig.getReadBufferSize());
        LOG.debug("SendBufferSize is {}", sessionConfig.getSendBufferSize());

        int maxLen = config.getInt("rpc.protocol.maxlength", Integer.MAX_VALUE);
        LOG.debug("rpc.protocol.maxlength is {}", maxLen);

        final RpcProtocolEncoder encoder = new RpcProtocolEncoder();
        final RpcProtocolDecoder decoder = new RpcProtocolDecoder();
        encoder.setMaxObjectSize(maxLen);
        decoder.setMaxObjectSize(maxLen);

        // add filter
        DefaultIoFilterChainBuilder chainBuilder = ioConnector.getFilterChain();
        chainBuilder.addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactory() {

            public ProtocolEncoder getEncoder(IoSession session) throws Exception {
                return encoder;
            }

            public ProtocolDecoder getDecoder(IoSession session) throws Exception {
                return decoder;
            }
        }));

        // set handler
        ioConnector.setHandler(new MinaClientHandler());

        String host = config.get(Constants.SERVICE_HOST, Constants.DEFAULT_SERVICE_HOST);
        int port = config.getInt(Constants.SERVICE_PORT, 7777);
        InetSocketAddress address = new InetSocketAddress(host, port);
        ioConnector.setDefaultRemoteAddress(address);
    }

    /**
     * @return
     */
    public IoSession createSession() {
        // create session
        LOG.info("create session on {} ", ioConnector.getDefaultRemoteAddress());
        long s = System.currentTimeMillis();
        ConnectFuture future = ioConnector.connect().awaitUninterruptibly();
        IoSession session = future.getSession();
        long t = System.currentTimeMillis() - s;
        LOG.info("create session on {} takes {}ms", ioConnector.getDefaultRemoteAddress(), t);
        return session;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.client.beanstalkc.internal.Connector#dispose()
     */
    public void dispose() {
        ioConnector.dispose(false);
    }

    /**
     *
     */
    public void incrementRefCount() {
        ++refCount;
    }

    /**
     *
     */
    public void decrementRefCount() {
        if (refCount > 0) {
            --refCount;
        }
    }

    /**
     * @return
     */
    public boolean isZeroRefCount() {
        return refCount == 0;
    }
}
