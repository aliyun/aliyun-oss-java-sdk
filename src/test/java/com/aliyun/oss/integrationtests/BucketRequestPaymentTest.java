package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.RequestPayer;
import junit.framework.Assert;
import org.junit.Test;

public class BucketRequestPaymentTest extends TestBase {

    @Test
    public void testBucketRequestPaymentRequest() {
        try {
            ossClient.putBucketRequestPayment(bucketName, RequestPayer.BucketOwner);
            assert (ossClient.getBucketRequesterPayment(bucketName) == RequestPayer.BucketOwner);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        try {
            ossClient.putBucketRequestPayment(bucketName, RequestPayer.Requester);
            assert (ossClient.getBucketRequesterPayment(bucketName) == RequestPayer.Requester);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
}
