import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;


public class BucketVersioningSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";
    private static String objectName = "*** Provide object name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 设置Bucket版本控制状态
            setBucketVersioning(ossClient);

            // 获取Bucket版本控制状态信息
            getBucketVersioning(ossClient);

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

    private static void setBucketVersioning(OSS ossClient) {
        // 设置存储空间版本控制状态为Enabled。
        BucketVersioningConfiguration configuration = new BucketVersioningConfiguration();
        configuration.setStatus(BucketVersioningConfiguration.ENABLED);
        SetBucketVersioningRequest request = new SetBucketVersioningRequest(bucketName, configuration);
        ossClient.setBucketVersioning(request);
    }

    private static void getBucketVersioning(OSS ossClient) {
        // 获取存储空间版本控制状态信息。
        BucketVersioningConfiguration versionConfiguration = ossClient.getBucketVersioning("<yourBucketName>");
        System.out.println("bucket versioning status: " + versionConfiguration.getStatus());

    }
}
