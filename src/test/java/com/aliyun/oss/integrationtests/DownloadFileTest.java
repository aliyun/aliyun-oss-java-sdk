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

import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.DownloadFileResult;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadFileResult;

public class DownloadFileTest extends TestBase {

    @Test
    public void testUploadFileWithoutCheckpoint() {
        final String bucketName = "bucket-download-file-wcp";
        final String key = "obj-download-file-wcp";
        
        try {
            secondClient.createBucket(bucketName);
            
            File file = createSampleFile(key, 1024 * 500);
                        
            // upload file
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.addUserMetadata("prop", "propval");
            uploadFileRequest.setObjectMetadata(objMetadata);
            
            UploadFileResult uploadRes = secondClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
            
            // download file
            String filePathNew = key + "-new.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(filePathNew);
            downloadFileRequest.setTaskNum(10);
            
            DownloadFileResult downloadRes = secondClient.downloadFile(downloadFileRequest);
            
            ObjectMetadata objMeta = downloadRes.getObjectMetadata();
            Assert.assertEquals(objMeta.getContentLength(), 102400);
            Assert.assertEquals(objMeta.getObjectType(), "Multipart");
            Assert.assertEquals(objMeta.getUserMetadata().get("prop"), "propval");

            File fileNew = new File(filePathNew);
            Assert.assertTrue("comparte file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            
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
        final String bucketName = "bucket-download-file-cp";
        final String key = "obj-download-file-cp";
        
        try {
            secondClient.createBucket(bucketName);
            
            File file = createSampleFile(key, 1024 * 500);
                        
            // upload file
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            uploadFileRequest.setEnableCheckpoint(true);
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.addUserMetadata("prop", "propval");
            uploadFileRequest.setObjectMetadata(objMetadata);
            
            UploadFileResult uploadRes = secondClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);            
            
            // download file
            String filePathNew = key + "-new.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(filePathNew);
            downloadFileRequest.setTaskNum(10);
            downloadFileRequest.setEnableCheckpoint(true);
            
            DownloadFileResult downloadRes = secondClient.downloadFile(downloadFileRequest);
            
            ObjectMetadata objMeta = downloadRes.getObjectMetadata();
            Assert.assertEquals(objMeta.getContentLength(), 102400);
            Assert.assertEquals(objMeta.getObjectType(), "Multipart");
            Assert.assertEquals(objMeta.getUserMetadata().get("prop"), "propval");

            File fileNew = new File(filePathNew);
            Assert.assertTrue("comparte file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            
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
        final String bucketName = "bucket-download-file-cpf";
        final String key = "obj-download-file-cpf";
        
        try {
            secondClient.createBucket(bucketName);
            
            File file = createSampleFile(key, 1024 * 500);
                        
            // upload file
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            uploadFileRequest.setEnableCheckpoint(true);
            uploadFileRequest.setCheckpointFile("BingWallpaper.ucp");
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.addUserMetadata("prop", "propval");
            uploadFileRequest.setObjectMetadata(objMetadata);
            
            UploadFileResult uploadRes = secondClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
            
            // download file
            String filePathNew = key + "-new.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(filePathNew);
            downloadFileRequest.setTaskNum(10);
            downloadFileRequest.setEnableCheckpoint(true);
            downloadFileRequest.setCheckpointFile("BingWallpaper.dcp");
            
            DownloadFileResult downloadRes = secondClient.downloadFile(downloadFileRequest);
            
            ObjectMetadata objMeta = downloadRes.getObjectMetadata();
            Assert.assertEquals(objMeta.getContentLength(), 102400);
            Assert.assertEquals(objMeta.getObjectType(), "Multipart");
            Assert.assertEquals(objMeta.getUserMetadata().get("prop"), "propval");

            File fileNew = new File(filePathNew);
            Assert.assertTrue("comparte file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            
            secondClient.deleteObject(bucketName, key);
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            secondClient.deleteBucket(bucketName);
        }
    }

}
