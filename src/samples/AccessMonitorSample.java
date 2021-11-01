import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;


public class AccessMonitorSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 更新 Bucket 访问跟踪状态。
            putBucketAccessMonitor(ossClient);

            // 获取当前Bucket的访问跟踪的状态。
            getBucketAccessMonitor(ossClient);

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

    private static void putBucketAccessMonitor(OSS ossClient) {
        ossClient.putBucketAccessMonitor(bucketName, AccessMonitorStatus.Enabled);
    }

    private static void getBucketAccessMonitor(OSS ossClient) {
        // 获取当前Bucket的访问跟踪的状态。
        AccessMonitor config = ossClient.getBucketAccessMonitor(bucketName);

        // 打印Bucket的访问跟踪的状态。
        System.out.println("status: " + config.getStatus());
    }
}
