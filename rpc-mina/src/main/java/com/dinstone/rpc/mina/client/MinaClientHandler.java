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

import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.rpc.CallFuture;
import com.dinstone.rpc.RpcException;
import com.dinstone.rpc.protocol.Result;
import com.dinstone.rpc.protocol.RpcResponse;

public class MinaClientHandler extends IoHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MinaClientHandler.class);

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        SessionUtil.getConnection(session).destroy();

        // session.updateThroughput(System.currentTimeMillis(), true);
        // long id = session.getId();
        // LOG.info("session[{}] is closed", id);
        // LOG.info("Session[{}] ReadBytes : {} Byte", id,
        // session.getReadBytes());
        // LOG.info("Session[{}] WrittenBytes : {} Byte", id,
        // session.getWrittenBytes());
        // LOG.info("Session[{}] ReadBytesThroughput : {} Byte/Sec", id,
        // session.getReadBytesThroughput());
        // LOG.info("Session[{}] WrittenBytesThroughput : {} Byte/Sec", id,
        // session.getWrittenBytesThroughput());
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        LOG.error("Unhandled Exception", cause);
        session.close(true);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache.mina.core.session.IoSession,
     *      java.lang.Object)
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        handle(session, (RpcResponse) message);
    }

    private void handle(IoSession session, RpcResponse response) {
        Map<Integer, CallFuture> cfMap = SessionUtil.getCallFutureMap(session);
        CallFuture future = cfMap.remove(response.getId());
        if (future != null) {
            try {
                Result result = response.getResult();
                if (result.getCode() != 200) {
                    future.setException(new RpcException(result.getCode(), result.getMessage()));
                } else {
                    future.setResult(result.getData());
                }
            } catch (Exception e) {
                LOG.error("Unhandled Exception", e);
                future.setException(new RpcException(400, e.getMessage()));
            }
        }
    }

}
