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

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.rpc.protocol.RpcMessage;
import com.dinstone.rpc.protocol.RpcPing;
import com.dinstone.rpc.protocol.RpcPong;
import com.dinstone.rpc.protocol.RpcRequest;
import com.dinstone.rpc.protocol.RpcResponse;
import com.dinstone.rpc.service.ServiceHandler;

/**
 * @author guojf
 * @version 1.0.0.2013-5-2
 */
public class MinaServerHandler extends IoHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MinaServerHandler.class);

    private ServiceHandler handler;

    /**
     * @param handler
     */
    public MinaServerHandler(ServiceHandler handler) {
        this.handler = handler;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (message instanceof RpcRequest) {
            RpcResponse response = handler.handle((RpcRequest) message);
            session.write(response);
        } else if (message instanceof RpcPing) {
            session.write(new RpcPong(((RpcMessage) message).getHeader()));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.mina.core.service.IoHandlerAdapter#sessionClosed(org.apache.mina.core.session.IoSession)
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        long id = session.getId();
        LOG.debug("Session[{}] is closed", id);
        // session.updateThroughput(System.currentTimeMillis(), true);
        // LOG.info("Session[{}] ReadBytes : {} Byte", id,
        // session.getReadBytes());
        // LOG.info("Session[{}] WrittenBytes : {} Byte", id,
        // session.getWrittenBytes());
        // LOG.info("Session[{}] ReadBytesThroughput : {} Byte/Sec", id,
        // session.getReadBytesThroughput());
        // LOG.info("Session[{}] WrittenBytesThroughput : {} Byte/Sec", id,
        // session.getWrittenBytesThroughput());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.mina.core.service.IoHandlerAdapter#exceptionCaught(org.apache.mina.core.session.IoSession,
     *      java.lang.Throwable)
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        if (cause instanceof ProtocolDecoderException) {
            session.close(true);
        }
    }
}
