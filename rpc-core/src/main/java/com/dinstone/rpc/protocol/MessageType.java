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

package com.dinstone.rpc.protocol;

/**
 * message type.
 * 
 * @author guojinfei
 * @version 1.0.0.2014-6-23
 */
public enum MessageType {
    RPC_REQUEST((byte) 1), RPC_RESPONSE((byte) 2), HEARTBEAT_PING((byte) 3), HEARTBEAT_PONG((byte) 4);

    private byte value;

    private MessageType(byte value) {
        this.value = value;
    }

    /**
     * the value to get
     * 
     * @return the value
     * @see MessageType#value
     */
    public byte getValue() {
        return value;
    }

    public static MessageType valueOf(int value) {
        switch (value) {
        case 1:
            return RPC_REQUEST;
        case 2:
            return RPC_RESPONSE;
        case 3:
            return HEARTBEAT_PING;
        case 4:
            return HEARTBEAT_PONG;

        default:
            break;
        }
        throw new IllegalArgumentException("unsupported message type [" + value + "]");
    }

}
