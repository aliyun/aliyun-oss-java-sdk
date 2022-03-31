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
import java.net.URL;
import java.util.Date;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import org.junit.jupiter.api.*;

import org.junit.Test;

import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

public class AsyncFetchTaskTest extends TestBase {
    private final String objectName = "test-async-fetch-task-object";
    private String contentMd5;
    private String url;

    private OSSClient ossClient;
    private String bucketName;
    private String endpoint;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        bucketName = super.bucketName + "-aysnc-fetch-task";
        endpoint = TestConfig.OSS_TEST_ENDPOINT;

        //create client
        ClientConfiguration conf = new ClientConfiguration().setSupportCname(false);
        Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(credentials), conf);

        ossClient.createBucket(bucketName);
        waitForCacheExpiration(2);

        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream("123".getBytes()));

        ObjectMetadata meta = ossClient.getObjectMetadata(bucketName, objectName);
        contentMd5 = meta.getContentMD5();

        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        URL signedUrl = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
        url = signedUrl.toString();
    }

    @Override
    public void tearDown() throws Exception {
        if (ossClient != null) {
            ossClient.shutdown();
            ossClient = null;
        }
        super.tearDown();
    }

    @Test
    public void testNormalAsyncFetchTask() {
        try {
            final String destObject = objectName + "-destination";
            AsyncFetchTaskConfiguration configuration = new AsyncFetchTaskConfiguration()
                    .withUrl(url).withContentMd5(contentMd5).withIgnoreSameKey(false)
                    .withObjectName(destObject);

            SetAsyncFetchTaskResult setTaskResult = ossClient.setAsyncFetchTask(bucketName, configuration);
            String taskId = setTaskResult.getTaskId();

            Thread.sleep(1000 * 5);

            GetAsyncFetchTaskResult getTaskResult = ossClient.getAsyncFetchTask(bucketName, taskId);
            Assertions.assertEquals(taskId, getTaskResult.getTaskId());
            Assertions.assertEquals(AsyncFetchTaskState.Success, getTaskResult.getAsyncFetchTaskState());
            Assertions.assertTrue(getTaskResult.getErrorMsg().isEmpty());

            AsyncFetchTaskConfiguration taskInfo = getTaskResult.getAsyncFetchTaskConfiguration();
            Assertions.assertEquals(url, taskInfo.getUrl());
            Assertions.assertEquals(contentMd5, taskInfo.getContentMd5());
            Assertions.assertFalse(taskInfo.getIgnoreSameKey());
            Assertions.assertEquals(destObject, taskInfo.getObjectName());
            Assertions.assertTrue(taskInfo.getHost().isEmpty());
            Assertions.assertTrue(taskInfo.getCallback().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testArgumentNull() {
        try {
            final String destObject = objectName + "-destination";
            AsyncFetchTaskConfiguration configuration = new AsyncFetchTaskConfiguration()
                    .withUrl(url)
                    .withObjectName(destObject);

            SetAsyncFetchTaskResult setTaskResult = ossClient.setAsyncFetchTask(bucketName, configuration);
            String taskId = setTaskResult.getTaskId();

            Thread.sleep(1000 * 5);

            GetAsyncFetchTaskResult getTaskResult = ossClient.getAsyncFetchTask(bucketName, taskId);
            Assertions.assertEquals(taskId, getTaskResult.getTaskId());
            Assertions.assertEquals(AsyncFetchTaskState.Success, getTaskResult.getAsyncFetchTaskState());
            Assertions.assertTrue(getTaskResult.getErrorMsg().isEmpty());

            AsyncFetchTaskConfiguration taskInfo = getTaskResult.getAsyncFetchTaskConfiguration();
            Assertions.assertEquals(url, taskInfo.getUrl());
            Assertions.assertEquals(destObject, taskInfo.getObjectName());
            Assertions.assertTrue(taskInfo.getContentMd5().isEmpty());
            Assertions.assertTrue(taskInfo.getHost().isEmpty());
            Assertions.assertTrue(taskInfo.getCallback().isEmpty());
            Assertions.assertTrue(taskInfo.getIgnoreSameKey());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }


    @Test
    public void testArgumentEmpty() {
        try {
            final String destObject = objectName + "-destination";
            AsyncFetchTaskConfiguration configuration = new AsyncFetchTaskConfiguration()
                    .withUrl(url)
                    .withObjectName(destObject)
                    .withHost("")
                    .withContentMd5("")
                    .withCallback("");

            SetAsyncFetchTaskResult setTaskResult = ossClient.setAsyncFetchTask(bucketName, configuration);
            String taskId = setTaskResult.getTaskId();

            Thread.sleep(1000 * 5);

            GetAsyncFetchTaskResult getTaskResult = ossClient.getAsyncFetchTask(bucketName, taskId);
            Assertions.assertEquals(taskId, getTaskResult.getTaskId());
            Assertions.assertEquals(AsyncFetchTaskState.Success, getTaskResult.getAsyncFetchTaskState());
            Assertions.assertTrue(getTaskResult.getErrorMsg().isEmpty());

            AsyncFetchTaskConfiguration taskInfo = getTaskResult.getAsyncFetchTaskConfiguration();
            Assertions.assertEquals(url, taskInfo.getUrl());
            Assertions.assertEquals(destObject, taskInfo.getObjectName());
            Assertions.assertTrue(taskInfo.getContentMd5().isEmpty());
            Assertions.assertTrue(taskInfo.getHost().isEmpty());
            Assertions.assertTrue(taskInfo.getCallback().isEmpty());
            Assertions.assertTrue(taskInfo.getIgnoreSameKey());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testFetchSuccessCallbackFailedState() {
        try {
            final String destObject = objectName + "-destination";
            String callbackContent = "{\"callbackUrl\":\"www.abc.com/callback\",\"callbackBody\":\"${etag}\"}";
            String callback = BinaryUtil.toBase64String(callbackContent.getBytes());
            AsyncFetchTaskConfiguration configuration = new AsyncFetchTaskConfiguration()
                    .withUrl(url).withContentMd5(contentMd5).withIgnoreSameKey(false)
                    .withObjectName(destObject).withCallback(callback);

            SetAsyncFetchTaskResult setTaskResult = ossClient.setAsyncFetchTask(bucketName, configuration);
            String taskId = setTaskResult.getTaskId();

            Thread.sleep(1000 * 5);

            GetAsyncFetchTaskResult getTaskResult = ossClient.getAsyncFetchTask(bucketName, taskId);
            Assertions.assertEquals(taskId, getTaskResult.getTaskId());
            Assertions.assertEquals(AsyncFetchTaskState.FetchSuccessCallbackFailed, getTaskResult.getAsyncFetchTaskState());
            Assertions.assertFalse(getTaskResult.getErrorMsg().isEmpty());

            AsyncFetchTaskConfiguration taskInfo = getTaskResult.getAsyncFetchTaskConfiguration();
            Assertions.assertEquals(callback, taskInfo.getCallback());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testFailedState() {
        try {
            final String destObject = objectName + "-destination";

            AsyncFetchTaskConfiguration configuration = new AsyncFetchTaskConfiguration()
                    .withUrl("invalidUrl").withContentMd5(contentMd5).withIgnoreSameKey(false)
                    .withObjectName(destObject);

            SetAsyncFetchTaskResult setTaskResult = ossClient.setAsyncFetchTask(bucketName, configuration);
            String taskId = setTaskResult.getTaskId();

            Thread.sleep(1000 * 5);

            GetAsyncFetchTaskResult getTaskResult = ossClient.getAsyncFetchTask(bucketName, taskId);
            Assertions.assertEquals(taskId, getTaskResult.getTaskId());
            Assertions.assertEquals(AsyncFetchTaskState.Failed, getTaskResult.getAsyncFetchTaskState());
            Assertions.assertFalse(getTaskResult.getErrorMsg().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testIgnoreSameKey() {
        final String destObject = objectName + "-destination";

        try {
            ossClient.putObject(bucketName, destObject, new ByteArrayInputStream("123".getBytes()));
        } catch (ClientException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        try {
            AsyncFetchTaskConfiguration configuration = new AsyncFetchTaskConfiguration()
                    .withUrl(url)
                    .withObjectName(destObject)
                    .withIgnoreSameKey(false);

            SetAsyncFetchTaskResult setTaskResult = ossClient.setAsyncFetchTask(bucketName, configuration);
            Assertions.assertNotNull(setTaskResult.getTaskId());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        try {
            AsyncFetchTaskConfiguration configuration = new AsyncFetchTaskConfiguration()
                    .withUrl(url)
                    .withObjectName(destObject)
                    .withIgnoreSameKey(true);

            SetAsyncFetchTaskResult setTaskResult = ossClient.setAsyncFetchTask(bucketName, configuration);
            Assertions.fail("dest object has already exist, fetch task failed.");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.OBJECT_ALREADY_EXISTS, e.getErrorCode());
        }

        try {
            AsyncFetchTaskConfiguration configuration = new AsyncFetchTaskConfiguration()
                    .withUrl(url)
                    .withObjectName(destObject);

            SetAsyncFetchTaskResult setTaskResult = ossClient.setAsyncFetchTask(bucketName, configuration);
            Assertions.fail("dest object has already exist, fetch task failed.");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.OBJECT_ALREADY_EXISTS, e.getErrorCode());
        }
    }

}
