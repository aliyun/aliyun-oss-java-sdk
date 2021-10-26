import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

import java.io.IOException;


public class RestoreObjectSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";
    private static String objectName = "*** Provide object name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 解冻归档文件
            restoreStorageObject(ossClient);

            // 解冻冷归档文件
            resotreObject(ossClient);

        } catch (OSSException oe) {
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            /*
             * Do not forget to shut down the client finally to release all allocated resources.
             */
            ossClient.shutdown();
        }
    }

    private static void resotreObject(OSS ossClient) {
        // 如需创建冷归档类型的文件，请参考以下代码。
        // 创建PutObjectRequest对象。
        // PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream("<yourContent>".getBytes()));
        // 在metadata中指定文件的存储类型为冷归档类型。
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.ColdArchive.toString());
        // putObjectRequest.setMetadata(metadata);
        // 上传文件的同时设置文件的存储类型。
        // ossClient.putObject(putObjectRequest);

        // 设置解冻冷归档文件的优先级。
        // RestoreTier.RESTORE_TIER_EXPEDITED 表示1小时内完成解冻。
        // RestoreTier.RESTORE_TIER_STANDARD 表示2~5小时内完成解冻。
        // RestoreTier.RESTORE_TIER_BULK 表示5~12小时内完成解冻。
        RestoreJobParameters jobParameters = new RestoreJobParameters(RestoreTier.RESTORE_TIER_EXPEDITED);

        // 配置解冻参数，以设置5小时内解冻完成，解冻状态保持2天为例。
        // 第一个参数表示保持解冻状态的天数，默认是1天，此参数适用于解冻Archive（归档）与ColdArchive（冷归档）类型文件。
        // 第二个参数jobParameters表示解冻优先级，只适用于解冻ColdArchive类型文件。
        RestoreConfiguration configuration = new RestoreConfiguration(2, jobParameters);

        // 发起解冻请求。
        ossClient.restoreObject(bucketName, objectName, configuration);
    }

    private static void restoreStorageObject(OSS ossClient) throws InterruptedException, IOException {
        ObjectMetadata objectMetadata = ossClient.getObjectMetadata(bucketName, objectName);

        // 校验文件是否为归档文件。
        StorageClass storageClass = objectMetadata.getObjectStorageClass();
        if (storageClass == StorageClass.Archive) {
            // 解冻文件。
            ossClient.restoreObject(bucketName, objectName);

            // 等待解冻完成。
            do {
                Thread.sleep(1000);
                objectMetadata = ossClient.getObjectMetadata(bucketName, objectName);
            } while (!objectMetadata.isRestoreCompleted());
        }

        // 获取解冻文件。
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
        ossObject.getObjectContent().close();
    }
}
