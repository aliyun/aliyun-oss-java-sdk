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

import static com.aliyun.oss.integrationtests.TestConstants.BUCKET_ACCESS_DENIED_ERR;
import static com.aliyun.oss.integrationtests.TestConstants.NO_SUCH_BUCKET_ERR;
import static com.aliyun.oss.integrationtests.TestConstants.NO_SUCH_LIFECYCLE_ERR;
import static com.aliyun.oss.integrationtests.TestUtils.genRandomString;
import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;
import static com.aliyun.oss.model.SetBucketLifecycleRequest.MAX_LIFECYCLE_RULE_LIMIT;
import static com.aliyun.oss.model.SetBucketLifecycleRequest.MAX_RULE_ID_LENGTH;

import java.text.ParseException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.LifecycleRule.AbortMultipartUpload;
import com.aliyun.oss.model.LifecycleRule.RuleStatus;
import com.aliyun.oss.model.SetBucketLifecycleRequest;

public class BucketLifecycleTest extends TestBase {

    @Test
    public void testNormalSetBucketLifecycle() throws ParseException {
        final String bucketName = "normal-set-bucket-lifecycle";
        final String ruleId0 = "delete obsoleted files";
        final String matchPrefix0 = "obsoleted0/";
        final String ruleId1 = "delete temporary files";
        final String matchPrefix1 = "temporary0/";
        final String ruleId2 = "delete obsoleted multipart files";
        final String matchPrefix2 = "obsoleted1/";
        final String ruleId3 = "delete temporary multipart files";
        final String matchPrefix3 = "temporary1/";
        final String ruleId4 = "delete temporary files(2)";
        final String matchPrefix4 = "temporary2/";
        
        try {
            secondClient.createBucket(bucketName);
            
            SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
            request.AddLifecycleRule(new LifecycleRule(ruleId0, matchPrefix0, RuleStatus.Enabled, 3));
            request.AddLifecycleRule(new LifecycleRule(ruleId1, matchPrefix1, RuleStatus.Enabled, 
                    DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z")));
            
            LifecycleRule rule = new LifecycleRule(ruleId2, matchPrefix2, RuleStatus.Enabled, 3);
            LifecycleRule.AbortMultipartUpload abortMultipartUpload = new LifecycleRule.AbortMultipartUpload();
            abortMultipartUpload.setExpirationDays(3);
            rule.setAbortMultipartUpload(abortMultipartUpload);
            request.AddLifecycleRule(rule);
            
            rule = new LifecycleRule(ruleId3, matchPrefix3, RuleStatus.Enabled, 
                    DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z"));
            abortMultipartUpload = new LifecycleRule.AbortMultipartUpload();
            abortMultipartUpload.setCreatedBeforeDate(DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z"));
            rule.setAbortMultipartUpload(abortMultipartUpload);
            request.AddLifecycleRule(rule);
            rule = new LifecycleRule(ruleId4, matchPrefix4, RuleStatus.Enabled);
            rule.setCreatedBeforeDate(DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z"));
            request.AddLifecycleRule(rule);
            secondClient.setBucketLifecycle(request);
            
            List<LifecycleRule> rules = secondClient.getBucketLifecycle(bucketName);
            Assert.assertEquals(rules.size(), 5);
            
            LifecycleRule r0 = rules.get(0);
            Assert.assertEquals(r0.getId(), ruleId0);
            Assert.assertEquals(r0.getPrefix(), matchPrefix0);
            Assert.assertEquals(r0.getStatus(), RuleStatus.Enabled);
            Assert.assertEquals(r0.getExpirationDays(), 3);
            Assert.assertTrue(r0.getAbortMultipartUpload() == null);
            
            LifecycleRule r1 = rules.get(1);
            Assert.assertEquals(r1.getId(), ruleId1);
            Assert.assertEquals(r1.getPrefix(), matchPrefix1);
            Assert.assertEquals(r1.getStatus(), RuleStatus.Enabled);
            Assert.assertEquals(DateUtil.formatIso8601Date(r1.getExpirationTime()), "2022-10-12T00:00:00.000Z");
            Assert.assertTrue(r1.getAbortMultipartUpload() == null);
            
            LifecycleRule r2 = rules.get(2);
            Assert.assertEquals(r2.getId(), ruleId2);
            Assert.assertEquals(r2.getPrefix(), matchPrefix2);
            Assert.assertEquals(r2.getStatus(), RuleStatus.Enabled);
            Assert.assertEquals(r2.getExpirationDays(), 3);
            Assert.assertNotNull(r2.getAbortMultipartUpload());
            Assert.assertEquals(r2.getAbortMultipartUpload().getExpirationDays(), 3);
            
            LifecycleRule r3 = rules.get(3);
            Assert.assertEquals(r3.getId(), ruleId3);
            Assert.assertEquals(r3.getPrefix(), matchPrefix3);
            Assert.assertEquals(r3.getStatus(), RuleStatus.Enabled);
            Assert.assertEquals(DateUtil.formatIso8601Date(r3.getExpirationTime()), "2022-10-12T00:00:00.000Z");
            Assert.assertNotNull(r3.getAbortMultipartUpload());
            Assert.assertEquals(DateUtil.formatIso8601Date(r3.getAbortMultipartUpload().getCreatedBeforeDate()), 
                    "2022-10-12T00:00:00.000Z");
            
            LifecycleRule r4 = rules.get(4);
            Assert.assertEquals(r4.getId(), ruleId4);
            Assert.assertEquals(r4.getPrefix(), matchPrefix4);
            Assert.assertEquals(r4.getStatus(), RuleStatus.Enabled);
            Assert.assertEquals(DateUtil.formatIso8601Date(r4.getCreatedBeforeDate()), "2022-10-12T00:00:00.000Z");
            Assert.assertTrue(r4.getAbortMultipartUpload() == null);
            
            // Override existing lifecycle rules
            final String nullRuleId = null;
            request.clearLifecycles();
            request.AddLifecycleRule(new LifecycleRule(nullRuleId, matchPrefix0, RuleStatus.Enabled, 7));
            secondClient.setBucketLifecycle(request);
            
            waitForCacheExpiration(5);
            
            rules = secondClient.getBucketLifecycle(bucketName);
            Assert.assertEquals(rules.size(), 1);
            
            r0 = rules.get(0);
            Assert.assertEquals(matchPrefix0, r0.getPrefix());
            Assert.assertEquals(r0.getStatus(), RuleStatus.Enabled);
            Assert.assertEquals(r0.getExpirationDays(), 7);
            
            secondClient.deleteBucketLifecycle(bucketName);
            
            // Try get bucket lifecycle again
            try {
                secondClient.getBucketLifecycle(bucketName);
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.NO_SUCH_LIFECYCLE, e.getErrorCode());
                Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_LIFECYCLE_ERR));
            }
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalSetBucketLifecycle() throws ParseException {
        final String bucketName = "unormal-set-bucket-lifecycle";
        final String ruleId0 = "delete obsoleted files";
        final String matchPrefix0 = "obsoleted/";
        
        try {
            secondClient.createBucket(bucketName);
            
            // Set non-existent bucket 
            final String nonexistentBucket = "nonexistent-bucket";            
            final LifecycleRule r = new LifecycleRule(ruleId0, matchPrefix0, RuleStatus.Enabled, 3);
            try {                
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                request.AddLifecycleRule(r);
                secondClient.setBucketLifecycle(request);
                
                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
                Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
            }
            
            // Set bucket without ownership
            final String bucketWithoutOwnership = "oss";
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketWithoutOwnership);
                request.AddLifecycleRule(r);
                secondClient.setBucketLifecycle(request);
                
                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (OSSException e) {
                Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
                Assert.assertTrue(e.getMessage().startsWith(BUCKET_ACCESS_DENIED_ERR));
            }
            
            // Set length of rule id exceeding RULE_ID_MAX_LENGTH(255)
            final String ruleId256 = genRandomString(MAX_RULE_ID_LENGTH + 1);
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
                request.AddLifecycleRule(new LifecycleRule(ruleId256, matchPrefix0, RuleStatus.Enabled, 3));
                
                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }
            
            // Set size of lifecycle rules exceeding LIFECYCLE_RULE_MAX_LIMIT(1000)
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                for (int i = 0; i < (MAX_LIFECYCLE_RULE_LIMIT + 1) ; i++) {
                    request.AddLifecycleRule(r);
                }
                
                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }
            
            // Set both rule id and prefix null
            final String nullRuleId = null;
            final String nullMatchPrefix = null;
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
                request.AddLifecycleRule(new LifecycleRule(nullRuleId, nullMatchPrefix, RuleStatus.Enabled, 3));
                secondClient.setBucketLifecycle(request);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            
            // Set both expiration day and expiration time
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                LifecycleRule invalidRule = new LifecycleRule();
                invalidRule.setId(ruleId0);
                invalidRule.setPrefix(matchPrefix0);
                invalidRule.setStatus(RuleStatus.Enabled);
                invalidRule.setExpirationTime(DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z"));
                invalidRule.setExpirationDays(3);
                request.AddLifecycleRule(invalidRule);
                
                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }
            
            // Set neither expiration day nor expiration time
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                LifecycleRule invalidRule = new LifecycleRule();
                invalidRule.setId(ruleId0);
                invalidRule.setPrefix(matchPrefix0);
                invalidRule.setStatus(RuleStatus.Enabled);
                request.AddLifecycleRule(invalidRule);
                
                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }
            
            // With abort multipart upload option
            try {
                SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(nonexistentBucket);
                LifecycleRule invalidRule = new LifecycleRule();
                invalidRule.setId(ruleId0);
                invalidRule.setPrefix(matchPrefix0);
                invalidRule.setStatus(RuleStatus.Enabled);
                invalidRule.setExpirationDays(3);
                LifecycleRule.AbortMultipartUpload abortMultipartUpload = new AbortMultipartUpload();
                abortMultipartUpload.setExpirationDays(3);
                abortMultipartUpload.setCreatedBeforeDate(DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z"));
                invalidRule.setAbortMultipartUpload(abortMultipartUpload);
                request.AddLifecycleRule(invalidRule);
                
                Assert.fail("Set bucket lifecycle should not be successful");
            } catch (Exception e) {
                Assert.assertTrue(e instanceof IllegalArgumentException);
            }
            
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalGetBucketLifecycle() {
        // Get non-existent bucket
        final String nonexistentBucket = "unormal-get-bucket-lifecycle";
        try {
            secondClient.getBucketLifecycle(nonexistentBucket);
            Assert.fail("Get bucket lifecycle should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
        }
        
        // Get bucket without ownership
        final String bucketWithoutOwnership = "oss";
        try {
            secondClient.getBucketLogging(bucketWithoutOwnership);
            Assert.fail("Get bucket lifecycle should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(BUCKET_ACCESS_DENIED_ERR));
        }
        
        // Get bucket without setting lifecycle configuration
        final String bucketWithoutLifecycleConfiguration = "bucket-without-lifecycle-configuration";
        try {
            secondClient.createBucket(bucketWithoutLifecycleConfiguration);
            
            secondClient.getBucketLifecycle(bucketWithoutLifecycleConfiguration);
            Assert.fail("Get bucket lifecycle should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_LIFECYCLE, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_LIFECYCLE_ERR));
        } finally {
            TestUtils.waitForCacheExpiration(5);
            secondClient.deleteBucket(bucketWithoutLifecycleConfiguration);
        }
    }
    
    @Test
    public void testUnormalDeleteBucketLifecycle() {
        // Delete non-existent bucket
        final String nonexistentBucket = "unormal-delete-bucket-lifecycle";
        try {
            secondClient.deleteBucketLifecycle(nonexistentBucket);
            Assert.fail("Delete bucket lifecycle should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(NO_SUCH_BUCKET_ERR));
        }
        
        // Delete bucket without ownership
        final String bucketWithoutOwnership = "oss";
        try {
            secondClient.deleteBucketLifecycle(bucketWithoutOwnership);
            Assert.fail("Delete bucket lifecycle should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(BUCKET_ACCESS_DENIED_ERR));
        }
        
        // Delete bucket without setting lifecycle configuration
        final String bucketWithoutLifecycleConfiguration = "bucket-without-lifecycle-configuration";
        try {
            secondClient.createBucket(bucketWithoutLifecycleConfiguration);
            secondClient.deleteBucketLifecycle(bucketWithoutLifecycleConfiguration);
            // TODO: Why not throw exception with NO_SUCH_LIFECYCLE error code?
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketWithoutLifecycleConfiguration);
        }
    }
    
}
