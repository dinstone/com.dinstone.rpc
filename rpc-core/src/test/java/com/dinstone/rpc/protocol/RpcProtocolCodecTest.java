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

import org.junit.Test;

import com.dinstone.rpc.serialize.SerializeType;

public class RpcProtocolCodecTest {

    @Test
    public void testDecodeRequest() throws Exception {
        jacksonDecodeRequest();
        hessianDecodeRequest();
    }

    private void hessianDecodeRequest() throws Exception {
        byte[] rbs = RpcProtocolCodec.encodeRequest(createRequest(SerializeType.HESSIAN));

        long st = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            RpcProtocolCodec.decodeRequest(rbs).getCall();
        }

        long et = System.currentTimeMillis() - st;

        System.out.println("decode(HESSIAN) request takes " + et + "ms, " + (10000 * 1000 / et) + " tps");
    }

    private void jacksonDecodeRequest() throws Exception {
        byte[] rbs = RpcProtocolCodec.encodeRequest(createRequest(SerializeType.JACKSON));

        long st = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
            RpcProtocolCodec.decodeRequest(rbs).getCall();
        }

        long et = System.currentTimeMillis() - st;

        System.out.println("decode(JACKSON) request takes " + et + "ms, " + (10000 * 1000 / et) + " tps");
    }

    @Test
    public void testEncodeRequest() throws Exception {
        jacksonEncodeReqeust();
        hessianEncodeReqeust();
    }

    private void hessianEncodeReqeust() throws Exception {
        long st = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            RpcProtocolCodec.encodeRequest(createRequest(SerializeType.HESSIAN));
        }
        long et = System.currentTimeMillis() - st;
        System.out.println("encode(HESSIAN) request takes " + et + "ms, " + (10000 * 1000 / et) + " tps");
    }

    private void jacksonEncodeReqeust() throws Exception {
        long st = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            RpcProtocolCodec.encodeRequest(createRequest(SerializeType.JACKSON));
        }
        long et = System.currentTimeMillis() - st;
        System.out.println("encode(JACKSON) request takes " + et + "ms, " + (10000 * 1000 / et) + " tps");
    }

    private RpcRequest createRequest(SerializeType st) {
        byte[] mb = new byte[8 * 1024];
        for (int i = 0; i < mb.length; i++) {
            mb[i] = 65;
        }
        final String name = new String(mb);

        return new RpcRequest(new Header(1, st), new Call("com.dinstone.rpc.cases.HelloService.sayHello",
            new Object[] { name }));
    }

    @Test
    public void testEncodeResponse() {
    }

    @Test
    public void testDecodeResponse() {
    }

}
