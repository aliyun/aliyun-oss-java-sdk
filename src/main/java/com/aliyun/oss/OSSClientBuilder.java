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
                getClientConfiguration());
    }

    @Override
    public OSS build(String endpoint, String accessKeyId, String secretAccessKey, String securityToken) {
        return new OSSClient(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken),
                getClientConfiguration());
    }

    @Override
    public OSS build(String endpoint, String accessKeyId, String secretAccessKey, ClientBuilderConfiguration config) {
        return new OSSClient(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey),
                getClientConfiguration(config));
    }

    @Override
    public OSS build(String endpoint, String accessKeyId, String secretAccessKey, String securityToken,
            ClientBuilderConfiguration config) {
        return new OSSClient(endpoint, getDefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken),
                getClientConfiguration(config));
    }

    @Override
    public OSS build(String endpoint, CredentialsProvider credsProvider) {
        return new OSSClient(endpoint, credsProvider, getClientConfiguration());
    }

    @Override
    public OSS build(String endpoint, CredentialsProvider credsProvider, ClientBuilderConfiguration config) {
        return new OSSClient(endpoint, credsProvider, getClientConfiguration(config));
    }

    private static ClientBuilderConfiguration getClientConfiguration() {
        return new ClientBuilderConfiguration();
    }

    private static ClientBuilderConfiguration getClientConfiguration(ClientBuilderConfiguration config) {
        if (config == null) {
            config = new ClientBuilderConfiguration();
        }
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
