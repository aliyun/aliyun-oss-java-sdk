package samples;

import com.aliyun.oss.*;
import com.aliyun.oss.model.*;
import com.aliyun.oss.integrationtests.TestConfig;

public class AccessPointSample {
    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";

    public static void main(String[] args) throws InterruptedException {

        /*
         * Constructs a client instance with your account for accessing OSS
         */
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            String accessPointName = "test-ap-jt-3";
            String networkOrigin = "internet";
            String accessPointPolicy = "{\"Version\":\"1\",\"Statement\":[{\"Action\":[\"oss:PutObject\",\"oss:GetObject\"],\"Effect\":\"Deny\",\"Principal\":[\""+TestConfig.OSS_TEST_USER_ID+"\"],\"Resource\":[\"acs:oss:"+TestConfig.OSS_TEST_REGION+":"+TestConfig.OSS_TEST_USER_ID+":accesspoint/"+accessPointName+"\",\"acs:oss:"+TestConfig.OSS_TEST_REGION+":"+TestConfig.OSS_TEST_USER_ID+":accesspoint/"+accessPointName+"/object/*\"]}]}";

            CreateAccessPointRequest createAccessPointRequest = new CreateAccessPointRequest(bucketName)
                    .withAccessPointName(accessPointName)
                    .withNetworkOrigin(networkOrigin)
                    .withVpc(new AccessPointVpcConfiguration());
            CreateAccessPointResult createReturn = ossClient.createAccessPoint(createAccessPointRequest);
            System.out.println("The status code for create access point is :" + createReturn.getResponse().getStatusCode());

            GetAccessPointRequest getAccessPointRequest = new GetAccessPointRequest(bucketName).withAccessPointName(accessPointName);
            GetAccessPointResult getReturn = ossClient.getAccessPoint(getAccessPointRequest);
            System.out.println("The status code for get access point is :" + getReturn.getResponse().getStatusCode());
            System.out.println("Access point name is :" + getReturn.getAccessPointName());
            System.out.println("Vpc id is :" + getReturn.getVpc());


            ListAccessPointsRequest listAccessPointsRequest = new ListAccessPointsRequest()
                    .withMaxKeys(10);
            ListAccessPointsResult listUserReturn = ossClient.listAccessPoints(listAccessPointsRequest);
            System.out.println("The status code for list access point is :" + listUserReturn.getResponse().getStatusCode());
            System.out.println("Access point name is :" + listUserReturn.getAccessPoints().get(0).getAccessPointName());
            System.out.println("Vpc id is :" + listUserReturn.getAccessPoints().get(0).getVpc());

            ListBucketAccessPointsRequest listBucketAccessPointsRequest = new ListBucketAccessPointsRequest(bucketName)
                    .withMaxKeys(10);
            ListAccessPointsResult listBucketReturn = ossClient.listBucketAccessPoints(listBucketAccessPointsRequest);
            System.out.println("The status code for list access point is :" + listBucketReturn.getResponse().getStatusCode());
            System.out.println("Access point name is :" + listBucketReturn.getAccessPoints().get(0).getAccessPointName());
            System.out.println("Vpc id is :" + listBucketReturn.getAccessPoints().get(0).getVpc());

            PutAccessPointPolicyRequest putAccessPointPolicyRequest = new PutAccessPointPolicyRequest(bucketName)
                    .withAccessPointName(accessPointName)
                    .withAccessPointPolicy(accessPointPolicy);
            VoidResult createPolicyReturn = ossClient.putAccessPointPolicy(putAccessPointPolicyRequest);
            System.out.println("The status code for put access point policy is :" + createPolicyReturn.getResponse().getStatusCode());

            GetAccessPointPolicyRequest getAccessPointPolicyRequest = new GetAccessPointPolicyRequest(bucketName).withAccessPointName(accessPointName);
            GetAccessPointPolicyResult getPolicyReturn = ossClient.getAccessPointPolicy(getAccessPointPolicyRequest);
            System.out.println("The status code for get access point policy is :" + getPolicyReturn.getResponse().getStatusCode());
            System.out.println("Access point policy is :" + getPolicyReturn.getAccessPointPolicy());

            DeleteAccessPointRequest deleteAccessPointRequest = new DeleteAccessPointRequest(bucketName).withAccessPointName(accessPointName);
            VoidResult delReturn = ossClient.deleteAccessPoint(deleteAccessPointRequest);
            System.out.println("The status code for delete access point is :" + delReturn.getResponse().getStatusCode());

            DeleteAccessPointPolicyRequest deleteAccessPointPolicyRequest = new DeleteAccessPointPolicyRequest(bucketName).withAccessPointName(accessPointName);
            VoidResult delPolicyReturn = ossClient.deleteAccessPointPolicy(deleteAccessPointPolicyRequest);
            System.out.println("The status code for delete access point policy is :" + delPolicyReturn.getResponse().getStatusCode());
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getMessage());
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