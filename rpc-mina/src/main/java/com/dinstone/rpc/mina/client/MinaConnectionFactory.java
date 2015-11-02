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

import java.util.concurrent.ConcurrentHashMap;

import com.dinstone.rpc.RpcConfiguration;
import com.dinstone.rpc.client.Connection;
import com.dinstone.rpc.client.ConnectionFactory;

public class MinaConnectionFactory implements ConnectionFactory {

    private static final ConnectionFactory factory = new MinaConnectionFactory();

    public static ConnectionFactory getInstance() {
        return factory;
    }

    private ConcurrentHashMap<Connection, MinaConnector> cachedConnectors;

    protected MinaConnectionFactory() {
        cachedConnectors = new ConcurrentHashMap<Connection, MinaConnector>();
    }

    public Connection create(RpcConfiguration config) {
        MinaConnector connector = new MinaConnector(config);
        MinaConnection connection = new MinaConnection(connector, config);
        cachedConnectors.put(connection, connector);
        return connection;
    }

    public void release(Connection connection) {
        MinaConnector connector = cachedConnectors.get(connection);
        if (connector != null) {
            connector.dispose();
        }
    }

}
