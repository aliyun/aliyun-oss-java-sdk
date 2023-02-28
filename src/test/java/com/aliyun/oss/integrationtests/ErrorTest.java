package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSSException;
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
            Assert.assertTrue(e.getHeader().contains("x-oss-ec=0026-00000001"));
            e.printStackTrace();
        }
    }
}
