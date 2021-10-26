import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;


public class SymLinkSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";
    private static String objectName = "*** Provide object name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 创建软链接
            createSymlink(ossClient);

            // 获取软链接指向的目标文件名称
            getSymlink(ossClient);

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

    private static void getSymlink(OSS ossClient) {
        String symLink = "<yourSymLink>";
        // 获取软链接。
        OSSSymlink symbolicLink = ossClient.getSymlink(bucketName, symLink);
        // 打印软链接指向的文件内容。
        System.out.println(symbolicLink.getSymlink());
        System.out.println(symbolicLink.getTarget());
        System.out.println(symbolicLink.getRequestId());
    }

    private static void createSymlink(OSS ossClient) {
        String symLink = "<yourSymLink>";
        String destinationObjectName = "<yourDestinationObjectName>";
        // 创建上传文件元信息。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/plain");
        // 设置自定义元信息property的值为property-value。
        metadata.addUserMetadata("property", "property-value");

        // 创建CreateSymlinkRequest。
        CreateSymlinkRequest createSymlinkRequest = new CreateSymlinkRequest(bucketName, symLink, destinationObjectName);

        // 设置元信息。
        createSymlinkRequest.setMetadata(metadata);

        // 创建软链接。
        ossClient.createSymlink(createSymlinkRequest);
    }
}
