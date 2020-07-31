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

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import junit.framework.Assert;
import org.junit.Test;

public class DoesBucketExistTest extends TestBase {
    @Test
    public void testDoesBucketExist() {
        // test owned bucket, TRUE
        try {
            Assert.assertTrue(ossClient.doesBucketExist(bucketName));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        // test non-exist bucket, FALSE
        try {
            Assert.assertFalse(ossClient.doesBucketExist(bucketName + "-non-exist"));
        } catch (OSSException e) {
            e.printStackTrace();
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }

        // test an exist bucket which owned by another endpoint. ACCESS_DENIED
        String anotherEndpoint = TestConfig.OSS_TEST_ENDPOINT.indexOf("cn-shenzhen") == -1 ?
                "oss-cn-shenzhen.aliyuncs.com" : "oss-cn-shanghai.aliyuncs.com";
        OSS anotherClient = new OSSClientBuilder().build(anotherEndpoint, TestConfig.OSS_TEST_ACCESS_KEY_ID,
                TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        String tempBucketName = bucketName + "-test-bucket-exist";
        try {
            anotherClient.createBucket(tempBucketName);
            Assert.assertTrue(anotherClient.doesBucketExist(tempBucketName));
            ossClient.doesBucketExist(tempBucketName);
            Assert.fail("should be failed here.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
        } finally {
            anotherClient.deleteBucket(tempBucketName);
            anotherClient.shutdown();
        }


        // test another user's bucket, ACCESS_DENIED
        try {
            ossClient.doesBucketExist("oss");
            Assert.fail("should be failed");
        } catch (OSSException e) {
            e.printStackTrace();
            Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
        }
    }
}
