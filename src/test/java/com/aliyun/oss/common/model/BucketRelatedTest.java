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

package com.aliyun.oss.common.model;

import com.aliyun.oss.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BucketRelatedTest {
    final private String bucketName = "test-bucket-name";

    @Test
    public void testAddBucketCnameRequest() {
        AddBucketCnameRequest request = new AddBucketCnameRequest(bucketName).withDomain("domain");
        assertEquals("domain", request.getDomain());

        request.setDomain("new domain");
        assertEquals("new domain", request.getDomain());
    }

    @Test
    public void testDeleteBucketCnameRequest() {
        DeleteBucketCnameRequest request = new DeleteBucketCnameRequest(bucketName).withDomain("domain");
        assertEquals("domain", request.getDomain());

        request.setDomain("new domain");
        assertEquals("new domain", request.getDomain());

        request = new DeleteBucketCnameRequest(bucketName);
        assertEquals(null, request.getDomain());
    }

    @Test
    public void testAddBucketReplicationRequest() {
        AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);

        request.setReplicationRuleID("id");
        assertEquals("id", request.getReplicationRuleID());

        request.setEnableHistoricalObjectReplication(false);
        assertEquals(false, request.isEnableHistoricalObjectReplication());
        request.setEnableHistoricalObjectReplication(true);
        assertEquals(true, request.isEnableHistoricalObjectReplication());

        List<String> prefixes = new ArrayList<String>();
        prefixes.add("prefix 1");
        prefixes.add("prefix 2");
        assertEquals(0, request.getObjectPrefixList().size());
        request.setObjectPrefixList(prefixes);
        assertEquals(prefixes, request.getObjectPrefixList());

        List<AddBucketReplicationRequest.ReplicationAction> replicationActionList =
                new ArrayList<AddBucketReplicationRequest.ReplicationAction>();
        assertEquals(0, request.getReplicationActionList().size());
        replicationActionList.add(AddBucketReplicationRequest.ReplicationAction.ALL);
        request.setReplicationActionList(replicationActionList);
        assertEquals(replicationActionList, request.getReplicationActionList());


        assertEquals(AddBucketReplicationRequest.ReplicationAction.ALL,
                AddBucketReplicationRequest.ReplicationAction.parse("ALL"));
        assertEquals(AddBucketReplicationRequest.ReplicationAction.ALL.toString(), "ALL");
        assertEquals(AddBucketReplicationRequest.ReplicationAction.PUT,
                AddBucketReplicationRequest.ReplicationAction.parse("PUT"));
        assertEquals(AddBucketReplicationRequest.ReplicationAction.DELETE,
                AddBucketReplicationRequest.ReplicationAction.parse("DELETE"));
        assertEquals(AddBucketReplicationRequest.ReplicationAction.ABORT,
                AddBucketReplicationRequest.ReplicationAction.parse("ABORT"));


    }

    @Test
    public void testDeleteBucketReplicationRequest() {
        DeleteBucketReplicationRequest request = new DeleteBucketReplicationRequest(bucketName).withReplicationRuleID("id");
        assertEquals("id", request.getReplicationRuleID());

        request.setReplicationRuleID("new id");
        assertEquals("new id", request.getReplicationRuleID());

        request = new DeleteBucketReplicationRequest(bucketName, "id1");
        assertEquals("id1", request.getReplicationRuleID());
    }

    @Test
    public void testPutBucketImageRequest() {
        PutBucketImageRequest request = new PutBucketImageRequest("bucket");
        request.setBucketName("bucket");
        request.SetIsForbidOrigPicAccess(false);
        request.SetIsUseStyleOnly(false);
        request.SetIsAutoSetContentType(false);
        request.SetIsUseSrcFormat(false);
        request.SetIsSetAttachName(false);
        request.SetDefault404Pic("404Pic");
        request.SetStyleDelimiters("del");

        assertEquals("bucket", request.getBucketName());
        assertEquals(false, request.GetIsForbidOrigPicAccess());
        assertEquals(false, request.GetIsUseStyleOnly());
        assertEquals(false, request.GetIsAutoSetContentType());
        assertEquals(false, request.GetIsUseSrcFormat());
        assertEquals(false, request.GetIsSetAttachName());
        assertEquals("404Pic", request.GetDefault404Pic());
        assertEquals("del", request.GetStyleDelimiters());
    }

    @Test
    public void testPutImageStyleRequest() {
        PutImageStyleRequest request = new PutImageStyleRequest();
        request.SetBucketName("bucket");
        request.SetStyleName("stylename");
        request.SetStyle("style");

        assertEquals("bucket", request.GetBucketName());
        assertEquals("stylename", request.GetStyleName());
        assertEquals("style", request.GetStyle());
    }

    @Test
    public void testSetBucketCORSRequest() {
        SetBucketCORSRequest.CORSRule rule = new SetBucketCORSRequest.CORSRule();
        rule.addAllowdOrigin(null);
        rule.addAllowdOrigin("");

        List<String> allowedOrigins = new ArrayList<String>();
        rule.setAllowedOrigins(null);
        rule.setAllowedOrigins(allowedOrigins);
        allowedOrigins.add("origin");
        rule.setAllowedOrigins(allowedOrigins);
        rule.addAllowdOrigin("here");
        rule.addAllowdOrigin("");
        rule.addAllowdOrigin(null);

        List<String> allowedMethods = new ArrayList<String>();
        rule.setAllowedMethods(null);
        rule.setAllowedMethods(allowedMethods);
        allowedMethods.add("PUT");
        rule.setAllowedMethods(allowedMethods);
        rule.addAllowedMethod("GET");
        rule.addAllowedMethod("");
        rule.addAllowedMethod(null);

        List<String> allowedHeaders = new ArrayList<String>();
        rule.setAllowedHeaders(null);
        rule.setAllowedHeaders(allowedHeaders);
        allowedHeaders.add("header");
        rule.setAllowedHeaders(allowedHeaders);
        rule.addAllowedHeader("header1");
        rule.addAllowedHeader("");
        rule.addAllowedHeader(null);

        List<String> exposeHeader = new ArrayList<String>();
        rule.setExposeHeaders(null);
        rule.setExposeHeaders(exposeHeader);
        exposeHeader.add("header");
        rule.setExposeHeaders(exposeHeader);
        rule.addExposeHeader("header1");
        rule.addExposeHeader("");
        rule.addExposeHeader(null);

        SetBucketCORSRequest request = new SetBucketCORSRequest("bucket");

        try {
            request.addCorsRule(null);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            rule = new SetBucketCORSRequest.CORSRule();
            rule.addAllowdOrigin("*");
            rule.addAllowedMethod("PUT");
            for (int i = 0; i < 11; i++) {
                request.addCorsRule(rule);
            }
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        //one addAllowdOrigin *
        try {
            rule = new SetBucketCORSRequest.CORSRule();
            rule.addAllowdOrigin("*");
            rule.addAllowdOrigin("*");
            rule.addAllowedMethod("PUT");
            request.addCorsRule(rule);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            rule = new SetBucketCORSRequest.CORSRule();
            rule.addAllowdOrigin("*");
            rule.addAllowedMethod("PUT");
            rule.addAllowedHeader("*");
            rule.addAllowedHeader("*");
            request.addCorsRule(rule);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            rule = new SetBucketCORSRequest.CORSRule();
            rule.addAllowdOrigin("*");
            rule.addAllowedMethod("");
            rule.addAllowedHeader("*");
            request.addCorsRule(rule);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            rule = new SetBucketCORSRequest.CORSRule();
            rule.addAllowdOrigin("*");
            rule.addAllowedMethod(null);
            rule.addAllowedHeader("*");
            request.addCorsRule(rule);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            request.setCorsRules(null);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            List<CORSRule> corsRules = new ArrayList<CORSRule>();
            request.setCorsRules(corsRules);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            List<CORSRule> corsRules = new ArrayList<CORSRule>();
            rule = new SetBucketCORSRequest.CORSRule();
            rule.addAllowdOrigin("*");
            rule.addAllowedMethod("PUT");
            corsRules.add(rule);
            request.setCorsRules(corsRules);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            List<CORSRule> corsRules = new ArrayList<CORSRule>();
            rule = new SetBucketCORSRequest.CORSRule();
            rule.addAllowdOrigin("*");
            rule.addAllowedMethod("PUT");

            for (int i = 0; i < 11; i++)
                corsRules.add(rule);
            request.setCorsRules(corsRules);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSSEAlgorithm() {
        assertEquals(SSEAlgorithm.AES256, SSEAlgorithm.getDefault());
        assertEquals("AES256", SSEAlgorithm.AES256.getAlgorithm());
        assertEquals(null, SSEAlgorithm.fromString(null));
        assertEquals(SSEAlgorithm.KMS, SSEAlgorithm.fromString("KMS"));

        try {
            SSEAlgorithm sse = SSEAlgorithm.fromString("UN");
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testServerSideEncryptionByDefault() {
        ServerSideEncryptionByDefault sseDefault = new ServerSideEncryptionByDefault()
                .withSSEAlgorithm(SSEAlgorithm.KMS)
                .withKMSMasterKeyID("id");

        assertEquals("KMS", sseDefault.getSSEAlgorithm());
        assertEquals("id", sseDefault.getKMSMasterKeyID());

        sseDefault.setSSEAlgorithm(SSEAlgorithm.AES256);
        assertEquals("AES256", sseDefault.getSSEAlgorithm());

        sseDefault = new ServerSideEncryptionByDefault(SSEAlgorithm.KMS);
        assertEquals("KMS", sseDefault.getSSEAlgorithm());
    }

    @Test
    public void testBucket() {
        Bucket bucket = new Bucket();
        bucket.setName("bucket-name");
        assertEquals("bucket-name", bucket.getName());

        bucket.setStorageClass(null);
        assertFalse(bucket.toString().contains("storageClass="));

        bucket.setStorageClass(StorageClass.IA);
        assertTrue(bucket.toString().contains("storageClass="));
    }

    @Test
    public void testDataRedundancyType() {
        assertEquals(DataRedundancyType.LRS, DataRedundancyType.parse("LRS"));
        assertEquals(DataRedundancyType.ZRS, DataRedundancyType.parse("ZRS"));

        try {
            DataRedundancyType type = DataRedundancyType.parse("ERROR");
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testOwner() {
        Owner owner = new Owner();
        assertEquals(0, owner.hashCode());

        owner.setId("id");
        owner.setDisplayName("name");
        assertFalse(owner.toString().isEmpty());

        Owner owner1 = new Owner("id1", "name");
        assertFalse(owner.equals(owner1));
    }

    @Test
    public void testSetBucketLifecycleRequest() {
        SetBucketLifecycleRequest request = new SetBucketLifecycleRequest("bucket");

        try {
            request.setLifecycleRules(null);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            List<LifecycleRule> lifecycleRules = new ArrayList<LifecycleRule>();
            request.setLifecycleRules(lifecycleRules);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            List<LifecycleRule> lifecycleRules = new ArrayList<LifecycleRule>();
            for (int i = 0; i < 1001; i++)
                lifecycleRules.add(new LifecycleRule());
            request.setLifecycleRules(lifecycleRules);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            request.AddLifecycleRule(null);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }


    }

    @Test
    public void testBucketProcess() {
        BucketProcess process = new BucketProcess(null);
        assertEquals(null, process.getImageProcess());

        ImageProcess imageProcess = new ImageProcess("compliedHost", false, "", "");
        process.setImageProcess(imageProcess);
        assertEquals(imageProcess, process.getImageProcess());
    }


    @SuppressWarnings("deprecation")
    @Test
    public void testBucketReferer() {
        BucketReferer refer = new BucketReferer();
        assertEquals(true, refer.allowEmpty());

        List<String> refererList = new ArrayList<String>();
        refererList.add("1");
        refererList.add("2");

        assertEquals(0, refer.getRefererList().size());
        refer.setRefererList(refererList);
        assertEquals(refererList, refer.getRefererList());
    }

    @Test
    public void testBucketVersioningConfiguration() {
        BucketVersioningConfiguration conf = new BucketVersioningConfiguration(BucketVersioningConfiguration.SUSPENDED);
        assertTrue(conf.getStatus().equals(BucketVersioningConfiguration.SUSPENDED));

        conf = new BucketVersioningConfiguration().withStatus(BucketVersioningConfiguration.ENABLED);
        assertTrue(conf.getStatus().equals(BucketVersioningConfiguration.ENABLED));
    }


    @Test
    public void testSetBucketWebsiteRequest() {
        SetBucketWebsiteRequest request = new SetBucketWebsiteRequest("bucket");

        try {
            request.setRoutingRules(null);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            List<RoutingRule> routingRules = new ArrayList<RoutingRule>();
            request.setRoutingRules(routingRules);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            List<RoutingRule> routingRules = new ArrayList<RoutingRule>();

            request = new SetBucketWebsiteRequest(bucketName);
            RoutingRule rule = new RoutingRule();
            rule.setNumber(2);
            rule.getCondition().setKeyPrefixEquals("~!@#$%^&*()-_=+|\\[]{}<>,./?`~");
            rule.getCondition().setHttpErrorCodeReturnedEquals(404);
            rule.getRedirect().setRedirectType(RoutingRule.RedirectType.Mirror);
            rule.getRedirect().setMirrorURL("http://oss-test.aliyun-inc.com/mirror-test/");
            rule.getRedirect().setMirrorSecondaryURL(null);
            rule.getRedirect().setMirrorProbeURL(null);
            rule.getRedirect().setPassQueryString(null);
            rule.getRedirect().setPassOriginalSlashes(null);

            routingRules.add(rule);
            request.setRoutingRules(routingRules);
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

}
