package com.aliyun.oss.integrationtests;/*
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

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.BucketWebsiteResult;
import com.aliyun.oss.model.RoutingRule;
import com.aliyun.oss.model.SetBucketWebsiteRequest;
import com.aliyun.oss.model.SubDirType;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aliyun.oss.integrationtests.TestConstants.NO_SUCH_BUCKET_ERR;
import static com.aliyun.oss.integrationtests.TestConstants.NO_SUCH_WEBSITE_CONFIGURATION_ERR;
import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

public class BucketWebsiteTest extends TestBase {

    @Test
    public void testNormalSetBucketWebsite() {
        final String bucketName = super.bucketName + "normal-set-bucket-website";
        final String indexDocument = "index.html";
        final String errorDocument = "error.html";

        try {
            ossClient.createBucket(bucketName);

            // Set both index document and error document
            SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(bucketName);
            request.setIndexDocument(indexDocument);
            request.setErrorDocument(errorDocument);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            BucketWebsiteResult result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertEquals(errorDocument, result.getErrorDocument());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            ossClient.deleteBucketWebsite(bucketName);

            // Set index document only
            request = new SetBucketWebsiteRequest(bucketName);
            request.setIndexDocument(indexDocument);
            request.setErrorDocument(null);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertTrue(result.getErrorDocument() == null);
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            ossClient.deleteBucketWebsite(bucketName);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testNormalSetBucketWebsiteWithMirror() {
        final String bucketName = super.bucketName + "normal-set-bucket-website-mirror";
        final String indexDocument = "index.html";

        try {
            ossClient.createBucket(bucketName);

            // Set index document and mirror
            SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(bucketName);
            RoutingRule rule = new RoutingRule();
            rule.setNumber(1);
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            rule.getRedirect().setMirrorURL("http://oss-test.aliyun-inc.com/mirror-test-source/");

            request.setIndexDocument(indexDocument);
            request.AddRoutingRule(rule);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            // check
            BucketWebsiteResult result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertEquals(result.getRoutingRules().size(), 1);
            RoutingRule rr = result.getRoutingRules().get(0);
            Assertions.assertEquals(rr.getNumber().intValue(), 1);
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.Mirror);
            Assertions.assertEquals(rr.getRedirect().getMirrorURL(), "http://oss-test.aliyun-inc.com/mirror-test-source/");
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            ossClient.deleteBucketWebsite(bucketName);

            // set mirror with key prefix
            request = new SetBucketWebsiteRequest(bucketName);
            rule = new RoutingRule();
            rule.setNumber(2);
            rule.getCondition().setKeyPrefixEquals("~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            rule.getRedirect().setMirrorURL("http://oss-test.aliyun-inc.com/mirror-test/");

            request.setIndexDocument(indexDocument);
            request.AddRoutingRule(rule);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            // check
            result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertEquals(result.getRoutingRules().size(), 1);
            rr = result.getRoutingRules().get(0);
            Assertions.assertEquals(rr.getNumber().intValue(), 2);
            Assertions.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.Mirror);
            Assertions.assertEquals(rr.getRedirect().getMirrorURL(), "http://oss-test.aliyun-inc.com/mirror-test/");
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            ossClient.deleteBucketWebsite(bucketName);

            // set mirror with secondary default mirror
            request = new SetBucketWebsiteRequest(bucketName);
            rule = new RoutingRule();
            rule.setNumber(2);
            rule.getCondition().setKeyPrefixEquals("~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            rule.getRedirect().setMirrorURL("http://oss-test.aliyun-inc.com/mirror-test/");
            rule.getRedirect().setMirrorSecondaryURL(null);
            rule.getRedirect().setMirrorProbeURL(null);
            rule.getRedirect().setPassQueryString(null);
            rule.getRedirect().setPassOriginalSlashes(null);

            request.setIndexDocument(indexDocument);
            request.AddRoutingRule(rule);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            // check
            result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertEquals(result.getRoutingRules().size(), 1);
            rr = result.getRoutingRules().get(0);
            Assertions.assertEquals(rr.getNumber().intValue(), 2);
            Assertions.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.Mirror);
            Assertions.assertEquals(rr.getRedirect().getMirrorURL(), "http://oss-test.aliyun-inc.com/mirror-test/");
            Assertions.assertNull(rr.getRedirect().getMirrorSecondaryURL());
            Assertions.assertNull(rr.getRedirect().getMirrorProbeURL());
            Assertions.assertFalse(rr.getRedirect().isPassQueryString());
            Assertions.assertFalse(rr.getRedirect().isPassOriginalSlashes());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            ossClient.deleteBucketWebsite(bucketName);

            // set mirror with secondary mirror
            request = new SetBucketWebsiteRequest(bucketName);
            rule = new RoutingRule();
            rule.setNumber(2);
            rule.getCondition().setKeyPrefixEquals("~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            rule.getRedirect().setMirrorURL("http://oss-test.aliyun-inc.com/mirror-test/");
            rule.getRedirect().setMirrorSecondaryURL("http://oss-test.aliyun-inc.com/mirror-secodary/");
            rule.getRedirect().setMirrorProbeURL("http://oss-test.aliyun-inc.com/mirror-probe/");
            rule.getRedirect().setPassQueryString(true);
            rule.getRedirect().setPassOriginalSlashes(true);

            request.setIndexDocument(indexDocument);
            request.AddRoutingRule(rule);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            // check
            result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertEquals(result.getRoutingRules().size(), 1);
            rr = result.getRoutingRules().get(0);
            Assertions.assertEquals(rr.getNumber().intValue(), 2);
            Assertions.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.Mirror);
            Assertions.assertEquals(rr.getRedirect().getMirrorURL(), "http://oss-test.aliyun-inc.com/mirror-test/");
            Assertions.assertEquals(rr.getRedirect().getMirrorSecondaryURL(), "http://oss-test.aliyun-inc.com/mirror-secodary/");
            Assertions.assertEquals(rr.getRedirect().getMirrorProbeURL(), "http://oss-test.aliyun-inc.com/mirror-probe/");
            Assertions.assertTrue(rr.getRedirect().isPassQueryString());
            Assertions.assertTrue(rr.getRedirect().isPassOriginalSlashes());
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            ossClient.deleteBucketWebsite(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testNormalSetBucketWebsiteWithRedirect() {
        final String bucketName = super.bucketName + "normal-set-bucket-website-redirect";
        final String indexDocument = "index.html";

        try {
            ossClient.createBucket(bucketName);

            // Set RoutingRule
            SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(bucketName);
            RoutingRule rule = new RoutingRule();
            rule.setNumber(1);
            rule.getCondition().setKeyPrefixEquals("~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);

            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.External);
            rule.getRedirect().setHostName("oss.aliyuncs.com");
            rule.getRedirect().setProtocol(RoutingRule.Protocol.Https);
            rule.getRedirect().setReplaceKeyWith("${key}.jpg");
            rule.getRedirect().setHttpRedirectCode(302);

            request.setIndexDocument(indexDocument);
            request.AddRoutingRule(rule);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            BucketWebsiteResult result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertEquals(result.getRoutingRules().size(), 1);
            RoutingRule rr = result.getRoutingRules().get(0);
            Assertions.assertEquals(rr.getNumber().intValue(), 1);
            Assertions.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);

            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.External);
            Assertions.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assertions.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Https);
            Assertions.assertEquals(rr.getRedirect().getReplaceKeyWith(), "${key}.jpg");
            Assertions.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 302);
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            ossClient.deleteBucketWebsite(bucketName);

            // Set RoutingRule
            request = new SetBucketWebsiteRequest(bucketName);
            rule = new RoutingRule();
            rule.setNumber(2);
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.External);
            rule.getRedirect().setHostName("oss.aliyuncs.com");
            rule.getRedirect().setProtocol(RoutingRule.Protocol.Https);
            rule.getRedirect().setReplaceKeyWith("${key}.jpg");
            rule.getRedirect().setHttpRedirectCode(302);
            request.AddRoutingRule(rule);

            rule = new RoutingRule();
            rule.setNumber(5);
            rule.getCondition().setHttpErrorCodeReturnedEquals(403);
            rule.getRedirect().setHostName("oss.aliyuncs.com");
            rule.getRedirect().setProtocol(RoutingRule.Protocol.Http);
            rule.getRedirect().setReplaceKeyPrefixWith("~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            rule.getRedirect().setHttpRedirectCode(303);
            request.AddRoutingRule(rule);

            request.setIndexDocument(indexDocument);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertEquals(result.getRoutingRules().size(), 2);
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            rr = result.getRoutingRules().get(0);
            Assertions.assertEquals(rr.getNumber().intValue(), 2);
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.External);
            Assertions.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assertions.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Https);
            Assertions.assertEquals(rr.getRedirect().getReplaceKeyWith(), "${key}.jpg");
            Assertions.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 302);

            rr = result.getRoutingRules().get(1);
            Assertions.assertEquals(rr.getNumber().intValue(), 5);
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 403);
            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.External);
            Assertions.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assertions.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Http);
            Assertions.assertEquals(rr.getRedirect().getReplaceKeyPrefixWith(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assertions.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 303);

            ossClient.deleteBucketWebsite(bucketName);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testNormalSetBucketWebsiteWithCDNRedirect() {
        final String bucketName = super.bucketName + "normal-set-bucket-website-redirect-cdn";
        final String indexDocument = "index.html";

        try {
            ossClient.createBucket(bucketName);

            // Set RoutingRule
            SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(bucketName);
            RoutingRule rule = new RoutingRule();
            rule.setNumber(1);
            rule.getCondition().setKeyPrefixEquals("~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);

            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.AliCDN);
            rule.getRedirect().setHostName("oss.aliyuncs.com");
            rule.getRedirect().setProtocol(RoutingRule.Protocol.Https);
            rule.getRedirect().setReplaceKeyWith("${key}.jpg");
            rule.getRedirect().setHttpRedirectCode(302);

            request.setIndexDocument(indexDocument);
            request.AddRoutingRule(rule);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            BucketWebsiteResult result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertEquals(result.getRoutingRules().size(), 1);
            RoutingRule rr = result.getRoutingRules().get(0);
            Assertions.assertEquals(rr.getNumber().intValue(), 1);
            Assertions.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);

            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.AliCDN);
            Assertions.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assertions.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Https);
            Assertions.assertEquals(rr.getRedirect().getReplaceKeyWith(), "${key}.jpg");
            Assertions.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 302);

            ossClient.deleteBucketWebsite(bucketName);

            // Set RoutingRule
            request = new SetBucketWebsiteRequest(bucketName);
            rule = new RoutingRule();
            rule.setNumber(2);
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.AliCDN);
            rule.getRedirect().setHostName("oss.aliyuncs.com");
            rule.getRedirect().setProtocol(RoutingRule.Protocol.Https);
            rule.getRedirect().setReplaceKeyWith("${key}.jpg");
            rule.getRedirect().setHttpRedirectCode(302);
            request.AddRoutingRule(rule);

            rule = new RoutingRule();
            rule.setNumber(5);
            rule.getCondition().setHttpErrorCodeReturnedEquals(403);
            rule.getRedirect().setHostName("oss.aliyuncs.com");
            rule.getRedirect().setProtocol(RoutingRule.Protocol.Http);
            rule.getRedirect().setReplaceKeyPrefixWith("~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            rule.getRedirect().setHttpRedirectCode(303);
            request.AddRoutingRule(rule);

            request.setIndexDocument(indexDocument);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertEquals(result.getRoutingRules().size(), 2);
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            rr = result.getRoutingRules().get(0);
            Assertions.assertEquals(rr.getNumber().intValue(), 2);
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.AliCDN);
            Assertions.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assertions.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Https);
            Assertions.assertEquals(rr.getRedirect().getReplaceKeyWith(), "${key}.jpg");
            Assertions.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 302);

            rr = result.getRoutingRules().get(1);
            Assertions.assertEquals(rr.getNumber().intValue(), 5);
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 403);
            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.External);
            Assertions.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assertions.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Http);
            Assertions.assertEquals(rr.getRedirect().getReplaceKeyPrefixWith(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assertions.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 303);

            ossClient.deleteBucketWebsite(bucketName);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testUnormalSetBucketWebsiteWithMirror() {
        final String bucketName = super.bucketName + "unormal-set-bucket-website-mirror";
        final String indexDocument = "index.html";

        try {
            ossClient.createBucket(bucketName);

            SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(bucketName);
            RoutingRule rule = new RoutingRule();
            rule.setNumber(-1);
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            rule.getRedirect().setMirrorURL("http://oss-test.aliyun-inc.com/mirror-test-source/");
            request.setIndexDocument(indexDocument);

            // rule Number invalid
            try {
                request.AddRoutingRule(rule);
                Assertions.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof IllegalArgumentException);
            }

            request = new SetBucketWebsiteRequest(bucketName);
            rule = new RoutingRule();
            rule.setNumber(1);
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            request.setIndexDocument(indexDocument);

            // rule MirrorURL invalid
            try {
                request.AddRoutingRule(rule);
                Assertions.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof IllegalArgumentException);
            }

            // rule MirrorURL invalid
            request = new SetBucketWebsiteRequest(bucketName);
            rule = new RoutingRule();
            rule.setNumber(1);
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            rule.getRedirect().setMirrorURL("oss-test.aliyun-inc.com/mirror-test-source/");
            request.setIndexDocument(indexDocument);

            try {
                request.AddRoutingRule(rule);
                Assertions.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof IllegalArgumentException);
            }

            // rule http error code invalid
            request = new SetBucketWebsiteRequest(bucketName);
            rule = new RoutingRule();
            rule.setNumber(1);
            rule.getCondition().setHttpErrorCodeReturnedEquals(403);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            rule.getRedirect().setMirrorURL("http://oss-test.aliyun-inc.com/mirror-test-source/");
            request.setIndexDocument(indexDocument);
            request.AddRoutingRule(rule);

            try {
                ossClient.setBucketWebsite(request);
                Assertions.fail("Set bucket website should not be successful");
            } catch (OSSException e) {
                Assertions.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testUnormalSetBucketWebsiteWithRedirect() {
        final String bucketName = super.bucketName + "unormal-set-bucket-website-redirect";
        final String indexDocument = "index.html";

        try {
            ossClient.createBucket(bucketName);

            SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(bucketName);
            RoutingRule rule = new RoutingRule();
            rule.setNumber(-1);
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.External);
            rule.getRedirect().setMirrorURL("http://oss-test.aliyun-inc.com/mirror-test-source/");
            request.setIndexDocument(indexDocument);

            // rule Number invalid
            try {
                request.AddRoutingRule(rule);
                Assertions.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof IllegalArgumentException);
            }

            request = new SetBucketWebsiteRequest(bucketName);
            rule = new RoutingRule();
            rule.setNumber(1);
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.External);
            request.setIndexDocument(indexDocument);

            // rule value invalid
            try {
                request.AddRoutingRule(rule);
                Assertions.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof IllegalArgumentException);
            }

            // rule ReplaceKeyPrefixWith&ReplaceKeyWith invalid
            request = new SetBucketWebsiteRequest(bucketName);
            rule = new RoutingRule();
            rule.setNumber(1);
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.External);
            rule.getRedirect().setReplaceKeyWith("");
            rule.getRedirect().setReplaceKeyPrefixWith("");
            request.setIndexDocument(indexDocument);

            // rule value invalid
            try {
                request.AddRoutingRule(rule);
                Assertions.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof IllegalArgumentException);
            }

        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testUnormalSetBucketWebsite() {
        final String bucketName = super.bucketName + "unormal-set-bucket-website";
        final String indexDocument = "index.html";
        final String errorDocument = "error.html";

        try {
            ossClient.createBucket(bucketName);

            // Set non-existent bucket
            final String nonexistentBucket = "nonexistent-bucket";
            try {
                SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(nonexistentBucket);
                request.setIndexDocument(indexDocument);
                request.setErrorDocument(errorDocument);
                ossClient.setBucketWebsite(request);

                Assertions.fail("Set bucket website should not be successful");
            } catch (OSSException e) {
                Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
                Assertions.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
            }

            // Set index document null
            try {
                SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(nonexistentBucket);
                request.setIndexDocument(null);
                request.setErrorDocument(null);
                ossClient.setBucketWebsite(request);

                Assertions.fail("Set bucket website should not be successful");
            } catch (Exception e) {
                Assertions.assertTrue(e instanceof IllegalArgumentException);
            }
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testUnormalGetBucketWebsite() {
        // Get non-existent bucket
        final String nonexistentBucket = super.bucketName + "unormal-get-bucket-website";
        try {
            ossClient.getBucketWebsite(nonexistentBucket);
            Assertions.fail("Get bucket website should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assertions.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
        }

        // Get bucket without ownership
        final String bucketWithoutOwnership = "oss";
        try {
            ossClient.getBucketLogging(bucketWithoutOwnership);
            Assertions.fail("Get bucket website should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
        }

        // Get bucket without setting website configuration
        final String bucketWithoutWebsiteConfiguration = "bucket-without-website-configuration";
        try {
            ossClient.createBucket(bucketWithoutWebsiteConfiguration);

            ossClient.getBucketWebsite(bucketWithoutWebsiteConfiguration);
            Assertions.fail("Get bucket website should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_WEBSITE_CONFIGURATION, e.getErrorCode());
            Assertions.assertTrue(e.getMessage().startsWith(NO_SUCH_WEBSITE_CONFIGURATION_ERR));
        } finally {
            ossClient.deleteBucket(bucketWithoutWebsiteConfiguration);
        }
    }

    @Test
    public void testUnormalDeleteBucketWebsite() {
        // Delete non-existent bucket
        final String nonexistentBucket = super.bucketName + "unormal-delete-bucket-website";
        try {
            ossClient.deleteBucketWebsite(nonexistentBucket);
            Assertions.fail("Delete bucket website should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assertions.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
        }

        // Delete bucket without ownership
        final String bucketWithoutOwnership = "oss";
        try {
            ossClient.deleteBucketWebsite(bucketWithoutOwnership);
            Assertions.fail("Delete bucket website should not be successful");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
        }

        // Delete bucket without setting website configuration
        final String bucketWithoutWebsiteConfiguration = "bucket-without-website-configuration";
        try {
            ossClient.createBucket(bucketWithoutWebsiteConfiguration);
            ossClient.deleteBucketWebsite(bucketWithoutWebsiteConfiguration);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketWithoutWebsiteConfiguration);
        }
    }



    @Test
    public void test() {
        final String bucketName = super.bucketName + "-test-redirect";
        final String indexDocument = "index.html";

        try {
            ossClient.createBucket(bucketName);

            // Set RoutingRule
            SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(bucketName);
            RoutingRule rule = new RoutingRule();
            rule.setNumber(1);
            rule.getCondition().setKeyPrefixEquals("~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            rule.getCondition().setKeySuffixEquals("~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);

            List<RoutingRule.IncludeHeader> includeHeaders =  new ArrayList<RoutingRule.IncludeHeader>();
            RoutingRule.IncludeHeader includeHeader1 =  new RoutingRule.IncludeHeader();
            includeHeader1.setKey("key1");
            includeHeader1.setEndsWith("end1");
            includeHeader1.setStartsWith("start1");
            RoutingRule.IncludeHeader includeHeader2 =  new RoutingRule.IncludeHeader();
            includeHeader2.setKey("key2");
            includeHeader2.setEquals("equals2");
            includeHeaders.add(includeHeader1);
            includeHeaders.add(includeHeader2);
            rule.getCondition().setIncludeHeaders(includeHeaders);

            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            rule.getRedirect().setMirrorURL("http://oss-test.aliyun-inc.com/mirror-test-source/");
            rule.getRedirect().setMirrorPassQueryString(true);
            rule.getRedirect().setEnableReplacePrefix(true);
            rule.getRedirect().setReplaceKeyPrefixWith("${key}.jpg");
            rule.getRedirect().setMirrorSwitchAllErrors(true);
            rule.getRedirect().setMirrorCheckMd5(true);
            rule.getRedirect().setMirrorFollowRedirect(true);
            rule.getRedirect().setMirrorUserLastModified(true);
            // rule.getRedirect().setMirrorTunnelId("tunnel_id");
            //rule.getRedirect().setMirrorDstVpcId("test_vpcid");
            rule.getRedirect().setMirrorDstRegion("cn-shenzhen");
            rule.getRedirect().setMirrorIsExpressTunnel(true);
            rule.getRedirect().setMirrorUsingRole(true);
            rule.getRedirect().setMirrorRole("test-role");

            List mirrorMultiAlternates = new ArrayList<RoutingRule.Redirect.MirrorMultiAlternate>();
            RoutingRule.Redirect.MirrorMultiAlternate mirrorMultiAlternate1 = new RoutingRule.Redirect.MirrorMultiAlternate();
            mirrorMultiAlternate1.setPrior(1);
            mirrorMultiAlternate1.setUrl("http://www.aliyun1.com/");
            RoutingRule.Redirect.MirrorMultiAlternate mirrorMultiAlternate2 = new RoutingRule.Redirect.MirrorMultiAlternate();
            mirrorMultiAlternate2.setPrior(2);
            mirrorMultiAlternate2.setUrl("http://www.aliyun2.com/");
            mirrorMultiAlternates.add(mirrorMultiAlternate1);
            mirrorMultiAlternates.add(mirrorMultiAlternate2);
            rule.getRedirect().setMirrorMultiAlternates(mirrorMultiAlternates);

            RoutingRule.MirrorHeaders mirrorHeaders = new RoutingRule.MirrorHeaders();
            mirrorHeaders.setPassAll(true);
            List passes = new ArrayList<String>();
            passes.add("pass-header1");
            passes.add("pass-header2");
            mirrorHeaders.setPass(passes);
            List removes = new ArrayList<String>();
            removes.add("remove-header1");
            removes.add("remove-header2");
            mirrorHeaders.setRemove(removes);
            List sets = new ArrayList<Map<String, String>>();
            Map header1 = new HashMap<String, String>();
            header1.put("Key", "key1");
            header1.put("Value", "value1");
            Map header2 = new HashMap<String, String>();
            header2.put("Key", "key2");
            header2.put("Value", "value2");
            sets.add(header1);
            sets.add(header2);
            mirrorHeaders.setSet(sets);
            rule.getRedirect().setMirrorHeaders(mirrorHeaders);

            request.setIndexDocument(indexDocument);
            request.setSupportSubDir(true);
            request.setSubDirType(SubDirType.Redirect);
            request.AddRoutingRule(rule);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            BucketWebsiteResult result = ossClient.getBucketWebsite(bucketName);
            Assertions.assertEquals(indexDocument, result.getIndexDocument());
            Assertions.assertTrue(result.isSupportSubDir());
            Assertions.assertEquals(SubDirType.Redirect.toString(), result.getSubDirType());
            Assertions.assertEquals(result.getRoutingRules().size(), 1);
            RoutingRule rr = result.getRoutingRules().get(0);
            Assertions.assertEquals(rr.getNumber().intValue(), 1);
            Assertions.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assertions.assertEquals(rr.getCondition().getKeySuffixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assertions.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assertions.assertEquals(RoutingRule.RedirectType.Mirror, rr.getRedirect().getRedirectType());
            Assertions.assertEquals(2, rr.getCondition().getIncludeHeaders().size());
            if (rr.getCondition().getIncludeHeaders().get(0).getKey().equals("key1")) {
                Assertions.assertEquals("start1", rr.getCondition().getIncludeHeaders().get(0).getStartsWith());
                Assertions.assertEquals("end1", rr.getCondition().getIncludeHeaders().get(0).getEndsWith());
                Assertions.assertNull(rr.getCondition().getIncludeHeaders().get(0).getEquals());
                Assertions.assertEquals("key2", rr.getCondition().getIncludeHeaders().get(1).getKey());
                Assertions.assertEquals("equals2", rr.getCondition().getIncludeHeaders().get(1).getEquals());
                Assertions.assertNull(rr.getCondition().getIncludeHeaders().get(1).getStartsWith());
                Assertions.assertNull(rr.getCondition().getIncludeHeaders().get(1).getEndsWith());
            } else {
                Assertions.assertEquals("key1", rr.getCondition().getIncludeHeaders().get(1).getKey());
                Assertions.assertEquals("start", rr.getCondition().getIncludeHeaders().get(1).getStartsWith());
                Assertions.assertEquals("end1", rr.getCondition().getIncludeHeaders().get(1).getEndsWith());
                Assertions.assertNull(rr.getCondition().getIncludeHeaders().get(1).getEquals());
                Assertions.assertEquals("key2", rr.getCondition().getIncludeHeaders().get(0).getKey());
                Assertions.assertEquals("equals2", rr.getCondition().getIncludeHeaders().get(0).getEquals());
                Assertions.assertNull(rr.getCondition().getIncludeHeaders().get(0).getStartsWith());
                Assertions.assertNull(rr.getCondition().getIncludeHeaders().get(0).getEndsWith());
            }



            Assertions.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.Mirror);
            Assertions.assertEquals(rr.getRedirect().getReplaceKeyPrefixWith(), "${key}.jpg");
            Assertions.assertTrue(rr.getRedirect().isMirrorPassQueryString());
            Assertions.assertEquals("${key}.jpg", rr.getRedirect().getReplaceKeyPrefixWith());
            Assertions.assertTrue(rr.getRedirect().isMirrorSwitchAllErrors());
            Assertions.assertTrue(rr.getRedirect().isMirrorCheckMd5());
            Assertions.assertTrue(rr.getRedirect().isMirrorFollowRedirect());
            Assertions.assertTrue(rr.getRedirect().isMirrorUserLastModified());
            Assertions.assertEquals("cn-shenzhen", rr.getRedirect().getMirrorDstRegion());
            Assertions.assertTrue(rr.getRedirect().isMirrorIsExpressTunnel());
            Assertions.assertTrue(rr.getRedirect().isMirrorUsingRole());
            Assertions.assertEquals("test-role", rr.getRedirect().getMirrorRole());

            Assertions.assertEquals(2, rr.getRedirect().getMirrorMultiAlternates().size());
            if (rr.getRedirect().getMirrorMultiAlternates().get(0).getPrior().equals(1)) {
                Assertions.assertEquals("http://www.aliyun1.com/", rr.getRedirect().getMirrorMultiAlternates().get(0).getUrl());
                Assertions.assertEquals(Integer.valueOf(2), rr.getRedirect().getMirrorMultiAlternates().get(1).getPrior());
                Assertions.assertEquals("http://www.aliyun2.com/", rr.getRedirect().getMirrorMultiAlternates().get(1).getUrl());
            } else {
                Assertions.assertEquals(Integer.valueOf(2), rr.getRedirect().getMirrorMultiAlternates().get(0).getPrior());
                Assertions.assertEquals("http://www.aliyun2.com/", rr.getRedirect().getMirrorMultiAlternates().get(0).getUrl());
                Assertions.assertEquals(Integer.valueOf(1), rr.getRedirect().getMirrorMultiAlternates().get(1).getPrior());
                Assertions.assertEquals("http://www.aliyun1.com/", rr.getRedirect().getMirrorMultiAlternates().get(1).getUrl());
            }

            RoutingRule.MirrorHeaders rMirrorHeaders = rr.getRedirect().getMirrorHeaders();
            Assertions.assertTrue(rMirrorHeaders.isPassAll());
            Assertions.assertEquals(2, rMirrorHeaders.getPass().size());
            Assertions.assertTrue(rMirrorHeaders.getPass().contains("pass-header1"));
            Assertions.assertTrue(rMirrorHeaders.getPass().contains("pass-header2"));
            Assertions.assertEquals(2, rMirrorHeaders.getRemove().size());
            Assertions.assertTrue(rMirrorHeaders.getRemove().contains("remove-header1"));
            Assertions.assertTrue(rMirrorHeaders.getRemove().contains("remove-header2"));
            List<Map<String, String>> rSet = rMirrorHeaders.getSet();
            Assertions.assertEquals(2, rSet.size());
            if (rSet.get(0).get("Key").equals("key1")) {
                Assertions.assertEquals("value1", rSet.get(0).get("Value"));
                Assertions.assertEquals("key2", rSet.get(1).get("Key"));
                Assertions.assertEquals("value2", rSet.get(1).get("Value"));
            } else {
                Assertions.assertEquals("key2", rSet.get(0).get("Key"));
                Assertions.assertEquals("value2", rSet.get(0).get("Value"));
                Assertions.assertEquals("key1", rSet.get(1).get("Key"));
                Assertions.assertEquals("value1", rSet.get(1).get("Value"));
            }

            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            ossClient.deleteBucketWebsite(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

}
