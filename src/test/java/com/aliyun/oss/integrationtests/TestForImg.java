package com.aliyun.oss.integrationtests;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.GetBucketImageResult;
import com.aliyun.oss.model.GetImageStyleResult;
import com.aliyun.oss.model.PutBucketImageRequest;
import com.aliyun.oss.model.PutImageStyleRequest;
import com.aliyun.oss.model.Style;

import junit.framework.Assert;
import junit.framework.TestCase;

@Ignore
public class TestForImg extends TestCase {
    private static String accessKeyId = "your-access-id";
    private static String accessKeySecret = "your-access-key";
    private static String bucketName = "img-sdk-test-bucket";
    private static String endpointImg = "http://img-test.aliyun-inc.com";
    private static String endpointOss = "http://oss-test.aliyun-inc.com";

    private static OSSClient clientImg = new OSSClient(endpointImg,
            accessKeyId, accessKeySecret,
            new ClientConfiguration().setSupportCname(false));
    private static OSSClient clientOss = new OSSClient(endpointOss,
            accessKeyId, accessKeySecret,
            new ClientConfiguration().setSupportCname(false));

    public void sleepSecond(int time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        clientOss.createBucket(bucketName);
        sleepSecond(10);
    }

    @After
    public void tearDown() {
        clientOss.deleteBucket(bucketName);
        clientImg.deleteBucketImage(bucketName);
    }

    public void testPutGetBucketImage() {
        PutBucketImageRequest request = new PutBucketImageRequest(bucketName);
        clientImg.putBucketImage(request);
        sleepSecond(10);
        GetBucketImageResult result = clientImg.getBucketImage(bucketName);

        Assert.assertEquals(result.GetBucketName(), bucketName);
        Assert.assertEquals(result.GetDefault404Pic(), "");
        Assert.assertFalse(result.GetIsAutoSetContentType());
        Assert.assertFalse(result.GetIsForbidOrigPicAccess());
        Assert.assertFalse(result.GetIsUseSrcFormat());
        Assert.assertFalse(result.GetIsSetAttachName());
        Assert.assertEquals(result.GetStyleDelimiters(), "!");
        Assert.assertEquals(result.GetStatus(), "Enabled");

        request.SetIsForbidOrigPicAccess(true);
        request.SetIsUseStyleOnly(true);
        request.SetIsAutoSetContentType(true);
        request.SetIsUseSrcFormat(true);
        request.SetIsSetAttachName(true);
        String default404Pic = "index.png";
        request.SetDefault404Pic(default404Pic);
        String styleDelimiters = "_";
        request.SetStyleDelimiters(styleDelimiters);

        clientImg.putBucketImage(request);
        sleepSecond(20);
        result = clientImg.getBucketImage(bucketName);
        Assert.assertEquals(result.GetBucketName(), bucketName);
        Assert.assertEquals(result.GetDefault404Pic(), default404Pic);
        Assert.assertFalse(!result.GetIsAutoSetContentType());
        // TODO img's problem
        Assert.assertFalse(!result.GetIsForbidOrigPicAccess());
        Assert.assertFalse(!result.GetIsUseSrcFormat());
        Assert.assertFalse(!result.GetIsSetAttachName());
        Assert.assertEquals(result.GetStyleDelimiters(), styleDelimiters);
        Assert.assertEquals(result.GetStatus(), "Enabled");
    }

    public void testForDeleteBucketImage() {
        PutBucketImageRequest request = new PutBucketImageRequest(bucketName);
        clientImg.putBucketImage(request);
        sleepSecond(10);
        clientImg.deleteBucketImage(bucketName);
        // List<Style> styleList = clientImg.listImageStyle(bucketName);
        // System.out.println("Style List:" + styleList.size());
        sleepSecond(10);
        try {
            clientImg.getBucketImage(bucketName);
            Assert.assertTrue(false);
        } catch (Exception e) {
            // e.printStackTrace();
            // Do Nothing
        }
    }

    public void testPutGetDeleteImageStyle() {
        PutBucketImageRequest request = new PutBucketImageRequest(bucketName);
        clientImg.putBucketImage(request);
        sleepSecond(10);
        PutImageStyleRequest requestStyle = new PutImageStyleRequest();
        requestStyle.SetBucketName(bucketName);
        String styleName = "myStyle";
        String style = "200w";
        requestStyle.SetStyle(style);
        requestStyle.SetStyleName(styleName);
        clientImg.putImageStyle(requestStyle);
        sleepSecond(10);
        GetImageStyleResult result = clientImg.getImageStyle(bucketName,
                styleName);
        Assert.assertEquals(result.GetStyle(), style);
        Assert.assertEquals(result.GetStyleName(), styleName);

        clientImg.deleteImageStyle(bucketName, styleName);
        sleepSecond(20);
        try {
            clientImg.getImageStyle(bucketName, styleName);
            Assert.assertTrue(false);
        } catch (Exception e) {
            // Do Nothing
        }
    }

    public void testListImageStyle() {
        // Clear all image style
        clientImg.deleteBucketImage(bucketName);
        sleepSecond(10);
        PutBucketImageRequest request = new PutBucketImageRequest(bucketName);
        clientImg.putBucketImage(request);
        sleepSecond(10);
        PutImageStyleRequest requestStyle = new PutImageStyleRequest();
        requestStyle.SetBucketName(bucketName);
        // Same length with style
        String[] styleName = { "myStyle", "myStyle1", "myStyle2", "myStyle3",
                "myStyle4" };
        String[] style = { "200w", "300w", "400w", "500w", "600w" };
        for (int i = 0; i < styleName.length; ++i) {
            requestStyle.SetStyle(style[i]);
            requestStyle.SetStyleName(styleName[i]);
            clientImg.putImageStyle(requestStyle);
        }
        sleepSecond(10);
        List<Style> styleList = clientImg.listImageStyle(bucketName);
        Assert.assertEquals(styleList.size(), styleName.length);
        int i;
        for (Style s : styleList) {
            for (i = 0; i < style.length; i++) {
                if (s.GetStyle().equals(style[i]))
                    break;
            }
            Assert.assertTrue(i != style.length);
            for (i = 0; i < styleName.length; i++) {
                if (s.GetStyleName().equals(styleName[i]))
                    break;
            }
            Assert.assertTrue(i != styleName.length);
        }
    }

    // 已抓包看了所有请求头
    public void testGenernicRequest() {
        PutBucketImageRequest request = new PutBucketImageRequest(bucketName);

        GenericRequest req = new GenericRequest();

        request.addHeader("oss-request-ip", "31.71.68.91");
        request.addHeader("oss-request-agent", "oss-java-sdk");
        request.addHeader("oss-operation", "PutBucketImage");
        try {
            clientImg.putBucketImage(request);
        } catch (Exception e) {

        }

        req.addHeader("oss-operation", "GetBucketImage");
        try {
            clientImg.getBucketImage(bucketName, req);
        } catch (Exception e) {
        }

        req.addHeader("oss-operation", "DeleteBucketImage");
        try {
            clientImg.deleteBucketImage(bucketName, req);
        } catch (Exception e) {
        }

        PutImageStyleRequest requestStyle = new PutImageStyleRequest();
        requestStyle.SetBucketName(bucketName);
        String styleName = "style";
        String styleContent = "200w";
        requestStyle.SetStyle(styleName);
        requestStyle.SetStyleName(styleContent);
        requestStyle.addHeader("oss-operation", "PutImageStyle");
        try {
            clientImg.putImageStyle(requestStyle);
        } catch (Exception e) {
        }

        req.addHeader("oss-operation", "DeleteImageStyle");
        try {
            clientImg.deleteImageStyle(bucketName, styleName, req);
        } catch (Exception e) {
        }

        req.addHeader("oss-operation", "GetImageStyle");
        try {
            clientImg.getImageStyle(bucketName, styleName, req);
        } catch (Exception e) {
        }

        req.addHeader("oss-operation", "ListImageStyle");
        try {
            clientImg.listImageStyle(bucketName, req);
        } catch (Exception e) {
        }
    }
}
