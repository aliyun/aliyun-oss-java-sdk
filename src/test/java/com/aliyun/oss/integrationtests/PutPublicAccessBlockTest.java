package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.GetPublicAccessBlockResult;
import com.aliyun.oss.model.PutPublicAccessBlockRequest;
import com.aliyun.oss.model.VoidResult;
import junit.framework.Assert;
import org.junit.Test;

public class PutPublicAccessBlockTest extends TestBase {
    @Test
    public void testPublicAccessBlock_1() throws InterruptedException {

        PutPublicAccessBlockRequest request = new PutPublicAccessBlockRequest()
                .withBlockPublicAccess(true);

        VoidResult result = ossClient.putPublicAccessBlock(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());

        Thread.sleep(10000);

        GetPublicAccessBlockResult getResult = ossClient.getPublicAccessBlock();
        Assert.assertEquals(true, getResult.getBlockPublicAccess());

        VoidResult delResult = ossClient.deletePublicAccessBlock();
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());
    }

    @Test
    public void testPublicAccessBlock_2() throws InterruptedException {

        PutPublicAccessBlockRequest request = new PutPublicAccessBlockRequest()
                .withBlockPublicAccess(false);

        VoidResult result = ossClient.putPublicAccessBlock(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());

        Thread.sleep(10000);

        GetPublicAccessBlockResult getResult = ossClient.getPublicAccessBlock();
        Assert.assertEquals(false, getResult.getBlockPublicAccess());

        VoidResult delResult = ossClient.deletePublicAccessBlock();
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());
    }

    @Test
    public void testPublicAccessBlock_3() {

        PutPublicAccessBlockRequest request = new PutPublicAccessBlockRequest();

        VoidResult result = ossClient.putPublicAccessBlock(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());


        GetPublicAccessBlockResult getResult = ossClient.getPublicAccessBlock();
        Assert.assertEquals(false, getResult.getBlockPublicAccess());

        VoidResult delResult = ossClient.deletePublicAccessBlock();
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());
    }
}
