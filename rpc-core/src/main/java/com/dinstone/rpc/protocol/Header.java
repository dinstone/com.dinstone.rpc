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

import com.dinstone.rpc.serialize.SerializeType;

/**
 * RPC protocol header part.
 * 
 * @author guojinfei
 * @version 1.0.0.2014-6-23
 */
public class Header implements IHeader, Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    private int messageId;

    private MessageType messageType;

    private SerializeType serializeType;

    public Header(int messageId, SerializeType serializeType, MessageType messageType) {
        super();
        this.messageId = messageId;
        this.messageType = messageType;
        this.serializeType = serializeType;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.protocol.IHeader#getMessageId()
     */
    public int getMessageId() {
        return messageId;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.protocol.IHeader#getMessageType()
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.dinstone.rpc.protocol.IHeader#getSerializeType()
     */
    public SerializeType getSerializeType() {
        return serializeType;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{id=" + messageId + ", rpcVersion=" + messageType + ", serializeType=" + serializeType + "}";
    }

}
