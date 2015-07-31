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

import static com.aliyun.oss.integrationtests.TestConfig.BUCKET_NAME_PREFIX;
import static com.aliyun.oss.integrationtests.TestConfig.DEFAULT_ACCESS_ID_1;
import static com.aliyun.oss.integrationtests.TestConfig.DEFAULT_ACCESS_KEY_1;
import static com.aliyun.oss.integrationtests.TestConfig.DEFAULT_ENDPOINT;
import static com.aliyun.oss.integrationtests.TestConfig.SECOND_ACCESS_ID;
import static com.aliyun.oss.integrationtests.TestConfig.SECOND_ACCESS_KEY;
import static com.aliyun.oss.integrationtests.TestConfig.SECOND_ENDPOINT;
import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;
import static com.aliyun.oss.model.DeleteObjectsRequest.DELETE_OBJECTS_ONETIME_LIMIT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.ListBucketsRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

public class TestBase {
	
	protected static String bucketName;
	
	protected static OSSClient defaultClient;
	protected static OSSClient secondClient;
	
	private static final Credentials defaultCreds = 
			new DefaultCredentials(DEFAULT_ACCESS_ID_1, DEFAULT_ACCESS_KEY_1);
	private static final Credentials secondCreds = 
			new DefaultCredentials(SECOND_ACCESS_ID, SECOND_ACCESS_KEY);
	
	public static final String DEFAULT_ENCODING_TYPE = "url";
	
	@BeforeClass
	public static void oneTimeSetUp() {
		cleanUpAllBuckets(getDefaultClient(), BUCKET_NAME_PREFIX);
		cleanUpAllBuckets(getSecondClient(), BUCKET_NAME_PREFIX);
	}
	
	@Before
	public void setUp() throws Exception {
		bucketName = createBucket();
	}

	@After
	public void tearDown() throws Exception {
		deleteBucket(bucketName);
		cleanUp();
	}
	
	public static OSSClient getDefaultClient() {
		if (defaultClient == null) {
			defaultClient = new OSSClient(DEFAULT_ENDPOINT, 
					new DefaultCredentialProvider(defaultCreds));
		}
		return defaultClient;
	}
	
	public static OSSClient getSecondClient() {
		if (secondClient == null) {
			secondClient = new OSSClient(SECOND_ENDPOINT, 
					new DefaultCredentialProvider(secondCreds));
		}
		return secondClient;
	}
	
	public static String createBucket() {
		long ticks = new Date().getTime() / 1000 + new Random().nextInt(5000);
		String bucketName = BUCKET_NAME_PREFIX + ticks;
		getDefaultClient().createBucket(bucketName);
		getSecondClient().createBucket(bucketName);
		waitForCacheExpiration(2);
		return bucketName;
	}
	
	public static void deleteBucket(String bucketName) {
		deleteBucketWithObjects(defaultClient, bucketName);
		deleteBucketWithObjects(secondClient, bucketName);
	}
	
	protected static void deleteBucketWithObjects(OSSClient client, String bucketName) {
		if (!client.doesBucketExist(bucketName)) {
			return;
		}
		
		List<String> allObjects = listAllObjects(client, bucketName);
		int total = allObjects.size();
		if (total > 0) {
			int opLoops = total / DELETE_OBJECTS_ONETIME_LIMIT;
			if (total % DELETE_OBJECTS_ONETIME_LIMIT != 0) {
				opLoops++;
			}
			
			List<String> objectsToDel = null;
			for (int i = 0; i < opLoops; i++) {
				int fromIndex = i * DELETE_OBJECTS_ONETIME_LIMIT;
				int len = 0;
				if (total <= DELETE_OBJECTS_ONETIME_LIMIT) {
					len = total;
				} else {
					len = (i + 1 == opLoops) ? (total - fromIndex) : DELETE_OBJECTS_ONETIME_LIMIT;					
				}
				objectsToDel = allObjects.subList(fromIndex, fromIndex + len);
				
				DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
				deleteObjectsRequest.setEncodingType(DEFAULT_ENCODING_TYPE);
				deleteObjectsRequest.setKeys(objectsToDel);
				client.deleteObjects(deleteObjectsRequest);
			}
		}
		client.deleteBucket(bucketName);
	}
	
	protected static List<String> listAllObjects(OSSClient client, String bucketName) {
		List<String> objs = new ArrayList<String>();
		ObjectListing objectListing = null;
		String nextMarker = null;
		
		do {
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName, null, nextMarker, null, 
					DELETE_OBJECTS_ONETIME_LIMIT);
			listObjectsRequest.setEncodingType(DEFAULT_ENCODING_TYPE);
			objectListing = client.listObjects(listObjectsRequest);
			if (DEFAULT_ENCODING_TYPE.equals(objectListing.getEncodingType())) {
				nextMarker = HttpUtil.urlDecode(objectListing.getNextMarker(), "UTF-8");
			} else {
				nextMarker = objectListing.getNextMarker();
			}
			
			List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
			for (OSSObjectSummary s : sums) {
				if (DEFAULT_ENCODING_TYPE.equals(objectListing.getEncodingType())) {
					objs.add(HttpUtil.urlDecode(s.getKey(), "UTF-8"));
				} else {
					objs.add(s.getKey());
				}
			}
		} while (objectListing.isTruncated());
		
		return objs;
	}
	
	protected static List<String> listAllBuckets(OSSClient client, String bucketPrefix) {
		List<String> bkts = new ArrayList<String>();
		String nextMarker = null;
		BucketList bucketList = null;
		
		do {
			ListBucketsRequest listBucketsRequest = new ListBucketsRequest(bucketPrefix, nextMarker, 
					ListBucketsRequest.MAX_RETURNED_KEYS);
			bucketList = client.listBuckets(listBucketsRequest);
			nextMarker = bucketList.getNextMarker();
			for (Bucket b : bucketList.getBucketList()) {
				bkts.add(b.getName());
			}
		} while (bucketList.isTruncated());
		
		return bkts;
	}
	
	protected static void cleanUpAllBuckets(OSSClient client, String bucketPrefix) {
		List<String> bkts = listAllBuckets(client, bucketPrefix);
		for (String b : bkts) {
			deleteBucketWithObjects(client, b);
		}
	}
	
	public static void restoreDefaultCredentials() {
		getDefaultClient().switchCredentials(defaultCreds);
	}
	
	public static void restoreDefaultEndpoint() {
		getDefaultClient().setEndpoint(DEFAULT_ENDPOINT);
	}
	
	public static void cleanUp() {
		if (defaultClient != null) {
			defaultClient.shutdown();
			defaultClient = null;
		}
		if (secondClient != null) {
			secondClient.shutdown();
			secondClient = null;
		}
	}
}

