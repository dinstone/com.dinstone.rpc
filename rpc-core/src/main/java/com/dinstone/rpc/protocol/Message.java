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
 * common message that includes a header and a content.
 * 
 * @author guojinfei
 * @version 1.0.0.2014-7-29
 */
public abstract class Message<H extends IHeader, C extends IContent> implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    protected H header;

    protected C content;

    public Message(H header, C content) {
        super();
        if (header == null) {
            throw new IllegalArgumentException("header is null");
        }
        if (content == null) {
            throw new IllegalArgumentException("body is null");
        }
        this.header = header;
        this.content = content;
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
     * the content to get
     * 
     * @return the content
     * @see Message#content
     */
    public C getContent() {
        return content;
    }

    public int getMessageId() {
        return header.getMessageId();
    }

    public MessageType getMessageType() {
        return header.getMessageType();
    }

    public SerializeType getSerializeType() {
        return header.getSerializeType();
    }
}