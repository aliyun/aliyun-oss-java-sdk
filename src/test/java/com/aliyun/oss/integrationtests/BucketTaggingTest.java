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

import junit.framework.Assert;

import org.junit.Test;

import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.SetBucketTaggingRequest;
import com.aliyun.oss.model.TagSet;

public class BucketTaggingTest extends TestBase {

    @Test
    public void testSetBucketTagging() {
        try {
            SetBucketTaggingRequest request = new SetBucketTaggingRequest(bucketName);
            request.setTag("tk1", "tv1");
            request.setTag("tk2", "tv2");
            secondClient.setBucketTagging(request);
            
            TagSet tagSet = secondClient.getBucketTagging(new GenericRequest(bucketName));
            Map<String, String> tags = tagSet.getAllTags();
            Assert.assertEquals(2, tags.size());
            Assert.assertTrue(tags.containsKey("tk1"));
            Assert.assertTrue(tags.containsKey("tk2"));
            
            secondClient.deleteBucketTagging(new GenericRequest(bucketName));
          
            waitForCacheExpiration(5);
            
            tagSet = secondClient.getBucketTagging(new GenericRequest(bucketName));
            tags = tagSet.getAllTags();
            Assert.assertTrue(tags.isEmpty());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
