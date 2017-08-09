package samples;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;

/**
 * Examples about metadata information get and set.
 * 
 */
public class ObjectMetaSample {
    
    private static String endpoint = "<endpoint, http://oss-cn-hangzhou.aliyuncs.com>";
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String bucketName = "<bucketName>";
    private static String key = "<key>";
    private static String content = "Hello OSS";
    
    
    public static void main(String[] args) throws IOException {
        /*
         * Constructs a client instance with your account for accessing OSS
         */
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
                
        try {
            ObjectMetadata meta = new ObjectMetadata();
            
            // Sets the content type.
            meta.setContentType("text/plain");
            // Sets the MD5 data---please update it with the actual value.
            meta.setContentMD5("");
            // Sets the custom metadata.
            meta.addUserMetadata("meta", "meta-value");
            
            // Uploads the file
            ossClient.putObject(bucketName, key, 
                    new ByteArrayInputStream(content.getBytes()), meta);
           
            // Gets the object metadata information.
            ObjectMetadata metadata = ossClient.getObjectMetadata(bucketName, key);
            System.out.println(metadata.getContentType());
            System.out.println(metadata.getLastModified());
            System.out.println(metadata.getUserMetadata().get("meta")); 
            
            ossClient.deleteObject(bucketName, key);
            
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        } finally {
            /*
             * Do not forget to shut down the client finally to release all allocated resources.
             */
            ossClient.shutdown();
        }
    }
    
}
