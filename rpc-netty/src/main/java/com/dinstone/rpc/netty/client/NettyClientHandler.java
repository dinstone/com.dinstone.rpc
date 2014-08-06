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

package com.dinstone.rpc.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinstone.rpc.CallFuture;
import com.dinstone.rpc.RpcException;
import com.dinstone.rpc.protocol.Result;
import com.dinstone.rpc.protocol.RpcResponse;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(NettyClientHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Map<Integer, CallFuture> cfMap = SessionUtil.getCallFutureMap(ctx.channel());
        RpcResponse response = (RpcResponse) msg;
        CallFuture future = cfMap.remove(response.getId());
        if (future != null) {
            try {
                Result result = response.getResult();
                if (result.getCode() != 200) {
                    Throwable fault = (Throwable) result.getData();
                    if (fault == null) {
                        fault = new RpcException(result.getCode(), result.getMessage());
                    }
                    future.setException(fault);
                } else {
                    future.setResult(result.getData());
                }
            } catch (Exception e) {
                LOG.error("Unhandled Exception", e);
                future.setException(new RpcException(400, e.getMessage()));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        LOG.error("Unhandled Exception", cause);
        ctx.close();
    }

}
