package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.junit.jupiter.api.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;

public class OverwriteEndpointTest  extends TestBase {

    private String secondEndpoint;
    private OSS ossClient2;
    private String key;

    public void setUp() throws Exception {
        super.setUp();
        if (TestConfig.OSS_TEST_ENDPOINT.indexOf("oss-cn-hangzhou.aliyuncs.com") > 0) {
            secondEndpoint = "oss-cn-shanghai.aliyuncs.com";
        } else {
            secondEndpoint = "oss-cn-hangzhou.aliyuncs.com";
        }
        ossClient2 = new OSSClientBuilder().build(secondEndpoint, TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        key = "test-key";
        ossClient.putObject(bucketName, key, new ByteArrayInputStream("".getBytes()));
    }

    @Test
    public void testOverwriteEndpoint() {
        try {
            ObjectAcl acl = ossClient.getObjectAcl(bucketName,key);
            Assertions.assertEquals(ObjectPermission.Default, acl.getPermission());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        try {
            ObjectAcl acl = ossClient2.getObjectAcl(bucketName,key);
            Assertions.fail("should not here");
        } catch (Exception e) {
            Assertions.assertTrue( e instanceof OSSException);
            OSSException e1 = (OSSException)e;
            Assertions.assertEquals(bucketName + "." + secondEndpoint, e1.getHostId());
        }

        try {
            GenericRequest request = new GenericRequest(bucketName, key);
            Assertions.assertEquals(null, request.getEndpoint());
            request.setEndpoint(TestConfig.OSS_TEST_ENDPOINT);
            ObjectAcl acl = ossClient2.getObjectAcl(request);
            Assertions.assertEquals(ObjectPermission.Default, acl.getPermission());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testInvalidEndpoint() {
        try {
            GenericRequest request = new GenericRequest(bucketName, key);
            request.setEndpoint("?invalid");
            ObjectAcl acl = ossClient2.getObjectAcl(request);
        } catch (Exception e) {
            Assertions.assertTrue( e instanceof IllegalArgumentException);
        }
    }
}
