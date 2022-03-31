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

import java.util.Date;
import java.util.List;

import com.aliyun.oss.model.GenericRequest;
import org.junit.jupiter.api.*;

import org.junit.Ignore;
import org.junit.Test;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CnameConfiguration;
import com.aliyun.oss.model.DeleteBucketCnameRequest;
import com.aliyun.oss.model.AddBucketCnameRequest;


public class BucketCnameTest extends TestBase {
    private static final String[] domains = {"001du.cn", "baidu.com",
            "filehomeworks.youcase.com", "files.100km.me", "flv.fls.net.cn"};

    @Ignore
    @SuppressWarnings("deprecation")
    public void testNormalAddBucketCname() {
        final String bucketName = "normal-add-bucket-cname";
        Date curDate;

        try {
            ossClient.createBucket(bucketName);

            // set cname
            ossClient.addBucketCname(new AddBucketCnameRequest(bucketName).withDomain(domains[0]));

            curDate = new Date(System.currentTimeMillis());
            waitForCacheExpiration(5);

            List<CnameConfiguration> cnames = ossClient.getBucketCname(bucketName);

            Assertions.assertEquals(cnames.size(), 1);
            Assertions.assertEquals(cnames.get(0).getDomain(), domains[0]);
            Assertions.assertEquals(cnames.get(0).getStatus(), CnameConfiguration.CnameStatus.Enabled);
            Assertions.assertEquals(cnames.get(0).getLastMofiedTime().getYear(), curDate.getYear());
            Assertions.assertEquals(cnames.get(0).getLastMofiedTime().getMonth(), curDate.getMonth());
            Assertions.assertEquals(cnames.get(0).getLastMofiedTime().getDay(), curDate.getDay());
            System.out.println(cnames.get(0));

            ossClient.deleteBucketCname(bucketName, domains[0]);

            cnames = ossClient.getBucketCname(bucketName);
            Assertions.assertEquals(cnames.size(), 0);

            // set multi cname
            for (String domain : domains) {
                AddBucketCnameRequest request = new AddBucketCnameRequest(bucketName);
                request.setDomain(domain);
                ossClient.addBucketCname(request);
            }

            curDate = new Date(System.currentTimeMillis());
            waitForCacheExpiration(5);

            cnames = ossClient.getBucketCname(bucketName);
            Assertions.assertEquals(cnames.size(), domains.length);
            for (int i = 0; i < cnames.size(); i++) {
                System.out.println(cnames.get(i));
                Assertions.assertEquals(cnames.get(i).getDomain(), domains[i]);
                Assertions.assertEquals(cnames.get(0).getStatus(), CnameConfiguration.CnameStatus.Enabled);
                Assertions.assertEquals(cnames.get(0).getLastMofiedTime().getYear(), curDate.getYear());
                Assertions.assertEquals(cnames.get(0).getLastMofiedTime().getMonth(), curDate.getMonth());
                Assertions.assertEquals(cnames.get(0).getLastMofiedTime().getDay(), curDate.getDay());
            }

            for (String domain : domains) {
                DeleteBucketCnameRequest req = new DeleteBucketCnameRequest(bucketName);
                req.setDomain(domain);
                ossClient.deleteBucketCname(req);
            }

            cnames = ossClient.getBucketCname(bucketName);
            Assertions.assertEquals(cnames.size(), 0);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Ignore
    public void testNormalDeleteBucketCname() {
        final String bucketName = "normal-delete-bucket-cname";

        try {
            ossClient.createBucket(bucketName);

            // set cname
            ossClient.addBucketCname(new AddBucketCnameRequest(bucketName).withDomain(domains[0]));

            waitForCacheExpiration(5);

            List<CnameConfiguration> cnames = ossClient.getBucketCname(bucketName);
            Assertions.assertEquals(cnames.size(), 1);

            ossClient.deleteBucketCname(bucketName, domains[0]);

            cnames = ossClient.getBucketCname(bucketName);
            Assertions.assertEquals(cnames.size(), 0);

            // delete not exist cname
            ossClient.deleteBucketCname(bucketName, domains[0]);

            cnames = ossClient.getBucketCname(bucketName);
            Assertions.assertEquals(cnames.size(), 0);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testUnormaladdBucketCname() {
        final String bucketName = "unormal-set-bucket-cname";

        // parameter invalid
        try {
            ossClient.addBucketCname(new AddBucketCnameRequest(bucketName));
            Assertions.fail("Set bucket cname should not be successful");
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof NullPointerException);
        }

        try {
            ossClient.deleteBucketCname(new DeleteBucketCnameRequest(bucketName));
            Assertions.fail("Delete bucket cname should not be successful");
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof NullPointerException);
        }

        // bucket non-existent 
        try {
            ossClient.addBucketCname(new AddBucketCnameRequest(bucketName).withDomain(domains[0]));
            Assertions.fail("Set bucket cname should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }

        try {
            ossClient.getBucketCname(bucketName);
            Assertions.fail("get bucket cname should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }

        try {
            ossClient.getBucketCname(new GenericRequest(bucketName));
            Assertions.fail("get bucket cname should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }

        try {
            ossClient.deleteBucketCname(new DeleteBucketCnameRequest(bucketName).withDomain(domains[0]));
            Assertions.fail("Delete bucket cname should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }

        try {
            ossClient.deleteBucketCname(bucketName, domains[0]);
            Assertions.fail("Delete bucket cname should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }

        // domain invalid
        try {
            ossClient.createBucket(bucketName);

            try {
                ossClient.addBucketCname(new AddBucketCnameRequest(bucketName).withDomain("your.com"));
                Assertions.fail("Set bucket cname should not be successful");
            } catch (OSSException e) {
                Assertions.assertEquals("NoSuchCnameInRecord", e.getErrorCode());
            }

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }
}
