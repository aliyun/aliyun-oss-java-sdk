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

import static com.aliyun.oss.integrationtests.TestConfig.SECOND_REPLICATION_ENDPOINT;
import static com.aliyun.oss.integrationtests.TestConfig.SECOND_REPLICATION_ACCESS_ID;
import static com.aliyun.oss.integrationtests.TestConfig.SECOND_REPLICATION_ACCESS_KEY;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.BucketReplicationProgress;
import com.aliyun.oss.model.DeleteBucketReplicationRequest;
import com.aliyun.oss.model.GetBucketReplicationProgressRequest;
import com.aliyun.oss.model.ReplicationRule;
import com.aliyun.oss.model.ReplicationStatus;
import com.aliyun.oss.model.AddBucketReplicationRequest;

@Ignore
public class BucketReplicationTest extends TestBase {
    protected static OSSClient replicationClient;
    final static String targetBucketName = "java-sdk-test-qd-15";
    final String targetBucketLoc = "oss-cn-qingdao";
    
    @BeforeClass
    public static void beforeClass() {
        if (replicationClient == null) {
            Credentials secondCreds = new DefaultCredentials(
                    SECOND_REPLICATION_ACCESS_ID, SECOND_REPLICATION_ACCESS_KEY);
            replicationClient = new OSSClient(SECOND_REPLICATION_ENDPOINT,
                    new DefaultCredentialProvider(secondCreds),
                    new ClientConfiguration().setSupportCname(false)
                            .setSLDEnabled(true));
            
          replicationClient.createBucket(targetBucketName);
            
          BucketList buckets = replicationClient.listBuckets(targetBucketName, "", 100);
          Assert.assertEquals(1, buckets.getBucketList().size());
        }
    }
    
    @AfterClass
    public static void afterClass() {
        replicationClient.deleteBucket(targetBucketName);
        replicationClient.shutdown();
    }
    
    @Test
    public void testNormalAddBucketReplication() throws ParseException {
        final String bucketName = "test-bucket-set-replication";
        final String ruleId = "bucket-replication-rule-id";

        try {
            secondClient.createBucket(bucketName);
            
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setReplicationRuleID(ruleId);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            
            secondClient.addBucketReplication(request);
                        
            List<ReplicationRule> rules = secondClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
                                    
            ReplicationRule r0 = rules.get(0);
            Assert.assertEquals(r0.getReplicationRuleID(), ruleId);
            Assert.assertEquals(r0.getTargetBucketName(), targetBucketName);
            Assert.assertEquals(r0.getTargetBucketLocation(), targetBucketLoc);
            Assert.assertEquals(r0.getReplicationStatus(), ReplicationStatus.Starting);
            
            BucketReplicationProgress progress = secondClient.getBucketReplicationProgress(bucketName, ruleId);
            Assert.assertEquals(progress.getReplicationRuleID(), ruleId);
            Assert.assertEquals(progress.getTargetBucketName(), targetBucketName);
            Assert.assertEquals(progress.getTargetBucketLocation(), targetBucketLoc);
            Assert.assertEquals(progress.getReplicationStatus(), ReplicationStatus.Starting);
            Assert.assertEquals(progress.getHistoricalObjectProgress(), Float.valueOf(0));
            Assert.assertEquals(progress.isEnableHistoricalObjectReplication(), true);
            Assert.assertEquals(progress.getNewObjectProgress(), null);
                        
            secondClient.deleteBucketReplication(new DeleteBucketReplicationRequest(bucketName, ruleId));
                        
            rules = secondClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
            
            r0 = rules.get(0);
            Assert.assertEquals(r0.getReplicationRuleID(), ruleId);
            Assert.assertEquals(r0.getTargetBucketName(), targetBucketName);
            Assert.assertEquals(r0.getTargetBucketLocation(), targetBucketLoc);
            Assert.assertEquals(r0.getReplicationStatus(), ReplicationStatus.Closing);
            
            List <String> locations = secondClient.getBucketReplicationLocation(bucketName);
            Assert.assertEquals(locations.size() > 0, true);
            
        } catch (OSSException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testNormalAddBucketReplicationWithDefaultRuleID() throws ParseException {
        final String bucketName = "test-bucket-replication-default-ruleid";

        try {
            secondClient.createBucket(bucketName);
            
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            secondClient.addBucketReplication(request);
                        
            List<ReplicationRule> rules = secondClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
            
            ReplicationRule r0 = rules.get(0);
            Assert.assertEquals(r0.getReplicationRuleID().length(), "d6a8bfe3-56f6-42dd-9e7f-b4301d99b0ed".length());
            Assert.assertEquals(r0.getTargetBucketName(), targetBucketName);
            Assert.assertEquals(r0.getTargetBucketLocation(), targetBucketLoc);
            Assert.assertEquals(r0.isEnableHistoricalObjectReplication(), true);
            Assert.assertEquals(r0.getReplicationStatus(), ReplicationStatus.Starting);
            
            secondClient.deleteBucketReplication(new DeleteBucketReplicationRequest(bucketName, r0.getReplicationRuleID()));
                        
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testNormalAddBucketReplicationWithRuleID() throws ParseException {
        final String bucketName = "test-bucket-replication-ruleid-3";
        final String repRuleID = "~`!@#$%^&*()-_+=|\\[]{}<>:;\"',./?";

        try {
            secondClient.createBucket(bucketName);
                        
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setReplicationRuleID(repRuleID);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            request.setEnableHistoricalObjectReplication(false);
            secondClient.addBucketReplication(request);
                                    
            List<ReplicationRule> rules = secondClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
                        
            ReplicationRule r0 = rules.get(0);
            Assert.assertEquals(r0.getReplicationRuleID(), repRuleID);
            Assert.assertEquals(r0.getTargetBucketName(), targetBucketName);
            Assert.assertEquals(r0.getTargetBucketLocation(), targetBucketLoc);
            Assert.assertEquals(r0.isEnableHistoricalObjectReplication(), false);
            Assert.assertEquals(r0.getReplicationStatus(), ReplicationStatus.Starting);
            
            BucketReplicationProgress process = secondClient.getBucketReplicationProgress(bucketName, repRuleID);
            Assert.assertEquals(process.getReplicationRuleID(), repRuleID);
            Assert.assertEquals(process.getTargetBucketName(), targetBucketName);
            Assert.assertEquals(process.getTargetBucketLocation(), targetBucketLoc);
            Assert.assertEquals(process.getReplicationStatus(), ReplicationStatus.Starting);
            Assert.assertEquals(process.getHistoricalObjectProgress(), Float.valueOf(0));
            
            secondClient.deleteBucketReplication(new DeleteBucketReplicationRequest(bucketName, repRuleID));
                        
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testNormalDeleteBucketReplication() throws ParseException {
        final String bucketName = "test-bucket-delete-replication";
        final String repRuleID = "test-replication-ruleid";

        try {
            secondClient.createBucket(bucketName);
            
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setReplicationRuleID(repRuleID);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            secondClient.addBucketReplication(request);
            
            List<ReplicationRule> rules = secondClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
            ReplicationRule r0 = rules.get(0);
            Assert.assertEquals(r0.getReplicationStatus(), ReplicationStatus.Starting);
            
            secondClient.deleteBucketReplication(bucketName, repRuleID);
            
            rules = secondClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
            r0 = rules.get(0);
            Assert.assertEquals(r0.getReplicationStatus(), ReplicationStatus.Closing);
            
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testNormalDeleteBucketReplicationWithRuleID() throws ParseException {
        final String bucketName = "test-bucket-delete-replication-ruleid";
        final String repRuleID = "test-replication-ruleid";

        try {
            secondClient.createBucket(bucketName);
            
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setReplicationRuleID(repRuleID);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            secondClient.addBucketReplication(request);
            
            List<ReplicationRule> rules = secondClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
            ReplicationRule r0 = rules.get(0);
            Assert.assertEquals(r0.getReplicationStatus(), ReplicationStatus.Starting);
            
            secondClient.deleteBucketReplication(new DeleteBucketReplicationRequest(bucketName).withReplicationRuleID(repRuleID));
            
            rules = secondClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
            r0 = rules.get(0);
            Assert.assertEquals(r0.getReplicationStatus(), ReplicationStatus.Closing);
            
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testNormalGetBucketReplicationProgress() throws ParseException {
        final String bucketName = "test-bucket-get-replication-progress";
        final String repRuleID = "test-replication-progress-ruleid";
        
        try {
            secondClient.createBucket(bucketName);
            
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setReplicationRuleID(repRuleID);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            secondClient.addBucketReplication(request);
            
            List<ReplicationRule> rules = secondClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
            ReplicationRule r0 = rules.get(0);
            Assert.assertEquals(r0.getReplicationRuleID(), repRuleID);
            Assert.assertEquals(r0.getTargetBucketName(), targetBucketName);
            Assert.assertEquals(r0.getTargetBucketLocation(), targetBucketLoc);
            Assert.assertEquals(r0.isEnableHistoricalObjectReplication(), true);
            Assert.assertEquals(r0.getReplicationStatus(), ReplicationStatus.Starting);
            
            BucketReplicationProgress process = secondClient.getBucketReplicationProgress(bucketName, repRuleID);
            Assert.assertEquals(process.getReplicationRuleID(), repRuleID);
            Assert.assertEquals(process.getTargetBucketName(), targetBucketName);
            Assert.assertEquals(process.getTargetBucketLocation(), targetBucketLoc);
            Assert.assertEquals(process.getReplicationStatus(), ReplicationStatus.Starting);
            Assert.assertEquals(process.isEnableHistoricalObjectReplication(), true);
            Assert.assertEquals(process.getHistoricalObjectProgress(), Float.valueOf(0));
            Assert.assertEquals(process.getNewObjectProgress(), null);
                        
            secondClient.deleteBucketReplication(new DeleteBucketReplicationRequest(bucketName, repRuleID));
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testNormalGetBucketReplicationProgressWithDisableHistory() throws ParseException {
        final String bucketName = "test-bucket-replication-progress-disable-history";
        final String repRuleID = "test-replication-ruleid";
//        Date now = Calendar.getInstance().getTime();

        try {
            secondClient.createBucket(bucketName);

            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setReplicationRuleID(repRuleID);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            request.setEnableHistoricalObjectReplication(false);
            secondClient.addBucketReplication(request);

            List<ReplicationRule> rules = secondClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
            ReplicationRule r0 = rules.get(0);
            Assert.assertEquals(r0.getReplicationRuleID(), repRuleID);
            Assert.assertEquals(r0.getTargetBucketName(), targetBucketName);
            Assert.assertEquals(r0.getTargetBucketLocation(), targetBucketLoc);
            Assert.assertEquals(r0.isEnableHistoricalObjectReplication(), false);
            Assert.assertEquals(r0.getReplicationStatus(), ReplicationStatus.Starting);

            BucketReplicationProgress process = secondClient
                    .getBucketReplicationProgress(new GetBucketReplicationProgressRequest(
                            bucketName).withReplicationRuleID(repRuleID));
            Assert.assertEquals(process.getReplicationRuleID(), repRuleID);
            Assert.assertEquals(process.getTargetBucketName(), targetBucketName);
            Assert.assertEquals(process.getTargetBucketLocation(), targetBucketLoc);
            Assert.assertEquals(process.getReplicationStatus(), ReplicationStatus.Starting);
            Assert.assertEquals(process.getHistoricalObjectProgress(), Float.valueOf(0));
            Assert.assertEquals(process.isEnableHistoricalObjectReplication(), false);
            Assert.assertEquals(process.getNewObjectProgress(), null);
            // Assert.assertEquals(diffSecond(process.getNewObjectProgress(), now) < 5, true);

            secondClient.deleteBucketReplication(new DeleteBucketReplicationRequest(
                            bucketName, repRuleID));
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testNormalGetBucketReplicationLocation() throws ParseException {
        final String bucketName = "test-bucket-replication-location";

        try {
            secondClient.createBucket(bucketName);
            
            List<String> locations = secondClient.getBucketReplicationLocation(bucketName);
            Assert.assertEquals(locations.size() > 0, true);
            
            for (String loc : locations) {
                System.out.println("loc:" + loc);
            }
            
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    // Negative
    @Test
    public void testUnormalSetBucketReplication() throws ParseException {
        final String bucketName = "test-unormal-bucket-replication";
        final String ruleId = "bucket-replication-rule-id";

        try {
            secondClient.createBucket(bucketName);
            
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            request.setReplicationRuleID(ruleId);
            secondClient.addBucketReplication(request);
            
            try {
                secondClient.addBucketReplication(request);
                Assert.fail("Set bucket replication should not be successful.");
            } catch (OSSException e) {
                Assert.assertEquals(e.getErrorCode(), "InvalidArgument");
                Assert.assertEquals(e.getMessage().startsWith("Rule ID is not unique."), true);
            }
            
            secondClient.deleteBucketReplication(new DeleteBucketReplicationRequest(bucketName, ruleId));
                        
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalSetBucketReplicationLocation() throws ParseException {
        final String bucketName = "test-unormal-bucket-replication-loc";
        final String ruleId = "bucket-replication-rule-id";

        try {
            secondClient.createBucket(bucketName);
            
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation("oss-cn-zhengzhou");
            request.setReplicationRuleID(ruleId);
            
            try {
                secondClient.addBucketReplication(request);
                Assert.fail("Set bucket replication should not be successful.");
            } catch (OSSException e) {
                Assert.assertEquals(e.getErrorCode(), "InvalidTargetLocation");
                Assert.assertEquals(e.getMessage().startsWith("The target bucket you specified does not locate in the target location"), true);
            }
                                    
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalGetBucketReplication() throws ParseException {
        final String bucketName = "test-unormal-get-bucket-replication";

        try {
            secondClient.createBucket(bucketName);
                     
            try {
                secondClient.getBucketReplication(bucketName);
                Assert.fail("Get bucket replication should not be successful.");
            } catch (OSSException e) {
                Assert.assertEquals(e.getErrorCode(), "NoSuchReplicationConfiguration");
                Assert.assertEquals(e.getMessage().startsWith("The bucket you specified does not have replication configuration"), true);
            }
              
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalGetBucketReplicationProgress() throws ParseException {
        final String bucketName = "test-unormal-bucket-replication-progress";
        final String repRuleID = "test-replication-progress-ruleid";
        
        try {
            secondClient.createBucket(bucketName);
            
            try {
                secondClient.getBucketReplicationProgress(bucketName, repRuleID);
                Assert.fail("Get bucket replication should not be successful.");
            } catch (OSSException e) {
                Assert.assertEquals(e.getErrorCode(), "NoSuchReplicationRule");
                Assert.assertEquals(e.getMessage().startsWith("The BucketReplicationRule you specified does not exist"), true);
            }
            
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalGetBucketReplicationLocation() throws ParseException {
        final String bucketName = "test-unormal-bucket-replication-location";

        try {
            secondClient.getBucketReplicationLocation(bucketName);
            Assert.fail("Get bucket replication location should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith("The specified bucket does not exist"));
        }
        
    }
    
    @Test
    public void testUnormalSetBucketReplicationInvalidParam() throws ParseException {
        final String bucketName = "test-unormal-bucket-delete-replication-param";
        
        try {
            secondClient.createBucket(bucketName);
                     
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setTargetBucketName(targetBucketName);
                
            try {
                secondClient.addBucketReplication(request);
                Assert.fail("Get bucket replication should not be successful.");
            } catch (NullPointerException e) {
            }
                        
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalDeleteBucketReplicationInvalidParam() throws ParseException {
        final String bucketName = "test-unormal-bucket-delete-replication-param";
        final String ruleId = "bucket-replication-rule-id";
        
        try {
            secondClient.createBucket(bucketName);
                     
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            request.setReplicationRuleID(ruleId);
            secondClient.addBucketReplication(request);
                
            try {
                secondClient.deleteBucketReplication(new DeleteBucketReplicationRequest(bucketName));
                Assert.fail("Get bucket replication should not be successful.");
            } catch (NullPointerException e) {
            }
            
            secondClient.deleteBucketReplication(new DeleteBucketReplicationRequest(bucketName, ruleId));
            
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUnormalGetBucketReplicationProgressInvalidParam() throws ParseException {
        final String bucketName = "test-unormal-bucket-replication-progress";
        final String ruleId = "bucket-replication-rule-id";
        
        try {
            secondClient.createBucket(bucketName);
                     
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            request.setReplicationRuleID(ruleId);
            secondClient.addBucketReplication(request);
                
            try {
                secondClient.getBucketReplicationProgress(new GetBucketReplicationProgressRequest(bucketName));
                Assert.fail("Get bucket replication should not be successful.");
            } catch (NullPointerException e) {
            }
            
            secondClient.deleteBucketReplication(new DeleteBucketReplicationRequest(bucketName, ruleId));
            
        } catch (OSSException e) {
            Assert.fail(e.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @SuppressWarnings("unused")
    private long diffSecond(Date post, Date pre) {
        long diff = post.getTime() - pre.getTime();
        return diff / 1000;
    }
}
