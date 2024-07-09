package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.GetBucketPolicyStatusResult;
import junit.framework.Assert;
import org.junit.Test;

public class BucketPolicyStatusTest extends TestBase {
    @Test
    public void testBucketPolicyStatus_1() {
        GetBucketPolicyStatusResult getResult = ossClient.getBucketPolicyStatus(bucketName);
        Assert.assertEquals(200, getResult.getResponse().getStatusCode());
        Assert.assertEquals(false, getResult.isPublic());
    }
}
