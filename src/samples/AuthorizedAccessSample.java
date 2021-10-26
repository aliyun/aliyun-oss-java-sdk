import com.aliyun.oss.*;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.aliyun.oss.internal.OSSHeaders.OSS_USER_METADATA_PREFIX;


public class AuthorizedAccessSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";
    private static String objectName = "*** Provide object name ***";
    // 从STS服务获取的安全令牌（SecurityToken）。
    private static String securityToken = "yourSecurityToken";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, securityToken);

        try {
            // 使用STS进行临时授权
            createSTSAuthorization(ossClient);

            // 使用签名url进行临时授权
            createSignedUrl(ossClient);

            // 生成单个以其他HTTP方法访问的签名URL
            createSignedUrlWithParamer(ossClient);

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

    private static void createSTSAuthorization(OSS ossClient) {
        // 执行OSS相关操作，例如上传、下载文件等。
        // 上传文件，此处以上传本地文件为例介绍。
        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new File("D:\\localpath\\examplefile.txt"));
        ossClient.putObject(putObjectRequest);

        // 下载OSS文件到本地文件。如果指定的本地文件存在则覆盖，不存在则新建。
        // 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
        //ossClient.getObject(new GetObjectRequest(bucketName, objectName), new File("D:\\localpath\\examplefile.txt"));
    }

    private static void createSignedUrl(OSS ossClient) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName, HttpMethod.PUT);
        // 设置签名URL过期时间为3600秒（1小时）。
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        request.setExpiration(expiration);
        // 设置ContentType。
        request.setContentType("text/plain");
        // 设置自定义元信息。
        request.addUserMetadata("author", "aliy");

        // 生成签名URL。
        URL signedUrl = ossClient.generatePresignedUrl(request);
        System.out.println(signedUrl);

        Map<String, String> requestHeaders = new HashMap<String, String>();
        // 设置ContentType，必须和生成签名URL时设置的ContentType一致。
        requestHeaders.put(HttpHeaders.CONTENT_TYPE, "text/plain");
        // 设置自定义元信息。
        requestHeaders.put(OSS_USER_METADATA_PREFIX + "author", "aliy");

        // 使用签名URL上传文件。
        ossClient.putObject(signedUrl, new ByteArrayInputStream("Hello OSS".getBytes()), -1, requestHeaders, true);

    }

    private static void createSignedUrlWithParamer(OSS ossClient) {
        // 创建请求。
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectName);
        // 设置HttpMethod为PUT。
        generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
        // 添加用户自定义元信息。
        generatePresignedUrlRequest.addUserMetadata("author", "baymax");
        // 设置ContentType。
        generatePresignedUrlRequest.setContentType("application/txt");
        // 设置签名URL过期时间为3600秒（1小时）。
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        generatePresignedUrlRequest.setExpiration(expiration);
        // 生成签名URL。
        URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
        System.out.println(url);
    }

    private static void setSymlinkTagging(OSS ossClient) {
        // 填写软链接完整路径，例如shortcut/myobject.txt。
        String symLink = "shortcut/myobject.txt";
        // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
        String destinationObjectName = "exampledir/exampleobject.txt";
        // 设置软链接的标签信息。
        Map<String, String> tags = new HashMap<String, String>();
        // 依次填写对象标签的键（例如owner）和值（例如John）。
        tags.put("owner", "John");
        tags.put("type", "document");

        // 创建上传文件元信息。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setObjectTagging(tags);

        // 创建CreateSymlinkRequest。
        CreateSymlinkRequest createSymlinkRequest = new CreateSymlinkRequest(bucketName, symLink, destinationObjectName);

        // 设置元信息。
        createSymlinkRequest.setMetadata(metadata);

        // 创建软链接。
        ossClient.createSymlink(createSymlinkRequest);

        // 查看软链接的标签信息。
        TagSet tagSet = ossClient.getObjectTagging(bucketName, symLink);
        Map<String, String> getTags = tagSet.getAllTags();
        System.out.println("symLink tagging: "+ getTags.toString());
    }
}
