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

import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dinstone.rpc.RpcConfiguration;
import com.dinstone.rpc.cases.HelloService;
import com.dinstone.rpc.cases.HelloServiceImpl;
import com.dinstone.rpc.netty.server.NettyServer;
import com.dinstone.rpc.serialize.SerializeType;

public class NettyClientTest {

    private static NettyServer server;

    @BeforeClass
    public static void startServer() {
        RpcConfiguration config = new RpcConfiguration();
        config.setServiceHost("localhost");
        server = new NettyServer(config);
        server.registService(HelloService.class, new HelloServiceImpl());
        server.start();
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testGetProxy() throws InterruptedException {
        byte[] mb = new byte[8 * 1024];
        for (int i = 0; i < mb.length; i++) {
            mb[i] = 65;
        }
        final String name = new String(mb);

        RpcConfiguration config = new RpcConfiguration();
        config.setServiceHost("localhost");
        NettyClient client = new NettyClient(config);
        final HelloService service = client.getProxy(HelloService.class);

        int count = 4;
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            Thread t = new Thread() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see java.lang.Thread#run()
                 */
                @Override
                public void run() {
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }

                    try {
                        long st = System.currentTimeMillis();

                        for (int i = 0; i < 1000; i++) {
                            service.sayHello(name);
                        }

                        long et = System.currentTimeMillis() - st;
                        System.out.println("it takes " + et + "ms, 1k : " + (1000 * 1000 / et) + "  tps");
                    } finally {
                        end.countDown();
                        // client.close();
                    }
                }
            };
            t.start();
        }

        start.countDown();
        long st = System.currentTimeMillis();
        end.await();
        long et = System.currentTimeMillis() - st;

        System.out.println("it takes " + et + "ms, avg 1k : " + (count * 1000 * 1000 / et) + " tps");

        client.close();
    }

    @Test
    public void testAsyncInvoke() throws InterruptedException, Throwable {
        RpcConfiguration config = new RpcConfiguration();
        config.setServiceHost("localhost");
        config.setSerializeType(SerializeType.HESSIAN);

        NettyClient client = new NettyClient(config);

        long st = System.currentTimeMillis();

        Object retObj = client.asyncInvoke("com.dinstone.rpc.cases.HelloService.sayHello", new Object[] { "dinstone" })
            .get();
        Assert.assertNotNull(retObj);

        Object list = client.syncInvoke("com.dinstone.rpc.service.ServiceStats.serviceList", null);
        Assert.assertNotNull(list);
        System.out.println(list);

        long et = System.currentTimeMillis() - st;
        System.out.println("it takes " + et + "ms, " + (1 * 1000 / et) + " tps");

        client.close();
    }

    @Test(expected = NullPointerException.class)
    public void testAsyncInvokeException() throws InterruptedException, Throwable {
        RpcConfiguration config = new RpcConfiguration();
        config.setServiceHost("localhost");
        config.setSerializeType(SerializeType.JACKSON);

        NettyClient client = new NettyClient(config);

        long st = System.currentTimeMillis();

        client.asyncInvoke("com.dinstone.rpc.cases.HelloService.sayHello", new Object[] { null }).get();

        long et = System.currentTimeMillis() - st;
        System.out.println("it takes " + et + "ms, " + (1 * 1000 / et) + " tps");

        client.close();
    }

}
