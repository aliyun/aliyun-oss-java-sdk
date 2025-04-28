package com.aliyun.oss.integrationtests;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;
import java.util.Date;

public class ErrorTest extends TestBase {
    
    @Test
    public void testNormalGetSimplifiedObjectMeta() {
        final String key = "normal-get-simplified-object-meta";
        
        try {
            ossClient.getObjectAcl(bucketName, key);
        } catch (OSSException e) {
            Assert.assertEquals(e.getEC(), "0026-00000001");
            Assert.assertTrue(e.getMessage().contains("<EC>0026-00000001</EC>"));
            e.printStackTrace();
        }
    }
    
    @Test
    public void testUnormalGetSimplifiedObjectMeta() throws Exception {
        final String noSuchKey = "normal-get-simplified-object-meta";
        try {
            GenericRequest genericRequest = new GenericRequest(bucketName, noSuchKey);
           ossClient.getObjectMetadata(genericRequest);
        } catch (OSSException e) {
            Assert.assertEquals(e.getEC(), "0026-00000001");
            e.printStackTrace();
        }
    }

    @Test
    public void testErr() throws Exception {
        final String noSuchKey = "normal-get-simplified-object-meta";
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(TestConfig.OSS_TEST_ENDPOINT)
                .credentialsProvider(new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET))
                .clientConfiguration(conf)
                .region(TestConfig.OSS_TEST_REGION)
                .cloudBoxId("1")
                .build();
        try {
            GenericRequest genericRequest = new GenericRequest(bucketName, noSuchKey);
            ossClient.getObjectMetadata(genericRequest);
        } catch (OSSException e) {
            Assert.assertEquals(e.getEC(), "0002-00000227");
            Assert.assertEquals(e.getErrorCode(), "InvalidArgument");
            Assert.assertEquals(e.getErrorMessage(), "Invalid signing product in Authorization header.");
            e.printStackTrace();
        }
    }

    @Test
    public void testRequestTimeTooSkewedErrWithV1(){
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignVersion.V1);

        // disable auto correct clock skew
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(TestConfig.OSS_TEST_ENDPOINT)
                .credentialsProvider(new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET))
                .clientConfiguration(conf)
                .region(TestConfig.OSS_TEST_REGION)
                .build();

        AccessControlList result = ossClient.getBucketAcl(bucketName);
        Assert.assertEquals(result.getCannedACL(), CannedAccessControlList.Private);

        // enable auto correct clock skew
        conf.setEnableAutoCorrectClockSkew(true);
        conf.setTickOffset(new Date().getTime() + 3600 * 1000);
        ossClient = OSSClientBuilder.create()
                .endpoint(TestConfig.OSS_TEST_ENDPOINT)
                .credentialsProvider(new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET))
                .clientConfiguration(conf)
                .region(TestConfig.OSS_TEST_REGION)
                .build();

        try {
            ossClient.getBucketAcl(bucketName);
            Assert.fail("should not be here");
        } catch (OSSException e) {
            Assert.assertEquals(e.getEC(), "0002-00000504");
            Assert.assertEquals(e.getErrorCode(), "RequestTimeTooSkewed");
            Assert.assertEquals(e.getErrorMessage(), "The difference between the request time and the current time is too large.");
            //Assert.assertEquals("0002-00000504", e.getHeaders().get("x-oss-ec"));
            //Assert.assertNotNull(e.getHeaders().get("Date"));
            //Assert.assertNotNull(e.getHeaders().get("Content-Type"));
            //Assert.assertNotNull(e.getHeaders().get("x-oss-request-id"));
            Assert.assertNotNull(e.getErrorFields().get("RequestTime").toString());
            Assert.assertNotNull(e.getErrorFields().get("ServerTime").toString());
            e.printStackTrace();

            result = ossClient.getBucketAcl(bucketName);
            Assert.assertEquals(result.getCannedACL(), CannedAccessControlList.Private);
        }
    }

    @Test
    public void testRequestTimeTooSkewedErrWithV4() {
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignVersion.V4);

        // disable auto correct clock skew
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(TestConfig.OSS_TEST_ENDPOINT)
                .credentialsProvider(new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET))
                .clientConfiguration(conf)
                .region(TestConfig.OSS_TEST_REGION)
                .build();

        AccessControlList result = ossClient.getBucketAcl(bucketName);
        Assert.assertEquals(result.getCannedACL(), CannedAccessControlList.Private);

        // enable auto correct clock skew
        conf.setEnableAutoCorrectClockSkew(true);
        conf.setTickOffset(new Date().getTime() + 3600 * 1000);
        ossClient = OSSClientBuilder.create()
                .endpoint(TestConfig.OSS_TEST_ENDPOINT)
                .credentialsProvider(new DefaultCredentialProvider(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET))
                .clientConfiguration(conf)
                .region(TestConfig.OSS_TEST_REGION)
                .build();

        try {
            ossClient.getBucketAcl(bucketName);
            Assert.fail("should not be here");
        } catch (OSSException e) {
            Assert.assertEquals(e.getEC(), "0002-00000504");
            Assert.assertEquals(e.getErrorCode(), "RequestTimeTooSkewed");
            Assert.assertEquals(e.getErrorMessage(), "The difference between the request time and the current time is too large.");
            //Assert.assertEquals("0002-00000504", e.getHeaders().get("x-oss-ec"));
            //Assert.assertNotNull(e.getHeaders().get("Date"));
            //Assert.assertNotNull(e.getHeaders().get("Content-Type"));
            //Assert.assertNotNull(e.getHeaders().get("x-oss-request-id"));
            Assert.assertNotNull(e.getErrorFields().get("RequestTime").toString());
            Assert.assertNotNull(e.getErrorFields().get("ServerTime").toString());
            e.printStackTrace();

            result = ossClient.getBucketAcl(bucketName);
            Assert.assertEquals(result.getCannedACL(), CannedAccessControlList.Private);
        }
    }
}
