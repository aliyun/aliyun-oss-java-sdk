package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.util.*;

public class BucketCallbackPolicyTest extends TestBase {

    @Test
    public void testNormalPolicy() {
        String objectName = "test-callback.txt";
        String policyName = "test1";
        String callbackContent = "{\"callbackUrl\":\"www.abc.com/callback\",\"callbackBody\":\"${etag}\"}";
        String callbackVarContent = "{\"x:var1\":\"value1\",\"x:var2\":\"value2\"}";

        String policyName2 = "test_2";
        String callbackContent2 = "{\"callbackUrl\":\"www.bbc.com/index.html\",\"callbackHost\":\"www.bbc.com\",\"callbackBody\":\"{\\\"mimeType\\\":${mimeType},\\\"size\\\":${size}}\"}";
        String callbackVarContent2 = "{\"x:a\":\"a\", \"x:b\":\"b\"}";

        try {
            SetBucketCallbackPolicyRequest setBucketCallbackPolicyRequest = new SetBucketCallbackPolicyRequest(bucketName);
            // Set empty policy
            ossClient.setBucketCallbackPolicy(setBucketCallbackPolicyRequest);

            String callback = BinaryUtil.toBase64String(callbackContent.getBytes());
            String callback2 = BinaryUtil.toBase64String(callbackContent2.getBytes());
            String callbackVar = BinaryUtil.toBase64String(callbackVarContent.getBytes());
            String callbackVar2 = BinaryUtil.toBase64String(callbackVarContent2.getBytes());

            List<PolicyCallbackItem> policyCallbackItems = new ArrayList<PolicyCallbackItem>();
            PolicyCallbackItem policyCallbackItem = new PolicyCallbackItem(policyName, callback).withCallbackVar(callbackVar);

            PolicyCallbackItem policyCallbackItem2 = new PolicyCallbackItem(policyName2, callback2).withCallbackVar(callbackVar2);
            policyCallbackItem2.setPolicyName(policyName2);
            policyCallbackItem2.setCallback(callback2);
            policyCallbackItem2.setCallbackVar(callbackVar2);

            policyCallbackItems.add(policyCallbackItem);
            policyCallbackItems.add(policyCallbackItem2);

            SetBucketCallbackPolicyRequest setBucketCallbackPolicyRequest2 = new SetBucketCallbackPolicyRequest(bucketName).withPolicyCallbackItems(policyCallbackItems);

            // Set callback policy
            VoidResult setResult = ossClient.setBucketCallbackPolicy(setBucketCallbackPolicyRequest2);
            Assert.assertEquals(200, setResult.getResponse().getStatusCode());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream("hello world".getBytes()));

            String callbackText = BinaryUtil.toBase64String("{\"callbackPolicy\":\"test_2\"} ".getBytes());
            Map<String, String> header = new HashMap<String, String>();
            header.put("x-oss-callback", callbackText);
            putObjectRequest.setHeaders(header);
            ossClient.putObject(putObjectRequest);

            // Get callback policy
            GetBucketCallbackPolicyResult result2 = ossClient.getBucketCallbackPolicy(new GenericRequest(bucketName));
            Assert.assertEquals(200, result2.getResponse().getStatusCode());
            Assert.assertEquals(2, result2.getPolicyCallbackItems().size());
            Assert.assertEquals(policyName, result2.getPolicyCallbackItems().get(0).getPolicyName());
            Assert.assertEquals(callback, result2.getPolicyCallbackItems().get(0).getCallback());
            Assert.assertEquals(callbackVar, result2.getPolicyCallbackItems().get(0).getCallbackVar());
            Assert.assertEquals(policyName2, result2.getPolicyCallbackItems().get(1).getPolicyName());
            Assert.assertEquals(callback2, result2.getPolicyCallbackItems().get(1).getCallback());
            Assert.assertEquals(callbackVar2, result2.getPolicyCallbackItems().get(1).getCallbackVar());

            // Delete callback policy
            VoidResult delResult = ossClient.deleteBucketCallbackPolicy(new GenericRequest(bucketName));
            Assert.assertEquals(204, delResult.getResponse().getStatusCode());

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            GetBucketCallbackPolicyResult getResult = ossClient.getBucketCallbackPolicy(new GenericRequest(bucketName));
            Assert.assertEquals(0, getResult.getPolicyCallbackItems().size());

        } catch (OSSException e) {
            Assert.assertEquals("BucketCallbackPolicyNotExist", e.getErrorCode());
        }
    }
}
