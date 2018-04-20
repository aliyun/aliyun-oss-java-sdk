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
     * Construct the URL of the authorization server
     * 
     * @return the url of authorization server
     * @throws ClientException
     */
    public URL buildUrl() throws ClientException;

    /**
     * Sends HTTP request to the authorization server
     * 
     * @param request
     *          
     * @return http response
     * @throws IOException
     */
    public HttpResponse send(HttpRequest request) throws IOException;

    /**
     * Parse the authorization information returned by the authorization server , resolved as Credentials
     * 
     * @param response
     * Authorization information returned by the authorization server
     * @return
     * @throws ClientException
     */
    public Credentials parse(HttpResponse response) throws ClientException;

    /**
     * Get authorization from the authorization server
     * 
     * @return credentials
     * @throws ClientException
     */
    public Credentials fetch() throws ClientException;

    /**
     * Get authorization from the authorization server
     * 
     * @param retryTimes
     * 
     * @return credentials
     * @throws ClientException
     */
    public Credentials fetch(int retryTimes) throws ClientException;
}
