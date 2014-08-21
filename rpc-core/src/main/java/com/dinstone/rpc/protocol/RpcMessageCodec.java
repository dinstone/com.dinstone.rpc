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
import com.dinstone.rpc.serialize.SerializerRegister;

/**
 * RPC message codec.
 * 
 * @author guojinfei
 * @version 1.0.0.2014-6-23
 */
public class RpcMessageCodec {

    private static final byte[] EMPTY_BYTES = new byte[0];

    private static final SerializerRegister REGISTER = SerializerRegister.getInstance();

    private RpcMessageCodec() {
    }

    /**
     * encode RPC Message.
     * 
     * @param message
     *        RpcMessage
     * @return
     * @throws Exception
     *         serializable exception
     */
    public static byte[] encodeMessage(RpcMessage message) throws Exception {
        Header header = message.getHeader();
        byte[] bodyBytes = EMPTY_BYTES;
        RpcMessage.Type messageType = null;
        if (message instanceof RpcRequest) {
            messageType = RpcMessage.Type.REQUEST;
            Call call = ((RpcRequest) message).getCall();
            bodyBytes = REGISTER.find(header.getSerializeType()).serialize(call, Call.class);
        } else if (message instanceof RpcResponse) {
            messageType = RpcMessage.Type.RESPONSE;
            Result result = ((RpcResponse) message).getResult();
            bodyBytes = REGISTER.find(header.getSerializeType()).serialize(result, Result.class);
        } else if (message instanceof RpcPing) {
            messageType = RpcMessage.Type.PING;
        } else if (message instanceof RpcPong) {
            messageType = RpcMessage.Type.PONG;
        } else {
            throw new IllegalStateException("unsupported message type [" + message.getClass() + "]");
        }

        ByteBuffer messageBuf = ByteBuffer.allocate(7 + bodyBytes.length);
        messageBuf.put(messageType.getValue());
        messageBuf.put(header.getRpcVersion().getValue());
        messageBuf.put(header.getSerializeType().getValue());
        messageBuf.putInt(header.getId());
        messageBuf.put(bodyBytes);
        messageBuf.flip();

        return messageBuf.array();
    }

    /**
     * decode RPC Message.
     * 
     * @param rpcBytes
     *        RpcMessage bytes
     * @return
     * @throws Exception
     *         deserialize exception
     */
    public static RpcMessage decodeMessage(byte[] rpcBytes) throws Exception {
        ByteBuffer messageBuf = ByteBuffer.wrap(rpcBytes);
        // parse header
        RpcMessage.Type messageType = RpcMessage.Type.valueOf(messageBuf.get());
        RpcVersion version = RpcVersion.valueOf(messageBuf.get());
        SerializeType type = SerializeType.valueOf(messageBuf.get());
        int id = messageBuf.getInt();
        Header header = new Header(id, version, type);

        byte[] bodyBytes = Arrays.copyOfRange(rpcBytes, 7, rpcBytes.length);
        if (messageType == RpcMessage.Type.REQUEST) {
            Call call = REGISTER.find(type).deserialize(bodyBytes, Call.class);
            return new RpcRequest(header, call);
        } else if (messageType == RpcMessage.Type.RESPONSE) {
            Result result = REGISTER.find(type).deserialize(bodyBytes, Result.class);
            return new RpcResponse(header, result);
        } else if (messageType == RpcMessage.Type.PING) {
            return new RpcPing(header);
        } else if (messageType == RpcMessage.Type.PONG) {
            return new RpcPong(header);
        } else {
            throw new IllegalStateException("unsupported message type [" + messageType + "]");
        }
    }
}
