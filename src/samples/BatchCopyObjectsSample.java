import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class BatchCopyObjectsSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";
    private static String key = "*** Provide object name ***";
    private static String targetKey = "*** Provide target object name ***";

    public static void main(String[] args) {
        // Create an OSSClient instance.
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // batch copy a batch of objects in the same bucket.
            byte[] content = { 'A', 'l', 'i', 'y', 'u', 'n' };
            ossClient.putObject(bucketName, key, new ByteArrayInputStream(content));

            CopyObjectsRequest copyObjectsRequest = new CopyObjectsRequest();
            List<CopyObjects> objects = new ArrayList<CopyObjects>();
            CopyObjects obj1 = new CopyObjects().withSourceKey(key).withTargetKey(targetKey);
            objects.add(obj1);
            copyObjectsRequest.setObjects(objects);
            copyObjectsRequest.setBucketName(bucketName);
            CopyObjectsResult copyObjectsResult = ossClient.copyObjects(copyObjectsRequest);
            System.out.println(copyObjectsResult.getSuccessObjects().get(0).getETag());
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
