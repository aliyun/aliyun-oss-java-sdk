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

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import junit.framework.Assert;
import org.junit.Test;

public class ClientBuilderTest extends TestBase {

    private final static String TEST_KEY = "test/test.txt";
    private final static String TEST_CONTENT = "Hello OSS.";

    @Test
    public void testClientBuilderDefault() {
        try {
            OSSClient ossClient = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
            Assert.assertFalse(ossClient.getClientConfiguration().isSupportCname());

            BucketInfo info = ossClient.getBucketInfo(bucketName);
            Assert.assertEquals(info.getBucket().getName(), bucketName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(TEST_CONTENT.getBytes().length);
            ossClient.putObject(bucketName, TEST_KEY, new ByteArrayInputStream(TEST_CONTENT.getBytes()), metadata);

            OSSObject ossObject = ossClient.getObject(bucketName, TEST_KEY);
            InputStream inputStream = ossObject.getObjectContent();
            inputStream.close();

            ossClient.deleteObject(bucketName, TEST_KEY);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testClientBuilderWithCredentialProvider() {
        try {
            OSSClient ossClient = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                            TestConfig.OSS_TEST_ACCESS_KEY_SECRET),
                    null);
            Assert.assertFalse(ossClient.getClientConfiguration().isSupportCname());

            BucketInfo info = ossClient.getBucketInfo(bucketName);
            Assert.assertEquals(info.getBucket().getName(), bucketName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(TEST_CONTENT.getBytes().length);
            ossClient.putObject(bucketName, TEST_KEY, new ByteArrayInputStream(TEST_CONTENT.getBytes()), metadata);

            OSSObject ossObject = ossClient.getObject(bucketName, TEST_KEY);
            InputStream inputStream = ossObject.getObjectContent();
            inputStream.close();

            ossClient.deleteObject(bucketName, TEST_KEY);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testClientBuilderWithBuilderConfiguration() {
        try {
            OSSClient ossClient = (OSSClient) new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                            TestConfig.OSS_TEST_ACCESS_KEY_SECRET),
                    new ClientBuilderConfiguration());
            Assert.assertFalse(ossClient.getClientConfiguration().isSupportCname());

            BucketInfo info = ossClient.getBucketInfo(bucketName);
            Assert.assertEquals(info.getBucket().getName(), bucketName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(TEST_CONTENT.getBytes().length);
            ossClient.putObject(bucketName, TEST_KEY, new ByteArrayInputStream(TEST_CONTENT.getBytes()), metadata);

            OSSObject ossObject = ossClient.getObject(bucketName, TEST_KEY);
            InputStream inputStream = ossObject.getObjectContent();
            inputStream.close();

            ossClient.deleteObject(bucketName, TEST_KEY);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
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
            Assert.assertTrue(ossClient.getClientConfiguration().isSupportCname());
            Assert.assertEquals(ossClient.getClientConfiguration().getConnectionTimeout(), 10000);

            Credentials cred = ossClient.getCredentialsProvider().getCredentials();
            Assert.assertEquals(cred.getAccessKeyId(), TestConfig.OSS_TEST_ACCESS_KEY_ID);
            Assert.assertEquals(cred.getSecretAccessKey(), TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
            Assert.assertEquals(cred.getSecurityToken(), "TOKEN");
            Assert.assertTrue(cred.useSecurityToken());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
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
            Assert.assertTrue(ossClient.getClientConfiguration().isSupportCname());
            Assert.assertEquals(ossClient.getClientConfiguration().getConnectionTimeout(), 10000);

            BucketInfo info = ossClient.getBucketInfo(bucketName);
            Assert.assertEquals(info.getBucket().getName(), bucketName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(TEST_CONTENT.getBytes().length);
            ossClient.putObject(bucketName, TEST_KEY, new ByteArrayInputStream(TEST_CONTENT.getBytes()), metadata);

            OSSObject ossObject = ossClient.getObject(bucketName, TEST_KEY);
            InputStream inputStream = ossObject.getObjectContent();
            inputStream.close();

            ossClient.deleteObject(bucketName, TEST_KEY);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}
