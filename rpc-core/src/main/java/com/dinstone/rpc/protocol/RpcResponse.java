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
 * RPC response includes a Header and a Result.
 * 
 * @author guojf
 * @version 1.0.0.2013-10-31
 */
public class RpcResponse extends RpcMessage implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    private byte[] resultBytes;

    private Result result;

    public RpcResponse(Header header, byte[] resultBytes) {
        super(header);
        this.resultBytes = resultBytes;
    }

    public RpcResponse(Header header, Result result) {
        super(header);
        this.result = result;
    }

    /**
     * the header to get
     * 
     * @return the header
     * @see RpcResponse#header
     */
    @Override
    public Header getHeader() {
        return header;
    }

    /**
     * the result to get
     * 
     * @return the result
     * @throws Exception
     * @see RpcResponse#result
     */
    public Result getResult() throws Exception {
        if (result == null && resultBytes != null) {
            result = SerializerRegister.getInstance().find(header.getSerializeType())
                .deserialize(resultBytes, Result.class);
        }
        return result;
    }

    /**
     * the resultBytes to get
     * 
     * @return the resultBytes
     * @throws Exception
     * @see RpcResponse#resultBytes
     */
    public byte[] getResultBytes() throws Exception {
        if (resultBytes == null && result != null) {
            resultBytes = SerializerRegister.getInstance().find(header.getSerializeType())
                .serialize(result, Result.class);
        }
        return resultBytes;
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
        Result resultObj = result;
        try {
            resultObj = getResult();
        } catch (Exception e) {
        }
        return "{header=" + header + ", result=" + resultObj + "}";
    }

}
