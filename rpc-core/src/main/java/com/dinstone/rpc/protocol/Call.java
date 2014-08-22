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

import java.io.Serializable;
import java.util.Arrays;

/**
 * The Call is the abstract of invoking method, RPC protocol body part.
 * 
 * @author guojinfei
 * @version 1.0.0.2014-6-23
 */
public class Call implements IBody, Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    private String method;

    private Object[] params;

    public Call() {
        super();
    }

    public Call(String method, Object[] params) {
        super();
        this.method = method;
        this.params = params;
    }

    /**
     * the method to get
     * 
     * @return the method
     * @see Call#method
     */
    public String getMethod() {
        return method;
    }

    /**
     * the method to set
     * 
     * @param method
     * @see Call#method
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * the params to get
     * 
     * @return the params
     * @see Call#params
     */
    public Object[] getParams() {
        return params;
    }

    /**
     * the params to set
     * 
     * @param params
     * @see Call#params
     */
    public void setParams(Object[] params) {
        this.params = params;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{method=" + method + ", params=" + Arrays.toString(params) + "}";
    }

}
