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

import static com.aliyun.oss.integrationtests.TestUtils.batchPutObject;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;

import org.junit.Test;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

public class ListObjectsTest extends TestBase {

    private static final int DEFAULT_MAX_RETURNED_KEYS = 100;
    private static final int MAX_RETURNED_KEYS_LIMIT = 1000;
    
    @Test
    public void testNormalListObjects() {
        final String bucketName = super.bucketName + "-normal-list-objects";
        
        try {
            ossClient.createBucket(bucketName);
            
            // List objects under empty bucket
            ObjectListing objectListing = ossClient.listObjects(bucketName);
            Assertions.assertEquals(0, objectListing.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getMaxKeys());
            Assertions.assertEquals(0, objectListing.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, objectListing.getBucketName());
            Assertions.assertNull(objectListing.getDelimiter());
            Assertions.assertNull(objectListing.getPrefix());
            Assertions.assertNull(objectListing.getMarker());
            Assertions.assertNull(objectListing.getNextMarker());
            Assertions.assertFalse(objectListing.isTruncated());
            Assertions.assertEquals(objectListing.getRequestId().length(), REQUEST_ID_LEN);
            
            // Add MAX_RETURNED_KEYS_LIMIT + 1 + lv2KeyCount objects to bucket
            List<String> existingKeys = new ArrayList<String>();
            final int lv1KeyCount = 102;
            final int lv2KeyCount = 11;
            final int keyCount = MAX_RETURNED_KEYS_LIMIT + 1 + lv2KeyCount;
            final String lv0KeyPrefix = "normal-list-lv0-objects-";
            final String lv1KeyPrefix = "normal-list-lv0-objects/lv1-objects-";
            final String lv2KeyPrefix = "normal-list-lv0-objects/lv1-objects/lv2-objects-";
            for (int i = 0; i < keyCount; i++) {
                if (i % 10 != 0) {
                    existingKeys.add(lv0KeyPrefix + i);
                } else {
                    existingKeys.add(lv1KeyPrefix + i);
                    if (i % 100 == 0) {                        
                        existingKeys.add(lv2KeyPrefix + i);
                    }
                }
            }
            
            if (!batchPutObject(ossClient, bucketName, existingKeys)) {
                Assertions.fail("batch put object failed");
            }
            
            // List objects under nonempty bucket
            objectListing = ossClient.listObjects(bucketName);
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getMaxKeys());
            Assertions.assertEquals(0, objectListing.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, objectListing.getBucketName());
            Assertions.assertNull(objectListing.getDelimiter());
            Assertions.assertNull(objectListing.getPrefix());
            Assertions.assertNull(objectListing.getMarker());
            Assertions.assertNotNull(objectListing.getNextMarker());
            Assertions.assertTrue(objectListing.isTruncated());
            Assertions.assertEquals(objectListing.getRequestId().length(), REQUEST_ID_LEN);
            
            // List objects with lv1KeyPrefix under nonempty bucket
            objectListing = ossClient.listObjects(bucketName, lv1KeyPrefix);
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getMaxKeys());
            Assertions.assertEquals(0, objectListing.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, objectListing.getBucketName());
            Assertions.assertNull(objectListing.getDelimiter());
            Assertions.assertEquals(lv1KeyPrefix, objectListing.getPrefix());
            Assertions.assertNull(objectListing.getMarker());
            Assertions.assertNotNull(objectListing.getNextMarker());
            Assertions.assertTrue(objectListing.isTruncated());
            Assertions.assertEquals(objectListing.getRequestId().length(), REQUEST_ID_LEN);
            
            // List objects with lv0KeyPrefix under nonempty bucket
            objectListing = ossClient.listObjects(bucketName, lv0KeyPrefix);
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getMaxKeys());
            Assertions.assertEquals(0, objectListing.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, objectListing.getBucketName());
            Assertions.assertNull(objectListing.getDelimiter());
            Assertions.assertEquals(lv0KeyPrefix, objectListing.getPrefix());
            Assertions.assertNull(objectListing.getMarker());
            Assertions.assertNotNull(objectListing.getNextMarker());
            Assertions.assertTrue(objectListing.isTruncated());
            Assertions.assertEquals(objectListing.getRequestId().length(), REQUEST_ID_LEN);
            
            // List object with 'prefix' and 'marker' under nonempty bucket
            String marker = objectListing.getNextMarker();
            objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName, lv0KeyPrefix, marker, null, null));
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getMaxKeys());
            Assertions.assertEquals(0, objectListing.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, objectListing.getBucketName());
            Assertions.assertNull(objectListing.getDelimiter());
            Assertions.assertEquals(lv0KeyPrefix, objectListing.getPrefix());
            Assertions.assertEquals(marker, objectListing.getMarker());
            Assertions.assertNotNull(objectListing.getNextMarker());
            Assertions.assertTrue(objectListing.isTruncated());
            Assertions.assertEquals(objectListing.getRequestId().length(), REQUEST_ID_LEN);

            // List object with 'prefix' and 'delimiter' under nonempty bucket
            final String delimiter = "/";
            final String keyPrefix0 = "normal-list-lv0-objects/";
            final String keyPrefix1 = "normal-list-lv0-objects/lv1-objects/";
            objectListing = ossClient.listObjects(
                    new ListObjectsRequest(bucketName, keyPrefix0, null, delimiter, MAX_RETURNED_KEYS_LIMIT));
            Assertions.assertEquals(lv1KeyCount, objectListing.getObjectSummaries().size());
            Assertions.assertEquals(MAX_RETURNED_KEYS_LIMIT, objectListing.getMaxKeys());
            Assertions.assertEquals(1, objectListing.getCommonPrefixes().size());
            Assertions.assertEquals(keyPrefix1, objectListing.getCommonPrefixes().get(0));
            Assertions.assertEquals(bucketName, objectListing.getBucketName());
            Assertions.assertEquals(delimiter, objectListing.getDelimiter());
            Assertions.assertEquals(keyPrefix0, objectListing.getPrefix());
            Assertions.assertNull(objectListing.getMarker());
            Assertions.assertNull(objectListing.getNextMarker());
            Assertions.assertFalse(objectListing.isTruncated());
            Assertions.assertEquals(objectListing.getRequestId().length(), REQUEST_ID_LEN);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            deleteBucketWithObjects(ossClient, bucketName);
        }
    }

    @Test
    public void testUnormalListObjects() {
        final String bucketName = super.bucketName + "-unormal-list-objects";
        
        try {
            ossClient.createBucket(bucketName);
            
            // List objects under non-existent bucket
            final String nonexistentBucket = super.bucketName + "-unormal-list-objects-bucket";
            try {
                ossClient.listObjects(nonexistentBucket);
                Assertions.fail("List objects should not be successful");
            } catch (OSSException e) {
                Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            }
            
            // List objects under bucket without ownership
            final String bucketWithoutOwnership = "oss";
            try {
                ossClient.listObjects(bucketWithoutOwnership);
                Assertions.fail("List objects should not be successful");
            } catch (OSSException e) {
                Assertions.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            }
            
            // Add DEFAULT_MAX_RETURNED_KEYS - 1 objects to bucket
            List<String> existingKeys = new ArrayList<String>();
            final int keyCount = DEFAULT_MAX_RETURNED_KEYS;
            final String keyPrefix = "unormal-list-objects-";
            final int unluckyNumber = 13;
            for (int i = 0; i <= keyCount; i++) {
                if (i != unluckyNumber) {
                    existingKeys.add(keyPrefix + i);
                }
            }
            
            if (!batchPutObject(ossClient, bucketName, existingKeys)) {
                Assertions.fail("batch put object failed");
            }
            
            // List object with nonexistent marker
            final String nonexistentMarker = keyPrefix + unluckyNumber;
            try {
                ListObjectsRequest request = new ListObjectsRequest(bucketName, null, nonexistentMarker, null, null);
                ObjectListing objectListing = ossClient.listObjects(request);
                Assertions.assertTrue(objectListing.getObjectSummaries().size() < keyCount);
                Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getMaxKeys());
                Assertions.assertEquals(0, objectListing.getCommonPrefixes().size());
                Assertions.assertEquals(bucketName, objectListing.getBucketName());
                Assertions.assertNull(objectListing.getDelimiter());
                Assertions.assertNull(objectListing.getPrefix());
                Assertions.assertEquals(nonexistentMarker, objectListing.getMarker());
                Assertions.assertNull(objectListing.getNextMarker());
                Assertions.assertFalse(objectListing.isTruncated());
            } catch (Exception e) {
                Assertions.fail(e.getMessage());
            }
            
            // Set 'max-keys' less than zero
            final int maxKeysExceedLowerLimit = -1;
            try {
                ListObjectsRequest request = new ListObjectsRequest(bucketName);
                request.setMaxKeys(maxKeysExceedLowerLimit);
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof IllegalArgumentException);
            }
            
            // Set 'max-keys' exceed MAX_RETURNED_KEYS_LIMIT
            final int maxKeysExceedUpperLimit = MAX_RETURNED_KEYS_LIMIT + 1;
            try {
                ListObjectsRequest request = new ListObjectsRequest(bucketName);
                request.setMaxKeys(maxKeysExceedUpperLimit);
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof IllegalArgumentException);
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            deleteBucketWithObjects(ossClient, bucketName);
        }
    }
    
    @Test
    public void testListObjectsWithEncodingType() {
        final String objectPrefix = "object-with-special-characters-";
        
        try {
            // Add several objects with special characters into bucket.
            List<String> existingKeys = new ArrayList<String>();
            existingKeys.add(objectPrefix + "\001\007");
            existingKeys.add(objectPrefix + "\002\007");
            
            if (!batchPutObject(ossClient, bucketName, existingKeys)) {
                Assertions.fail("batch put object failed");
            }
            
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
            try {
                ossClient.listObjects(listObjectsRequest);
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof OSSException);
                Assertions.assertEquals(OSSErrorCode.INVALID_RESPONSE, ((OSSException)e).getErrorCode());
            }
            
            // List objects under nonempty bucket
            listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setEncodingType(DEFAULT_ENCODING_TYPE);
            ObjectListing objectListing = ossClient.listObjects(listObjectsRequest);
            for (OSSObjectSummary s : objectListing.getObjectSummaries()) {
                String decodedKey = URLDecoder.decode(s.getKey(), "UTF-8");
                Assertions.assertTrue(existingKeys.contains(decodedKey));
            }
            Assertions.assertEquals(DEFAULT_ENCODING_TYPE, objectListing.getEncodingType());
            Assertions.assertEquals(existingKeys.size(), objectListing.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, objectListing.getMaxKeys());
            Assertions.assertEquals(0, objectListing.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, objectListing.getBucketName());
            Assertions.assertNull(objectListing.getDelimiter());
            Assertions.assertNull(objectListing.getPrefix());
            Assertions.assertNull(objectListing.getMarker());
            Assertions.assertNull(objectListing.getNextMarker());
            Assertions.assertFalse(objectListing.isTruncated());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }
    
}
