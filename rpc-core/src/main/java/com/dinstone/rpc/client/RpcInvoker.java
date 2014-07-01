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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.dinstone.rpc.CallFuture;
import com.dinstone.rpc.protocol.Call;

/**
 * @author guojf
 * @version 1.0.0.2013-10-28
 */
public class RpcInvoker implements InvocationHandler {

    private Connection connection;

    public RpcInvoker(Connection connection) {
        this.connection = connection;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxyObj, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals("hashCode")) {
            return new Integer(System.identityHashCode(proxyObj));
        } else if (methodName.equals("equals")) {
            return (proxyObj == args[0] ? Boolean.TRUE : Boolean.FALSE);
        } else if (methodName.equals("toString")) {
            return proxyObj.getClass().getName() + '@' + Integer.toHexString(proxyObj.hashCode());
        }

        methodName = method.getDeclaringClass().getName() + "." + methodName;
        return connection.call(new Call(methodName, args)).get();
    }

    /**
     * @param method
     * @param args
     * @return
     */
    public CallFuture invoke(String method, Object[] args) {
        return connection.call(new Call(method, args));
    }

}
