import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.TransferAcceleration;


public class BucketTransferAccelerationSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 设置Bucket的传输加速状态。
            // 当设置enabled为true时，表示开启传输加速；当设置enabled为false时，表示关闭传输加速。
            boolean enabled = true;
            ossClient.setBucketTransferAcceleration(bucketName, enabled);

            // 查询Bucket的传输加速状态。
            // 如果返回值为true，则Bucket已开启传输加速功能；如果返回值为false，则Bucket的传输加速功能为关闭状态。
            TransferAcceleration result = ossClient.getBucketTransferAcceleration(bucketName);
            System.out.println("Is transfer acceleration enabled:"+ result.isEnabled());

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
