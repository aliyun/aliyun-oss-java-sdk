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
import static com.aliyun.oss.integrationtests.TestConstants.NO_SUCH_WEBSITE_CONFIGURATION_ERR;
import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.utils.LogUtils;
import junit.framework.Assert;

import org.junit.Test;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.BucketWebsiteResult;
import com.aliyun.oss.model.RoutingRule;
import com.aliyun.oss.model.SetBucketWebsiteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BucketWebsiteTest extends TestBase {

    @Test
    public void testNormalSetBucketWebsite() {
        final String bucketName = "normal-set-bucket-website";
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
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertEquals(errorDocument, result.getErrorDocument());
            Assert.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            
            ossClient.deleteBucketWebsite(bucketName);
            
            // Set index document only
            request = new SetBucketWebsiteRequest(bucketName);
            request.setIndexDocument(indexDocument);
            request.setErrorDocument(null);
            ossClient.setBucketWebsite(request);
            
            waitForCacheExpiration(5);
            
            result = ossClient.getBucketWebsite(bucketName);
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertTrue(result.getErrorDocument() == null);
            Assert.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            
            ossClient.deleteBucketWebsite(bucketName);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testNormalSetBucketWebsiteWithMirror() {
        final String bucketName = "normal-set-bucket-website-mirror";
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
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertEquals(result.getRoutingRules().size(), 1);
            RoutingRule rr = result.getRoutingRules().get(0);
            Assert.assertEquals(rr.getNumber().intValue(), 1);
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.Mirror);
            Assert.assertEquals(rr.getRedirect().getMirrorURL(), "http://oss-test.aliyun-inc.com/mirror-test-source/");
            Assert.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            
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
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertEquals(result.getRoutingRules().size(), 1);
            rr = result.getRoutingRules().get(0);
            Assert.assertEquals(rr.getNumber().intValue(), 2);
            Assert.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.Mirror);
            Assert.assertEquals(rr.getRedirect().getMirrorURL(), "http://oss-test.aliyun-inc.com/mirror-test/");
            Assert.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            
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
            rule.getRedirect().setMirrorPassQueryString(null);
            rule.getRedirect().setMirrorPassOriginalSlashes(null);
            
            request.setIndexDocument(indexDocument);
            request.AddRoutingRule(rule);
            ossClient.setBucketWebsite(request);
            
            waitForCacheExpiration(5);
            
            // check
            result = ossClient.getBucketWebsite(bucketName);
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertEquals(result.getRoutingRules().size(), 1);
            rr = result.getRoutingRules().get(0);
            Assert.assertEquals(rr.getNumber().intValue(), 2);
            Assert.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.Mirror);
            Assert.assertEquals(rr.getRedirect().getMirrorURL(), "http://oss-test.aliyun-inc.com/mirror-test/");
            Assert.assertNull(rr.getRedirect().getMirrorSecondaryURL());
            Assert.assertNull(rr.getRedirect().getMirrorProbeURL());
            Assert.assertFalse(rr.getRedirect().isMirrorPassQueryString());
            Assert.assertFalse(rr.getRedirect().isMirrorPassOriginalSlashes());
            Assert.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

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
            rule.getRedirect().setMirrorPassQueryString(true);
            rule.getRedirect().setMirrorPassOriginalSlashes(true);
            
            request.setIndexDocument(indexDocument);
            request.AddRoutingRule(rule);
            ossClient.setBucketWebsite(request);
            
            waitForCacheExpiration(5);
            
            // check
            result = ossClient.getBucketWebsite(bucketName);
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertEquals(result.getRoutingRules().size(), 1);
            rr = result.getRoutingRules().get(0);
            Assert.assertEquals(rr.getNumber().intValue(), 2);
            Assert.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.Mirror);
            Assert.assertEquals(rr.getRedirect().getMirrorURL(), "http://oss-test.aliyun-inc.com/mirror-test/");
            Assert.assertEquals(rr.getRedirect().getMirrorSecondaryURL(), "http://oss-test.aliyun-inc.com/mirror-secodary/");
            Assert.assertEquals(rr.getRedirect().getMirrorProbeURL(), "http://oss-test.aliyun-inc.com/mirror-probe/");
            Assert.assertTrue(rr.getRedirect().isMirrorPassQueryString());
            Assert.assertTrue(rr.getRedirect().isMirrorPassOriginalSlashes());
            Assert.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            
            ossClient.deleteBucketWebsite(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testNormalSetBucketWebsiteWithMirrorURLs() {
        final String bucketName = "normal-set-bucket-website-mirror";
        final String indexDocument = "index.html";

        try {
            ossClient.createBucket(bucketName);

            // Set index document and mirror
            SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(bucketName);
            RoutingRule rule = new RoutingRule();
            rule.setNumber(3);
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            rule.getRedirect().setMirrorURL("http://oss-test.aliyun-inc.com/mirror-test/");

            List<RoutingRule.Redirect.MirrorMultiAlternate> mirrorURLs = new ArrayList<RoutingRule.Redirect.MirrorMultiAlternate>();

            RoutingRule.Redirect.MirrorMultiAlternate mirrorMultiAlternate1 = new RoutingRule.Redirect.MirrorMultiAlternate();
            mirrorMultiAlternate1.setPrior(2);
            mirrorMultiAlternate1.setUrl("http://2.com/2/");

            RoutingRule.Redirect.MirrorMultiAlternate mirrorMultiAlternate2 = new RoutingRule.Redirect.MirrorMultiAlternate();
            mirrorMultiAlternate2.setPrior(3);
            mirrorMultiAlternate2.setUrl("http://3.com/3/");

            RoutingRule.Redirect.MirrorMultiAlternate mirrorMultiAlternate3 = new RoutingRule.Redirect.MirrorMultiAlternate();
            mirrorMultiAlternate3.setPrior(4);
            mirrorMultiAlternate3.setUrl("http://4.com/4/");

            RoutingRule.Redirect.MirrorMultiAlternate mirrorMultiAlternate4 = new RoutingRule.Redirect.MirrorMultiAlternate();
            mirrorMultiAlternate4.setPrior(5);
            mirrorMultiAlternate4.setUrl("http://5.com/5/");

            mirrorURLs.add(mirrorMultiAlternate1);
            mirrorURLs.add(mirrorMultiAlternate2);
            mirrorURLs.add(mirrorMultiAlternate3);
            mirrorURLs.add(mirrorMultiAlternate4);

            rule.getRedirect().setMirrorMultiAlternates(mirrorURLs);

            request.setIndexDocument(indexDocument);
            request.AddRoutingRule(rule);
            ossClient.setBucketWebsite(request);

            waitForCacheExpiration(5);

            // check
            BucketWebsiteResult result = ossClient.getBucketWebsite(bucketName);
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertEquals(result.getRoutingRules().size(), 1);
            RoutingRule rr = result.getRoutingRules().get(0);
            Assert.assertEquals(rr.getNumber().intValue(), 3);
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.Mirror);
            Assert.assertEquals(rr.getRedirect().getMirrorMultiAlternates().size(), 4);
            Assert.assertTrue(rr.getRedirect().getMirrorMultiAlternates().get(0).getPrior() == 2);
            Assert.assertEquals(rr.getRedirect().getMirrorMultiAlternates().get(0).getUrl(), "http://2.com/2/");
            Assert.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);

            ossClient.deleteBucketWebsite(bucketName);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testNormalSetBucketWebsiteWithRedirect() {
        final String bucketName = "normal-set-bucket-website-redirect";
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
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertEquals(result.getRoutingRules().size(), 1);
            RoutingRule rr = result.getRoutingRules().get(0);
            Assert.assertEquals(rr.getNumber().intValue(), 1);
            Assert.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.External);
            Assert.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assert.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Https);
            Assert.assertEquals(rr.getRedirect().getReplaceKeyWith(), "${key}.jpg");
            Assert.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 302);
            Assert.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            
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
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertEquals(result.getRoutingRules().size(), 2);
            Assert.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            
            rr = result.getRoutingRules().get(0);
            Assert.assertEquals(rr.getNumber().intValue(), 2);
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.External);
            Assert.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assert.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Https);
            Assert.assertEquals(rr.getRedirect().getReplaceKeyWith(), "${key}.jpg");
            Assert.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 302);
            
            rr = result.getRoutingRules().get(1);
            Assert.assertEquals(rr.getNumber().intValue(), 5);
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 403);
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.External);
            Assert.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assert.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Http);
            Assert.assertEquals(rr.getRedirect().getReplaceKeyPrefixWith(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assert.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 303);
            
            ossClient.deleteBucketWebsite(bucketName);
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testNormalSetBucketWebsiteWithCDNRedirect() {
        final String bucketName = "normal-set-bucket-website-redirect-cdn";
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
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertEquals(result.getRoutingRules().size(), 1);
            RoutingRule rr = result.getRoutingRules().get(0);
            Assert.assertEquals(rr.getNumber().intValue(), 1);
            Assert.assertEquals(rr.getCondition().getKeyPrefixEquals(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.AliCDN);
            Assert.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assert.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Https);
            Assert.assertEquals(rr.getRedirect().getReplaceKeyWith(), "${key}.jpg");
            Assert.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 302);
            
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
            Assert.assertEquals(indexDocument, result.getIndexDocument());
            Assert.assertEquals(result.getRoutingRules().size(), 2);
            Assert.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            
            rr = result.getRoutingRules().get(0);
            Assert.assertEquals(rr.getNumber().intValue(), 2);
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 404);
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.AliCDN);
            Assert.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assert.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Https);
            Assert.assertEquals(rr.getRedirect().getReplaceKeyWith(), "${key}.jpg");
            Assert.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 302);
            
            rr = result.getRoutingRules().get(1);
            Assert.assertEquals(rr.getNumber().intValue(), 5);
            Assert.assertEquals(rr.getCondition().getHttpErrorCodeReturnedEquals().intValue(), 403);
            Assert.assertEquals(rr.getRedirect().getRedirectType(), RoutingRule.RedirectType.External);
            Assert.assertEquals(rr.getRedirect().getHostName(), "oss.aliyuncs.com");
            Assert.assertEquals(rr.getRedirect().getProtocol(), RoutingRule.Protocol.Http);
            Assert.assertEquals(rr.getRedirect().getReplaceKeyPrefixWith(), "~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            Assert.assertEquals(rr.getRedirect().getHttpRedirectCode().intValue(), 303);
            
            ossClient.deleteBucketWebsite(bucketName);
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalSetBucketWebsiteWithMirror() {
        final String bucketName = "unormal-set-bucket-website-mirror";
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
                Assert.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
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
                Assert.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
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
                Assert.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
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
                Assert.fail("Set bucket website should not be successful");
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalSetBucketWebsiteWithRedirect() {
        final String bucketName = "unormal-set-bucket-website-redirect";
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
                Assert.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
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
                Assert.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
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
                Assert.fail("Add routing rule should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }
            
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalSetBucketWebsite() {
        final String bucketName = "unormal-set-bucket-website";
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
                
                Assert.fail("Set bucket website should not be successful");
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
                Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
            }
            
            // Set index document null
            try {                
                SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(nonexistentBucket);
                request.setIndexDocument(null);
                request.setErrorDocument(null);
                ossClient.setBucketWebsite(request);
                
                Assert.fail("Set bucket website should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }
        } finally {
            ossClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalGetBucketWebsite() {
        // Get non-existent bucket
        final String nonexistentBucket = "unormal-get-bucket-website";
        try {
            ossClient.getBucketWebsite(nonexistentBucket);
            Assert.fail("Get bucket website should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
        }
        
        // Get bucket without ownership
        final String bucketWithoutOwnership = "oss";
        try {
            ossClient.getBucketLogging(bucketWithoutOwnership);
            Assert.fail("Get bucket website should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
        }
        
        // Get bucket without setting website configuration
        final String bucketWithoutWebsiteConfiguration = "bucket-without-website-configuration";
        try {
            ossClient.createBucket(bucketWithoutWebsiteConfiguration);
            
            ossClient.getBucketWebsite(bucketWithoutWebsiteConfiguration);
            Assert.fail("Get bucket website should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_WEBSITE_CONFIGURATION, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_WEBSITE_CONFIGURATION_ERR));
        } finally {
            ossClient.deleteBucket(bucketWithoutWebsiteConfiguration);
        }
    }
    
    @Test
    public void testUnormalDeleteBucketWebsite() {
        // Delete non-existent bucket
        final String nonexistentBucket = "unormal-delete-bucket-website";
        try {
            ossClient.deleteBucketWebsite(nonexistentBucket);
            Assert.fail("Delete bucket website should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
        }
        
        // Delete bucket without ownership
        final String bucketWithoutOwnership = "oss";
        try {
            ossClient.deleteBucketWebsite(bucketWithoutOwnership);
            Assert.fail("Delete bucket website should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
        }
        
        // Delete bucket without setting website configuration
        final String bucketWithoutWebsiteConfiguration = "bucket-without-website-configuration";
        try {
            ossClient.createBucket(bucketWithoutWebsiteConfiguration);
            ossClient.deleteBucketWebsite(bucketWithoutWebsiteConfiguration);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketWithoutWebsiteConfiguration);
        }
    }

    @Test
    public void testInvalidPriorForMirrorMultiAlternate() {
        RoutingRule.Redirect.MirrorMultiAlternate mirrorMultiAlternate = new RoutingRule.Redirect.MirrorMultiAlternate();
        try {
            mirrorMultiAlternate.setPrior(0);
        } catch (ClientException e) {
            Assert.assertEquals(e.getErrorCode(), OSSErrorCode.INVALID_ARGUMENT);
        }

        try {
            mirrorMultiAlternate.setPrior(10001);
        } catch (ClientException e) {
            Assert.assertEquals(e.getErrorCode(), OSSErrorCode.INVALID_ARGUMENT);
        }
    }
}
