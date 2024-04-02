package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.GetPublicAccessBlockResult;
import com.aliyun.oss.model.PutPublicAccessBlockRequest;
import com.aliyun.oss.model.VoidResult;
import junit.framework.Assert;
import org.junit.Test;

public class PutPublicAccessBlockTest extends TestBase {
    @Test
    public void testPublicAccessBlock_1() {

        PutPublicAccessBlockRequest request = new PutPublicAccessBlockRequest(bucketName)
                .withBlockPublicAccess(true);

        VoidResult result = ossClient.putPublicAccessBlock(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());


        GetPublicAccessBlockResult getResult = ossClient.getPublicAccessBlock(bucketName);
        Assert.assertEquals(true, getResult.getBlockPublicAccess());

        VoidResult delResult = ossClient.deletePublicAccessBlock(bucketName);
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());
    }

    @Test
    public void testPublicAccessBlock_2() {

        PutPublicAccessBlockRequest request = new PutPublicAccessBlockRequest(bucketName)
                .withBlockPublicAccess(false);

        VoidResult result = ossClient.putPublicAccessBlock(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());


        GetPublicAccessBlockResult getResult = ossClient.getPublicAccessBlock(bucketName);
        Assert.assertEquals(false, getResult.getBlockPublicAccess());

        VoidResult delResult = ossClient.deletePublicAccessBlock(bucketName);
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());
    }

    @Test
    public void testPublicAccessBlock_3() {

        PutPublicAccessBlockRequest request = new PutPublicAccessBlockRequest(bucketName);

        VoidResult result = ossClient.putPublicAccessBlock(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());


        GetPublicAccessBlockResult getResult = ossClient.getPublicAccessBlock(bucketName);
        Assert.assertEquals(false, getResult.getBlockPublicAccess());

        VoidResult delResult = ossClient.deletePublicAccessBlock(bucketName);
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());
    }
}
