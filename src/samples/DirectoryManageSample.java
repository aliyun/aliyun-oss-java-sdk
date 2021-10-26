import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;


public class DirectoryManageSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";
    private static String objectName = "*** Provide object name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建目录
            createDirectory(ossClient);

            // 重命名目录
            renameDirectory(ossClient);

            // 删除目录
            deleteDirectory(ossClient);

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

    private static void createDirectory(OSS ossClient) {
        // 填写目录绝对路径。目录绝对路径中不能包含Bucket名称。
        String directoryName = "exampledir";
        // 创建目录。
        CreateDirectoryRequest createDirectoryRequest = new CreateDirectoryRequest(bucketName, directoryName);
        ossClient.createDirectory(createDirectoryRequest);
    }

    private static void renameDirectory(OSS ossClient) {
        // 填写源目录绝对路径。目录绝对路径中不能包含Bucket名称。
        String sourceDir = "exampledir";
        // 填写与源目录处于同一Bucket中的目标目录绝对路径。目录绝对路径中不能包含Bucket名称。
        String destnationDir = "newexampledir";

        // 将存储空间中的源目录绝对路径重命名为目标目录绝对路径。
        RenameObjectRequest renameObjectRequest = new RenameObjectRequest(bucketName, sourceDir, destnationDir);
        ossClient.renameObject(renameObjectRequest);
    }

    private static void deleteDirectory(OSS ossClient) {
        // 填写目录绝对路径。目录绝对路径中不能包含Bucket名称。
        String directoryName = "exampledir";

        // 删除目录, 默认为非递归删除。请确保已清空该目录下的文件和子目录。
        DeleteDirectoryRequest deleteDirectoryRequest = new DeleteDirectoryRequest(bucketName, directoryName);
        DeleteDirectoryResult deleteDirectoryResult = ossClient.deleteDirectory(deleteDirectoryRequest);

        // 删除的目录绝对路径。
        System.out.println("delete dir name :" + deleteDirectoryResult.getDirectoryName());
        // 本次删除的文件和目录的总数量。
        System.out.println("delete number:" + deleteDirectoryResult.getDeleteNumber());

    }
}
