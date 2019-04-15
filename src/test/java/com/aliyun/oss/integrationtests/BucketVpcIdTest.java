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
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

public class BucketVpcIdTest extends TestBase {

	@Test
	public void testSetBucketTagging() {
		try {
			bucketName = createBucket();

			// 绑定 vpc id
			PutBucketVpcIdRequest putBucketVpcIdRequest = new PutBucketVpcIdRequest();
			putBucketVpcIdRequest.setBucketName(bucketName);
			putBucketVpcIdRequest.setVpcId("vpc-sn5dtl523m4cyahgk9ang");
			putBucketVpcIdRequest.setVpcRegion("cn-hangzhou-test-306");
			putBucketVpcIdRequest.setVpcTag("test-tag");
			ossClient.putBucketVpcId(putBucketVpcIdRequest);
			BucketVpcIdList bucketVpcIdList = ossClient.listBucketVpcId(new GenericRequest(bucketName));
			Assert.assertEquals(1, bucketVpcIdList.getList().size());

			// 删除 vpc id
			DeleteBucketVpcIdRequest deleteBucketVpcIdRequest = new DeleteBucketVpcIdRequest();
			deleteBucketVpcIdRequest.setBucketName(bucketName);
			deleteBucketVpcIdRequest.setVpcId("vpc-sn5dtl523m4cyahgk9ang");
			deleteBucketVpcIdRequest.setVpcRegion("cn-hangzhou-test-306");
			putBucketVpcIdRequest.setVpcTag("test-tag");
			ossClient.deleteBucketVpcId(deleteBucketVpcIdRequest);
			BucketVpcIdList bucketVpcIdList1 = ossClient.listBucketVpcId(new GenericRequest(bucketName));
			Assert.assertEquals(0, bucketVpcIdList1.getList().size());

			ossClient.deleteBucket(bucketName);
		} catch (Exception e) {
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
