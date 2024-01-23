package samples;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

public class BucketDataRedundancyTransitionSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";

    public static void main(String[] args) {
        // Create an OSSClient instance.
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // Create a storage redundancy conversion task for the bucket.
            CreateBucketDataRedundancyTransitionResult createResult = ossClient.createBucketDataRedundancyTransition(bucketName, "ZRS");

            // Obtain storage redundancy conversion tasks.
            GetBucketDataRedundancyTransitionRequest getBucketDataRedundancyTransitionRequest = new GetBucketDataRedundancyTransitionRequest(bucketName, createResult.getTaskId());
            GetBucketDataRedundancyTransitionResult getResult = ossClient.getBucketDataRedundancyTransition(getBucketDataRedundancyTransitionRequest);
            System.out.println(getResult.getTaskId());
            System.out.println(getResult.getCreateTime());
            System.out.println(getResult.getStatus());
            System.out.println(getResult.getStartTime());
            System.out.println(getResult.getEndTime());
            System.out.println(getResult.getProcessPercentage());
            System.out.println(getResult.getEstimatedRemainingTime());

            // List all storage redundancy conversion tasks of the requester.
            ListUserDataRedundancyTransitionRequest request = new ListUserDataRedundancyTransitionRequest();
            ListUserDataRedundancyTransitionResult listUserResult = ossClient.listUserDataRedundancyTransition(request);
            System.out.println(listUserResult.getTruncated());
            System.out.println(listUserResult.getNextContinuationToken());
            System.out.println(listUserResult.getBucketDataRedundancyTransition().get(0).getBucket());
            System.out.println(listUserResult.getBucketDataRedundancyTransition().get(0).getTaskId());
            System.out.println(listUserResult.getBucketDataRedundancyTransition().get(0).getStatus());
            System.out.println(listUserResult.getBucketDataRedundancyTransition().get(0).getCreateTime());
            System.out.println(listUserResult.getBucketDataRedundancyTransition().get(0).getStartTime());
            System.out.println(listUserResult.getBucketDataRedundancyTransition().get(0).getEndTime());
            System.out.println(listUserResult.getBucketDataRedundancyTransition().get(0).getEstimatedRemainingTime());
            System.out.println(listUserResult.getBucketDataRedundancyTransition().get(0).getProcessPercentage());

            // List all storage redundancy conversion tasks under a certain bucket.
            List<GetBucketDataRedundancyTransitionResult> list = ossClient.listBucketDataRedundancyTransition(bucketName);
            System.out.println(list.get(0).getBucket());
            System.out.println(list.get(0).getTaskId());
            System.out.println(list.get(0).getStatus());
            System.out.println(list.get(0).getCreateTime());
            System.out.println(list.get(0).getStartTime());
            System.out.println(list.get(0).getEndTime());
            System.out.println(list.get(0).getProcessPercentage());
            System.out.println(list.get(0).getEstimatedRemainingTime());

            // Delete storage redundancy conversion task.
            VoidResult delResult = ossClient.deleteBucketDataRedundancyTransition(bucketName, getResult.getTaskId());

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
