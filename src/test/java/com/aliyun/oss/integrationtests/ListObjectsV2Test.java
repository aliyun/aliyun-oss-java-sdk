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

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyun.oss.model.ListObjectsV2Request;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import org.junit.jupiter.api.*;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import static com.aliyun.oss.integrationtests.TestUtils.batchPutObject;
import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_CHARSET_NAME;

public class ListObjectsV2Test extends TestBase {
    private static final int DEFAULT_MAX_RETURNED_KEYS = 100;

    private OSSClient ossClient;
    private String bucketName;
    private String endpoint;

    public void setUp() throws Exception {
        super.setUp();

        bucketName = super.bucketName + "-list-v2";
        endpoint = "http://oss-ap-southeast-2.aliyuncs.com";

        // create client
        ClientConfiguration conf = new ClientConfiguration().setSupportCname(false);
        Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(credentials), conf);

        ossClient.createBucket(bucketName);
        waitForCacheExpiration(2);
    }

    public void tearDown() throws Exception {
        if (ossClient != null) {
            ossClient.shutdown();
            ossClient = null;
        }
        super.tearDown();
    }

    @Test
    public void testNormalListObjects() {
        final String bucketName = super.bucketName + "-normalist-objectsv2";

        try {
            ossClient.createBucket(bucketName);

            // List objects under empty bucket
            ListObjectsV2Result result = ossClient.listObjectsV2(bucketName);
            Assertions.assertEquals(0, result.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, result.getMaxKeys());
            Assertions.assertEquals(0, result.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, result.getBucketName());
            Assertions.assertEquals(0, result.getKeyCount());
            Assertions.assertNull(result.getEncodingType());
            Assertions.assertNull(result.getContinuationToken());
            Assertions.assertNull(result.getNextContinuationToken());
            Assertions.assertNull(result.getStartAfter());
            Assertions.assertNull(result.getDelimiter());
            Assertions.assertNull(result.getPrefix());
            Assertions.assertFalse(result.isTruncated());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            // 99 files under sub-dir
            String dir1 = "test-dir/";
            String subDir = "sub-dir/";
            String keyPrefix = dir1 + subDir + "test-file";
            for (int i = 0; i < 99; i++) {
                String key = keyPrefix + i + ".txt";
                ossClient.putObject(bucketName, key, new ByteArrayInputStream("1".getBytes()));
            }

            // 1 file under dir1
            ossClient.putObject(bucketName, dir1 + "sub-file.txt", new ByteArrayInputStream("1".getBytes()));

            // 3 top dir files
            String bigLetterPrefix = "z";
            ossClient.putObject(bucketName, bigLetterPrefix + "1.txt", new ByteArrayInputStream("1".getBytes()));
            ossClient.putObject(bucketName, bigLetterPrefix +"2.txt", new ByteArrayInputStream("1".getBytes()));
            ossClient.putObject(bucketName, bigLetterPrefix + "3.txt", new ByteArrayInputStream("1".getBytes()));

            // List objects under nonempty bucket
            result = ossClient.listObjectsV2(bucketName);
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, result.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, result.getMaxKeys());
            Assertions.assertEquals(0, result.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, result.getBucketName());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, result.getKeyCount());
            Assertions.assertNull(result.getEncodingType());
            Assertions.assertNull(result.getContinuationToken());
            Assertions.assertNotNull(result.getNextContinuationToken());
            Assertions.assertNull(result.getStartAfter());
            Assertions.assertNull(result.getDelimiter());
            Assertions.assertNull(result.getPrefix());
            Assertions.assertTrue(result.isTruncated());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            // List object with continuationToken.
            String continuationToken = result.getNextContinuationToken();
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(bucketName)
                    .withContinuationToken(continuationToken);
            result = ossClient.listObjectsV2(listObjectsV2Request);
            Assertions.assertEquals(3, result.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, result.getMaxKeys());
            Assertions.assertEquals(0, result.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, result.getBucketName());
            Assertions.assertEquals(3, result.getKeyCount());
            Assertions.assertNull(result.getEncodingType());
            Assertions.assertEquals(continuationToken, result.getContinuationToken());
            Assertions.assertNull(result.getNextContinuationToken());
            Assertions.assertNull(result.getDelimiter());
            Assertions.assertNull(result.getPrefix());
            Assertions.assertNull(result.getStartAfter());
            Assertions.assertFalse(result.isTruncated());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            // List objects with prefix
            result = ossClient.listObjectsV2(bucketName, bigLetterPrefix);
            Assertions.assertEquals(3, result.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, result.getMaxKeys());
            Assertions.assertEquals(0, result.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, result.getBucketName());
            Assertions.assertEquals(3, result.getKeyCount());
            Assertions.assertNull(result.getEncodingType());
            Assertions.assertNull(result.getContinuationToken());
            Assertions.assertNull(result.getNextContinuationToken());
            Assertions.assertNull(result.getStartAfter());
            Assertions.assertNull(result.getDelimiter());
            Assertions.assertEquals(bigLetterPrefix, result.getPrefix());
            Assertions.assertFalse(result.isTruncated());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            // List objects with prefix and delimiter
            listObjectsV2Request = new ListObjectsV2Request(bucketName, bigLetterPrefix);
            listObjectsV2Request.setDelimiter("/");
            result = ossClient.listObjectsV2(listObjectsV2Request);
            Assertions.assertEquals(3, result.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, result.getMaxKeys());
            Assertions.assertEquals(0, result.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, result.getBucketName());
            Assertions.assertEquals(3, result.getKeyCount());
            Assertions.assertNull(result.getEncodingType());
            Assertions.assertNull(result.getContinuationToken());
            Assertions.assertNull(result.getNextContinuationToken());
            Assertions.assertEquals("/", result.getDelimiter());
            Assertions.assertEquals(bigLetterPrefix, result.getPrefix());
            Assertions.assertNull(result.getStartAfter());
            Assertions.assertFalse(result.isTruncated());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            // List object with prefix, delimiter, encodeType
            result = ossClient.listObjectsV2(bucketName, dir1, null, null,
                    "/", null, DEFAULT_ENCODING_TYPE, false);
            Assertions.assertEquals(1, result.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, result.getMaxKeys());
            Assertions.assertEquals(1, result.getCommonPrefixes().size());
            Assertions.assertEquals(HttpUtil.urlEncode(dir1 + subDir,DEFAULT_CHARSET_NAME), result.getCommonPrefixes().get(0));
            Assertions.assertEquals(bucketName, result.getBucketName());
            Assertions.assertEquals(2, result.getKeyCount());
            Assertions.assertEquals(DEFAULT_ENCODING_TYPE, result.getEncodingType());
            Assertions.assertNull(result.getContinuationToken());
            Assertions.assertNull(result.getNextContinuationToken());
            String delimiter = result.getDelimiter();
            Assertions.assertEquals(HttpUtil.urlEncode("/",DEFAULT_CHARSET_NAME), delimiter);
            Assertions.assertEquals("%2F", delimiter);
            Assertions.assertEquals(HttpUtil.urlEncode(dir1, DEFAULT_CHARSET_NAME), result.getPrefix());
            Assertions.assertNull(result.getStartAfter());
            Assertions.assertFalse(result.isTruncated());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            // List objects with startAfter
            listObjectsV2Request = new ListObjectsV2Request(bucketName);
            listObjectsV2Request.setStartAfter(bigLetterPrefix);
            result = ossClient.listObjectsV2(listObjectsV2Request);
            Assertions.assertEquals(3, result.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, result.getMaxKeys());
            Assertions.assertEquals(0, result.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, result.getBucketName());
            Assertions.assertEquals(3, result.getKeyCount());
            Assertions.assertNull(result.getEncodingType());
            Assertions.assertNull(result.getContinuationToken());
            Assertions.assertNull(result.getNextContinuationToken());
            Assertions.assertNull(result.getDelimiter());
            Assertions.assertNull(result.getPrefix());
            Assertions.assertEquals(bigLetterPrefix, result.getStartAfter());
            Assertions.assertFalse(result.isTruncated());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            deleteBucketWithObjects(ossClient, bucketName);
        }
    }

    @Test
    public void testNormalListObjectsFetchOwner() {
        final String bucketName = super.bucketName + "list-objects-v2-fetch-owner";

        try {
            ossClient.createBucket(bucketName);

            ossClient.putObject(bucketName, "1.txt", new ByteArrayInputStream("1".getBytes()));
            ossClient.putObject(bucketName, "2.txt", new ByteArrayInputStream("1".getBytes()));

            // List objects without fetch-owner
            ListObjectsV2Result result = ossClient.listObjectsV2(bucketName);
            Assertions.assertEquals(2, result.getKeyCount());
            List<OSSObjectSummary> ossObjectSummaries = result.getObjectSummaries();
            Assertions.assertEquals(2, ossObjectSummaries.size());
            for (OSSObjectSummary obj : ossObjectSummaries) {
                Assertions.assertEquals(bucketName, obj.getBucketName());
                Assertions.assertNotNull(obj.getKey());
                Assertions.assertNotNull(obj.getType());
                Assertions.assertEquals(1, obj.getSize());
                Assertions.assertNotNull(obj.getETag());
                Assertions.assertNotNull(obj.getLastModified());
                Assertions.assertNotNull(obj.getStorageClass());
                Assertions.assertNull(obj.getOwner());
            }

            // List objects with fetch-owner
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(bucketName);
            listObjectsV2Request.setFetchOwner(true);
            result = ossClient.listObjectsV2(listObjectsV2Request);
            Assertions.assertEquals(2, result.getKeyCount());
            ossObjectSummaries = result.getObjectSummaries();
            Assertions.assertEquals(2, ossObjectSummaries.size());
            for (OSSObjectSummary obj : ossObjectSummaries) {
                Assertions.assertEquals(bucketName, obj.getBucketName());
                Assertions.assertNotNull(obj.getKey());
                Assertions.assertNotNull(obj.getType());
                Assertions.assertEquals(1, obj.getSize());
                Assertions.assertNotNull(obj.getETag());
                Assertions.assertNotNull(obj.getLastModified());
                Assertions.assertNotNull(obj.getStorageClass());
                Assertions.assertNotNull(obj.getOwner());
                Assertions.assertNotNull(obj.getOwner().getId());
                Assertions.assertNotNull(obj.getOwner().getDisplayName());
            }
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            deleteBucketWithObjects(ossClient, bucketName);
        }
    }

    @Test
    public void testNormalListObjectsWithMaxKeys() {
        final String bucketName = super.bucketName + "-normalist-objects-v2-maxkeys";

        try {
            ossClient.createBucket(bucketName);

            ossClient.putObject(bucketName,  "1.txt", new ByteArrayInputStream("1".getBytes()));
            ossClient.putObject(bucketName, "2.txt", new ByteArrayInputStream("1".getBytes()));
            ossClient.putObject(bucketName, "3.txt", new ByteArrayInputStream("1".getBytes()));

            // List objects under nonempty bucket
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(bucketName);
            listObjectsV2Request.setMaxKeys(2);
            ListObjectsV2Result result = ossClient.listObjectsV2(listObjectsV2Request);
            Assertions.assertEquals(2, result.getObjectSummaries().size());
            Assertions.assertEquals(2, result.getMaxKeys());
            Assertions.assertEquals(0, result.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, result.getBucketName());
            Assertions.assertEquals(2, result.getKeyCount());
            Assertions.assertNull(result.getEncodingType());
            Assertions.assertNull(result.getContinuationToken());
            Assertions.assertNotNull(result.getNextContinuationToken());
            Assertions.assertNull(result.getStartAfter());
            Assertions.assertNull(result.getDelimiter());
            Assertions.assertNull(result.getPrefix());
            Assertions.assertTrue(result.isTruncated());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            deleteBucketWithObjects(ossClient, bucketName);
        }
    }

    @Test
    public void testListObjectsWithEncodingType() {
        final String objectPrefix = "object-with-special-characters";

        try {
            // Add several objects with special characters into bucket.
            List<String> existingKeys = new ArrayList<String>();
            existingKeys.add(objectPrefix + "\001\007");
            existingKeys.add(objectPrefix + "\002\007");
            
            if (!batchPutObject(ossClient, bucketName, existingKeys)) {
                Assertions.fail("batch put object failed");
            }
            
            ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request(bucketName);
            try {
                ossClient.listObjectsV2(listObjectsV2Request);
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof OSSException);
                Assertions.assertEquals(OSSErrorCode.INVALID_RESPONSE, ((OSSException)e).getErrorCode());
            }
            
            // List objects under nonempty bucket
            listObjectsV2Request = new ListObjectsV2Request(bucketName);
            listObjectsV2Request.setEncodingType(DEFAULT_ENCODING_TYPE);
            ListObjectsV2Result result = ossClient.listObjectsV2(listObjectsV2Request);
            for (OSSObjectSummary s : result.getObjectSummaries()) {
                String decodedKey = URLDecoder.decode(s.getKey(), "UTF-8");
                Assertions.assertTrue(existingKeys.contains(decodedKey));
            }
            Assertions.assertEquals(2, result.getObjectSummaries().size());
            Assertions.assertEquals(DEFAULT_MAX_RETURNED_KEYS, result.getMaxKeys());
            Assertions.assertEquals(0, result.getCommonPrefixes().size());
            Assertions.assertEquals(bucketName, result.getBucketName());
            Assertions.assertEquals(2, result.getKeyCount());
            Assertions.assertEquals(DEFAULT_ENCODING_TYPE, result.getEncodingType());
            Assertions.assertNull(result.getContinuationToken());
            Assertions.assertNull(result.getNextContinuationToken());
            Assertions.assertNull(result.getDelimiter());
            Assertions.assertNull(result.getPrefix());
            Assertions.assertNull(result.getStartAfter());
            Assertions.assertFalse(result.isTruncated());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testRequestMethodChaining() {
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix("fun")
                .withContinuationToken("tokenTest")
                .withStartAfter("A")
                .withDelimiter("/")
                .withMaxKeys(10)
                .withEncodingType("url")
                .withFetchOwner(true);
        Assertions.assertEquals(bucketName, request.getBucketName());
        Assertions.assertEquals("fun", request.getPrefix());
        Assertions.assertEquals("tokenTest", request.getContinuationToken());
        Assertions.assertEquals("A", request.getStartAfter());
        Assertions.assertEquals("/", request.getDelimiter());
        Assertions.assertEquals(Integer.valueOf(10), request.getMaxKeys());
        Assertions.assertEquals("url", request.getEncodingType());
        Assertions.assertTrue(request.isFetchOwner());
    }

    @Test
    public void testUnnormalListObjects() {
        final String bucketName = super.bucketName + "-unormal-list-objects";

        try {
            ossClient.createBucket(bucketName);
            ossClient.putObject(bucketName,  "1.txt", new ByteArrayInputStream("1".getBytes()));
            ossClient.putObject(bucketName, "2.txt", new ByteArrayInputStream("1".getBytes()));
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // List object with nonexistent continueToken.
        final String nonexistentContinueToken = "none-exist-continue-token";
        try {
            ListObjectsV2Request request = new ListObjectsV2Request(bucketName);
            request.setContinuationToken(nonexistentContinueToken);
            ossClient.listObjectsV2(request);
            Assertions.fail("should be failed here.");
        } catch (OSSException e) {
            Assertions.assertEquals(e.getErrorCode(),OSSErrorCode.INVALID_ARGUMENT);
        }
    }

}
