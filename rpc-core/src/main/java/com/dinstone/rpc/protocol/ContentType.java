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

public enum ContentType {
    PING((byte) 1), PONG((byte) 2), CALL((byte) 3), RESULT((byte) 4);

    private byte value;

    private ContentType(byte value) {
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

    public static ContentType valueOf(int value) {
        switch (value) {
        case 1:
            return PING;
        case 2:
            return PONG;
        case 3:
            return CALL;
        case 4:
            return RESULT;
        default:
            break;
        }
        throw new IllegalArgumentException("unsupported type [" + value + "]");
    }
}