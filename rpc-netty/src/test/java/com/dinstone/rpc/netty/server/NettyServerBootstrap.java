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

import com.dinstone.rpc.RpcConfiguration;
import com.dinstone.rpc.Server;
import com.dinstone.rpc.cases.HelloService;
import com.dinstone.rpc.cases.HelloServiceImpl;

/**
 * @author guojf
 * @version 1.0.0.2013-5-2
 */
public class NettyServerBootstrap {

    public static void main(String[] args) {
        RpcConfiguration config = new RpcConfiguration();
        config.setServiceHost("localhost");
        Server server = new NettyServer(config);
        server.registService(HelloService.class, new HelloServiceImpl());
        server.start();

        try {
            Thread.sleep(2000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.stop();
    }

}
