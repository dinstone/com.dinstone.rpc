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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.rpc.RpcException;
import com.dinstone.rpc.protocol.Result;
import com.dinstone.rpc.protocol.RpcRequest;
import com.dinstone.rpc.protocol.RpcResponse;

/**
 * @author guojinfei
 * @version 1.0.0.2014-7-29
 */
public class DefaultServiceHandler implements ServiceHandler, ServiceStats {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultServiceHandler.class);

    private Map<Class<?>, Object> interfaceMap = new ConcurrentHashMap<Class<?>, Object>();

    private Map<String, Service> serviceMap = new ConcurrentHashMap<String, Service>();

    public DefaultServiceHandler() {
        regist(ServiceStats.class, this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.service.ServiceHandler#regist(java.lang.Class,
     *      java.lang.Object)
     */
    public synchronized void regist(Class<?> serviceInterface, Object serviceObject) {
        if (!serviceInterface.isInstance(serviceObject)) {
            throw new RpcException(501,
                "the specified service object is not assignment-compatible with the object represented by this Class.");
        }

        Object obj = interfaceMap.get(serviceInterface);
        if (obj != null) {
            throw new RpcException(502, "multiple object registed with the service interface " + serviceInterface);
        } else {
            interfaceMap.put(serviceInterface, serviceObject);
        }

        String classPrefix = serviceInterface.getName() + ".";
        Map<String, Service> tmpMap = new HashMap<String, Service>();
        Method[] methods = serviceInterface.getDeclaredMethods();
        for (Method method : methods) {
            Service service = new Service(serviceObject, method);
            String key = classPrefix + method.getName();
            if (tmpMap.containsKey(key)) {
                throw new RpcException(503, "method overloading is not supported");
            }
            tmpMap.put(key, service);
        }

        serviceMap.putAll(tmpMap);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.service.ServiceHandler#handle(com.dinstone.rpc.protocol.RpcRequest)
     */
    public RpcResponse handle(RpcRequest request) {
        Result result = null;
        try {
            Service service = find(request.getMethod());
            if (service == null) {
                result = new Result(505, "no serivce");
            } else {
                Object resObj = service.call(request.getParams());
                result = new Result(200, resObj);
            }
        } catch (RpcException e) {
            LOG.warn("Invoke service exception", e);
            result = new Result(e.getCode(), e.getMessage());
        }

        catch (IllegalArgumentException e) {
            result = new Result(600, e.getMessage(), e);
        } catch (IllegalAccessException e) {
            result = new Result(601, e.getMessage(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            result = new Result(500, t.getMessage(), t);
        } catch (Exception e) {
            result = new Result(509, "unkown exception");
        }

        return new RpcResponse(request.getHeader(), result);
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.service.ServiceStats#serviceList()
     */
    public List<String> serviceList() {
        List<String> services = new ArrayList<String>(serviceMap.size());
        for (Service service : serviceMap.values()) {
            services.add(description(service));
        }
        return services;
    }

    private Service find(String methodName) {
        return serviceMap.get(methodName);
    }

    private String description(Service service) {
        Method method = service.getMethod();
        StringBuilder desc = new StringBuilder();
        desc.append(getTypeName(method.getReturnType()) + " ");
        desc.append(getTypeName(method.getDeclaringClass()) + ".");
        desc.append(method.getName() + "(");
        Class<?>[] params = method.getParameterTypes();
        for (int j = 0; j < params.length; j++) {
            desc.append(getTypeName(params[j]));
            if (j < (params.length - 1)) {
                desc.append(",");
            }
        }
        desc.append(")");
        return desc.toString();
    }

    private static String getTypeName(Class<?> type) {
        if (type.isArray()) {
            try {
                Class<?> cl = type;
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(cl.getName());
                for (int i = 0; i < dimensions; i++) {
                    sb.append("[]");
                }
                return sb.toString();
            } catch (Throwable e) {
            }
        }
        return type.getName();
    }

}
