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

import static com.aliyun.oss.integrationtests.TestConstants.NO_SUCH_BUCKET_ERR;
import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.oss.model.SetBucketRefererRequest;
import com.aliyun.oss.model.VoidResult;
import junit.framework.Assert;

import org.junit.Test;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.BucketReferer;

public class BucketRefererTest extends TestBase {

    private static final boolean DEFAULT_EMPTY_REFERER_ALLOWED = true;

    @Test
    public void testNormalSetBucketReferer() {
        final String bucketName = genBucketName() + "-referer-source";
        final String referer0 = "http://www.aliyun.com";
        final String referer1 = "https://www.aliyun.com";
        final String referer2 = "http://www.*.com";
        final String referer3 = "https://www.?.aliyuncs.com";

        try {
            ossClient.createBucket(bucketName);

            // Set non-empty referer list
            BucketReferer r = new BucketReferer();
            List<String> refererList = new ArrayList<String>();
            refererList.add(referer0);
            refererList.add(referer1);
            refererList.add(referer2);
            refererList.add(referer3);
            r.setRefererList(refererList);
            ossClient.setBucketReferer(bucketName, r);

            waitForCacheExpiration(5);

            r = ossClient.getBucketReferer(bucketName);
            List<String> returedRefererList = r.getRefererList();
            Assert.assertTrue(r.isAllowEmptyReferer());
            Assert.assertTrue(returedRefererList.contains(referer0));
            Assert.assertTrue(returedRefererList.contains(referer1));
            Assert.assertTrue(returedRefererList.contains(referer2));
            Assert.assertTrue(returedRefererList.contains(referer3));
            Assert.assertEquals(4, returedRefererList.size());
            Assert.assertEquals(r.getRequestId().length(), REQUEST_ID_LEN);

            // Set empty referer list
            r.clearRefererList();
            ossClient.setBucketReferer(bucketName, r);

            r = ossClient.getBucketReferer(bucketName);
            returedRefererList = r.getRefererList();
            Assert.assertTrue(r.isAllowEmptyReferer());
            Assert.assertEquals(0, returedRefererList.size());
            Assert.assertEquals(r.getRequestId().length(), REQUEST_ID_LEN);

            // Referer list not allowed to be empty
            refererList.clear();
            refererList.add(referer0);
            refererList.add(referer3);
            r.setRefererList(refererList);
            r.setAllowEmptyReferer(false);
            ossClient.setBucketReferer(bucketName, r);

            r = ossClient.getBucketReferer(bucketName);
            returedRefererList = r.getRefererList();
            Assert.assertFalse(r.isAllowEmptyReferer());
            Assert.assertTrue(returedRefererList.contains(referer0));
            Assert.assertTrue(returedRefererList.contains(referer3));
            Assert.assertEquals(2, returedRefererList.size());
            Assert.assertEquals(r.getRequestId().length(), REQUEST_ID_LEN);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testUnormalSetBucketReferer() {
        final String bucketName = genBucketName() + "unormal-set-bucket-referer";
        final String referer0 = "http://www.aliyun.com";
        final String referer1 = "https://www.aliyun.com";

        try {
            ossClient.createBucket(bucketName);

            BucketReferer r = new BucketReferer();
            List<String> refererList = new ArrayList<String>();
            refererList.add(referer0);
            refererList.add(referer1);
            r.setRefererList(refererList);

            // Set non-existent source bucket 
            final String nonexistentBucket = "nonexistent-bucket";
            try {
                SetBucketRefererRequest request = new SetBucketRefererRequest(nonexistentBucket)
                        .withReferer(r);
                ossClient.setBucketReferer(request);
                Assert.fail("Set bucket referer should not be successful");
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
                Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
            }

            // Set bucket without ownership
            final String bucketWithoutOwnership = "oss" + NOT_OWNERSHIP_BUCKET_SUFFIX;
            try {
                ossClient.setBucketReferer(bucketWithoutOwnership, r);
                Assert.fail("Set bucket referer should not be successful");
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            }

            // Not allow referer list to be empty but we set it empty on purpose.
            // TODO: Why not failed ?
            try {
                r.setAllowEmptyReferer(false);
                r.clearRefererList();
                ossClient.setBucketReferer(bucketName, r);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }

        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testUnormalGetBucketReferer() {
        // Get non-existent bucket
        final String nonexistentBucket = genBucketName() + "unormal-get-bucket-referer";
        try {
            ossClient.getBucketReferer(nonexistentBucket);
            Assert.fail("Get bucket referer should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
        }

        // Get bucket without ownership
        final String bucketWithoutOwnership = "oss" + NOT_OWNERSHIP_BUCKET_SUFFIX;
        try {
            ossClient.getBucketReferer(bucketWithoutOwnership);
            Assert.fail("Get bucket referer should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
        }

        // Get bucket without setting referer list
        final String bucketWithoutRefererRule = genBucketName() + "bucket-without-referer";
        try {
            ossClient.createBucket(bucketWithoutRefererRule);

            BucketReferer r = ossClient.getBucketReferer(bucketWithoutRefererRule);
            Assert.assertEquals(DEFAULT_EMPTY_REFERER_ALLOWED, r.isAllowEmptyReferer());
            Assert.assertEquals(0, r.getRefererList().size());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketWithoutRefererRule);
        }
    }

    @Test
    public void testNormalSetBucketBlackReferer() {
        final String bucketName = genBucketName() + "-referer-source";

        final String referer1 = "https://www.aliyun.com";
        final String referer2 = "http://www.*.com";
        final String referer3 = "https://www.?.aliyuncs.com";
        final String blackReferer1 = "http://www.*.com";
        final String blackReferer2 = "https://www.?.aliyuncs.com";
        final String blackReferer3 = "https://www.————（）（*（*（*&&……@*……%%￥#@？》《}{.com";

        try {
            ossClient.createBucket(bucketName);

            // Set non-empty referer list
            BucketReferer r = new BucketReferer();
            List<String> refererList = new ArrayList<String>();
            refererList.add(referer1);
            refererList.add(referer2);
            refererList.add(referer3);
            r.setRefererList(refererList);

            List<String> refererBlackList = new ArrayList<String>();
            refererBlackList.add(blackReferer1);
            refererBlackList.add(blackReferer2);
            refererBlackList.add(blackReferer3);
            r.setBlackRefererList(refererBlackList);

            ossClient.setBucketReferer(bucketName, r);

            waitForCacheExpiration(5);

            r = ossClient.getBucketReferer(bucketName);
            List<String> returedRefererList = r.getRefererList();
            List<String> returnBlackRefererList = r.getBlackRefererList();
            Assert.assertTrue(r.isAllowEmptyReferer());
            Assert.assertTrue(r.isAllowTruncateQueryString());

            Assert.assertTrue(returedRefererList.contains(referer1));
            Assert.assertTrue(returedRefererList.contains(referer2));
            Assert.assertTrue(returedRefererList.contains(referer3));
            Assert.assertTrue(returnBlackRefererList.contains(blackReferer1));
            Assert.assertTrue(returnBlackRefererList.contains(blackReferer2));
            Assert.assertTrue(returnBlackRefererList.contains(blackReferer3));
            Assert.assertEquals(3, returedRefererList.size());
            Assert.assertEquals(3, returnBlackRefererList.size());
            Assert.assertEquals(r.getRequestId().length(), REQUEST_ID_LEN);

            // Set empty referer list
            r.clearRefererList();
            r.clearBlackRefererList();
            ossClient.setBucketReferer(bucketName, r);

            r = ossClient.getBucketReferer(bucketName);
            returedRefererList = r.getRefererList();
            returnBlackRefererList = r.getRefererList();
            Assert.assertTrue(r.isAllowEmptyReferer());
            Assert.assertTrue(r.isAllowTruncateQueryString());
            Assert.assertEquals(0, returedRefererList.size());
            Assert.assertEquals(0, returnBlackRefererList.size());
            Assert.assertEquals(r.getRequestId().length(), REQUEST_ID_LEN);

            // only black referer
            refererBlackList.clear();
            refererBlackList.add(blackReferer1);
            refererBlackList.add(blackReferer2);

            r.setBlackRefererList(refererBlackList);
            r.setAllowEmptyReferer(false);
            r.setAllowTruncateQueryString(false);
            ossClient.setBucketReferer(bucketName, r);

            r = ossClient.getBucketReferer(bucketName);
            returnBlackRefererList = r.getBlackRefererList();
            Assert.assertFalse(r.isAllowEmptyReferer());
            Assert.assertFalse(r.isAllowTruncateQueryString());
            Assert.assertTrue(returnBlackRefererList.contains(blackReferer1));
            Assert.assertTrue(returnBlackRefererList.contains(blackReferer2));
            Assert.assertEquals(2, returnBlackRefererList.size());
            Assert.assertEquals(r.getRequestId().length(), REQUEST_ID_LEN);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testSpecialSetBucketRefererWithBlackRefer() {
        final String bucketName = genBucketName() + "-blackreferer";


        try {
            ossClient.createBucket(bucketName);

            // set null
            BucketReferer r = new BucketReferer();
            r.setRefererList(null);
            r.setBlackRefererList(null);


            SetBucketRefererRequest request = new SetBucketRefererRequest(bucketName)
                    .withReferer(r);
            VoidResult result = ossClient.setBucketReferer(request);
            Assert.assertEquals(result.getResponse().getStatusCode(), 200);

            r = ossClient.getBucketReferer(bucketName);
            Assert.assertEquals(0, r.getRefererList().size());
            Assert.assertEquals(0, r.getBlackRefererList().size());

            // set empty
            List<String> referer = new ArrayList<String>();
            List<String> refererBlackList = new ArrayList<String>();
            r.setRefererList(referer);
            r.setBlackRefererList(refererBlackList);
            SetBucketRefererRequest request2 = new SetBucketRefererRequest(bucketName)
                    .withReferer(r);
            VoidResult result2 = ossClient.setBucketReferer(request2);
            Assert.assertEquals(result2.getResponse().getStatusCode(), 200);

            r = ossClient.getBucketReferer(bucketName);
            Assert.assertEquals(0, r.getRefererList().size());
            Assert.assertEquals(0, r.getBlackRefererList().size());

            // test with
            BucketReferer r3 = new BucketReferer(true, null).withAllowTruncateQueryString(null).withBlackRefererList(null);
            SetBucketRefererRequest request3 = new SetBucketRefererRequest(bucketName)
                    .withReferer(r3);
            VoidResult result3 = ossClient.setBucketReferer(request3);
            Assert.assertEquals(result3.getResponse().getStatusCode(), 200);

            r = ossClient.getBucketReferer(bucketName);
            Assert.assertEquals(true, r.isAllowEmptyReferer());
            Assert.assertEquals(Boolean.TRUE, r.isAllowTruncateQueryString());
            Assert.assertEquals(0, r.getRefererList().size());
            Assert.assertEquals(0, r.getBlackRefererList().size());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

}
