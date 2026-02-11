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


/**
 * Credentials provider factory to share providers across potentially many
 * clients.
 */
public class CredentialsProviderFactory {

    /**
     * Create an instance of DefaultCredentialProvider.
     * 
     * @param accessKeyId
     *            Access Key ID.
     * @param secretAccessKey
     *            Secret Access Key.
     * @return A {@link DefaultCredentialProvider} instance.
     */
    public static DefaultCredentialProvider newDefaultCredentialProvider(String accessKeyId, String secretAccessKey) {
        return new DefaultCredentialProvider(accessKeyId, secretAccessKey);
    }

    /**
     * Create an instance of DefaultCredentialProvider.
     * 
     * @param accessKeyId
     *            Access Key ID.
     * @param secretAccessKey
     *            Secret Access Key.
     * @param securityToken
     *            Security Token from STS.
     * @return A {@link DefaultCredentialProvider} instance.
     */
    public static DefaultCredentialProvider newDefaultCredentialProvider(String accessKeyId, String secretAccessKey,
            String securityToken) {
        return new DefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken);
    }

    /**
     * Create an instance of EnvironmentVariableCredentialsProvider by reading
     * the environment variable to obtain the ak/sk, such as OSS_ACCESS_KEY_ID
     * and OSS_ACCESS_KEY_SECRET
     * 
     * @return A {@link EnvironmentVariableCredentialsProvider} instance.
     * @throws RuntimeException runtime exception.
     */
    public static EnvironmentVariableCredentialsProvider newEnvironmentVariableCredentialsProvider() {
        return new EnvironmentVariableCredentialsProvider();
    }

    /**
     * Create an instance of EnvironmentVariableCredentialsProvider by reading
     * the java system property used when starting up the JVM to enable the
     * default metrics collected by the OSS SDK, such as -Doss.accessKeyId and
     * -Doss.accessKeySecret.
     * 
     * @return A {@link SystemPropertiesCredentialsProvider} instance.
     * @throws RuntimeException runtime exception.

     */
    public static SystemPropertiesCredentialsProvider newSystemPropertiesCredentialsProvider() {
        return new SystemPropertiesCredentialsProvider();
    }

}