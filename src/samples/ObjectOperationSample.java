import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

import java.io.ByteArrayInputStream;


public class ObjectOperationSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";
    private static String objectName = "<yourObjectName>";
    private static String sourceBucketName = "<yourSourceBucketName>";
    private static String sourceObjectName = "<yourSourceObjectName>";
    private static String destinationBucketName = "<yourDestinationBucketName>";
    private static String destinationObjectName = "<yourDestinationObjectName>";
    private static OSS ossClient = null;

    public static void main(String[] args) {
        // 创建OSSClient实例。
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 禁止覆盖同名文件-上传文件。
            putObject();

            // 禁止覆盖同名文件-拷贝文件。
            copyObject();

            // 重命名文件。
            renameObject();
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

    public static void putObject() {
        String content = "Hello OSS!";
        // 创建PutObjectRequest对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream(content.getBytes()));

        // 指定上传文件操作时是否覆盖同名Object。
        // 不指定x-oss-forbid-overwrite时，默认覆盖同名Object。
        // 指定x-oss-forbid-overwrite为false时，表示允许覆盖同名Object。
        // 指定x-oss-forbid-overwrite为true时，表示禁止覆盖同名Object，如果同名Object已存在，程序将报错。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader("x-oss-forbid-overwrite", "true");
        putObjectRequest.setMetadata(metadata);

        // 上传文件。
        ossClient.putObject(putObjectRequest);
    }

    public static void copyObject() {
        // 创建CopyObjectRequest对象。
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourceBucketName, sourceObjectName, destinationBucketName, destinationObjectName);

        // 设置新的文件元信息。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/html");
        // 指定拷贝文件操作时是否覆盖同名Object。
        // 不指定x-oss-forbid-overwrite时，默认覆盖同名Object
        // 指定x-oss-forbid-overwrite为false时，表示允许覆盖同名Object。
        // 指定x-oss-forbid-overwrite为true时，表示禁止覆盖同名Object，如果同名Object已存在，程序将报错。
        metadata.setHeader("x-oss-forbid-overwrite", "true");
        copyObjectRequest.setNewObjectMetadata(metadata);

        // 拷贝文件。
        CopyObjectResult result = ossClient.copyObject(copyObjectRequest);
        System.out.println("ETag: " + result.getETag() + " LastModified: " + result.getLastModified());
    }

    public static void renameObject() {
        // 填写源Object绝对路径。Object绝对路径中不能包含Bucket名称。
        String sourceObject = "exampleobject.txt";
        // 填写与源Object处于同一Bucket中的目标Object绝对路径。Object绝对路径中不能包含Bucket名称。
        String destnationObject = "newexampleobject.txt";

        // 将存储空间中的源Object绝对路径重命名为目标Object绝对路径。
        RenameObjectRequest renameObjectRequest = new RenameObjectRequest(bucketName, sourceObject, destnationObject);
        ossClient.renameObject(renameObjectRequest);
    }
}

