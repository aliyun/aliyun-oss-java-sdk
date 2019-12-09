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

import java.io.File;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.model.*;
import junit.framework.Assert;

import org.junit.Test;

public class UploadFileTest extends TestBase {

    @Test
    public void testUploadFileWithoutCheckpoint() {
        final String key = "obj-upload-file-wcp";
        
        try {            
            File file = createSampleFile(key, 1024 * 500);
             
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);

            uploadFileRequest = new UploadFileRequest(bucketName, key, file.getAbsolutePath(), (1024 * 100),10);
            
            UploadFileResult uploadRes = ossClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
                
            ObjectListing objects = ossClient.listObjects(bucketName, key);
            Assert.assertEquals(objects.getObjectSummaries().size(), 1);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getKey(), key);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getSize(), file.length());
            Assert.assertEquals(objects.getRequestId().length(), REQUEST_ID_LEN);

            ObjectMetadata meta = ossClient.getObjectMetadata(bucketName, key);
            Assert.assertEquals(meta.getContentLength(), file.length());
            Assert.assertEquals(meta.getContentType(), "text/plain");
            
            File fileNew = new File(key + "-new.txt");
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            ossClient.getObject(getObjectRequest, fileNew);
            Assert.assertEquals(file.length(), fileNew.length());
            
            ossClient.deleteObject(bucketName, key);
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testUploadFileSequential() {
        final String key = "obj-upload-file-Sequential";

        String testBucketName = super.bucketName + "-upload-sequential";
        String endpoint = "http://oss-cn-shanghai.aliyuncs.com";

        //create client
        ClientConfiguration conf = new ClientConfiguration().setSupportCname(false);
        Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        OSS testOssClient = new OSSClient(endpoint, new DefaultCredentialProvider(credentials), conf);
        testOssClient.createBucket(testBucketName);


        try {
            File file = createSampleFile(key, 600 * 1024);

            UploadFileRequest uploadFileRequest = new UploadFileRequest(testBucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            testOssClient.uploadFile(uploadFileRequest);

            GetObjectRequest getObjectRequest = new GetObjectRequest(testBucketName, key);
            OSSObject ossObject = testOssClient.getObject(getObjectRequest);
            Assert.assertNull(ossObject.getResponse().getHeaders().get("Content-MD5"));
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            File file = createSampleFile(key, 600 * 1024);

            UploadFileRequest uploadFileRequest = new UploadFileRequest(testBucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setSequentialMode(true);

            testOssClient.uploadFile(uploadFileRequest);

            GetObjectRequest getObjectRequest = new GetObjectRequest(testBucketName, key);
            OSSObject ossObject = testOssClient.getObject(getObjectRequest);

            Assert.assertNotNull(ossObject.getResponse().getHeaders().get("Content-MD5"));
            testOssClient.deleteObject(testBucketName, key);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            testOssClient.deleteBucket(testBucketName);
        }
    }

    @Test
    public void testUploadFileWithCheckpoint() {
        final String key = "obj-upload-file-cp";
        
        try {            
            File file = createSampleFile(key, 1024 * 500);
             
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            uploadFileRequest.setEnableCheckpoint(true);
            uploadFileRequest.setPartSize(1024);

            uploadFileRequest = new UploadFileRequest(bucketName, key, file.getAbsolutePath(), (1024 * 100),10, true);
            uploadFileRequest.setTaskNum(0);
            Assert.assertEquals(1, uploadFileRequest.getTaskNum());
            uploadFileRequest.setTaskNum(1001);
            Assert.assertEquals(1000, uploadFileRequest.getTaskNum());
            uploadFileRequest.setTaskNum(10);

            UploadFileResult uploadRes = ossClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
                
            ObjectListing objects = ossClient.listObjects(bucketName, key);
            Assert.assertEquals(objects.getObjectSummaries().size(), 1);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getKey(), key);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getSize(), file.length());
            Assert.assertEquals(objects.getRequestId().length(), REQUEST_ID_LEN);

            ObjectMetadata meta = ossClient.getObjectMetadata(bucketName, key);
            Assert.assertEquals(meta.getContentLength(), file.length());
            Assert.assertEquals(meta.getContentType(), "text/plain");
            
            File fileNew = new File(key + "-new.txt");
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            ossClient.getObject(getObjectRequest, fileNew);
            Assert.assertEquals(file.length(), fileNew.length());
            
            ossClient.deleteObject(bucketName, key);
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testUploadFileWithCheckpointFile() {
        final String key = "obj-upload-file-cpf";
        
        try {            
            File file = createSampleFile(key, 1024 * 500);
             
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            uploadFileRequest.setEnableCheckpoint(true);
            uploadFileRequest.setCheckpointFile("BingWallpaper.ucp");

            uploadFileRequest = new UploadFileRequest(bucketName, key, file.getAbsolutePath(),
                    (1024 * 100),10,
                    true, "BingWallpaper.ucp");

            UploadFileResult uploadRes = ossClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
                
            ObjectListing objects = ossClient.listObjects(bucketName, key);
            Assert.assertEquals(objects.getObjectSummaries().size(), 1);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getKey(), key);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getSize(), file.length());
            Assert.assertEquals(objects.getRequestId().length(), REQUEST_ID_LEN);

            ObjectMetadata meta = ossClient.getObjectMetadata(bucketName, key);
            Assert.assertEquals(meta.getContentLength(), file.length());
            Assert.assertEquals(meta.getContentType(), "text/plain");
            
            File fileNew = new File(key + "-new.txt");
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            ossClient.getObject(getObjectRequest, fileNew);
            Assert.assertEquals(file.length(), fileNew.length());
            
            ossClient.deleteObject(bucketName, key);
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            ossClient.deleteBucket(bucketName);
        }
    }
    
}
