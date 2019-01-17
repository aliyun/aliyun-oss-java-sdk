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

import com.aliyun.oss.model.*;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;
import junit.framework.Assert;
import org.junit.Test;
import java.util.List;

public class BucketEncryptionTest extends TestBase {
    

    @Test
    public void testNormalSetBucketEncryption() {
        final String bucketName = "normal-set-bucket-encryption";

        try {
            ossClient.createBucket(bucketName);
            
            // Set bucket encryption
            SetBucketEncryptionRequest request = new SetBucketEncryptionRequest(bucketName);

            request.setAlgorithm(SSEAlgorithm.AES256);
            
            ossClient.setBucketEncryption(request);
            
            // Get bucket encryption
            ServerSideEncryptionRule testRule1 = ossClient.getBucketEncryption(new GenericRequest(bucketName));

            Assert.assertEquals(SSEAlgorithm.AES256.toString(), testRule1.getAlgorithm().toString());

            // Get bucket info
            BucketInfo bucketInfo1 = ossClient.getBucketInfo(bucketName);

            Assert.assertEquals(SSEAlgorithm.AES256.toString(), bucketInfo1.getEncryptionRule().getAlgorithm().toString());
            
            // Override existing bucket encryption
            SetBucketEncryptionRequest request2 = new SetBucketEncryptionRequest(bucketName);

            request2.setAlgorithm(SSEAlgorithm.KMS);

            request2.setKMSMasterKeyID("shasdjahsdjajhdhjasdaxxxxtest");

            ossClient.setBucketEncryption(request2);
            
            ServerSideEncryptionRule testRule2 = ossClient.getBucketEncryption(new GenericRequest(bucketName));

            Assert.assertEquals(SSEAlgorithm.KMS.toString(), testRule2.getAlgorithm().toString());

            BucketInfo bucketInfo2 = ossClient.getBucketInfo(bucketName);

            Assert.assertEquals(SSEAlgorithm.AES256.toString(), bucketInfo2.getEncryptionRule().getAlgorithm().toString());

            // Delete bucket encryption
            ossClient.deleteBucketEncryption(new GenericRequest(bucketName));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

}
