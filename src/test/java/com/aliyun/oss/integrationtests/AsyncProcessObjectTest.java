package com.aliyun.oss.integrationtests;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.*;
import com.aliyun.oss.utils.ResourceUtils;
import junit.framework.Assert;
import org.junit.Test;
import java.io.File;

import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

public class AsyncProcessObjectTest extends TestBase {

    private static String originalImage = "oss/example.jpg";
    private static String newImage = "oss/new-example.jpg";

    private OSSClient ossClient;
    private String bucketName;
    private String endpoint;

    public void setUp() throws Exception {
        super.setUp();

        bucketName = genBucketName() + "-async-process";
        endpoint = "http://oss-cn-hangzhou.aliyuncs.com";

        //create client
        ClientConfiguration conf = new ClientConfiguration().setSupportCname(false);
        Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(credentials), conf);

        ossClient.createBucket(bucketName);
        waitForCacheExpiration(2);
        ossClient.putObject(bucketName, originalImage, new File(ResourceUtils.getTestFilename(originalImage)));
    }

    public void tearDown() throws Exception {
        if (ossClient != null) {
            ossClient.shutdown();
            ossClient = null;
        }
        super.tearDown();
    }

    @Test
    public void testAsyncProcessObject() {
        try {

            StringBuilder styleBuilder = new StringBuilder();
            styleBuilder.append("image/resize,m_fixed,w_100,h_100");
            styleBuilder.append("|sys/saveas,");
            styleBuilder.append("o_" + BinaryUtil.toBase64String(newImage.getBytes()).replaceAll("=", ""));
            styleBuilder.append(",");
            styleBuilder.append("b_" + BinaryUtil.toBase64String(bucketName.getBytes()).replaceAll("=", ""));

            AsyncProcessObjectRequest request = new AsyncProcessObjectRequest(bucketName, originalImage, styleBuilder.toString());

            AsyncProcessObjectResult asyncProcessObject = ossClient.asyncProcessObject(request);

            Assert.assertEquals(asyncProcessObject.getRequestId().length(), REQUEST_ID_LEN);
            Assert.assertNotNull(asyncProcessObject.getEventId());
            Assert.assertNotNull(asyncProcessObject.getAsyncRequestId());
            Assert.assertNotNull(asyncProcessObject.getTaskId());

        } catch (OSSException e) {
            e.printStackTrace();
            Assert.assertTrue(e.getErrorMessage().contains("ResourceNotFound, The specified resource Attachment is not found."));
        }
    }
}
