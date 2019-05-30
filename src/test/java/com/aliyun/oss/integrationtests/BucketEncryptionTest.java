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

import junit.framework.Assert;

import org.junit.Test;

import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.SSEAlgorithm;
import com.aliyun.oss.model.ServerSideEncryptionByDefault;
import com.aliyun.oss.model.ServerSideEncryptionConfiguration;
import com.aliyun.oss.model.SetBucketEncryptionRequest;

public class BucketEncryptionTest extends TestBase {

    @Test
    public void testSetBucketEncryption() {
  
        try {
            // set
            ServerSideEncryptionByDefault applyServerSideEncryptionByDefault =
                new ServerSideEncryptionByDefault(SSEAlgorithm.AES256);
            ServerSideEncryptionConfiguration setConfiguration = new ServerSideEncryptionConfiguration();
            setConfiguration.setApplyServerSideEncryptionByDefault(applyServerSideEncryptionByDefault);
            SetBucketEncryptionRequest setRequest = new SetBucketEncryptionRequest(bucketName, setConfiguration);

            ossClient.setBucketEncryption(setRequest);

            // get
            ServerSideEncryptionConfiguration getConfiguration = ossClient.getBucketEncryption(bucketName);
            Assert.assertEquals(SSEAlgorithm.AES256.toString(),
                getConfiguration.getApplyServerSideEncryptionByDefault().getSSEAlgorithm());
            Assert.assertNull(getConfiguration.getApplyServerSideEncryptionByDefault().getKMSMasterKeyID());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDeleteBucketEncryption() {
    	
        try {
            // set
            ServerSideEncryptionByDefault applyServerSideEncryptionByDefault =
                new ServerSideEncryptionByDefault(SSEAlgorithm.KMS);
            applyServerSideEncryptionByDefault.setKMSMasterKeyID("test-kms-master-key-id");
            ServerSideEncryptionConfiguration setConfiguration = new ServerSideEncryptionConfiguration();
            setConfiguration.setApplyServerSideEncryptionByDefault(applyServerSideEncryptionByDefault);
            SetBucketEncryptionRequest setRequest = new SetBucketEncryptionRequest(bucketName, setConfiguration);

            ossClient.setBucketEncryption(setRequest);

            // get
            ServerSideEncryptionConfiguration getConfiguration = ossClient.getBucketEncryption(bucketName);
            Assert.assertEquals(SSEAlgorithm.KMS.toString(),
                getConfiguration.getApplyServerSideEncryptionByDefault().getSSEAlgorithm());
            Assert.assertEquals("test-kms-master-key-id",
                getConfiguration.getApplyServerSideEncryptionByDefault().getKMSMasterKeyID());

            // delete
            ossClient.deleteBucketEncryption(bucketName);
            waitForCacheExpiration(3);

            // check
            try {
                ossClient.getBucketEncryption(bucketName);
            } catch (OSSException e) {
                Assert.assertEquals("NoSuchServerSideEncryptionRule", e.getErrorCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testBucketInfo() {
    	
        try {
            // set 1
            ServerSideEncryptionByDefault applyServerSideEncryptionByDefault =
                new ServerSideEncryptionByDefault(SSEAlgorithm.AES256);
            ServerSideEncryptionConfiguration setConfiguration = new ServerSideEncryptionConfiguration();
            setConfiguration.setApplyServerSideEncryptionByDefault(applyServerSideEncryptionByDefault);
            SetBucketEncryptionRequest setRequest = new SetBucketEncryptionRequest(bucketName, setConfiguration);

            ossClient.setBucketEncryption(setRequest);

            // get
            BucketInfo bucketInfo = ossClient.getBucketInfo(bucketName);
            Assert.assertEquals(SSEAlgorithm.AES256.toString(), bucketInfo.getServerSideEncryptionConfiguration()
                .getApplyServerSideEncryptionByDefault().getSSEAlgorithm());
            Assert.assertNull(bucketInfo.getServerSideEncryptionConfiguration()
                .getApplyServerSideEncryptionByDefault().getKMSMasterKeyID());

            // delete
            ossClient.deleteBucketEncryption(bucketName);
            waitForCacheExpiration(3);

            // set 2
            applyServerSideEncryptionByDefault = new ServerSideEncryptionByDefault(SSEAlgorithm.KMS);
            applyServerSideEncryptionByDefault.setKMSMasterKeyID("test-kms-master-key-id");
            setConfiguration = new ServerSideEncryptionConfiguration();
            setConfiguration.setApplyServerSideEncryptionByDefault(applyServerSideEncryptionByDefault);
            setRequest = new SetBucketEncryptionRequest(bucketName, setConfiguration);

            ossClient.setBucketEncryption(setRequest);

            // get
            bucketInfo = ossClient.getBucketInfo(bucketName);
            Assert.assertEquals(SSEAlgorithm.KMS.toString(), bucketInfo.getServerSideEncryptionConfiguration()
                .getApplyServerSideEncryptionByDefault().getSSEAlgorithm());
            Assert.assertEquals("test-kms-master-key-id", bucketInfo.getServerSideEncryptionConfiguration()
                .getApplyServerSideEncryptionByDefault().getKMSMasterKeyID());

            // delete
            ossClient.deleteBucketEncryption(bucketName);
            waitForCacheExpiration(3);

            // get
            bucketInfo = ossClient.getBucketInfo(bucketName);
            Assert.assertEquals("None", bucketInfo.getServerSideEncryptionConfiguration()
                .getApplyServerSideEncryptionByDefault().getSSEAlgorithm());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
