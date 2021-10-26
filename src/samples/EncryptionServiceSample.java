import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;


public class EncryptionServiceSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";
    private static String objectName = "*** Provide object name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 配置Bucket加密
            setBucketEncryption(ossClient);

            // 获取Bucket加密配置
            getBucketEncryption(ossClient);

            // 删除Bucket加密配置
            deleteBucketEncryption(ossClient);

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

    private static void setBucketEncryption(OSS ossClient) {
        // 设置Bucket加密。
        ServerSideEncryptionByDefault applyServerSideEncryptionByDefault = new ServerSideEncryptionByDefault(SSEAlgorithm.KMS);
        applyServerSideEncryptionByDefault.setKMSMasterKeyID("<yourTestKmsId>");
        ServerSideEncryptionConfiguration sseConfig = new ServerSideEncryptionConfiguration();
        sseConfig.setApplyServerSideEncryptionByDefault(applyServerSideEncryptionByDefault);
        SetBucketEncryptionRequest request = new SetBucketEncryptionRequest("<yourBucketName>", sseConfig);
        ossClient.setBucketEncryption(request);
    }

    private static void getBucketEncryption(OSS ossClient) {
        // 获取Bucket加密配置。
        ServerSideEncryptionConfiguration sseConfig = ossClient.getBucketEncryption("<yourBucketName>");
        System.out.println("get Algorithm: " + sseConfig.getApplyServerSideEncryptionByDefault().getSSEAlgorithm());
        System.out.println("get kmsid: " + sseConfig.getApplyServerSideEncryptionByDefault().getKMSMasterKeyID());
    }

    private static void deleteBucketEncryption(OSS ossClient) {
        // 删除Bucket加密配置。
        ossClient.deleteBucketEncryption("<yourBucketName>");
    }
}
