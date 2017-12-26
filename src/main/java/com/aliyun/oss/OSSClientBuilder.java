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

package com.aliyun.oss;

import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;

/**
 * Fluent builder for OSS Client. Use of the builder is preferred over using
 * constructors of the client class.
 */
public class OSSClientBuilder implements OSSBuilder {

    @Override
    public OSS build(String endpoint, String accessKeyId, String secretAccessKey) {
        return new OSSClient(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey),
                getDefaultClientConfiguration());
    }

    @Override
    public OSS build(String endpoint, String accessKeyId, String secretAccessKey, String securityToken) {
        return new OSSClient(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken),
                getDefaultClientConfiguration());
    }

    @Override
    public OSS build(String endpoint, String accessKeyId, String secretAccessKey, ClientConfiguration config) {
        if (config == null) {
            config = getDefaultClientConfiguration();
        }
        return new OSSClient(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey), config);
    }

    @Override
    public OSS build(String endpoint, String accessKeyId, String secretAccessKey, String securityToken,
            ClientConfiguration config) {
        if (config == null) {
            config = getDefaultClientConfiguration();
        }
        return new OSSClient(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken),
                config);
    }

    @Override
    public OSS build(String endpoint, CredentialsProvider credsProvider) {
        return new OSSClient(endpoint, credsProvider, getDefaultClientConfiguration());
    }

    @Override
    public OSS build(String endpoint, CredentialsProvider credsProvider, ClientConfiguration config) {
        if (config == null) {
            config = getDefaultClientConfiguration();
        }
        return new OSSClient(endpoint, credsProvider, config);
    }

    private static ClientConfiguration getDefaultClientConfiguration() {
        ClientConfiguration config = new ClientConfiguration();
        config.setSupportCname(false);
        return config;
    }

    private static DefaultCredentialProvider getDefaultCredentialProvider(String accessKeyId, String secretAccessKey) {
        return new DefaultCredentialProvider(accessKeyId, secretAccessKey);
    }

    private static DefaultCredentialProvider getDefaultCredentialProvider(String accessKeyId, String secretAccessKey,
            String securityToken) {
        return new DefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken);
    }

}
