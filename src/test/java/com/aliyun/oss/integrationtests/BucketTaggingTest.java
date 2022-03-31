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

import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

import java.util.Map;

import com.aliyun.oss.model.*;
import org.junit.jupiter.api.*;

import org.junit.Test;

public class BucketTaggingTest extends TestBase {

    @Test
    public void testSetBucketTagging() {
        try {
            SetBucketTaggingRequest request = new SetBucketTaggingRequest(bucketName);
            request.setTag("tk1", "tv1");
            request.setTag("tk2", "tv2");
            ossClient.setBucketTagging(request);
            
            TagSet tagSet = ossClient.getBucketTagging(new GenericRequest(bucketName));
            Assertions.assertEquals(tagSet.getRequestId().length(), REQUEST_ID_LEN);
            Map<String, String> tags = tagSet.getAllTags();
            Assertions.assertEquals(2, tags.size());
            Assertions.assertTrue(tags.containsKey("tk1"));
            Assertions.assertTrue(tags.containsKey("tk2"));
            Assertions.assertEquals("tv1", tagSet.getTag("tk1"));
            tagSet.toString();
            tagSet.clear();
            //
            request = new SetBucketTaggingRequest(bucketName,tagSet);
            tagSet = ossClient.getBucketTagging(new GenericRequest(bucketName));
            Assertions.assertEquals(tagSet.getRequestId().length(), REQUEST_ID_LEN);
            tags = tagSet.getAllTags();
            Assertions.assertEquals(2, tags.size());
            Assertions.assertTrue(tags.containsKey("tk1"));
            Assertions.assertTrue(tags.containsKey("tk2"));

            request = new SetBucketTaggingRequest(bucketName).withTagSet(tagSet);
            tagSet = ossClient.getBucketTagging(new GenericRequest(bucketName));
            Assertions.assertEquals(tagSet.getRequestId().length(), REQUEST_ID_LEN);
            tags = tagSet.getAllTags();
            Assertions.assertEquals(2, tags.size());
            Assertions.assertTrue(tags.containsKey("tk1"));
            Assertions.assertTrue(tags.containsKey("tk2"));

            request = new SetBucketTaggingRequest(bucketName, tags);
            tagSet = ossClient.getBucketTagging(new GenericRequest(bucketName));
            Assertions.assertEquals(tagSet.getRequestId().length(), REQUEST_ID_LEN);
            tags = tagSet.getAllTags();
            Assertions.assertEquals(2, tags.size());
            Assertions.assertTrue(tags.containsKey("tk1"));
            Assertions.assertTrue(tags.containsKey("tk2"));

            ossClient.deleteBucketTagging(new GenericRequest(bucketName));
          
            waitForCacheExpiration(5);
            
            tagSet = ossClient.getBucketTagging(new GenericRequest(bucketName));
            Assertions.assertEquals(tagSet.getRequestId().length(), REQUEST_ID_LEN);
            tags = tagSet.getAllTags();
            Assertions.assertTrue(tags.isEmpty());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testListBucketsWithTag() {
        String bucketName1 = bucketName + "-1";
        String bicketName2 = bucketName + "-2";

        try {
            // Prepare
            ossClient.createBucket(bucketName1);
            ossClient.createBucket(bicketName2);

            TagSet tagSet  = new TagSet();
            tagSet.setTag("tk1", "tv1");
            tagSet.setTag("tk2", "tv2");
            ossClient.setBucketTagging(bucketName1, tagSet);

            waitForCacheExpiration(3);

            // List
            ListBucketsRequest listBucketsRequest = new ListBucketsRequest();
            listBucketsRequest.setTag("tk1", "tv1");

            BucketList bucketList = ossClient.listBuckets(listBucketsRequest);
            Assertions.assertEquals(1, bucketList.getBucketList().size());
            Assertions.assertEquals(bucketName1, bucketList.getBucketList().get(0).getName());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.getBucketTagging(bucketName1);
            ossClient.deleteBucketTagging(bucketName1);
            ossClient.deleteBucket(bucketName1);
            ossClient.deleteBucket(bicketName2);
        }
    }

}
