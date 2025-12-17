package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;
import java.io.InputStream;
import static com.aliyun.oss.integrationtests.TestUtils.genFixedLengthInputStream;

public class SealAppendObjectTest extends TestBase {

    @Test
    public void testSealAppendObject() {
        String key = "seal-append-object.txt";
        final long instreamLength = 128 * 1024;

        try {
            InputStream instream = genFixedLengthInputStream(instreamLength);
            AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucketName, key, instream, null);
            appendObjectRequest.setPosition(0L);
            AppendObjectResult appendObjectResult = ossClient.appendObject(appendObjectRequest);

            // get object
            OSSObject o = ossClient.getObject(bucketName, key);
            Assert.assertEquals(key, o.getKey());
            Assert.assertEquals(instreamLength, o.getObjectMetadata().getContentLength());
            Assert.assertEquals(APPENDABLE_OBJECT_TYPE, o.getObjectMetadata().getObjectType());

            Assert.assertEquals(appendObjectResult.getRequestId().length(), REQUEST_ID_LEN);
            Assert.assertEquals(o.getRequestId().length(), REQUEST_ID_LEN);
            Assert.assertNull(o.getResponse().getHeaders().get("x-oss-sealed-time"));
            Assert.assertNull(o.getObjectMetadata().getSealedTime());

            // head object
            ObjectMetadata metadata = ossClient.headObject(bucketName, key);
            Assert.assertNull(metadata.getSealedTime());
            Assert.assertNull(metadata.getRawMetadata().get(OSSHeaders.SEALED_TIME));

            try {
                // seal append object
                ossClient.sealAppendObject(new SealAppendObjectRequest(bucketName, key).withPosition(instreamLength));

                // get object
                OSSObject obj = ossClient.getObject(bucketName, key);
                Assert.assertEquals(key, obj.getKey());
                Assert.assertEquals(instreamLength, obj.getObjectMetadata().getContentLength());
                Assert.assertEquals(APPENDABLE_OBJECT_TYPE, obj.getObjectMetadata().getObjectType());

                Assert.assertEquals(appendObjectResult.getRequestId().length(), REQUEST_ID_LEN);
                Assert.assertEquals(obj.getRequestId().length(), REQUEST_ID_LEN);
                Assert.assertNotNull(obj.getResponse().getHeaders().get("x-oss-sealed-time"));
                Assert.assertNotNull(obj.getObjectMetadata().getSealedTime());
                Assert.assertEquals(obj.getObjectMetadata().getSealedTime(), DateUtil.parseRfc822Date(obj.getResponse().getHeaders().get("x-oss-sealed-time")));

                // head object
                ObjectMetadata metadata2 = ossClient.headObject(bucketName, key);
                Assert.assertNotNull(metadata2.getSealedTime());
                Assert.assertNotNull(metadata2.getRawMetadata().get(OSSHeaders.SEALED_TIME));
                Assert.assertEquals(metadata2.getSealedTime(), DateUtil.parseRfc822Date(metadata2.getRawMetadata().get(OSSHeaders.SEALED_TIME).toString()));
            } catch (OSSException e) {
                Assert.assertEquals("MethodNotAllowed", e.getErrorCode());
            }

        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void testSealAppendObjectFail() {
        try {
            String bucket = null;
            String key = null;
            Long instreamLength = null;
            ossClient.sealAppendObject(new SealAppendObjectRequest(bucket, key).withPosition(instreamLength));
        } catch (NullPointerException e) {
            Assert.assertTrue(e.getMessage().contains("bucketName"));
        }

        try {
            String bucket = "12/";
            String key = null;
            Long instreamLength = null;
            ossClient.sealAppendObject(new SealAppendObjectRequest(bucket, key).withPosition(instreamLength));
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Bucket \"12/\""));
        }

        try {
            String bucket = "bucket-123";
            String key = null;
            Long instreamLength = null;
            ossClient.sealAppendObject(new SealAppendObjectRequest(bucket, key).withPosition(instreamLength));
        } catch (NullPointerException e) {
            Assert.assertTrue(e.getMessage().contains("key"));
        }

        try {
            String bucket = "bucket-123";
            String key = "";
            Long instreamLength = null;
            ossClient.sealAppendObject(new SealAppendObjectRequest(bucket, key).withPosition(instreamLength));
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("Key"));
        }

        try {
            String bucket = "bucket-123";
            String key = "key";
            Long instreamLength = null;
            ossClient.sealAppendObject(new SealAppendObjectRequest(bucket, key).withPosition(instreamLength));
        } catch (NullPointerException e) {
            Assert.assertTrue(e.getMessage().contains("position"));
        }
    }
}
