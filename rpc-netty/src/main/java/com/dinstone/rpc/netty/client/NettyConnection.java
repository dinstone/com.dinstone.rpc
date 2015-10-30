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

package com.dinstone.rpc.netty.client;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.dinstone.rpc.CallFuture;
import com.dinstone.rpc.RpcConfiguration;
import com.dinstone.rpc.client.Connection;
import com.dinstone.rpc.protocol.Call;
import com.dinstone.rpc.protocol.RpcRequest;
import com.dinstone.rpc.serialize.SerializeType;

public class NettyConnection implements Connection {

    private static final AtomicInteger IDGEN = new AtomicInteger();

    private boolean closed;

    private NettyConnector connector;

    private SerializeType serializeType;

    private Channel ioSession;

    public NettyConnection(NettyConnector connector, RpcConfiguration config) {
        this.connector = connector;

        serializeType = config.getSerializeType();
    }

    public synchronized void close() {
        if (ioSession != null) {
            ioSession.close();
        }
        closed = true;
    }

    public CallFuture call(Call call) {
        connect();

        int id = IDGEN.incrementAndGet();
        Map<Integer, CallFuture> futureMap = SessionUtil.getCallFutureMap(ioSession);
        final CallFuture callFuture = new CallFuture();
        futureMap.put(id, callFuture);

        ioSession.writeAndFlush(new RpcRequest(id, serializeType, call));

        return callFuture;
    }

    private synchronized void connect() {
        if (closed) {
            throw new RuntimeException("connection is closed");
        }

        if (ioSession == null || !ioSession.isActive()) {
            ioSession = connector.createSession();
        }
    }

}
