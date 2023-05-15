package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.*;
import java.util.List;
public class BucketRtcTest extends TestBase {
    private static OSS ossClient = null;
    private static OSS targetOssClient = null;
    private static String endpoint = "oss-cn-hangzhou.aliyuncs.com";
    private static String targetEndpoint = "oss-cn-qingdao.aliyuncs.com";
    private static String bucketName = "test-bucket-rtc-test";
    private static String repRuleID = "rult-id-001";
    private static String targetBucketName = "test-bucket-rtc-target-test";
    private static String targetBucketLoc = "oss-cn-qingdao";

    @BeforeClass
    public static void before(){
        ossClient = new OSSClientBuilder().build(endpoint, TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        targetOssClient = new OSSClientBuilder().build(targetEndpoint, TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);

        if(!ossClient.doesBucketExist(bucketName)){
            ossClient.createBucket(bucketName);
        }
        if(!targetOssClient.doesBucketExist(targetBucketName)){
            targetOssClient.createBucket(targetBucketName);
        }

        //Enable area replication
        AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
        request.setReplicationRuleID(repRuleID);
        request.setTargetBucketName(targetBucketName);
        request.setTargetBucketLocation(targetBucketLoc);
        request.setEnableHistoricalObjectReplication(false);
        request.setRtcStatus(RtcStatus.Enabled);
        ossClient.addBucketReplication(request);
    }

    @AfterClass
    public static void after(){

        if(ossClient.doesBucketExist(bucketName)){
            ossClient.deleteBucket(bucketName);
        }
        if(targetOssClient.doesBucketExist(targetBucketName)){
            targetOssClient.deleteBucket(targetBucketName);
        }
        if(ossClient != null){
            ossClient.shutdown();
        }
        if(targetOssClient != null){
            targetOssClient.shutdown();
        }
    }
    @Test
    public void testNormalPutRtcStatus() {

        try {
            List<ReplicationRule> rules = ossClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules.size(), 1);
            Assert.assertEquals(rules.get(0).getRtcStatus(), "enabling");
            //Enable RTC
            ossClient.putBucketRTC(new PutBucketRTCRequest(bucketName, rules.get(0).getReplicationRuleID(), RtcStatus.Disabled));
            //Check if the RTC configuration has been changed
            List<ReplicationRule> rules2 = ossClient.getBucketReplication(bucketName);
            Assert.assertEquals(rules2.size(), 1);

        } catch (OSSException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testReplicationLocationV2() {

        try {
            BucketReplicationLocationResult result = ossClient.getBucketReplicationLocationV2(bucketName);

            Assert.assertTrue(result.getLocationRTCConstraint().size() > 0);
            Assert.assertTrue(result.getLocationTransferTypeConstraint().size() > 0);
            Assert.assertTrue(result.getLocations().size() > 0);
        } catch (OSSException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }
}