package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

public class BucketHttpsConfigTest extends TestBase {
    @Test
    public void testBucketHttpsConfig_1() {
        List<String> tlsVersion = new ArrayList<String>();
        tlsVersion.add("TLSv1.2");
        tlsVersion.add("TLSv1.3");

        PutBucketHttpsConfigRequest request = new PutBucketHttpsConfigRequest(bucketName)
                .withEnabled(true)
                .withTlsVersion(tlsVersion);

        VoidResult result = ossClient.putBucketHttpsConfig(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());


        GetBucketHttpsConfigResult getResult = ossClient.getBucketHttpsConfig(bucketName);
        Assert.assertEquals(request.isEnable(), getResult.isEnable());
        Assert.assertEquals(tlsVersion.toArray().length, getResult.getTlsVersion().toArray().length);
    }

    @Test
    public void testBucketHttpsConfig_2() {
        PutBucketHttpsConfigRequest request = new PutBucketHttpsConfigRequest(bucketName);

        VoidResult result = ossClient.putBucketHttpsConfig(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());


        GetBucketHttpsConfigResult getResult = ossClient.getBucketHttpsConfig(bucketName);
        Assert.assertEquals(request.isEnable(), getResult.isEnable());
        Assert.assertEquals(0, getResult.getTlsVersion().toArray().length);
    }

    @Test
    public void testBucketHttpsConfigException_1() {
        PutBucketHttpsConfigRequest request = new PutBucketHttpsConfigRequest(bucketName)
                .withEnabled(true);
        try {
            VoidResult result = ossClient.putBucketHttpsConfig(request);
        } catch (OSSException e) {
            Assert.assertEquals("The XML you provided was not well-formed or did not validate against our published schema.", e.getErrorMessage());
        } catch (Exception e1) {
            Assert.fail(e1.getMessage());
        }
    }

    @Test
    public void testBucketHttpsConfigException_2() {
        List<String> tlsVersion = new ArrayList<String>();
        tlsVersion.add("aaa");
        tlsVersion.add("aaa");

        PutBucketHttpsConfigRequest request = new PutBucketHttpsConfigRequest(bucketName)
                .withEnabled(true)
                .withTlsVersion(tlsVersion);

        try {
            VoidResult result = ossClient.putBucketHttpsConfig(request);
        } catch (OSSException e) {
            Assert.assertEquals("The XML you provided was not well-formed or did not validate against our published schema.", e.getErrorMessage());
        } catch (Exception e1) {
            Assert.fail(e1.getMessage());
        }
    }

    @Test
    public void testBucketHttpsConfigException_3() {
        List<String> tlsVersion = new ArrayList<String>();
        tlsVersion.add("TLSv1.2");
        tlsVersion.add("TLSv1.2");

        PutBucketHttpsConfigRequest request = new PutBucketHttpsConfigRequest(bucketName)
                .withEnabled(true)
                .withTlsVersion(tlsVersion);

        VoidResult result = ossClient.putBucketHttpsConfig(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());


        GetBucketHttpsConfigResult getResult = ossClient.getBucketHttpsConfig(bucketName);
        Assert.assertEquals(request.isEnable(), getResult.isEnable());
        Assert.assertEquals(tlsVersion.toArray().length-1, getResult.getTlsVersion().toArray().length);
    }
}
