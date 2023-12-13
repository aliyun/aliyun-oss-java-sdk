package samples;

import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.*;

public class SignV4Sample {
    private static String region = "<region, cn-chengdu";
    private static String endpoint = "<endpoint, http://oss-cn-hangzhou.aliyuncs.com>";
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String bucketName = "<bucketName>";
    private static final String saveAsKey = "<syncSaveObjectName>";
    private static final String key = "<objectName>";

    public static void main(String[] args) throws ParseException {

        /*
         * Constructs a client instance with your account for accessing OSS
         */
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setProtocol(Protocol.HTTP);
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(new DefaultCredentialProvider(accessKeyId, accessKeySecret))
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();
        try {
            String content = "hello world";

            if(!ossClient.doesBucketExist(bucketName)){
                ossClient.createBucket(bucketName);
            }

            ossClient.putObject(bucketName, key, new ByteArrayInputStream(content.getBytes()));


            String expirationString = "Mon, 17 Dec 2023 03:12:27 GMT";
            Date expiration = DateUtil.parseRfc822Date(expirationString);

            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.GET);
            generatePresignedUrlRequest.setExpiration(expiration);;

            URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
            System.out.println(url);

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            ossClient.getObject(getObjectRequest, new File("D://"+key));

            Map<String, String> customHeaders = new HashMap<String, String>();
            OSSObject ossObject = ossClient.getObject(url, customHeaders);

            ossClient.deleteObject(bucketName, key);

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getMessage());
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