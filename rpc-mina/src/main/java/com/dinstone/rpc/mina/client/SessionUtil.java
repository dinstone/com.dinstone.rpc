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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

import com.dinstone.rpc.CallFuture;
import com.dinstone.rpc.client.Connection;

public class SessionUtil {

    /**  */
    private static final String CALL_MAP = "OPERATION_QUEUE";

    @SuppressWarnings("unchecked")
    public static Map<Integer, CallFuture> getCallFutureMap(IoSession session) {
        Map<Integer, CallFuture> cfMap = (Map<Integer, CallFuture>) session.getAttribute(CALL_MAP);
        if (cfMap == null) {
            cfMap = new ConcurrentHashMap<Integer, CallFuture>();
            Map<Integer, CallFuture> oldMap = (Map<Integer, CallFuture>) session.setAttributeIfAbsent(CALL_MAP, cfMap);
            if (oldMap != null) {
                cfMap = oldMap;
            }
        }

        return cfMap;
    }

    public static void setConnection(IoSession session, Connection connection) {
        session.setAttribute(Connection.class.getName(), connection);
    }

    public static Connection getConnection(IoSession session) {
        return (Connection) session.getAttribute(Connection.class.getName());
    }
}
