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

import com.aliyun.oss.internal.OSSHeaders;
import junit.framework.Assert;

import org.junit.Test;

import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.SSEAlgorithm;
import com.aliyun.oss.model.ServerSideEncryptionByDefault;
import com.aliyun.oss.model.ServerSideEncryptionConfiguration;
import com.aliyun.oss.model.SetBucketEncryptionRequest;

import java.io.File;
import java.util.Map;

public class BucketEncryptionTest extends TestBase {

    private void testSetBucketEncryptionInternal(SSEAlgorithm algorithm) {

        try {
            // set
            ServerSideEncryptionByDefault applyServerSideEncryptionByDefault =
                    new ServerSideEncryptionByDefault(algorithm.toString());
            ServerSideEncryptionConfiguration setConfiguration = new ServerSideEncryptionConfiguration();
            setConfiguration.setApplyServerSideEncryptionByDefault(applyServerSideEncryptionByDefault);
            SetBucketEncryptionRequest setRequest = new SetBucketEncryptionRequest(bucketName, setConfiguration);

            ossClient.setBucketEncryption(setRequest);

            // get
            ServerSideEncryptionConfiguration getConfiguration = ossClient.getBucketEncryption(bucketName);
            Assert.assertEquals(algorithm.toString(),
                    getConfiguration.getApplyServerSideEncryptionByDefault().getSSEAlgorithm());
            Assert.assertNull(getConfiguration.getApplyServerSideEncryptionByDefault().getKMSMasterKeyID());

            String fileName = TestUtils.genFixedLengthFile(1024);
            String objectName = "encryption-" + TestUtils.genRandomString(10);
            ossClient.putObject(bucketName, objectName, new File(fileName));

            Map<String, String> headers = ossClient.getObject(bucketName, objectName).getResponse().getHeaders();
            Assert.assertEquals(algorithm.toString(), headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSetBucketEncryption() {
        testSetBucketEncryptionInternal(SSEAlgorithm.AES256);
        testSetBucketEncryptionInternal(SSEAlgorithm.SM4);
    }

    private void testDeleteBucketEncryptionInternal(SSEAlgorithm algorithm) {

        try {
            // set
            ServerSideEncryptionByDefault applyServerSideEncryptionByDefault =
                    new ServerSideEncryptionByDefault().withSSEAlgorithm(algorithm);
            if (algorithm == SSEAlgorithm.KMS)
                applyServerSideEncryptionByDefault.setKMSMasterKeyID("test-kms-master-key-id");
            ServerSideEncryptionConfiguration setConfiguration = new ServerSideEncryptionConfiguration()
                    .withApplyServerSideEncryptionByDefault(applyServerSideEncryptionByDefault);
            setConfiguration.setApplyServerSideEncryptionByDefault(applyServerSideEncryptionByDefault);
            SetBucketEncryptionRequest setRequest = new SetBucketEncryptionRequest(bucketName)
                    .withServerSideEncryptionConfiguration(setConfiguration);

            ossClient.setBucketEncryption(setRequest);

            // get
            ServerSideEncryptionConfiguration getConfiguration = ossClient.getBucketEncryption(bucketName);
            Assert.assertEquals(algorithm.toString(),
                    getConfiguration.getApplyServerSideEncryptionByDefault().getSSEAlgorithm());
            if (algorithm == SSEAlgorithm.KMS)
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
    public void testDeleteBucketEncryption() {
        testDeleteBucketEncryptionInternal(SSEAlgorithm.AES256);
        testDeleteBucketEncryptionInternal(SSEAlgorithm.SM4);
        testDeleteBucketEncryptionInternal(SSEAlgorithm.KMS);
    }

    public void testBucketInfoInternal(SSEAlgorithm algorithm) {

        try {
            // set 1
            ServerSideEncryptionByDefault applyServerSideEncryptionByDefault =
                    new ServerSideEncryptionByDefault(algorithm);
            ServerSideEncryptionConfiguration setConfiguration = new ServerSideEncryptionConfiguration();
            setConfiguration.setApplyServerSideEncryptionByDefault(applyServerSideEncryptionByDefault);
            SetBucketEncryptionRequest setRequest = new SetBucketEncryptionRequest(bucketName, setConfiguration);

            ossClient.setBucketEncryption(setRequest);

            // get
            BucketInfo bucketInfo = ossClient.getBucketInfo(bucketName);
            Assert.assertEquals(algorithm.toString(), bucketInfo.getServerSideEncryptionConfiguration()
                    .getApplyServerSideEncryptionByDefault().getSSEAlgorithm());
            Assert.assertNull(bucketInfo.getServerSideEncryptionConfiguration()
                    .getApplyServerSideEncryptionByDefault().getKMSMasterKeyID());

            // delete
            ossClient.deleteBucketEncryption(bucketName);
            waitForCacheExpiration(3);

            // set 2
            applyServerSideEncryptionByDefault = new ServerSideEncryptionByDefault().withSSEAlgorithm(SSEAlgorithm.KMS.toString());
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

    @Test
    public void testBucketInfo() {
        testBucketInfoInternal(SSEAlgorithm.AES256);
        testBucketInfoInternal(SSEAlgorithm.SM4);
    }
}
