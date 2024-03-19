package samples;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import java.util.ArrayList;
import java.util.List;

public class BucketHttpsConfigSample {
    private static String endpoint = "<endpoint, http://oss-cn-hangzhou.aliyuncs.com>";
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String bucketName = "<bucketName>";

    public static void main(String[] args) {
        /*
         * Constructs a client instance with your account for accessing OSS
         */
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // Set bucket https config
            List<String> tlsVersion = new ArrayList<String>();
            tlsVersion.add("TLSv1.2");
            tlsVersion.add("TLSv1.3");

            PutBucketHttpsConfigRequest request = new PutBucketHttpsConfigRequest(bucketName)
                    .withEnabled(true)
                    .withTlsVersion(tlsVersion);

            ossClient.putBucketHttpsConfig(request);

            // Get bucket https config
            GetBucketHttpsConfigResult result = ossClient.getBucketHttpsConfig(bucketName);
            System.out.println("Enable:" + result.isEnable());
            System.out.println("TLSVersion:" + result.getTlsVersion().get(0));
            System.out.println("TLSVersion:" + result.getTlsVersion().get(1));
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorMessage());
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
