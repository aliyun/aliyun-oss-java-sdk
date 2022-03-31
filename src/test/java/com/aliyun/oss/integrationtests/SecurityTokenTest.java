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

import static com.aliyun.oss.integrationtests.TestConstants.SECURITY_TOKEN_ACCESS_DENIED_ERR;
import static com.aliyun.oss.integrationtests.TestUtils.createSessionClient;
import static com.aliyun.oss.integrationtests.TestUtils.genFixedLengthInputStream;
import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.*;

import org.codehaus.jettison.json.JSONException;
import org.junit.Ignore;
import org.junit.Test;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.BucketLoggingResult;
import com.aliyun.oss.model.BucketReferer;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.Grant;
import com.aliyun.oss.model.GroupGrantee;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.LifecycleRule.RuleStatus;
import com.aliyun.oss.model.ListPartsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.PartListing;
import com.aliyun.oss.model.Permission;
import com.aliyun.oss.model.SetBucketCORSRequest;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.aliyun.oss.model.SetBucketLoggingRequest;
import com.aliyun.oss.model.SetBucketWebsiteRequest;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;

@Ignore
public class SecurityTokenTest {
    
    private static final String DUMMY_SUFFIX = "xyz";
    private static final String STS_USER = "sts";

    @SuppressWarnings("deprecation")
	@Test
    public void testBucketOperationsWithToken() throws JSONException {
        List<String> actions = new ArrayList<String>();
        actions.add("oss:ListBuckets");
        List<String> resources = new ArrayList<String>();
        resources.add("acs:oss:*:" + STS_USER + ":*");
                
        // List buckets with security token is not supported
        OSSClient sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.listBuckets();
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Delete bucket if already exists
        final String bucketName = "test-bucket-operations-with-token";
        actions.add("oss:DeleteBucket");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.deleteBucket(bucketName);
        } catch (OSSException oe) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, oe.getErrorCode());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        waitForCacheExpiration(2);
        
        // Put bucket with valid security token
        actions.add("oss:PutBucket");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.createBucket(bucketName);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
        } 
        
        // Put unmatched bucket with valid security token
        String unmatchedBucketName = bucketName + DUMMY_SUFFIX;
        try {
            sessionClient.createBucket(unmatchedBucketName);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            Assertions.assertTrue(e.getMessage().startsWith(SECURITY_TOKEN_ACCESS_DENIED_ERR));
        } finally {
            actions.clear();
            resources.clear();
        }
        
        // Put bucket with non-existent username && valid security token
        final String nonexistentUser = "non-existent-user";
        actions.add("oss:PutBucket");
        resources.add("acs:oss:*:" + nonexistentUser + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.createBucket(unmatchedBucketName);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            Assertions.assertTrue(e.getMessage().startsWith(SECURITY_TOKEN_ACCESS_DENIED_ERR));
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Allow anyone to trigger operations start with 'Put' 
        final String anyone = "*";
        actions.add("oss:Put*");
        resources.add("acs:oss:*:" + anyone + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.createBucket(bucketName);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Put bucket acl
        actions.add("oss:PutBucketAcl");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
        }
        
        try {
            sessionClient.getBucketAcl(bucketName);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            Assertions.assertTrue(e.getMessage().startsWith(SECURITY_TOKEN_ACCESS_DENIED_ERR));
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Get bucket acl
        actions.add("oss:GetBucketAcl");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            AccessControlList returnedAcl = sessionClient.getBucketAcl(bucketName);
            Set<Grant> grants = returnedAcl.getGrants();
            Assertions.assertEquals(1, grants.size());
            Grant grant = (Grant) grants.toArray()[0];
            Assertions.assertEquals(GroupGrantee.AllUsers, grant.getGrantee());
            Assertions.assertEquals(Permission.Read, grant.getPermission());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
        }
        
        try {
            sessionClient.setBucketAcl(bucketName, CannedAccessControlList.Private);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            Assertions.assertTrue(e.getMessage().startsWith(SECURITY_TOKEN_ACCESS_DENIED_ERR));
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Put bucket logging
        final String targetPrefix = "bucket-logging-prefix";
        actions.add("oss:PutBucketLogging");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            SetBucketLoggingRequest request = new SetBucketLoggingRequest(bucketName);
            request.setTargetBucket(bucketName);
            request.setTargetPrefix(targetPrefix);
            sessionClient.setBucketLogging(request);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
        }
        
        try {
            sessionClient.getBucketLogging(bucketName);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            Assertions.assertTrue(e.getMessage().startsWith(SECURITY_TOKEN_ACCESS_DENIED_ERR));
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Get bucket logging
        actions.add("oss:GetBucketLogging");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            BucketLoggingResult result = sessionClient.getBucketLogging(bucketName);
            Assertions.assertEquals(bucketName, result.getTargetBucket());
            Assertions.assertEquals(targetPrefix, result.getTargetPrefix());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
        }
        
        try {
            SetBucketLoggingRequest request = new SetBucketLoggingRequest(bucketName);
            request.setTargetBucket(bucketName);
            request.setTargetPrefix(targetPrefix);
            sessionClient.setBucketLogging(request);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.ACCESS_DENIED, e.getErrorCode());
            Assertions.assertTrue(e.getMessage().startsWith(SECURITY_TOKEN_ACCESS_DENIED_ERR));
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Delete bucket logging
        actions.add("oss:DeleteBucketLogging");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.deleteBucketLogging(bucketName);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Put bucket website
        final String indexDocument = "index.html";
        final String errorDocument = "error.html";
        actions.add("oss:PutBucketWebsite");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            SetBucketWebsiteRequest request = new SetBucketWebsiteRequest(bucketName);
            request.setIndexDocument(indexDocument);
            request.setErrorDocument(errorDocument);
            sessionClient.setBucketWebsite(request);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Put bucket referer
        final String referer0 = "http://www.aliyun.com";
        final String referer1 = "https://www.aliyun.com";
        final String referer2 = "http://www.*.com";
        final String referer3 = "https://www.?.aliyuncs.com";
        actions.add("oss:PutBucketReferer");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            // Set non-empty referer list
            BucketReferer r = new BucketReferer();
            List<String> refererList = new ArrayList<String>();
            refererList.add(referer0);
            refererList.add(referer1);
            refererList.add(referer2);
            refererList.add(referer3);
            r.setRefererList(refererList);
            sessionClient.setBucketReferer(bucketName, r);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Put bucket lifecycle
        final String ruleId0 = "delete obsoleted files";
        final String matchPrefix0 = "obsoleted/";
        final String ruleId1 = "delete temporary files";
        final String matchPrefix1 = "temporary/";
        actions.add("oss:PutBucketLifecycle");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
            request.AddLifecycleRule(new LifecycleRule(ruleId0, matchPrefix0, RuleStatus.Enabled, 3));
            request.AddLifecycleRule(new LifecycleRule(ruleId1, matchPrefix1, RuleStatus.Enabled, 
                    DateUtil.parseIso8601Date("2022-10-12T00:00:00.000Z")));
            sessionClient.setBucketLifecycle(request);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Put bucket cors
        actions.add("oss:PutBucketCors");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            SetBucketCORSRequest request = new SetBucketCORSRequest(bucketName);
            CORSRule r0 = new CORSRule();
            r0.addAllowdOrigin("http://www.a.com");
            r0.addAllowdOrigin("http://www.b.com");
            r0.addAllowedMethod("GET");
            r0.addAllowedHeader("Authorization");
            r0.addExposeHeader("x-oss-test");
            r0.addExposeHeader("x-oss-test1");
            r0.setMaxAgeSeconds(100);
            request.addCorsRule(r0);
            sessionClient.setBucketCORS(request);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // List objects
        actions.add("oss:ListObjects");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            ObjectListing objectListing = sessionClient.listObjects(bucketName);
            Assertions.assertEquals(0, objectListing.getObjectSummaries().size());
            Assertions.assertEquals(bucketName, objectListing.getBucketName());
            Assertions.assertNull(objectListing.getDelimiter());
            Assertions.assertNull(objectListing.getPrefix());
            Assertions.assertNull(objectListing.getMarker());
            Assertions.assertNull(objectListing.getNextMarker());
            Assertions.assertFalse(objectListing.isTruncated());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Cleanup bucket if already exists
        actions.add("oss:DeleteBucket");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.deleteBucket(bucketName);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
    }
    
    @Test
    public void testObjectOperationsWithToken() throws JSONException {
        List<String> actions = new ArrayList<String>();
        List<String> resources = new ArrayList<String>();
        
        // Put bucket with valid security token
        final String bucketName = "test-object-operations-with-token-bucket0";
        actions.add("oss:PutBucket");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        OSSClient sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.createBucket(bucketName);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Put object
        final String key = "test-object-operations-with-token-key0";
        final long instreamLength = 1024;
        InputStream instream = null;
        actions.add("oss:PutObject");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName + "/*");
        sessionClient = createSessionClient(actions, resources);
        try {
            instream = genFixedLengthInputStream(instreamLength);
            sessionClient.putObject(bucketName, key, instream, null);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            
            if (instream != null) {
                try {
                    instream.close();
                } catch (IOException e) { }
            }
            
            sessionClient.shutdown();
        }
        
        // Get object
        actions.add("oss:GetObject");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName + "/*");
        sessionClient = createSessionClient(actions, resources);
        try {
            OSSObject o = sessionClient.getObject(bucketName, key);
            Assertions.assertEquals(instreamLength, o.getObjectMetadata().getContentLength());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Copy object
        actions.add("oss:GetObject");
        actions.add("oss:PutObject");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName + "/*");
        sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.copyObject(bucketName, key, bucketName, key + DUMMY_SUFFIX);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Initiate multipart upload and upload single part
        final int partSize = 128 * 1024;     //128KB
        String uploadId = null;
        List<PartETag> partETags = new ArrayList<PartETag>();
        actions.add("oss:PutObject");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName + "/*");
        sessionClient = createSessionClient(actions, resources);
        try {
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
            InitiateMultipartUploadResult result = sessionClient.initiateMultipartUpload(request);
            
            instream = genFixedLengthInputStream(partSize);
            uploadId = result.getUploadId();
            
            // Upload single part
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucketName);
            uploadPartRequest.setKey(key);
            uploadPartRequest.setInputStream(instream);
            uploadPartRequest.setPartNumber(1);
            uploadPartRequest.setPartSize(partSize);
            uploadPartRequest.setUploadId(uploadId);
            UploadPartResult uploadPartResult = sessionClient.uploadPart(uploadPartRequest);
            partETags.add(uploadPartResult.getPartETag());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // List parts
        actions.add("oss:ListParts");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName + "/*");
        sessionClient = createSessionClient(actions, resources);
        try {
            ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, key, uploadId);
            PartListing partListing = sessionClient.listParts(listPartsRequest);
            Assertions.assertEquals(1, partListing.getParts().size());
            Assertions.assertEquals(bucketName, partListing.getBucketName());
            Assertions.assertEquals(key, partListing.getKey());
            Assertions.assertEquals(uploadId, partListing.getUploadId());
            Assertions.assertEquals(1000, partListing.getMaxParts().intValue());
            Assertions.assertEquals(1, partListing.getNextPartNumberMarker().intValue());
            Assertions.assertFalse(partListing.isTruncated());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Complete multipart
        actions.add("oss:PutObject");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName + "/*");
        sessionClient = createSessionClient(actions, resources);
        try {
            CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
            sessionClient.completeMultipartUpload(request);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        // Cleanup objects and bucket
        actions.add("oss:DeleteObject");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName + "/*");
        sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.deleteObject(bucketName, key);
            sessionClient.deleteObject(bucketName, key + DUMMY_SUFFIX);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
        
        actions.add("oss:DeleteBucket");
        resources.add("acs:oss:*:" + STS_USER + ":" + bucketName);
        sessionClient = createSessionClient(actions, resources);
        try {
            sessionClient.deleteBucket(bucketName);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            actions.clear();
            resources.clear();
            sessionClient.shutdown();
        }
    }

}
