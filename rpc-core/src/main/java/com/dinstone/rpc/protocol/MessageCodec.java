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
public class MessageCodec {

    private static final SerializerRegister REGISTER = SerializerRegister.getInstance();

    private MessageCodec() {
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
    public static byte[] encodeMessage(Message<? extends IHeader, ? extends IBody> message) throws Exception {
        IHeader header = message.getHeader();
        IBody body = message.getBody();

        byte[] bodyBytes = REGISTER.find(header.getSerializeType()).serialize(body, body.getClass());

        ByteBuffer messageBuf = ByteBuffer.allocate(7 + bodyBytes.length);
        messageBuf.put(message.getType().getValue());
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
    public static Message<? extends IHeader, ? extends IBody> decodeMessage(byte[] rpcBytes) throws Exception {
        ByteBuffer messageBuf = ByteBuffer.wrap(rpcBytes);
        // parse header
        Message.Type messageType = Message.Type.valueOf(messageBuf.get());
        RpcVersion version = RpcVersion.valueOf(messageBuf.get());
        SerializeType type = SerializeType.valueOf(messageBuf.get());
        int id = messageBuf.getInt();
        Header header = new Header(id, version, type);

        byte[] bodyBytes = Arrays.copyOfRange(rpcBytes, 7, rpcBytes.length);
        if (messageType == Message.Type.CALL) {
            Call call = REGISTER.find(type).deserialize(bodyBytes, Call.class);
            return new RpcRequest(header, call);
        } else if (messageType == Message.Type.RESULT) {
            Result result = REGISTER.find(type).deserialize(bodyBytes, Result.class);
            return new RpcResponse(header, result);
        } else if (messageType == Message.Type.PING) {
            Ping ping = REGISTER.find(type).deserialize(bodyBytes, Ping.class);
            return new HeartbeatPing(header, ping);
        } else if (messageType == Message.Type.PONG) {
            Pong pong = REGISTER.find(type).deserialize(bodyBytes, Pong.class);
            return new HeartbeatPong(header, pong);
        } else {
            throw new IllegalStateException("unsupported message type [" + messageType + "]");
        }
    }
}
