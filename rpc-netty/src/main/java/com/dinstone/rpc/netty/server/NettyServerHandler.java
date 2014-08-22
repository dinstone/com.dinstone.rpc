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

package com.dinstone.rpc.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.dinstone.rpc.protocol.HeartbeatPing;
import com.dinstone.rpc.protocol.HeartbeatPong;
import com.dinstone.rpc.protocol.Pong;
import com.dinstone.rpc.protocol.RpcRequest;
import com.dinstone.rpc.protocol.RpcResponse;
import com.dinstone.rpc.service.ServiceHandler;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private ServiceHandler handler;

    public NettyServerHandler(ServiceHandler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) {
        if (message instanceof RpcRequest) {
            RpcResponse response = handler.handle((RpcRequest) message);
            ctx.write(response);
        } else if (message instanceof HeartbeatPing) {
            ctx.write(new HeartbeatPong(((HeartbeatPing) message).getHeader(), new Pong()));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
