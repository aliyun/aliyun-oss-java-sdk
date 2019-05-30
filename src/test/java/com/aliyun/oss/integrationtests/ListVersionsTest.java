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
import static com.aliyun.oss.integrationtests.TestUtils.genFixedLengthInputStream;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ListVersionsRequest;
import com.aliyun.oss.model.OSSVersionSummary;
import com.aliyun.oss.model.VersionListing;

public class ListVersionsTest extends TestBase {

    private static String bucketName = "mingdi-test";

    @Test
    public void testNormalListVersions() {
        String prefix = "version-test-put-object";
        String key1 = prefix + "/dir1/key1";
        String key2 = prefix + "/dir2/key2";
        long inputStreamLength = 64;

        try {
            InputStream instream = genFixedLengthInputStream(inputStreamLength);
            ossClient.putObject(bucketName, key1, instream);
            ossClient.putObject(bucketName, key1, instream);
            ossClient.putObject(bucketName, key1, instream);
            ossClient.putObject(bucketName, key2, instream);
            ossClient.deleteObject(bucketName, key2);

            // List default
            ListVersionsRequest listVersionsRequest = new ListVersionsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix);

            VersionListing versionListing = ossClient.listVersions(listVersionsRequest);
            Assert.assertEquals(bucketName, versionListing.getBucketName());
            Assert.assertEquals(prefix, versionListing.getPrefix());
            Assert.assertEquals(5, versionListing.getVersionSummaries().size());
            Assert.assertNull(versionListing.getDelimiter());
            Assert.assertFalse(versionListing.isTruncated());

            // List by max
            listVersionsRequest = new ListVersionsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix)
                .withMaxResults(3);

            versionListing = ossClient.listVersions(listVersionsRequest);
            Assert.assertEquals(bucketName, versionListing.getBucketName());
            Assert.assertEquals(prefix, versionListing.getPrefix());
            Assert.assertEquals(3, versionListing.getMaxKeys());
            Assert.assertEquals(3, versionListing.getVersionSummaries().size());
            Assert.assertNull(versionListing.getDelimiter());
            Assert.assertTrue(versionListing.isTruncated());

            // List by page
            int numOfVersion = 0;
            String nextKeyMarker = null;
            String nextVersionMarker = null;
            do {
                listVersionsRequest = new ListVersionsRequest()
                    .withBucketName(bucketName)
                    .withPrefix(prefix)
                    .withKeyMarker(nextKeyMarker)
                    .withVersionIdMarker(nextVersionMarker)
                    .withMaxResults(2);

                versionListing = ossClient.listVersions(listVersionsRequest);
                numOfVersion += versionListing.getVersionSummaries().size();

                nextKeyMarker = versionListing.getNextKeyMarker();
                nextVersionMarker = versionListing.getNextVersionIdMarker();
            } while (versionListing.isTruncated());
            Assert.assertEquals(5, numOfVersion);

            // List by delimiter
            listVersionsRequest = new ListVersionsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix)
                .withDelimiter("/")
                .withMaxResults(5);

            versionListing = ossClient.listVersions(listVersionsRequest);
            Assert.assertEquals(1, versionListing.getCommonPrefixes().size());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testListVersionsWithEncodingType() {
        final String objectPrefix = "version-test-key-with-special-characters-";

        try {
            // Prepare
            List<String> existingKeys = new ArrayList<String>();
            existingKeys.add(objectPrefix + "\001\007");
            existingKeys.add(objectPrefix + "\002\007");

            if (!batchPutObject(ossClient, bucketName, existingKeys)) {
                Assert.fail("batch put object failed");
            }

            try {
                // Unormal
                ListVersionsRequest listVersionsRequest = new ListVersionsRequest()
                    .withBucketName(bucketName)
                    .withPrefix(objectPrefix)
                    .withEncodingType(null);

                ossClient.listVersions(listVersionsRequest);
                Assert.fail("List version should not be successful.");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof OSSException);
                Assert.assertEquals(OSSErrorCode.INVALID_RESPONSE, ((OSSException)e).getErrorCode());
            }

            // Normal
            ListVersionsRequest listVersionsRequest = new ListVersionsRequest()
                .withBucketName(bucketName)
                .withPrefix(objectPrefix);

            VersionListing versionListing = ossClient.listVersions(listVersionsRequest);
            for (OSSVersionSummary version : versionListing.getVersionSummaries()) {
                String decodedKey = URLDecoder.decode(version.getKey(), "UTF-8");
                Assert.assertTrue(existingKeys.contains(decodedKey));
            }
            Assert.assertNull(versionListing.getEncodingType());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}
