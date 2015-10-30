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

import java.util.HashMap;
import java.util.Map;

import com.dinstone.rpc.RpcConfiguration;
import com.dinstone.rpc.client.Connection;
import com.dinstone.rpc.client.ConnectionFactory;
import com.dinstone.rpc.client.ConnectionKey;

public class MinaConnectionFactory implements ConnectionFactory {

    private static final ConnectionFactory factory = new MinaConnectionFactory();

    public static ConnectionFactory getInstance() {
        return factory;
    }

    private final Map<ConnectionKey, MinaConnector> cachedConnectors;

    protected MinaConnectionFactory() {
        cachedConnectors = new HashMap<ConnectionKey, MinaConnector>();
    }

    public Connection createConnection(RpcConfiguration config) {
        ConnectionKey ckey = new ConnectionKey(config);
        synchronized (cachedConnectors) {
            MinaConnector connector = cachedConnectors.get(ckey);
            if (connector == null) {
                connector = new MinaConnector(config);
                cachedConnectors.put(ckey, connector);
            }
            connector.incrementRefCount();
            return new MinaConnection(connector, config);
        }
    }

    public void releaseConnection(RpcConfiguration config) {
        ConnectionKey ckey = new ConnectionKey(config);
        synchronized (cachedConnectors) {
            MinaConnector connector = cachedConnectors.get(ckey);
            if (connector != null) {
                connector.decrementRefCount();
                if (connector.isZeroRefCount()) {
                    cachedConnectors.remove(ckey);
                    connector.dispose();
                }
            }
        }
    }

}
