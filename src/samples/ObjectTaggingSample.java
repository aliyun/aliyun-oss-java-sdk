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
        //  Create an OSSClient instance.
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // Add tags to an object when you upload the object
            setObjectTagging(ossClient);

            // Add tags to or modify the tags of an existing object
            addObjectTaggingeExisting(ossClient);

            // Add tags to or modify the tags of a specified version of an object
            setObjectTaggingByVersion(ossClient);

            // Add tags to a symbolic link
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
        // Specify the key and value of the object tag. For example, set the key to owner and the value to John.
        tags.put("owner", "John");
        tags.put("type", "document");

        // Configure the tags in the HTTP header.
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setObjectTagging(tags);

        // Upload the object and add tags to it.
        String content = "<yourtContent>";
        ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(content.getBytes()), metadata);

    }

    private static void addObjectTaggingeExisting(OSS ossClient) {
        Map<String, String> tags = new HashMap<String, String>();
        // Specify the key and value of the object tag. For example, set the key to owner and the value to John.
        tags.put("owner", "John");
        tags.put("type", "document");

        // Add tags to the object.
        ossClient.setObjectTagging(bucketName, objectName, tags);
    }

    private static void setObjectTaggingByVersion(OSS ossClient) {
        // Specify the version ID of the object. Example: CAEQMxiBgICAof2D0BYiIDJhMGE3N2M1YTI1NDQzOGY5NTkyNTI3MGYyMzJm****.
        String versionId = "CAEQMxiBgICAof2D0BYiIDJhMGE3N2M1YTI1NDQzOGY5NTkyNTI3MGYyMzJm****";
        Map<String, String> tags = new HashMap<String, String>(1);
        // Specify the key and value of the object tag. For example, set the key to owner and the value to John.
        tags.put("owner", "John");
        tags.put("type", "document");

        SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(bucketName, objectName, tags);
        setObjectTaggingRequest.setVersionId(versionId);
        ossClient.setObjectTagging(setObjectTaggingRequest);
    }

    private static void setSymlinkTagging(OSS ossClient) {
        // Specify the full path of the symbolic link. Example: shortcut/myobject.txt.
        String symLink = "shortcut/myobject.txt";
        // Specify the full path of the object. Example: exampledir/exampleobject.txt. The full path of the object cannot contain the bucket name.
        String destinationObjectName = "exampledir/exampleobject.txt";
        // Configure the tags to be added to the symbolic link.
        Map<String, String> tags = new HashMap<String, String>();
        // Specify the key and value of the object tag. For example, set the key to owner and the value to John.
        tags.put("owner", "John");
        tags.put("type", "document");

        // Create metadata for the object to upload.
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setObjectTagging(tags);

        // Create a request to create the symbolic link.
        CreateSymlinkRequest createSymlinkRequest = new CreateSymlinkRequest(bucketName, symLink, destinationObjectName);

        // Set the object metadata.
        createSymlinkRequest.setMetadata(metadata);

        // Create the symbolic link.
        ossClient.createSymlink(createSymlinkRequest);

        //  View the tags added to the symbolic link.
        TagSet tagSet = ossClient.getObjectTagging(bucketName, symLink);
        Map<String, String> getTags = tagSet.getAllTags();
        System.out.println("symLink tagging: "+ getTags.toString());
    }
}
