import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;


public class BucketPolicySample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 填写policyText。
            String policyText = "{\"Statement\": [{\"Effect\": \"Allow\", \"Action\": [\"oss:GetObject\", \"oss:ListObjects\"], \"Resource\": [\"acs:oss:*:*:*/user1/*\"]}], \"Version\": \"1\"}";
            // 设置授权策略。
            ossClient.setBucketPolicy(bucketName, policyText);

            // 获取授权策略。
            GetBucketPolicyResult result = ossClient.getBucketPolicy(bucketName);
            System.out.println("policyText:"+ result.getPolicyText());

            // 删除授权策略。
            ossClient.deleteBucketPolicy(bucketName);

        } catch (OSSException oe) {
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
        } finally {
            /*
             * Do not forget to shut down the client finally to release all allocated resources.
             */
            ossClient.shutdown();
        }
    }
}
