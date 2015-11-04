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

package com.dinstone.rpc.mina.client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dinstone.rpc.Client;
import com.dinstone.rpc.Configuration;
import com.dinstone.rpc.RpcException;
import com.dinstone.rpc.cases.HelloService;
import com.dinstone.rpc.cases.HelloServiceImpl;
import com.dinstone.rpc.cases.SuperInterface;
import com.dinstone.rpc.mina.server.MinaServer;
import com.dinstone.rpc.serialize.SerializeType;

/**
 * @author guojf
 * @version 1.0.0.2013-5-2
 */
public class ClientTest {

    private static MinaServer server;

    private Client client;

    @BeforeClass
    public static void startServer() {
        Configuration config = new Configuration();
        config.setServiceHost("localhost");
        server = new MinaServer(config);
        server.registService(HelloService.class, new HelloServiceImpl());
        server.start();
    }

    @AfterClass
    public static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Configuration config = new Configuration();
        config.setServiceHost("localhost");

        client = new MinaClient(config);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void testAsyncInvoke01() throws Throwable {
        Configuration config = new Configuration();
        config.setServiceHost("localhost");
        config.setSerializeType(SerializeType.HESSIAN);

        client = new MinaClient(config);

        long st = System.currentTimeMillis();

        client.asyncInvoke("com.dinstone.rpc.cases.HelloService.sayHello", new Object[] { "dddd" }).get();

        Object list = client.syncInvoke("com.dinstone.rpc.service.ServiceStats.serviceList", null);
        System.out.println(list);

        long et = System.currentTimeMillis() - st;
        System.out.println("it takes " + et + "ms, " + (1 * 1000 / et) + " tps");

    }

    @Test
    public void testSend01() throws IOException {
        HelloService service = client.getProxy(HelloService.class);
        long st = System.currentTimeMillis();

        service.sayHello("dinstone");

        long et = System.currentTimeMillis() - st;
        System.out.println("it takes " + et + "ms, " + (1 * 1000 / et) + " tps");

    }

    @Test(expected = RpcException.class)
    public void testSend02() {
        SuperInterface service = client.getProxy(HelloService.class);
        service.sayHello("dinstone", 31);
    }

    @Test
    public void testSend03() throws IOException {
        byte[] mb = new byte[1 * 1024];
        for (int i = 0; i < mb.length; i++) {
            mb[i] = 65;
        }

        String name = new String(mb);

        HelloService service = client.getProxy(HelloService.class);

        long st = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            service.sayHello(name);
        }

        long et = System.currentTimeMillis() - st;
        System.out.println("it takes " + et + "ms, 1k : " + (10000 * 1000 / et) + " tps");
    }

    @Test
    public void testSend031() throws IOException {
        byte[] mb = new byte[1 * 1024];
        for (int i = 0; i < mb.length; i++) {
            mb[i] = 65;
        }

        String name = new String(mb);

        HelloService service = client.getProxy(HelloService.class);

        long st = System.currentTimeMillis();

        int count = 120000;
        for (int i = 0; i < count; i++) {
            service.sayHello(name);
        }

        long et = System.currentTimeMillis() - st;
        System.out.println("it takes " + et + "ms, 1k : " + (count * 1000 / et) + " tps");
    }

    @Test
    public void testSend04() throws IOException {
        byte[] mb = new byte[2 * 1024];
        for (int i = 0; i < mb.length; i++) {
            mb[i] = 66;
        }

        String name = new String(mb);

        HelloService service = client.getProxy(HelloService.class);

        long st = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            service.sayHello(name);
        }

        long et = System.currentTimeMillis() - st;
        System.out.println("it takes " + et + "ms, 2k : " + (10000 * 1000 / et) + " tps");
    }

    @Test
    public void testSend05() throws IOException {
        byte[] mb = new byte[5 * 1024];
        for (int i = 0; i < mb.length; i++) {
            mb[i] = 67;
        }

        String name = new String(mb);

        HelloService service = client.getProxy(HelloService.class);

        long st = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            service.sayHello(name);
        }

        long et = System.currentTimeMillis() - st;
        System.out.println("it takes " + et + "ms, 5k : " + (10000 * 1000 / et) + " tps");
    }

    @Test
    public void testSend06() throws IOException, InterruptedException {
        byte[] mb = new byte[1 * 1024];
        for (int i = 0; i < mb.length; i++) {
            mb[i] = 65;
        }
        final String name = new String(mb);

        final Configuration config = new Configuration();
        config.setServiceHost("localhost");

        int count = 132;
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
                    Client client = new MinaClient(config);
                    HelloService service = client.getProxy(HelloService.class);
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }

                    try {
                        long st = System.currentTimeMillis();

                        for (int i = 0; i < 10000; i++) {
                            service.sayHello(name);
                        }

                        long et = System.currentTimeMillis() - st;
                        System.out.println("it takes " + et + "ms, 1k : " + (10000 * 1000 / et) + " tps");
                    } finally {
                        end.countDown();
                        client.close();
                    }
                }
            };
            t.start();
        }

        start.countDown();
        long st = System.currentTimeMillis();
        end.await();
        long et = System.currentTimeMillis() - st;

        System.out.println("it takes " + et + "ms, avg 1k : " + (count * 10000 * 1000 / et) + " tps");
    }

    @Test
    public void testSend07() throws IOException, InterruptedException {
        byte[] mb = new byte[8 * 1024];
        for (int i = 0; i < mb.length; i++) {
            mb[i] = 65;
        }
        final String name = new String(mb);

        final HelloService service = client.getProxy(HelloService.class);

        int count = 16;
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
    }

    @Test
    public void testSend08() throws IOException, InterruptedException {
        byte[] mb = new byte[8 * 1024];
        for (int i = 0; i < mb.length; i++) {
            mb[i] = 65;
        }
        final String name = new String(mb);

        final int count = 4;
        final HelloService[] services = new HelloService[count];

        // final HelloService service = client.getProxy(HelloService.class);
        Configuration config = new Configuration();
        config.setServiceHost("localhost");

        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            services[i] = new MinaClient(config).getProxy(HelloService.class);
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
                            getService(i).sayHello(name);
                        }

                        long et = System.currentTimeMillis() - st;
                        System.out.println("it takes " + et + "ms, 1k : " + (1000 * 1000 / et) + "  tps");
                    } finally {
                        end.countDown();
                        // client.close();
                    }
                }

                private HelloService getService(int i) {
                    return services[i % count];
                }
            };
            t.start();
        }

        start.countDown();
        long st = System.currentTimeMillis();
        end.await();
        long et = System.currentTimeMillis() - st;

        System.out.println("it takes " + et + "ms, avg 1k : " + (count * 1000 * 1000 / et) + " tps");
    }

}
