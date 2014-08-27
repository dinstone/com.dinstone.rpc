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
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.session.IoSession;

import com.dinstone.rpc.CallFuture;
import com.dinstone.rpc.Configuration;
import com.dinstone.rpc.Constants;
import com.dinstone.rpc.client.Connection;
import com.dinstone.rpc.protocol.Call;
import com.dinstone.rpc.protocol.Header;
import com.dinstone.rpc.protocol.MessageType;
import com.dinstone.rpc.protocol.RpcRequest;
import com.dinstone.rpc.serialize.SerializeType;

public class MinaConnection implements Connection {

    private static final AtomicInteger IDGEN = new AtomicInteger();

    private boolean closed;

    private IoSession ioSession;

    private final MinaConnector connector;

    private SerializeType serializeType;

    private MessageType rpcVersion;

    public MinaConnection(MinaConnector connector, Configuration config) {
        this.connector = connector;

        String rpcv = config.get(Constants.RPC_MESSAGE_TYPE);
        if (rpcv == null || rpcv.length() == 0) {
            rpcVersion = MessageType.RPC1;
        } else {
            rpcVersion = MessageType.valueOf(Integer.parseInt(rpcv));
        }

        String stype = config.get(Constants.RPC_SERIALIZE_TYPE);
        if (stype == null || stype.length() == 0) {
            serializeType = SerializeType.JACKSON;
        } else {
            serializeType = SerializeType.valueOf(Integer.parseInt(stype));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.client.Connection#call(com.dinstone.rpc.protocol.Call)
     */
    public CallFuture call(Call call) {
        connect();

        int id = getId();
        Map<Integer, CallFuture> futureMap = SessionUtil.getCallFutureMap(ioSession);
        final CallFuture callFuture = new CallFuture();
        futureMap.put(id, callFuture);

        ioSession.write(new RpcRequest(new Header(id, rpcVersion, serializeType), call));

        return callFuture;
    }

    /**
     * @return
     */
    private int getId() {
        return IDGEN.incrementAndGet();
    }

    public synchronized void close() {
        destroy();
        closed = true;
    }

    public synchronized void destroy() {
        if (ioSession != null) {
            Map<Integer, CallFuture> futureMap = SessionUtil.getCallFutureMap(ioSession);
            for (CallFuture future : futureMap.values()) {
                future.setException(new RuntimeException("connection is closed"));
            }

            ioSession.close(true);
        }
        ioSession = null;
    }

    private synchronized void connect() {
        if (closed) {
            throw new RuntimeException("connection is closed");
        }

        if (ioSession == null || !ioSession.isConnected()) {
            ioSession = connector.createSession();
            SessionUtil.setConnection(ioSession, this);
        }
    }

}
