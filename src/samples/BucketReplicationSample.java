import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.AddBucketReplicationRequest;
import com.aliyun.oss.model.BucketReplicationProgress;
import com.aliyun.oss.model.ReplicationRule;

import java.util.List;


public class BucketReplicationSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            AddBucketReplicationRequest request = new AddBucketReplicationRequest(bucketName);
            request.setReplicationRuleID("<yourRuleId>");
            request.setTargetBucketName("<yourTargetBucketName>");
            // 目标Endpoint以北京为例。
            request.setTargetBucketLocation("oss-cn-beijing");
            // 设置禁止同步历史数据。默认会同步历史数据。
            request.setEnableHistoricalObjectReplication(false);
            ossClient.addBucketReplication(request);

            // 查看跨区域复制。
            List<ReplicationRule> rules = ossClient.getBucketReplication(bucketName);
            for (ReplicationRule rule : rules) {
                System.out.println(rule.getReplicationRuleID());
                System.out.println(rule.getTargetBucketLocation());
                System.out.println(rule.getTargetBucketName());
            }

            // 查看跨区域复制进度。
            BucketReplicationProgress process = ossClient.getBucketReplicationProgress(bucketName, "<yourRuleId>");
            System.out.println(process.getReplicationRuleID());
            // 是否开启了历史数据同步。
            System.out.println(process.isEnableHistoricalObjectReplication());
            // 历史数据同步进度。
            System.out.println(process.getHistoricalObjectProgress());
            // 实时数据同步进度。
            System.out.println(process.getNewObjectProgress());

            // 查看可同步的目标地域。
            List<String> locations = ossClient.getBucketReplicationLocation(bucketName);
            for (String loc : locations) {
                System.out.println(loc);
            }

            // 关闭跨区域复制。关闭后目标存储空间内的文件依然存在，只是不再同步源存储空间内文件的所有改动。
            ossClient.deleteBucketReplication(bucketName, "<yourRuleId>");


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
