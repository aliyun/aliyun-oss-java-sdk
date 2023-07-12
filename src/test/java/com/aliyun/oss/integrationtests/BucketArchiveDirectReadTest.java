package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetBucketArchiveDirectReadResult;
import com.aliyun.oss.model.PutBucketArchiveDirectReadRequest;
import com.aliyun.oss.model.VoidResult;
import junit.framework.Assert;
import org.junit.Test;
public class BucketArchiveDirectReadTest extends TestBase {
    @Test
    public void testBucketArchiveDirectRead() {
        try {
            PutBucketArchiveDirectReadRequest readRequest = new PutBucketArchiveDirectReadRequest(bucketName, true);
            VoidResult result = ossClient.putBucketArchiveDirectRead(readRequest);
            Assert.assertEquals(200, result.getResponse().getStatusCode());
            GetBucketArchiveDirectReadResult getResult = ossClient.getBucketArchiveDirectRead(bucketName);
            Assert.assertEquals(true, getResult.getEnabled());
        } catch (OSSException e) {
            Assert.assertEquals("NoSuchArchiveDirectReadConfiguration", e.getErrorCode());
        } catch (Exception e1) {
            Assert.fail(e1.getMessage());
        }
        try {
            PutBucketArchiveDirectReadRequest readRequest = new PutBucketArchiveDirectReadRequest(bucketName);
            VoidResult result = ossClient.putBucketArchiveDirectRead(readRequest);
            Assert.assertEquals(200, result.getResponse().getStatusCode());
            GetBucketArchiveDirectReadResult getResult = ossClient.getBucketArchiveDirectRead(bucketName);
            Assert.assertEquals(false, getResult.getEnabled());
        } catch (Exception e1) {
            Assert.fail(e1.getMessage());
        }
    }
}