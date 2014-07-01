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

package com.dinstone.rpc;

/**
 * Constants.
 * 
 * @author guojf
 * @version 1.0.0.2013-7-2
 */
public abstract class Constants {

    /** service host name */
    public static final String SERVICE_HOST = "rpc.service.host";

    /** default service host name */
    public static final String DEFAULT_SERVICE_HOST = "localhost";

    /** service port */
    public static final String SERVICE_PORT = "rpc.service.port";

    /** default service port */
    public static final int DEFAULT_SERVICE_PORT = 7777;

    /** RPC protocol max length */
    public static final String MAX_LENGTH = "rpc.protocol.maxlength";

    /** serialize type */
    public static final String RPC_SERIALIZE_TYPE = "rpc.serialize.type";

    /** RPC protocol version */
    public static final String RPC_PROTOCOL_VERSION = "rpc.protocol.version";

}
