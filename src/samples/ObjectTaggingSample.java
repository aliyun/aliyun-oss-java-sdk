import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;


public class ObjectTaggingSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";
    private static String objectName = "*** Provide object name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 上传Object时添加对象标签
            setObjectTagging(ossClient);

            // 为已上传Object添加或更改对象标签
            updateObjectTagging(ossClient);

            // 为Object指定版本添加或更改对象标签
            setObjectTaggingByVersion(ossClient);

            // 为软链接文件设置对象标签
            setSymlinkTagging(ossClient);

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

    private static void setObjectTagging(OSS ossClient) {
        Map<String, String> tags = new HashMap<String, String>();
        // 依次填写对象标签的键（例如owner）和值（例如John）。
        tags.put("owner", "John");
        tags.put("type", "document");

        // 在HTTP header中设置标签信息。
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setObjectTagging(tags);

        // 上传文件的同时设置标签信息。
        String content = "<yourtContent>";
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()), metadata);

    }

    private static void updateObjectTagging(OSS ossClient) {
        Map<String, String> tags = new HashMap<String, String>();
        // 依次填写对象标签的键（例如owner）和值（例如John）。
        tags.put("owner", "John");
        tags.put("type", "document");

        // 为文件设置标签。
        ossClient.setObjectTagging(bucketName, objectName, tags);
    }

    private static void setObjectTaggingByVersion(OSS ossClient) {
        // 填写Object的版本ID，例如CAEQMxiBgICAof2D0BYiIDJhMGE3N2M1YTI1NDQzOGY5NTkyNTI3MGYyMzJm****。
        String versionId = "CAEQMxiBgICAof2D0BYiIDJhMGE3N2M1YTI1NDQzOGY5NTkyNTI3MGYyMzJm****";
        Map<String, String> tags = new HashMap<String, String>(1);
        // 依次填写对象标签的键（例如owner）和值（例如John）。
        tags.put("owner", "John");
        tags.put("type", "document");

        SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(bucketName, objectName, tags);
        setObjectTaggingRequest.setVersionId(versionId);
        ossClient.setObjectTagging(setObjectTaggingRequest);
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
