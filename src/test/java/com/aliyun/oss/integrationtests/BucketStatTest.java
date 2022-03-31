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

import org.junit.jupiter.api.*;

import java.io.File;

import org.junit.Test;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.AbortMultipartUploadRequest;
import com.aliyun.oss.model.BucketStat;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadFileResult;

public class BucketStatTest extends TestBase {

    @Test
    public void testGetBucketStat() {
    	String key = "obj-upload-file-stat.txt";
    	String uploadId = null;
    	
        try {
        	
            File file = createSampleFile(key, 1024 * 500);
            
            // upload a file
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            
            UploadFileResult uploadRes = ossClient.uploadFile(uploadFileRequest);
            Assertions.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assertions.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
            
            // init upload
            InitiateMultipartUploadRequest initiateMultipartUploadRequest = 
                    new InitiateMultipartUploadRequest(bucketName, key);
            InitiateMultipartUploadResult initiateMultipartUploadResult = 
                    ossClient.initiateMultipartUpload(initiateMultipartUploadRequest);
            Assertions.assertEquals(initiateMultipartUploadResult.getRequestId().length(), REQUEST_ID_LEN);
            uploadId = initiateMultipartUploadResult.getUploadId();
            
            BucketStat stat = ossClient.getBucketStat(bucketName);
            System.out.println(stat.getStorageSize() + "," + stat.getObjectCount() + "," + stat.getMultipartUploadCount());
            Assertions.assertTrue(stat.getStorageSize() >= 1024 * 300);
            Assertions.assertTrue(stat.getObjectCount() >= 1);
            Assertions.assertTrue(stat.getMultipartUploadCount() >= 1);
            Assertions.assertEquals(stat.getRequestId().length(), REQUEST_ID_LEN);
        } catch (Exception e) {
        	e.printStackTrace();
            Assertions.fail(e.getMessage());
        } catch (Throwable e) {
        	e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
        	if (uploadId != null) {
                AbortMultipartUploadRequest AbortMultipartUploadRequest = 
                		new AbortMultipartUploadRequest(bucketName, key, uploadId);
                ossClient.abortMultipartUpload(AbortMultipartUploadRequest);
        	}
        }
    }
    
    @Test
    public void testUnormalGetBucketStat() {
        final String bucketName = "unormal-get-bucket-stat";
        
        // bucket non-existent 
        try {            
            ossClient.getBucketStat(bucketName);
            Assertions.fail("Get bucket stat should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }

    }
    
}
