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
package com.dinstone.rpc.serialize;

import java.util.Date;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author guojinfei
 * @version 1.0.0.2014-10-9
 */
public class JacksonSerializerTest {

    private JacksonSerializer js;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        js = new JacksonSerializer();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("name", "dinstone");
        data.put("birthday", new Date());
        data.put("street", "中国 \r\n \"AA\" 'BB'");
        System.out.println("before serialize data is " + data);
        try {
            byte[] bs = js.serialize(data);
            System.out.println(new String(bs));
            HashMap dsm = js.deserialize(bs, data.getClass());
            System.out.println("deserialize data is " + dsm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test01() {
        String[] data = new String[] { "a", "b" };
        try {
            byte[] bs = js.serialize(data);
            System.out.println(new String(bs));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
