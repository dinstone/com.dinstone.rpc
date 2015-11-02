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

import java.util.concurrent.ConcurrentHashMap;

import com.dinstone.rpc.RpcConfiguration;
import com.dinstone.rpc.client.Connection;
import com.dinstone.rpc.client.ConnectionFactory;

public class NettyConnectionFactory implements ConnectionFactory {

    private static NettyConnectionFactory INSTANCE = new NettyConnectionFactory();

    public static ConnectionFactory getInstance() {
        return INSTANCE;
    }

    private ConcurrentHashMap<Connection, NettyConnector> cachedConnectors;

    protected NettyConnectionFactory() {
        cachedConnectors = new ConcurrentHashMap<Connection, NettyConnector>();
    }

    public Connection create(RpcConfiguration config) {
        NettyConnector connector = new NettyConnector(config);
        return new NettyConnection(connector, config);
    }

    public void release(Connection connection) {
        NettyConnector connector = cachedConnectors.get(connection);
        if (connector != null) {
            connector.dispose();
        }
    }

}
