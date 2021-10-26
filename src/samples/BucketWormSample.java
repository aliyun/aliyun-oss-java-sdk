import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;

import com.aliyun.oss.model.GetBucketWormResult;
import com.aliyun.oss.model.InitiateBucketWormRequest;
import com.aliyun.oss.model.InitiateBucketWormResult;


public class BucketWormSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 新建合规保留策略
            // 创建InitiateBucketWormRequest对象。
            InitiateBucketWormRequest initiateBucketWormRequest = new InitiateBucketWormRequest(bucketName);
            // 指定Object保护天数为1天。
            initiateBucketWormRequest.setRetentionPeriodInDays(1);

            // 创建合规保留策略。
            InitiateBucketWormResult initiateBucketWormResult = ossClient.initiateBucketWorm(initiateBucketWormRequest);

            // 查看合规保留策略Id。
            String wormId = initiateBucketWormResult.getWormId();
            System.out.println(wormId);

            // 锁定合规保留策略。
            ossClient.completeBucketWorm(bucketName, wormId);

            // 获取合规保留策略。
            GetBucketWormResult getBucketWormResult = ossClient.getBucketWorm(bucketName);

            // 查看合规保留策略Id。
            System.out.println(getBucketWormResult.getWormId());
            // 查看合规保留策略状态。未锁定状态下为"InProgress", 锁定状态下为"Locked"。
            System.out.println(getBucketWormResult.getWormState());
            // 查看Object的保护时间。
            System.out.println(getBucketWormResult.getRetentionPeriodInDays());
            // 查看合规保留策略的创建时间。
            System.out.println(getBucketWormResult.getCreationDate());

            // 延长已锁定的合规保留策略中Object的保留天数。
            ossClient.extendBucketWorm(bucketName, wormId, 2);

            // 取消合规保留策略。
            ossClient.abortBucketWorm(bucketName);

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
}
