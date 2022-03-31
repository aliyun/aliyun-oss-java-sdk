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

package com.aliyun.oss.integrationtests;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import org.junit.jupiter.api.*;
import org.junit.Test;

import static com.aliyun.oss.OSSErrorCode.REQUEST_TIME_TOO_SKEWED;

public class ClientBuilderTest extends TestBase {

    private final static String TEST_KEY = "test/test.txt";
    private final static String TEST_CONTENT = "Hello OSS.";

    @Test
    public void testClientBuilderDefault() {
        try {
            OSSClient ossClient = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
            Assertions.assertFalse(ossClient.getClientConfiguration().isSupportCname());

            BucketInfo info = ossClient.getBucketInfo(bucketName);
            Assertions.assertEquals(info.getBucket().getName(), bucketName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(TEST_CONTENT.getBytes().length);
            ossClient.putObject(bucketName, TEST_KEY, new ByteArrayInputStream(TEST_CONTENT.getBytes()), metadata);

            OSSObject ossObject = ossClient.getObject(bucketName, TEST_KEY);
            InputStream inputStream = ossObject.getObjectContent();
            inputStream.close();

            ossClient.deleteObject(bucketName, TEST_KEY);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testClientBuilderWithCredentialProvider() {
        try {
            OSSClient ossClient = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                            TestConfig.OSS_TEST_ACCESS_KEY_SECRET),
                    null);
            Assertions.assertFalse(ossClient.getClientConfiguration().isSupportCname());

            BucketInfo info = ossClient.getBucketInfo(bucketName);
            Assertions.assertEquals(info.getBucket().getName(), bucketName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(TEST_CONTENT.getBytes().length);
            ossClient.putObject(bucketName, TEST_KEY, new ByteArrayInputStream(TEST_CONTENT.getBytes()), metadata);

            OSSObject ossObject = ossClient.getObject(bucketName, TEST_KEY);
            InputStream inputStream = ossObject.getObjectContent();
            inputStream.close();

            ossClient.deleteObject(bucketName, TEST_KEY);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testClientBuilderWithBuilderConfiguration() {
        try {
            OSSClient ossClient = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                            TestConfig.OSS_TEST_ACCESS_KEY_SECRET),
                    new ClientBuilderConfiguration());
            Assertions.assertFalse(ossClient.getClientConfiguration().isSupportCname());

            BucketInfo info = ossClient.getBucketInfo(bucketName);
            Assertions.assertEquals(info.getBucket().getName(), bucketName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(TEST_CONTENT.getBytes().length);
            ossClient.putObject(bucketName, TEST_KEY, new ByteArrayInputStream(TEST_CONTENT.getBytes()), metadata);

            OSSObject ossObject = ossClient.getObject(bucketName, TEST_KEY);
            InputStream inputStream = ossObject.getObjectContent();
            inputStream.close();

            ossClient.deleteObject(bucketName, TEST_KEY);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testClientBuilderWithSTS() {
        try {
            ClientBuilderConfiguration config = new ClientBuilderConfiguration();
            config.setSupportCname(true);
            config.setConnectionTimeout(10000);
            OSSClient ossClient = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                            TestConfig.OSS_TEST_ACCESS_KEY_SECRET, "TOKEN"), config);
            Assertions.assertTrue(ossClient.getClientConfiguration().isSupportCname());
            Assertions.assertEquals(ossClient.getClientConfiguration().getConnectionTimeout(), 10000);

            Credentials cred = ossClient.getCredentialsProvider().getCredentials();
            Assertions.assertEquals(cred.getAccessKeyId(), TestConfig.OSS_TEST_ACCESS_KEY_ID);
            Assertions.assertEquals(cred.getSecretAccessKey(), TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
            Assertions.assertEquals(cred.getSecurityToken(), "TOKEN");
            Assertions.assertTrue(cred.useSecurityToken());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testClientBuilderWithAll() {
        try {
            ClientBuilderConfiguration config = new ClientBuilderConfiguration();
            config.setSupportCname(true);
            config.setConnectionTimeout(10000);
            OSSClient ossClient = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                            TestConfig.OSS_TEST_ACCESS_KEY_SECRET),
                    config);
            Assertions.assertTrue(ossClient.getClientConfiguration().isSupportCname());
            Assertions.assertEquals(ossClient.getClientConfiguration().getConnectionTimeout(), 10000);

            BucketInfo info = ossClient.getBucketInfo(bucketName);
            Assertions.assertEquals(info.getBucket().getName(), bucketName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(TEST_CONTENT.getBytes().length);
            ossClient.putObject(bucketName, TEST_KEY, new ByteArrayInputStream(TEST_CONTENT.getBytes()), metadata);

            OSSObject ossObject = ossClient.getObject(bucketName, TEST_KEY);
            InputStream inputStream = ossObject.getObjectContent();
            inputStream.close();

            ossClient.deleteObject(bucketName, TEST_KEY);

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testClientBuilderSpecialEpochTicks() {
        OSSClient client = null;
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();
        Assertions.assertEquals(config.getTickOffset(), 0);
        config.setSupportCname(true);
        config.setConnectionTimeout(10000);
        config.setSlowRequestsThreshold(0);
        try {
            long epochTicks = new Date().getTime();
            epochTicks -= 16*600*1000;
            config.setTickOffset(epochTicks);
            client = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                            TestConfig.OSS_TEST_ACCESS_KEY_SECRET),
                    config);
            Assertions.assertTrue(client.getClientConfiguration().isSupportCname());
            Assertions.assertEquals(client.getClientConfiguration().getConnectionTimeout(), 10000);
            Assertions.assertEquals(client.getClientConfiguration().getTickOffset(), -16*600*1000);

            BucketInfo info = client.getBucketInfo(bucketName);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                String errorCode = ((ServiceException) e).getErrorCode();
                Assertions.assertEquals(errorCode, REQUEST_TIME_TOO_SKEWED);
            } else {
                Assertions.assertTrue(false);
            }
        } finally {
            client.shutdown();
        }

        try {
            long epochTicks = new Date().getTime();
            config.setTickOffset(epochTicks);
            client = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                            TestConfig.OSS_TEST_ACCESS_KEY_SECRET),
                    config);
            Assertions.assertTrue(client.getClientConfiguration().isSupportCname());
            Assertions.assertEquals(client.getClientConfiguration().getConnectionTimeout(), 10000);

            BucketInfo info = client.getBucketInfo(bucketName);
            Assertions.assertEquals(info.getBucket().getName(), bucketName);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        } finally {
            client.shutdown();
        }
    }

    @Test
    public void testClientBuilderWithInvalidEndpoint() {
        OSSClient client = null;
        try {
            OSSClient ossClient = (OSSClient) new OSSClientBuilder().build(
                    "http://oss-cn-hangzhou.aliyuncs.com\\oss-cn-shenzhen.aliyuncs.com?test=123",
                    new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                            TestConfig.OSS_TEST_ACCESS_KEY_SECRET),
                    new ClientBuilderConfiguration());
            Assertions.fail("should not here.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testClientBuilderLogConnectionPoolStats() {
        OSSClient client = null;
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();
        config.setMaxConnections(10);
        Assertions.assertEquals(config.isLogConnectionPoolStatsEnable(), false);
        config.setLogConnectionPoolStats(true);
        Assertions.assertEquals(config.isLogConnectionPoolStatsEnable(), true);
        config.setSlowRequestsThreshold(0);
        try {
            client = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                            TestConfig.OSS_TEST_ACCESS_KEY_SECRET),
                    config);

            final OSSClient innerClient = client;

            int threadCount = 20;
            Thread[] ts = new Thread[threadCount];
            for (int i = 0; i < threadCount; i++) {
                final int seqNum = i;
                Runnable r = new Runnable() {

                    @Override
                    public void run() {
                        try {
                            BucketInfo info = innerClient.getBucketInfo(bucketName);
                            Assertions.assertEquals(info.getBucket().getName(), bucketName);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                };

                ts[i] = new Thread(r);
            }

            for (int i = 0; i < threadCount; i++) {
                ts[i].start();
            }

            for (int i = 0; i < threadCount; i++) {
                ts[i].join();
            }

            String str  = client.getConnectionPoolStats();
            Assertions.assertTrue(str.indexOf("leased: 0") != -1);
            Assertions.assertTrue(str.indexOf("pending: 0") != -1);
            Assertions.assertTrue(str.indexOf("available: 10") != -1);
            Assertions.assertTrue(str.indexOf("max: 10") != -1);

        } catch (Exception e) {
            Assertions.assertTrue(false);
        } finally {
            client.shutdown();
        }
    }
}
