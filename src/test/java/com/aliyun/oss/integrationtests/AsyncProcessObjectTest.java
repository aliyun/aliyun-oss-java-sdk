package com.aliyun.oss.integrationtests;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import com.aliyun.oss.utils.ResourceUtils;
import junit.framework.Assert;
import org.junit.Test;
import java.io.File;

import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

public class AsyncProcessObjectTest extends TestBase {
    private static StringBuilder styleBuilder = new StringBuilder();
    private static final String saveAsKey = "out-test-video";
    private static final String key = "oss/test-video.mp4";
    private static final String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
    private static final String bucketName = "examplebucket-zxl-test";

    @Test
    public void testSetBucketVersioning() throws Exception{
        OSSClient ossClient = null;
//        final String bucketName = super.bucketName + "-async-process-object";
        try {

            // create client
            ClientConfiguration conf = new ClientConfiguration().setSupportCname(false);
            Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
            ossClient = new OSSClient(endpoint, new DefaultCredentialProvider(credentials), conf);

            // 创建固定在杭州的bucket
            ossClient.createBucket(bucketName);
            waitForCacheExpiration(2);

            File file = new File(ResourceUtils.getTestFilename(key));

            // 上传原视频到oss
            ossClient.putObject(bucketName, key, file);

            // 设置process
            styleBuilder.append("video/convert,f_mp4,vcodec_h265,s_1920x1080,vb_2000000,fps_30,acodec_aac,ab_100000,sn_1");  // resize
            styleBuilder.append("|sys/saveas,");
            styleBuilder.append("o_" + BinaryUtil.toBase64String(saveAsKey.getBytes()).replaceAll("=", ""));
            styleBuilder.append(",");
            styleBuilder.append("b_" + BinaryUtil.toBase64String(bucketName.getBytes()).replaceAll("=", ""));

            AsyncProcessObjectRequest request = new AsyncProcessObjectRequest(bucketName, key, styleBuilder.toString());

            // 调用asyncProcessObject
            AsyncProcessObjectResult asyncProcessObject = ossClient.asyncProcessObject(request);

            // 如果开启了imm，并且创建了一个imm的project，然后绑定了project和bucket，既开始进入如下正常测试流程
            Assert.assertEquals(asyncProcessObject.getRequestId().length(), REQUEST_ID_LEN);
            Assert.assertNotNull(asyncProcessObject.getEventId());
            Assert.assertNotNull(asyncProcessObject.getAsyncRequestId());
            Assert.assertNotNull(asyncProcessObject.getTaskId());

            // 睡眠一段时间，等待异步视频处理完成。先根据视频大小/1m，然后再加5秒
            Thread.sleep((file.length() / 1000) + 5000);

            Assert.assertTrue(ossClient.doesObjectExist(bucketName, saveAsKey+".mp4"));

            // 删除视频文件和处理后的文件
            VoidResult delKey = ossClient.deleteObject(bucketName, key);
            Assert.assertEquals(delKey.getResponse().getStatusCode(), 204);
            VoidResult delSaveKey = ossClient.deleteObject(bucketName, saveAsKey+".mp4");
            Assert.assertEquals(delSaveKey.getResponse().getStatusCode(), 204);

        } catch (OSSException e) {
            e.printStackTrace();
            // 如果没有开启imm，异步流程，暂时报如下错误
            Assert.assertEquals("operation not support post: video/convert", e.getErrorMessage());

        } finally {
            // 先删除文件，再删除bucket
            VoidResult delKey = ossClient.deleteObject(bucketName, key);
            Assert.assertEquals(delKey.getResponse().getStatusCode(), 204);
            VoidResult delBucket = ossClient.deleteBucket(bucketName);
            Assert.assertEquals(delBucket.getResponse().getStatusCode(), 204);

            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

}
