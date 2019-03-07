package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.PutBucketVersioningRequest;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import sun.jvm.hotspot.debugger.cdbg.AccessControl;

import static com.aliyun.oss.integrationtests.TestConfig.OSS_TEST_REGION;

public class BucketVersioningTest extends TestBase {

    static final String bucketName = "bucket-with-versioning-test";

    @BeforeClass
    public static void beforeClass() {
//        System.out.println("Start creating bucketing:");
//        Boolean isBucketExist = ossClient.doesBucketExist(bucketName);
//        if (!isBucketExist) {
//            ossClient.createBucket(bucketName);
//        }
    }

    @AfterClass
    public static void afterClass() {
//        ossClient.deleteBucket(bucketName);
    }

    /*
     * 一开始创建 bucket后获取bucket的多版本状态应该是"Disabled"
     */
    @Test
    public void getBucketVersioningTest() {
        String version = ossClient.getBucketVersioning(bucketName);
        Assert.assertEquals(version,"Disabled");
    }

    @Test
    public void setBucketVersioning() {
        PutBucketVersioningRequest putBucketVersioningRequest = new PutBucketVersioningRequest(bucketName);
        putBucketVersioningRequest.setBucketVersion("Suspended");
        ossClient.putBucketVersioning(putBucketVersioningRequest);

        // 状态设置为"Suspended" 验证 Suspended;
        String bucketVersion = ossClient.getBucketVersioning(bucketName);
        Assert.assertEquals(bucketVersion, "Suspended");

        putBucketVersioningRequest.setBucketVersion("Enabled");
        ossClient.putBucketVersioning(putBucketVersioningRequest);

        // 状态设置为"Suspended" 验证 Suspended;
        Assert.assertEquals(ossClient.getBucketVersioning(bucketName), "Enabled");
    }

    
}
