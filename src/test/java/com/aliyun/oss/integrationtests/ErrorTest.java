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
            Assert.assertEquals(e.getErrorMessage(), "Authorization header is invalid.");
            e.printStackTrace();
        }
    }
}
