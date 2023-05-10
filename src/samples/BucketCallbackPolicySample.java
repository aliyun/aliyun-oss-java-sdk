package samples;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;

public class BucketCallbackPolicySample {
    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";

    private static String bucketName = "*** Provide bucket name ***";
    private static String key = "*** Provide key ***";

    // callback policy name
    private static String policyName = "test1";
    // callback content
    private static String callbackContent = "{\"callbackUrl\":\"www.abc.com/callback\",\"callbackBody\":\"${etag}\"}";
    // custom callback parameters
    private static String callbackVarContent = "{\"x:var1\":\"value1\",\"x:var2\":\"value2\"}";

    // callback policy name
    private static String policyName2 = "test_2";
    // callback content
    private static String callbackContent2 = "{\"callbackUrl\":\"www.bbc.com/index.html\",\"callbackHost\":\"www.bbc.com\",\"callbackBody\":\"{\\\"mimeType\\\":${mimeType},\\\"size\\\":${size}}\"}";
    // custom callback parameters
    private static String callbackVarContent2 = "{\"x:a\":\"a\", \"x:b\":\"b\"}";

    public static void main(String[] args) throws Throwable {

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

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
            System.out.println("set bucket callback policy status: "+setResult.getResponse().getStatusCode());

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, new ByteArrayInputStream("hello world".getBytes()));

            // Upload File Trigger Callback
            String callbackText = BinaryUtil.toBase64String("{\"callbackPolicy\":\"test_2\"} ".getBytes());
            Map<String, String> header = new HashMap<String, String>();
            header.put("x-oss-callback", callbackText);
            putObjectRequest.setHeaders(header);
            ossClient.putObject(putObjectRequest);

            // Get callback policy
            GetBucketCallbackPolicyResult getResult = ossClient.getBucketCallbackPolicy(new GenericRequest(bucketName));
            System.out.println("get bucket callback policy status: "+getResult.getResponse().getStatusCode());
            System.out.println("PolicyName: "+getResult.getPolicyCallbackItems().get(0).getPolicyName());
            System.out.println("Callback: "+getResult.getPolicyCallbackItems().get(0).getCallback());
            System.out.println("CallbackVar: "+getResult.getPolicyCallbackItems().get(0).getCallbackVar());

            // Delete callback policy
            VoidResult delResult = ossClient.deleteBucketCallbackPolicy(new GenericRequest(bucketName));
            System.out.println("delete bucket callback policy status: "+delResult.getResponse().getStatusCode());

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        } finally {
            /*
             * Do not forget to shut down the client finally to release all allocated resources.
             */
            ossClient.shutdown();
        }
    }

}