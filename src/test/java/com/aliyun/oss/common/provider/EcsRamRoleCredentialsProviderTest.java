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

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.provider.mock.EcsRamRoleCredentialsFetcherMock;
import com.aliyun.oss.common.provider.mock.EcsRamRoleCredentialsFetcherMock.ResponseCategory;
import junit.framework.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;

public class EcsRamRoleCredentialsProviderTest extends TestBase {

    @Test
    public void testGetNormalCredentials() {
        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.Normal);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            BasicCredentials credentials = (BasicCredentials) credentialsProvider.getCredentials();
            Assert.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assert.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assert.assertEquals(credentials.getSecurityToken().length(), 536);
            Assert.assertTrue(credentials.useSecurityToken());
            Assert.assertFalse(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetNormalWithoutExpirationCredentials() {
        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.NormalWithoutExpiration);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            BasicCredentials credentials = (BasicCredentials) credentialsProvider.getCredentials();
            Assert.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assert.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assert.assertFalse(credentials.useSecurityToken());
            Assert.assertFalse(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetNormalWithoutTokenCredentials() {
        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.NormalWithoutToken);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            BasicCredentials credentials = (BasicCredentials) credentialsProvider.getCredentials();
            Assert.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assert.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assert.assertFalse(credentials.useSecurityToken());
            Assert.assertFalse(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetExpiredCredentials() {
        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.Expired);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            BasicCredentials credentials = (BasicCredentials) credentialsProvider.getCredentials();
            Assert.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assert.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assert.assertEquals(credentials.getSecurityToken().length(), 536);
            Assert.assertTrue(credentials.useSecurityToken());
            Assert.assertTrue(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetCredentialsServerHalt() {
        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.ServerHalt);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            Credentials credentials = credentialsProvider.getCredentials();
            Assert.assertNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetExceptionalCredentials() {
        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.Exceptional);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            Credentials credentials = credentialsProvider.getCredentials();
            Assert.assertNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.ExceptionalWithoutStatus);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            Credentials credentials = credentialsProvider.getCredentials();
            Assert.assertNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.ExceptionalFailStatus);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            Credentials credentials = credentialsProvider.getCredentials();
            Assert.assertNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.ExceptionalWithoutAK);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            Credentials credentials = credentialsProvider.getCredentials();
            Assert.assertNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.ExceptionalWithoutSK);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            Credentials credentials = credentialsProvider.getCredentials();
            Assert.assertNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRefreshCredentials() {
        try {
            EcsRamRoleCredentialsFetcher credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(
                    TestConfig.OSS_AUTH_SERVER_HOST).withResponseCategory(ResponseCategory.Expired);
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST).withCredentialsFetcher(credentialsFetcher);

            BasicCredentials credentials = (BasicCredentials) credentialsProvider.getCredentials();
            Assert.assertTrue(credentials.willSoonExpire());

            credentialsFetcher = new EcsRamRoleCredentialsFetcherMock(TestConfig.OSS_AUTH_SERVER_HOST)
                    .withResponseCategory(ResponseCategory.Normal);
            credentialsProvider = new EcsRamRoleCredentialsProvider(TestConfig.ECS_ROLE_NAME)
                    .withCredentialsFetcher(credentialsFetcher);

            credentials = (BasicCredentials) credentialsProvider.getCredentials();
            Assert.assertFalse(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetCredentialsNegative() {
        try {
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST + "/noteixst");
            Credentials credentials = credentialsProvider.getCredentials();
            Assert.assertNull(credentials);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    public void testGetCredentialsFromAuthInOss() {
        try {
            EcsRamRoleCredentialsProvider credentialsProvider = new EcsRamRoleCredentialsProvider(
                    TestConfig.OSS_AUTH_SERVER_HOST);
            Credentials credentials = credentialsProvider.getCredentials();
            Assert.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assert.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assert.assertEquals(credentials.getSecurityToken().length(), 516);
            Assert.assertTrue(credentials.useSecurityToken());

            String key = "test.txt";
            String content = "HelloOSS";
            String bucketName = getRandomBucketName();

            OSS ossClient = new OSSClientBuilder().build(TestConfig.OSS_ENDPOINT, credentials.getAccessKeyId(),
                    credentials.getSecretAccessKey(), credentials.getSecurityToken());
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
