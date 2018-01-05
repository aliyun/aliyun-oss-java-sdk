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

package com.aliyun.oss.common.auth;

import java.io.IOException;
import java.net.URL;

import com.aliyun.oss.common.auth.Credentials;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.HttpRequest;
import com.aliyuncs.http.HttpResponse;

public interface CredentialsFetcher {

    /**
     * 构造授权服务器的URL
     * 
     * @return the url of authorization server
     * @throws ClientException
     */
    public URL buildUrl() throws ClientException;

    /**
     * 发送HTTP请求到授权服务器
     * 
     * @param request
     *            HTTP请求
     * @return http response
     * @throws IOException
     */
    public HttpResponse send(HttpRequest request) throws IOException;

    /**
     * 解析授权服务器返回的授权信息，解析为Credentials
     * 
     * @param response
     *            授权服务器返回的授权信息
     * @return
     * @throws ClientException
     */
    public Credentials parse(HttpResponse response) throws ClientException;

    /**
     * 从授权服务器获取授权
     * 
     * @return credentials
     * @throws ClientException
     */
    public Credentials fetch() throws ClientException;

    /**
     * 从授权服务器获取授权
     * 
     * @param retryTimes
     *            失败重试此时
     * @return credentials
     * @throws ClientException
     */
    public Credentials fetch(int retryTimes) throws ClientException;
}
