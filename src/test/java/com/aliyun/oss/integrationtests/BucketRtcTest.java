package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;
import java.util.List;
public class BucketRtcTest extends TestBase {
    @Test
    public void testNormalPutRtcStatus() {
        final String bucketName = "test-bucket-rtc2";
        final String repRuleID = "~`!@#$%^&*()-_+=|\\[]{}<>:;\"',./?";
        final String targetBucketName = "test-bucket-rtc-target2";
        final String targetBucketLoc = "oss-cn-hangzhou";
        try {
            if(!ossClient.doesBucketExist(bucketName)){
                ossClient.createBucket(bucketName);
            }
            if(!ossClient.doesBucketExist(targetBucketName)){
                ossClient.createBucket(targetBucketName);
            }
            //开启区域复制
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setReplicationRuleID(repRuleID);
            request.setTargetBucketName(targetBucketName);
            request.setTargetBucketLocation(targetBucketLoc);
            request.setEnableHistoricalObjectReplication(false);
            request.setRtcStatus(RtcStatus.Enabled);
            ossClient.addBucketReplication(request);
            //获取区域复制信息
            List<ReplicationRule> rules = ossClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
            Assert.assertEquals(rules.get(0).getRtcStatus(), RtcStatus.Enabled);
            //开启rtc
            ossClient.putBucketRTC(new PutBucketRTCRequest(bucketName, rules.get(0).getReplicationRuleID(), RtcStatus.Disabled));
            //查看rtc配置是否更改
            List<ReplicationRule> rules2 = ossClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules2.size(), 1);
            Assert.assertEquals(rules2.get(0).getRtcStatus(), RtcStatus.Disabled);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);
            ossClient.deleteBucket(targetBucketName);
        }
    }
}