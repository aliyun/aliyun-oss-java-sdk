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
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.InstanceProfileCredentials;
import com.aliyun.oss.common.auth.InstanceProfileCredentialsFetcher;
import com.aliyun.oss.common.auth.InstanceProfileCredentialsProvider;
import com.aliyun.oss.common.provider.mock.InstanceProfileCredentialsFetcherMock;
import com.aliyun.oss.common.provider.mock.InstanceProfileCredentialsFetcherMock.ResponseCategory;
import org.junit.jupiter.api.*;
import org.junit.Ignore;
import org.junit.Test;

public class InstanceProfileCredentialsProviderTest extends TestBase {

    @Test
    public void testGetNormalCredentials() {
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.Normal).withRoleName(TestConfig.ECS_ROLE_NAME);
            InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider(
                    TestConfig.ECS_ROLE_NAME).withCredentialsFetcher(credentialsFetcher);

            InstanceProfileCredentials credentials = credentialsProvider.getCredentials();
            Assertions.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assertions.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assertions.assertEquals(credentials.getSecurityToken().length(), 536);
            Assertions.assertTrue(credentials.useSecurityToken());
            Assertions.assertFalse(credentials.willSoonExpire());
            Assertions.assertFalse(credentials.isExpired());
            Assertions.assertTrue(credentials.shouldRefresh());

            credentials.setLastFailedRefreshTime();
            Assertions.assertFalse(credentials.isExpired());
            Assertions.assertFalse(credentials.shouldRefresh());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetExpiredCredentials() {
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.Expired).withRoleName(TestConfig.ECS_ROLE_NAME);
            InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider(
                    TestConfig.ECS_ROLE_NAME).withCredentialsFetcher(credentialsFetcher);

            InstanceProfileCredentials credentials = credentialsProvider.getCredentials();
            Assertions.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assertions.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assertions.assertEquals(credentials.getSecurityToken().length(), 536);
            Assertions.assertTrue(credentials.useSecurityToken());
            Assertions.assertTrue(credentials.willSoonExpire());
            Assertions.assertTrue(credentials.isExpired());
            Assertions.assertTrue(credentials.shouldRefresh());

            credentials.setLastFailedRefreshTime();
            Assertions.assertTrue(credentials.isExpired());
            Assertions.assertFalse(credentials.shouldRefresh());

            credentials = credentialsProvider.getCredentials();
            Assertions.assertNotNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetCredentialsServerHalt() {
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.ServerHalt).withRoleName(TestConfig.ECS_ROLE_NAME);
            InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider(
                    TestConfig.ECS_ROLE_NAME).withCredentialsFetcher(credentialsFetcher);

            InstanceProfileCredentials credentials = credentialsProvider.getCredentials();
            Assertions.assertNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetExceptionalCredentials() {
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.Exceptional).withRoleName(TestConfig.ECS_ROLE_NAME);
            InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider(
                    TestConfig.ECS_ROLE_NAME).withCredentialsFetcher(credentialsFetcher);

            InstanceProfileCredentials credentials = credentialsProvider.getCredentials();
            Assertions.assertNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRefreshCredentials() {
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.Expired).withRoleName(TestConfig.ECS_ROLE_NAME);
            InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider(
                    TestConfig.ECS_ROLE_NAME).withCredentialsFetcher(credentialsFetcher);

            InstanceProfileCredentials credentials = credentialsProvider.getCredentials();
            Assertions.assertTrue(credentials.willSoonExpire());

            InstanceProfileCredentialsFetcher credentialsFetcher2 = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.Normal).withRoleName(TestConfig.ECS_ROLE_NAME);
            credentialsProvider.withCredentialsFetcher(credentialsFetcher2);

            credentials = credentialsProvider.getCredentials();
            Assertions.assertFalse(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testGetCredentialsNegative() {
        try {
            InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider(
                    "NotExistRoleName");
            InstanceProfileCredentials credentials = credentialsProvider.getCredentials();
            Assertions.assertNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    /**
     * NOTE: Run this case on ecs.
     */
    @Ignore
    public void testGetCredentialsOnEcs() {
        try {
            // TODO: Establish a simulated ECS metadata service
            InstanceProfileCredentialsProvider credentialsProvider = new InstanceProfileCredentialsProvider(
                    TestConfig.ECS_ROLE_NAME);
            InstanceProfileCredentials credentials = credentialsProvider.getCredentials();
            Assertions.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assertions.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assertions.assertEquals(credentials.getSecurityToken().length(), 536);
            Assertions.assertTrue(credentials.useSecurityToken());
            Assertions.assertFalse(credentials.willSoonExpire());
            Assertions.assertFalse(credentials.isExpired());
            Assertions.assertTrue(credentials.shouldRefresh());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    /**
     * NOTE: Run this case on ecs.
     */
    @Ignore
    public void testGetOssCredentialsOnEcs() {
        try {
            // TODO: Establish a simulated ECS metadata service
            CredentialsProvider credentialsProvider = CredentialsProviderFactory
                    .newInstanceProfileCredentialsProvider(TestConfig.RAM_REGION_ID);

            String key = "test.txt";
            String content = "HelloOSS";

            OSS ossClient = new OSSClientBuilder().build(TestConfig.OSS_ENDPOINT, credentialsProvider);
            ossClient.putObject(TestConfig.OSS_BUCKET, key, new ByteArrayInputStream(content.getBytes()));
            ossClient.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

}
