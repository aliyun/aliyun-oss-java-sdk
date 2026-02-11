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

package com.aliyun.oss.common.provider;

import java.io.ByteArrayInputStream;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.SystemPropertiesCredentialsProvider;
import com.aliyun.oss.common.utils.AuthUtils;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class SystemPropertiesCredentialsProviderTest extends TestBase {

    @Test
    public void testSystemPropertiesCredentialsProvider() {
        try {
            System.setProperty(AuthUtils.ACCESS_KEY_SYSTEM_PROPERTY, TestConfig.ROOT_ACCESS_KEY_ID);
            System.setProperty(AuthUtils.SECRET_KEY_SYSTEM_PROPERTY, TestConfig.ROOT_ACCESS_KEY_SECRET);
            System.clearProperty(AuthUtils.SESSION_TOKEN_SYSTEM_PROPERTY);

            SystemPropertiesCredentialsProvider credentialsProvider = new SystemPropertiesCredentialsProvider();
            Credentials credentials = credentialsProvider.getCredentials();
            Assert.assertEquals(credentials.getAccessKeyId(), TestConfig.ROOT_ACCESS_KEY_ID);
            Assert.assertEquals(credentials.getSecretAccessKey(), TestConfig.ROOT_ACCESS_KEY_SECRET);
            Assert.assertFalse(credentials.useSecurityToken());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSystemPropertiesCredentialsProviderInOss() {
        try {
            System.setProperty(AuthUtils.ACCESS_KEY_SYSTEM_PROPERTY, TestConfig.ROOT_ACCESS_KEY_ID);
            System.setProperty(AuthUtils.SECRET_KEY_SYSTEM_PROPERTY, TestConfig.ROOT_ACCESS_KEY_SECRET);
            System.setProperty(AuthUtils.SESSION_TOKEN_SYSTEM_PROPERTY, "");

            SystemPropertiesCredentialsProvider credentialsProvider = CredentialsProviderFactory
                    .newSystemPropertiesCredentialsProvider();

            String key = "test.txt";
            String content = "HelloOSS";
            String bucketName = getRandomBucketName();

            OSS ossClient = new OSSClientBuilder().build(TestConfig.OSS_ENDPOINT, credentialsProvider);
            ossClient.createBucket(bucketName);
            waitForCacheExpiration(2);
            ossClient.putObject(bucketName, key, new ByteArrayInputStream(content.getBytes()));
            ossClient.deleteObject(bucketName, key);
            ossClient.deleteBucket(bucketName);
            ossClient.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
