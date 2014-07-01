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

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.dinstone.rpc.serialize.SerializeType;

/**
 * RPC protocol codec.
 * 
 * @author guojinfei
 * @version 1.0.0.2014-6-23
 */
public class RpcProtocolCodec {

    public static RpcRequest decodeRequest(byte[] rpcBytes) throws Exception {
        Header header = parseHeader(rpcBytes);

        byte[] bodyBytes = Arrays.copyOfRange(rpcBytes, 6, rpcBytes.length);
        RpcRequest request = new RpcRequest(header, bodyBytes);

        return request;
    }

    public static RpcObject decodeResponse(byte[] rpcBytes) throws Exception {
        Header header = parseHeader(rpcBytes);

        byte[] bodyBytes = Arrays.copyOfRange(rpcBytes, 6, rpcBytes.length);
        RpcObject response = new RpcResponse(header, bodyBytes);

        return response;
    }

    private static Header parseHeader(byte[] rpcBytes) {
        ByteBuffer rpcbuf = ByteBuffer.wrap(rpcBytes);
        // parse header
        RpcVersion version = RpcVersion.valueOf(rpcbuf.get());
        SerializeType type = SerializeType.valueOf(rpcbuf.get());
        int id = rpcbuf.getInt();
        return new Header(id, version, type);
    }

    public static byte[] encodeRequest(RpcRequest request) throws Exception {
        byte[] bytes = request.getCallBytes();
        ByteBuffer rpcBuf = ByteBuffer.allocate(6 + bytes.length);

        Header header = request.getHeader();
        rpcBuf.put(header.getRpcVersion().getValue());
        rpcBuf.put(header.getSerializeType().getValue());
        rpcBuf.putInt(header.getId());
        rpcBuf.put(bytes);
        rpcBuf.flip();

        return rpcBuf.array();
    }

    public static byte[] encodeResponse(RpcResponse response) throws Exception {
        byte[] bytes = response.getResultBytes();
        ByteBuffer rpcBuf = ByteBuffer.allocate(6 + bytes.length);

        Header header = response.getHeader();
        rpcBuf.put(header.getRpcVersion().getValue());
        rpcBuf.put(header.getSerializeType().getValue());
        rpcBuf.putInt(header.getId());
        rpcBuf.put(bytes);
        rpcBuf.flip();

        return rpcBuf.array();
    }

}
