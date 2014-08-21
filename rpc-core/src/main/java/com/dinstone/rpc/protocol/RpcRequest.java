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

import com.dinstone.rpc.serialize.SerializerRegister;

/**
 * RPC request includes a Header and a Call.
 * 
 * @author guojf
 * @version 1.0.0.2013-10-31
 */
public class RpcRequest extends RpcMessage implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    private byte[] callBytes;

    private Call call;

    public RpcRequest(Header header, byte[] callBytes) {
        super(header);
        this.callBytes = callBytes;
    }

    public RpcRequest(Header header, Call call) {
        super(header);
        this.call = call;
    }

    /**
     * the header to get
     * 
     * @return the header
     * @see RpcRequest#header
     */
    @Override
    public Header getHeader() {
        return header;
    }

    /**
     * the call to get
     * 
     * @return the call
     * @throws Exception
     * @see RpcRequest#call
     */
    public Call getCall() throws Exception {
        if (call == null && callBytes != null) {
            call = SerializerRegister.getInstance().find(header.getSerializeType()).deserialize(callBytes, Call.class);
        }
        return call;
    }

    /**
     * the callBytes to get
     * 
     * @return the callBytes
     * @throws Exception
     * @see RpcRequest#callBytes
     */
    public byte[] getCallBytes() throws Exception {
        if (callBytes == null && call != null) {
            callBytes = SerializerRegister.getInstance().find(header.getSerializeType()).serialize(call, Call.class);
        }
        return callBytes;
    }

    public String getMethod() throws Exception {
        return getCall().getMethod();
    }

    public Object[] getParams() throws Exception {
        return getCall().getParams();
    }

    public Integer getId() {
        return header.getId();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Call callObj = call;
        try {
            callObj = getCall();
        } catch (Exception e) {
        }
        return "{header=" + header + ", call=" + callObj + "}";
    }

}
