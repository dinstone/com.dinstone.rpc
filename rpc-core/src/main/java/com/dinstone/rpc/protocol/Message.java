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

import java.io.Serializable;

/**
 * RPC protocol message that includes a header and a body.
 * 
 * @author guojinfei
 * @version 1.0.0.2014-7-29
 */
public abstract class Message<H extends IHeader, B extends IBody> implements Serializable {

    public static enum Type {
        PING((byte) 1), PONG((byte) 2), CALL((byte) 3), RESULT((byte) 4);

        private byte value;

        private Type(byte value) {
            this.value = value;
        }

        /**
         * the value to get
         * 
         * @return the value
         * @see RpcVersion#value
         */
        public byte getValue() {
            return value;
        }

        public static Type valueOf(int value) {
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
            throw new IllegalArgumentException("unsupported message type [" + value + "]");
        }
    }

    /**  */
    private static final long serialVersionUID = 1L;

    protected H header;

    protected B body;

    public Message(H header, B body) {
        super();
        if (header == null) {
            throw new IllegalArgumentException("header is null");
        }
        if (body == null) {
            throw new IllegalArgumentException("body is null");
        }
        this.header = header;
        this.body = body;
    }

    /**
     * the header to get
     * 
     * @return the header
     * @see Message#header
     */
    public H getHeader() {
        return header;
    }

    /**
     * the body to get
     * 
     * @return the body
     * @see Message#body
     */
    public B getBody() {
        return body;
    }

    public abstract Type getType();
}