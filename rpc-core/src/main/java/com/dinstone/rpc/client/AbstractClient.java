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

package com.dinstone.rpc.client;

import java.lang.reflect.Proxy;

import com.dinstone.rpc.CallFuture;
import com.dinstone.rpc.Client;
import com.dinstone.rpc.Configuration;

/**
 * the interface Client implements.
 * 
 * @author guojinfei
 * @version 1.0.0.2014-6-30
 */
public abstract class AbstractClient implements Client {

    protected Configuration config;

    protected RpcInvocationProxy invoker;

    protected Connection connection;

    public AbstractClient(Configuration config, ConnectionFactory factory) {
        if (config == null) {
            throw new IllegalArgumentException("config is null");
        }
        this.config = config;

        if (factory == null) {
            throw new IllegalArgumentException("factory is null");
        }
        this.connection = factory.createConnection(config);
        this.invoker = new RpcInvocationProxy(connection);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.Client#getProxy(java.lang.Class)
     */
    public <T> T getProxy(Class<T> proxy) {
        return proxy.cast(Proxy.newProxyInstance(proxy.getClassLoader(), new Class[] { proxy }, invoker));
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.Client#asyncInvoke(java.lang.String,
     *      java.lang.Object[])
     */
    public CallFuture asyncInvoke(String method, Object[] args) throws Throwable {
        return invoker.invoke(method, args);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.Client#syncInvoke(java.lang.String,
     *      java.lang.Object[])
     */
    public Object syncInvoke(String method, Object[] args) throws Throwable {
        return asyncInvoke(method, args).get();
    }

    public void close() {
        if (connection != null) {
            connection.close();
        }
    }

}