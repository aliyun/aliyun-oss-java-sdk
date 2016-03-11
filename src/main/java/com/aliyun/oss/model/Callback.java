/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.oss.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传成功后OSS可以向callbackUrl发送回调请求，请求方法为POST，body为callbackBody指定的内容。
 * 支持CallBack的API接口有：PutObject、PostObject、CompleteMultipartUpload。
 * 
 */
public class Callback {

    public static enum CalbackBodyType {
        URL(1), JSON(2);

        private int nCode;

        private CalbackBodyType(int nCode) {
            this.nCode = nCode;
        }

        @Override
        public String toString() {
            return String.valueOf(this.nCode);
        }
    }
    
    public String getCallbackUrl() {
        return callbackUrl;
    }

    /**
     * 文件上传成功后OSS向此url发送回调请求，请求方法为POST，body为callbackBody指定的内容。正常情况下，
     * 该url需要响应“HTTP/1.1 200 OK”，body必须为JSON格式，响应头Content-Length必须为合法的值，
     * 且不超过3MB。 
     * 支持同时配置最多5个url，以";"分割。OSS会依次发送请求直到第一个返回成功为止。 
     * 如果没有配置或者值为空则认为没有配置callback。 支持HTTPS地址 。
     * 为了保证正确处理中文等情况，callbackUrl需做url编码处理。
     * 
     * @param callbackUrl OSS回调请求发送地址
     */
    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackHost() {
        return callbackHost;
    }

    /**
     * 发起回调请求时Host头的值，只有在设置了callbackUrl时才有效 。如果没有配置 callbckHost，
     * 则会解析callbackUrl中的url并将解析出的host填充到callbackHost中。
     * 
     * @param callbackHost OSS回调请求头中的Host值
     */
    public void setCallbackHost(String callbackHost) {
        this.callbackHost = callbackHost;
    }

    public String getCallbackBody() {
        return callbackBody;
    }

    /**
     * OSS发起回调时请求body的值，例如：key=$(key)&etag=$(etag)&my_var=$(x:my_var)。
     * 支持OSS系统变量、自定义变量和常量 。自定义变量的callbackVar传递。
     * 
     * @param callbackBody OSS回调请求的Body值
     */
    public void setCallbackBody(String callbackBody) {
        this.callbackBody = callbackBody;
    }

    
    public CalbackBodyType getCalbackBodyType() {
        return calbackBodyType;
    }

    /**
     * OSS发起回调请求的Content-Type，支持application/x-www-form-urlencoded(url)和application/json(json)，
     * 默认为前者。如果为application/x-www-form-urlencoded，则callbackBody中的变量将会被经过url编码的值替换掉，
     * 如果为application/json，则会按照json格式替换其中的变量。
     * 
     * @param calbackBodyType OSS回调请求头Content-Type值
     */
    public void setCalbackBodyType(CalbackBodyType calbackBodyType) {
        this.calbackBodyType = calbackBodyType;
    }

    public Map<String, String> getCallbackVar() {
        return callbackVar;
    }

    /**
     * 用户置自定义参数。
     * 
     * 自定义参数是一个Key-Value的Map，用户可以配置自己需要的参数到这个Map。在OSS发起POST回调请求的时，
     * 会将这些参数和系统参数一起放在POST请求的body中以方便接收回调方获取。
     * 用户自定义参数的Key一定要以"x:"开头，如 x:my_var。
     * 
     * @param callbackVar 用户自定义参数
     */
    public void setCallbackVar(Map<String, String> callbackVar) {
        this.callbackVar.clear();
        if (callbackVar != null && !callbackVar.isEmpty()) {
            this.callbackVar.putAll(callbackVar);
        }
    }
    
    /**
     * 用户置自定义参数。
     * 
     * 自定义参数是一个Key-Value的Map，用户可以配置自己需要的参数到这个Map。在OSS发起POST回调请求的时，
     * 会将这些参数和系统参数一起放在POST请求的body中以方便接收回调方获取。
     * 用户自定义参数的Key一定要以"x:"开头，如 x:my_var。
     * 
     * @param key 用户自定义参数Key
     * @param value 用户自定义参数Value
     */
    public void addCallbackVar(String key, String value) {
        this.callbackVar.put(key, value);
    }
    
    public boolean hasCallbackVar() {
        if (this.callbackVar != null && this.callbackVar.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 文件上传成功后OSS向此url发送回调请求，请求方法为POST，body为callbackBody指定的内容。
     */
    private String callbackUrl;
    
    /**
     * 发起回调请求时Host头的值，只有在设置了callbackUrl时才有效; 如果没有配置callbckHost，
     * 则会解析callbackUrl中的url并将解析出的host填充到callbackHost中。
     */
    private String callbackHost;
    
    /**
     * 发起回调时请求body的值，支持OSS系统变量、自定义变量和常量。
     */
    private String callbackBody;

    /**
     * 发起回调请求的Content-Type，支持url和json，默认为前者。
     */
    private CalbackBodyType calbackBodyType;

    /**
     * 自定义参数是一个Key-Value的Map，Key一定要以x:开头。OSS发起POST回调请求的时，
     * 会将自定义参数参数和系统参数一起放在POST请求的body中以方便接收回调方获取。
     */
    private Map<String, String> callbackVar = new HashMap<String, String>();

}
