import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

import java.io.IOException;


public class StorageTypeSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";


    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 转换文件存储类型
            // 以下代码用于将Object的存储类型从标准或低频访问转换为归档类型
            changeStorageTypeObject(ossClient);

            // 转换文件存储类型
            // 以下代码用于将Object的存储类型从归档转换为低频访问类型
            changeStorageTypeObject2(ossClient);

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

    private static void changeStorageTypeObject(OSS ossClient) {
        // 本示例中的Bucket与Object需提前创建好, 且Object类型为标准或低频访问存储类型。
        String objectName = "*** Provide object name ***";
        // 创建CopyObjectRequest对象。
        CopyObjectRequest request = new CopyObjectRequest(bucketName, objectName, bucketName, objectName) ;

        // 创建ObjectMetadata对象。
        ObjectMetadata objectMetadata = new ObjectMetadata();

        // 封装header，此处以设置存储类型为归档类型为例。
        objectMetadata.setHeader("x-oss-storage-class", StorageClass.Archive);
        request.setNewObjectMetadata(objectMetadata);

        // 更改文件存储类型。
        CopyObjectResult result = ossClient.copyObject(request);
        System.out.println(result.getResponse().getStatusCode());
    }

    private static void changeStorageTypeObject2(OSS ossClient) throws InterruptedException, IOException {
        // 本示例中的Bucket与Object需提前创建好, 且Object类型为标准或低频访问存储类型。
        String objectName = "*** Provide object name ***";
        // 获取文件元信息。
        ObjectMetadata objectMetadata = ossClient.getObjectMetadata(bucketName, objectName);

        // 检查目标文件是否为归档类型。如果是，则需要先解冻才能更改存储类型。
        StorageClass storageClass = objectMetadata.getObjectStorageClass();
        System.out.println("storage type:" + storageClass);
        if (storageClass == StorageClass.Archive) {
            // 解冻文件。
            ossClient.restoreObject(bucketName, objectName);

            // 等待解冻完成。
            do {
                Thread.sleep(1000);
                objectMetadata = ossClient.getObjectMetadata(bucketName, objectName);
            } while (!objectMetadata.isRestoreCompleted());
        }

        // 创建CopyObjectRequest对象。
        CopyObjectRequest request = new CopyObjectRequest(bucketName, objectName, bucketName, objectName) ;

        // 创建ObjectMetadata对象。
        objectMetadata = new ObjectMetadata();

        // 封装header，此处以设置存储类型为低频访问类型为例。
        objectMetadata.setHeader("x-oss-storage-class", StorageClass.IA);
        request.setNewObjectMetadata(objectMetadata);

        // 更改文件存储类型。
        CopyObjectResult result = ossClient.copyObject(request);
        System.out.println(result.getResponse().getStatusCode());
    }
}
