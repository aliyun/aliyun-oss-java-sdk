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

import java.util.List;
import java.util.Map;

import com.aliyun.oss.model.*;
import junit.framework.Assert;

import org.junit.Test;

public class BucketTaggingTest extends TestBase {

	@Test
	public void testSetBucketTagging() {
		try {
			bucketName = createBucket();
			SetBucketTaggingRequest request = new SetBucketTaggingRequest(bucketName);
			request.setTag("tk1", "tv1");
			request.setTag("tk2", "tv2");
			ossClient.setBucketTagging(request);

			TagSet tagSet = ossClient.getBucketTagging(new GenericRequest(bucketName));
			Assert.assertEquals(tagSet.getRequestId().length(), REQUEST_ID_LEN);
			Map<String, String> tags = tagSet.getAllTags();
			Assert.assertEquals(2, tags.size());
			Assert.assertTrue(tags.containsKey("tk1"));
			Assert.assertTrue(tags.containsKey("tk2"));

			ListBucketsRequest listBucketsRequest = new ListBucketsRequest();
			listBucketsRequest.setTagSet(tagSet);

			BucketList bucketList = ossClient.listBuckets(listBucketsRequest);

			List<Bucket> list = bucketList.getBucketList();

			Assert.assertTrue(list.size() > 0);

			for (Bucket bucket : list) {
				TagSet tagSet1 = bucket.getTagSet();
				Map<String, String> allTags = tagSet1.getAllTags();
				Assert.assertTrue(allTags.containsKey("tk1"));
				Assert.assertTrue(allTags.containsKey("tk2"));
				Assert.assertEquals("tv1", allTags.get("tk1"));
				Assert.assertEquals("tv2", allTags.get("tk2"));
			}

			ossClient.deleteBucketTagging(new GenericRequest(bucketName));

			waitForCacheExpiration(2);

			tagSet = ossClient.getBucketTagging(new GenericRequest(bucketName));
			Assert.assertEquals(tagSet.getRequestId().length(), REQUEST_ID_LEN);
			tags = tagSet.getAllTags();
			Assert.assertTrue(tags.isEmpty());

			ossClient.deleteBucket(bucketName);
		} catch (Exception e) {
			ossClient.deleteBucket(bucketName);
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Override
	public void setUp() throws Exception {
	}

	@Override
	public void tearDown() throws Exception {
	}
}
