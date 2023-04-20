package samples;

import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.*;

public class AsyncProcessObjectSample {
    private static String endpoint = "<endpoint, http://oss-cn-hangzhou.aliyuncs.com>";
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String bucketName = "<bucketName>";
    private static final String saveAsKey = "<syncSaveObjectName>";
    private static final String key = "<objectName>";

    public static void main(String[] args) {
        /*
         * Constructs a client instance with your account for accessing OSS
         */
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            StringBuilder styleBuilder = new StringBuilder();
            styleBuilder.append("video/convert,f_mp4,vcodec_h265,s_1920x1080,vb_2000000,fps_30,acodec_aac,ab_100000,sn_1");  // resize
            styleBuilder.append("|sys/saveas,");
            styleBuilder.append("o_" + BinaryUtil.toBase64String(saveAsKey.getBytes()).replaceAll("=", ""));
            styleBuilder.append(",");
            styleBuilder.append("b_" + BinaryUtil.toBase64String(bucketName.getBytes()).replaceAll("=", ""));

            AsyncProcessObjectRequest request = new AsyncProcessObjectRequest(bucketName, key, styleBuilder.toString());

            AsyncProcessObjectResult asyncProcessObject = ossClient.asyncProcessObject(request);
            System.out.println(asyncProcessObject.getAsyncRequestId());
            System.out.println(asyncProcessObject.getEventId());
            System.out.println(asyncProcessObject.getTaskId());
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