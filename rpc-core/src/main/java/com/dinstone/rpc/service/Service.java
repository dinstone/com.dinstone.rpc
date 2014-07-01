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

package com.dinstone.rpc.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.dinstone.rpc.RpcException;

public class Service {

    private Object instance;

    private Method method;

    private Class<?>[] argTypes;

    /**
     * @param instance
     * @param method
     * @param argTypes
     */
    public Service(Object instance, Method method) {
        super();
        this.instance = instance;
        this.method = method;
        this.argTypes = method.getParameterTypes();
    }

    /**
     * the method to get
     * 
     * @return the method
     * @see Service#method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * the instance to get
     * 
     * @return the instance
     * @see Service#instance
     */
    public Object getInstance() {
        return instance;
    }

    /**
     * the argTypes to get
     * 
     * @return the argTypes
     * @see Service#argTypes
     */
    public Class<?>[] getArgTypes() {
        return argTypes;
    }

    /**
     * @param params
     * @return
     */
    public Object call(Object[] params) {
        try {
            return method.invoke(instance, params);
        } catch (IllegalArgumentException e) {
            throw new RpcException(600, "IllegalArgumentException:" + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RpcException(601, "IllegalAccessException:" + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new RpcException(602, "InvocationTargetException:" + e.getTargetException(), e.getTargetException());
        }
    }

}