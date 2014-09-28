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

package com.dinstone.rpc.server;

import com.dinstone.rpc.Configuration;
import com.dinstone.rpc.Server;
import com.dinstone.rpc.service.ServiceHandler;

public abstract class AbstractServer implements Server {

    protected Configuration config;

    protected ServiceHandler handler;

    public AbstractServer(Configuration config, ServiceHandler handler) {
        if (config == null) {
            throw new IllegalArgumentException("config is null");
        }
        this.config = config;

        if (handler == null) {
            throw new IllegalArgumentException("handler is null");
        }
        this.handler = handler;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.Server#registService(java.lang.Class,
     *      java.lang.Object)
     */
    public <T> void registService(Class<T> serviceInterface, T serviceObject) {
        handler.regist(serviceInterface, serviceObject);
    }

}