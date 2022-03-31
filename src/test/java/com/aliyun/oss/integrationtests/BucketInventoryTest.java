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

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.model.*;
import org.junit.jupiter.api.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class BucketInventoryTest extends TestBase {
    private String destinBucket;
    private OSSClient ossClient;
    private String bucketName;
    private String endpoint;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        bucketName = super.bucketName + "-inventory";
        endpoint = "http://oss-ap-southeast-2.aliyuncs.com";

        //create client
        ClientConfiguration conf = new ClientConfiguration().setSupportCname(false);
        Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(credentials), conf);

        ossClient.createBucket(bucketName);
        Thread.sleep(2000);

        String destinBucketName = bucketName + "-destin";
        ossClient.createBucket(destinBucketName);
        Thread.sleep(2000);
        destinBucket = destinBucketName;
    }

    public void tearDown() throws Exception {
        if (ossClient != null) {
            ossClient.shutdown();
            ossClient = null;
        }
        super.tearDown();
    }

    private InventoryConfiguration createTestInventoryConfiguration(String configurationId) {
        if (configurationId == null) {
            throw new RuntimeException("inventory configuration id should not be null.");
        }

        // fields
        List<String> fields = new ArrayList<String>();
        fields.add(InventoryOptionalFields.Size);
        fields.add(InventoryOptionalFields.LastModifiedDate);
        fields.add(InventoryOptionalFields.ETag);
        fields.add(InventoryOptionalFields.StorageClass);
        fields.add(InventoryOptionalFields.IsMultipartUploaded);
        fields.add(InventoryOptionalFields.EncryptionStatus);

        // schedule
        InventorySchedule inventorySchedule = new InventorySchedule().withFrequency(InventoryFrequency.Daily);

        // filter
        InventoryFilter inventoryFilter = new InventoryFilter().withPrefix("testPrefix");

        // destination
        InventoryEncryption inventoryEncryption = new InventoryEncryption();
        inventoryEncryption.setServerSideKmsEncryption(new InventoryServerSideEncryptionKMS().withKeyId("123"));
        InventoryOSSBucketDestination ossBucketDestin = new InventoryOSSBucketDestination()
                .withFormat(InventoryFormat.CSV)
                .withPrefix("bucket-prefix")
                .withAccountId(TestConfig.RAM_UID)
                .withRoleArn(TestConfig.RAM_ROLE_ARN)
                .withBucket(destinBucket)
                .withEncryption(inventoryEncryption);

        InventoryDestination destination = new InventoryDestination().withOSSBucketDestination(ossBucketDestin);

        InventoryConfiguration inventoryConfiguration = new InventoryConfiguration()
                .withInventoryId(configurationId)
                .withEnabled(false)
                .withIncludedObjectVersions(InventoryIncludedObjectVersions.Current)
                .withOptionalFields(fields)
                .withFilter(inventoryFilter)
                .withSchedule(inventorySchedule)
                .withDestination(destination);

        return inventoryConfiguration;
    }

    @Test
    public void testBucketInventoryNormal() {
        String inventoryId = "testid";
        // fields
        List<String> fields = new ArrayList<String>();
        fields.add(InventoryOptionalFields.Size);
        fields.add(InventoryOptionalFields.LastModifiedDate);
        fields.add(InventoryOptionalFields.ETag);
        fields.add(InventoryOptionalFields.StorageClass);
        fields.add(InventoryOptionalFields.IsMultipartUploaded);
        fields.add(InventoryOptionalFields.EncryptionStatus);

        // schedule
        InventorySchedule inventorySchedule = new InventorySchedule().withFrequency(InventoryFrequency.Weekly);

        // filter
        InventoryFilter inventoryFilter = new InventoryFilter().withPrefix("testPrefix");

        // destination
        InventoryEncryption inventoryEncryption = new InventoryEncryption();
        inventoryEncryption.setServerSideOssEncryption(new InventoryServerSideEncryptionOSS());
        InventoryOSSBucketDestination ossBucketDestin = new InventoryOSSBucketDestination()
                .withFormat(InventoryFormat.CSV)
                .withPrefix("bucket-prefix")
                .withAccountId(TestConfig.RAM_UID)
                .withRoleArn(TestConfig.RAM_ROLE_ARN)
                .withBucket(destinBucket)
                .withEncryption(inventoryEncryption);

        InventoryDestination destination = new InventoryDestination().withOSSBucketDestination(ossBucketDestin);

        InventoryConfiguration inventoryConfiguration = new InventoryConfiguration()
                .withInventoryId(inventoryId)
                .withEnabled(false)
                .withIncludedObjectVersions(InventoryIncludedObjectVersions.All)
                .withOptionalFields(fields)
                .withFilter(inventoryFilter)
                .withSchedule(inventorySchedule)
                .withDestination(destination);

        // put
        try {
            ossClient.setBucketInventoryConfiguration(bucketName, inventoryConfiguration);
        } catch (ClientException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        // get and delete
        try {
            GetBucketInventoryConfigurationResult result = ossClient.getBucketInventoryConfiguration(
                   new GetBucketInventoryConfigurationRequest(bucketName, inventoryId));

            InventoryConfiguration actualConfig = result.getInventoryConfiguration();
            Assertions.assertEquals(inventoryId, actualConfig.getInventoryId());
            Assertions.assertEquals(InventoryIncludedObjectVersions.All.toString(), actualConfig.getIncludedObjectVersions());
            Assertions.assertEquals("testPrefix", actualConfig.getInventoryFilter().getPrefix());
            Assertions.assertEquals(InventoryFrequency.Weekly.toString(), actualConfig.getSchedule().getFrequency());
            Assertions.assertEquals(6, actualConfig.getOptionalFields().size());

            InventoryOSSBucketDestination actualDestin = actualConfig.getDestination().getOssBucketDestination();

            Assertions.assertEquals(TestConfig.RAM_UID, actualDestin.getAccountId());
            Assertions.assertEquals(destinBucket, actualDestin.getBucket());
            Assertions.assertEquals(TestConfig.RAM_ROLE_ARN, actualDestin.getRoleArn());
            Assertions.assertEquals(InventoryFormat.CSV.toString(), actualDestin.getFormat());
            Assertions.assertEquals("bucket-prefix", actualDestin.getPrefix());
            Assertions.assertNotNull(actualDestin.getEncryption().getServerSideOssEncryption());
            Assertions.assertNull(actualDestin.getEncryption().getServerSideKmsEncryption());
        } catch (ClientException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucketInventoryConfiguration(new DeleteBucketInventoryConfigurationRequest(bucketName, inventoryId));
        }
    }

    @Test
    public void testErrorInventoryEncryption() {
        try {
            InventoryEncryption inventoryEncryption = new InventoryEncryption();
            inventoryEncryption.setServerSideOssEncryption(new InventoryServerSideEncryptionOSS());
            inventoryEncryption.setServerSideKmsEncryption(new InventoryServerSideEncryptionKMS().withKeyId("test-kms-id"));
            Assertions.fail("The KMS encryption and OSS encryption only can be selected one");
        } catch (ClientException e) {
        }
    }

    @Test
    public void testListFewInventoryConfiguration() {
        String idPrefix = "testid-";
        int sum = 3;
        try {
            for (int i = 0; i < sum; i++) {
                String id = idPrefix + String.valueOf(i);
                InventoryConfiguration inventoryConfiguration = createTestInventoryConfiguration(id);
                ossClient.setBucketInventoryConfiguration(bucketName, inventoryConfiguration);
            }
        } catch (ClientException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        try {
            ListBucketInventoryConfigurationsRequest request = new ListBucketInventoryConfigurationsRequest(bucketName);
            ListBucketInventoryConfigurationsResult result = ossClient.listBucketInventoryConfigurations(request);
            Assertions.assertEquals(sum, result.getInventoryConfigurationList().size());
        } catch (ClientException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            for (int i = 0; i < sum; i++) {
                String id = idPrefix + String.valueOf(i);
                ossClient.deleteBucketInventoryConfiguration(bucketName, id);
            }
        }
    }

    @Test
    public void testListLotInventoryConfiguration() {
        String idPrefix = "testid-";
        int sum = 102;
        try {
            for (int i = 0; i < sum; i++) {
                String id = idPrefix + String.valueOf(i);
                InventoryConfiguration inventoryConfiguration = createTestInventoryConfiguration(id);
                ossClient.setBucketInventoryConfiguration(bucketName, inventoryConfiguration);
            }
        } catch (ClientException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        try {
            int count = 0;
            ListBucketInventoryConfigurationsRequest request = new ListBucketInventoryConfigurationsRequest(bucketName);

            ListBucketInventoryConfigurationsResult result = ossClient.listBucketInventoryConfigurations(request);
            count += result.getInventoryConfigurationList().size();
            Assertions.assertEquals(true, result.isTruncated());
            Assertions.assertNull(result.getContinuationToken());
            Assertions.assertNotNull(result.getNextContinuationToken());

            String continuationToken = result.getNextContinuationToken();
            request = new ListBucketInventoryConfigurationsRequest(bucketName, continuationToken);
            result = ossClient.listBucketInventoryConfigurations(request);
            count += result.getInventoryConfigurationList().size();
            Assertions.assertEquals(false, result.isTruncated());
            Assertions.assertEquals(continuationToken, result.getContinuationToken());
            Assertions.assertNull(result.getNextContinuationToken());
            Assertions.assertEquals(sum , count);
        } catch (ClientException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            for (int i = 0; i < sum; i++) {
                String id = idPrefix + String.valueOf(i);
                ossClient.deleteBucketInventoryConfiguration(bucketName, id);
            }
        }
    }

    @Test
    public void testListOneHundredInventoryConfiguration() {
        String idPrefix = "testid-";
        int sum = 100;
        try {
            for (int i = 0; i < sum; i++) {
                String id = idPrefix + String.valueOf(i);
                InventoryConfiguration inventoryConfiguration = createTestInventoryConfiguration(id);
                ossClient.setBucketInventoryConfiguration(bucketName, inventoryConfiguration);
            }
        } catch (ClientException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }

        try {
            ListBucketInventoryConfigurationsRequest request = new ListBucketInventoryConfigurationsRequest(bucketName);
            ListBucketInventoryConfigurationsResult result = ossClient.listBucketInventoryConfigurations(request);
            Assertions.assertEquals(false, result.isTruncated());
        } catch (ClientException e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            for (int i = 0; i < sum; i++) {
                String id = idPrefix + String.valueOf(i);
                ossClient.deleteBucketInventoryConfiguration(bucketName, id);
            }
        }
    }

    @Test
    public void testListNoneInventoryConfiguration() {
        try {
            ListBucketInventoryConfigurationsRequest request = new ListBucketInventoryConfigurationsRequest(bucketName);
            ListBucketInventoryConfigurationsResult result = ossClient.listBucketInventoryConfigurations(request);
            Assertions.fail("There is no inventory configuration, should be failed.");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_INVENTORY, e.getErrorCode());
        }
    }

}
