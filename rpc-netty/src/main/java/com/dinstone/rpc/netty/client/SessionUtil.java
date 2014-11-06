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
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dinstone.rpc.CallFuture;

public class SessionUtil {

    private static final AttributeKey<Object> CALL_FUTURE_KEY = AttributeKey.valueOf(ConcurrentHashMap.class.getName());

    @SuppressWarnings("unchecked")
    public static Map<Integer, CallFuture> getCallFutureMap(Channel session) {
        Attribute<Object> attrValue = session.attr(CALL_FUTURE_KEY);
        return (Map<Integer, CallFuture>) attrValue.get();
    }

    public static void setCallFutureMap(Channel session) {
        Attribute<Object> attrValue = session.attr(CALL_FUTURE_KEY);
        attrValue.set(new ConcurrentHashMap<Integer, CallFuture>());
    }
}
