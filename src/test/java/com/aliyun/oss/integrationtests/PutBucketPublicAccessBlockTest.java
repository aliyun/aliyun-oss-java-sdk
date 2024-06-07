package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.GetBucketPublicAccessBlockResult;
import com.aliyun.oss.model.PutBucketPublicAccessBlockRequest;
import com.aliyun.oss.model.VoidResult;
import junit.framework.Assert;
import org.junit.Test;

public class PutBucketPublicAccessBlockTest extends TestBase {
    @Test
    public void testBucketPublicAccessBlock_1() {

        PutBucketPublicAccessBlockRequest request = new PutBucketPublicAccessBlockRequest(bucketName)
                .withBlockPublicAccess(true);

        VoidResult result = ossClient.putBucketPublicAccessBlock(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());


        GetBucketPublicAccessBlockResult getResult = ossClient.getBucketPublicAccessBlock(bucketName);
        Assert.assertEquals(true, getResult.getBlockPublicAccess());

        VoidResult delResult = ossClient.deleteBucketPublicAccessBlock(bucketName);
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());
    }

    @Test
    public void testBucketPublicAccessBlock_2() {

        PutBucketPublicAccessBlockRequest request = new PutBucketPublicAccessBlockRequest(bucketName)
                .withBlockPublicAccess(false);

        VoidResult result = ossClient.putBucketPublicAccessBlock(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());


        GetBucketPublicAccessBlockResult getResult = ossClient.getBucketPublicAccessBlock(bucketName);
        Assert.assertEquals(false, getResult.getBlockPublicAccess());

        VoidResult delResult = ossClient.deleteBucketPublicAccessBlock(bucketName);
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());
    }

    @Test
    public void testBucketPublicAccessBlock_3() {

        PutBucketPublicAccessBlockRequest request = new PutBucketPublicAccessBlockRequest(bucketName);

        VoidResult result = ossClient.putBucketPublicAccessBlock(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());


        GetBucketPublicAccessBlockResult getResult = ossClient.getBucketPublicAccessBlock(bucketName);
        Assert.assertEquals(false, getResult.getBlockPublicAccess());

        VoidResult delResult = ossClient.deleteBucketPublicAccessBlock(bucketName);
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());
    }
}
