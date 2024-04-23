package samples;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetBucketPublicAccessBlockResult;
import com.aliyun.oss.model.GetPublicAccessBlockResult;
import com.aliyun.oss.model.PutBucketPublicAccessBlockRequest;
import com.aliyun.oss.model.PutPublicAccessBlockRequest;

public class PublicAccessBlockSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";

    public static void main(String[] args) throws InterruptedException {
        // Create an OSSClient instance.
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // Set user level to block public access
            PutPublicAccessBlockRequest request = new PutPublicAccessBlockRequest()
                    .withBlockPublicAccess(true);
            ossClient.putPublicAccessBlock(request);

            Thread.sleep(10000);

            // Get user level to block public access
            GetPublicAccessBlockResult getResult = ossClient.getPublicAccessBlock();
            System.out.println("Public access status is:"+ getResult.getBlockPublicAccess());

            // Delete user level to block public access
            ossClient.deletePublicAccessBlock();

            // Set bucket level to block public access
            PutBucketPublicAccessBlockRequest request2 = new PutBucketPublicAccessBlockRequest(bucketName)
                    .withBlockPublicAccess(true);
            ossClient.putBucketPublicAccessBlock(request2);

            Thread.sleep(10000);

            // Get bucket level to block public access
            GetBucketPublicAccessBlockResult getResult2 = ossClient.getBucketPublicAccessBlock(bucketName);
            System.out.println("Public access status is:"+ getResult2.getBlockPublicAccess());

            // Delete bucket level to block public access
            ossClient.deleteBucketPublicAccessBlock(bucketName);
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
