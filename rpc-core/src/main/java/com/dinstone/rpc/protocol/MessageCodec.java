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
    public static byte[] encodeMessage(Message<? extends IHeader, ? extends IContent> message) throws Exception {
        IContent body = message.getContent();
        byte[] bodyBytes = REGISTER.find(message.getSerializeType()).serialize(body);

        ByteBuffer messageBuf = ByteBuffer.allocate(6 + bodyBytes.length);
        messageBuf.putInt(message.getMessageId());
        messageBuf.put(message.getMessageType().getValue());
        messageBuf.put(message.getSerializeType().getValue());
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
    public static Message<? extends IHeader, ? extends IContent> decodeMessage(byte[] rpcBytes) throws Exception {
        ByteBuffer messageBuf = ByteBuffer.wrap(rpcBytes);
        // parse header
        int messageId = messageBuf.getInt();
        MessageType messageType = MessageType.valueOf(messageBuf.get());
        SerializeType serializeType = SerializeType.valueOf(messageBuf.get());

        byte[] bodyBytes = Arrays.copyOfRange(rpcBytes, 6, rpcBytes.length);
        if (messageType == MessageType.RPC_REQUEST) {
            Call call = REGISTER.find(serializeType).deserialize(bodyBytes, Call.class);
            return new RpcRequest(messageId, serializeType, call);
        } else if (messageType == MessageType.RPC_RESPONSE) {
            Result result = REGISTER.find(serializeType).deserialize(bodyBytes, Result.class);
            return new RpcResponse(messageId, serializeType, result);
        } else if (messageType == MessageType.HEARTBEAT_PING) {
            Ping ping = REGISTER.find(serializeType).deserialize(bodyBytes, Ping.class);
            return new HeartbeatPing(messageId, ping);
        } else if (messageType == MessageType.HEARTBEAT_PONG) {
            Pong pong = REGISTER.find(serializeType).deserialize(bodyBytes, Pong.class);
            return new HeartbeatPong(messageId, pong);
        } else {
            throw new IllegalStateException("unsupported content type [" + messageType + "]");
        }
    }
}
