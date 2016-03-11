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
import junit.framework.Assert;

import org.junit.Test;

import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadFileResult;

public class UploadFileTest extends TestBase {

    @Test
    public void testUploadFileWithoutCheckpoint() {
        final String bucketName = "bucket-upload-file-wcp";
        final String key = "obj-upload-file-wcp";
        
        try {
            secondClient.createBucket(bucketName);
            
            File file = createSampleFile(key, 1024 * 500);
             
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            
            UploadFileResult uploadRes = secondClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
                
            ObjectListing objects = secondClient.listObjects(bucketName, key);
            Assert.assertEquals(objects.getObjectSummaries().size(), 1);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getKey(), key);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getSize(), file.length());

            ObjectMetadata meta = secondClient.getObjectMetadata(bucketName, key);
            Assert.assertEquals(meta.getContentLength(), file.length());
            
            File fileNew = new File(key + "-new.txt");
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            secondClient.getObject(getObjectRequest, fileNew);
            Assert.assertEquals(file.length(), fileNew.length());
            
            secondClient.deleteObject(bucketName, key);
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUploadFileWithCheckpoint() {
        final String bucketName = "bucket-upload-file-cp";
        final String key = "obj-upload-file-cp";
        
        try {
            secondClient.createBucket(bucketName);
            
            File file = createSampleFile(key, 1024 * 500);
             
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            uploadFileRequest.setEnableCheckpoint(true);
            
            UploadFileResult uploadRes = secondClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
                
            ObjectListing objects = secondClient.listObjects(bucketName, key);
            Assert.assertEquals(objects.getObjectSummaries().size(), 1);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getKey(), key);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getSize(), file.length());

            ObjectMetadata meta = secondClient.getObjectMetadata(bucketName, key);
            Assert.assertEquals(meta.getContentLength(), file.length());
            
            File fileNew = new File(key + "-new.txt");
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            secondClient.getObject(getObjectRequest, fileNew);
            Assert.assertEquals(file.length(), fileNew.length());
            
            secondClient.deleteObject(bucketName, key);
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            secondClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testUploadFileWithCheckpointFile() {
        final String bucketName = "bucket-upload-file-cpf";
        final String key = "obj-upload-file-cpf";
        
        try {
            secondClient.createBucket(bucketName);
            
            File file = createSampleFile(key, 1024 * 500);
             
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            uploadFileRequest.setEnableCheckpoint(true);
            uploadFileRequest.setCheckpointFile("BingWallpaper.ucp");
            
            UploadFileResult uploadRes = secondClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
                
            ObjectListing objects = secondClient.listObjects(bucketName, key);
            Assert.assertEquals(objects.getObjectSummaries().size(), 1);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getKey(), key);
            Assert.assertEquals(objects.getObjectSummaries().get(0).getSize(), file.length());

            ObjectMetadata meta = secondClient.getObjectMetadata(bucketName, key);
            Assert.assertEquals(meta.getContentLength(), file.length());
            
            File fileNew = new File(key + "-new.txt");
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            secondClient.getObject(getObjectRequest, fileNew);
            Assert.assertEquals(file.length(), fileNew.length());
            
            secondClient.deleteObject(bucketName, key);
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            secondClient.deleteBucket(bucketName);
        }
    }
    
}
