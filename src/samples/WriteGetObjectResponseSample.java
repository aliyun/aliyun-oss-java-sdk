package samples;

import com.aliyun.oss.model.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class WriteGetObjectResponseSample {
    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";

    public static void main(String[] args) throws InterruptedException {

        /*
         * Constructs a client instance with your account for accessing OSS
         */
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            final int instreamLength = 128 * 1024;
            InputStream instream = null;
            String route = "test-ap-process-name-128364***6515-opap.oss-cn-beijing-internal.oss-object-process.aliyuncs.com";
            String token = "OSSV1#UMoA43+Bi9b6Q1Lu6UjhLXnmq4I/wIFac3uZfBkgJtg2xtHkZJ4bZglDWyOgWRlGTrA8y/i6D9eH8PmAiq2NL2R/MD/UX6zvRhT8WMHUewgc9QWPs9LPHiZytkUZnGa39mnv/73cyPWTuxgxyk4dNhlzEE6U7PdzmCCu8gIrjuYLPrA9psRn0ZC8J2/DCZGVx0BE7AmIJTcNtLKTSjxsJyTts******";
            int status = 200;

            instream = genFixedLengthInputStream(instreamLength);
            WriteGetObjectResponseRequest writeGetObjectResponseRequest = new WriteGetObjectResponseRequest(route, token, status, instream);
            // add CommonHeader
            // writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-Accept-Ranges", "*** Provide your Accept Ranges ***");
            // writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-Cache-Control", "*** Provide your Cache Control ***");
            // writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-Content-Disposition", "*** Provide your Content Disposition ***");
            // writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-Content-Encoding", "*** Provide your Content Encoding ***");
            // writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-Content-Range", "*** Provide your Content Range ***");
            // writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-Content-Type", "*** Provide your Content Type ***");
            // writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-ETag", "*** Provide your ETag ***");
            // writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-Expires", "*** Provide your Expires ***");
            // writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-Last-Modified", "*** Provide your Last Modified ***");
            // writeGetObjectResponseRequest.addHeader("x-oss-fwd-header-Location", "*** Provide your Location ***");

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(instreamLength);
            writeGetObjectResponseRequest.setMetadata(metadata);

            ossClient.writeGetObjectResponse(writeGetObjectResponseRequest);
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

    public static InputStream genFixedLengthInputStream(long fixedLength) {
        byte[] buf = new byte[(int) fixedLength];
        for (int i = 0; i < buf.length; i++)
            buf[i] = 'a';
        return new ByteArrayInputStream(buf);
    }
}