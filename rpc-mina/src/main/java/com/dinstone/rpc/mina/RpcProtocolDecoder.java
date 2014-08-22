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

package com.dinstone.rpc.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.dinstone.rpc.protocol.Message;
import com.dinstone.rpc.protocol.MessageCodec;

/**
 * RPC Protocol Decoder.
 * 
 * @author guojinfei
 * @version 1.0.0.2014-6-19
 */
public class RpcProtocolDecoder extends CumulativeProtocolDecoder {

    /** 2GB */
    private int maxObjectSize = Integer.MAX_VALUE;

    public RpcProtocolDecoder() {
    }

    /**
     * the maxObjectSize to get
     * 
     * @return the maxObjectSize
     * @see RpcProtocolEncoder#maxObjectSize
     */
    public int getMaxObjectSize() {
        return maxObjectSize;
    }

    /**
     * the maxObjectSize to set
     * 
     * @param maxObjectSize
     * @see RpcProtocolEncoder#maxObjectSize
     */
    public void setMaxObjectSize(int maxObjectSize) {
        if (maxObjectSize <= 0) {
            throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
        }

        this.maxObjectSize = maxObjectSize;
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        byte[] rpcBytes = readFrame(in);
        if (rpcBytes == null) {
            return false;
        }

        Message message = MessageCodec.decodeMessage(rpcBytes);
        out.write(message);

        return true;
    }

    private byte[] readFrame(IoBuffer in) {
        int remaining = in.remaining();
        if (remaining < 4) {
            return null;
        }

        int objectSize = in.getInt(0);
        if (objectSize > maxObjectSize) {
            throw new IllegalArgumentException("The encoded object is too big: " + objectSize + " (> " + maxObjectSize
                    + ')');
        }

        if (remaining - 4 >= objectSize) {
            // RPC object size
            in.getInt();
            byte[] rpcBytes = new byte[objectSize];
            in.get(rpcBytes);
            return rpcBytes;
        }

        return null;
    }
}
