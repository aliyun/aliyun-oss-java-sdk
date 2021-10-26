import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

import java.util.Map;

public class BucketTaggingSample {

    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";
    private static String bucketName = "*** Provide bucket name ***";

    public static void main(String[] args) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 设置Bucket标签。
            SetBucketTaggingRequest request = new SetBucketTaggingRequest(bucketName);
            // 依次填写Bucket标签的键（例如owner）和值（例如John）。
            request.setTag("owner", "John");
            request.setTag("location", "hangzhou");
            ossClient.setBucketTagging(request);

            // 获取Bucket标签信息。
            TagSet tagSet = ossClient.getBucketTagging(new GenericRequest(bucketName));
            Map<String, String> tags = tagSet.getAllTags();
            for(Map.Entry tag:tags.entrySet()){
                System.out.println("key:"+tag.getKey()+" value:"+tag.getValue());
            }

            // 列举带指定标签的Bucket。
            ListBucketsRequest listBucketsRequest = new ListBucketsRequest();
            // 依次填写Bucket标签的键（例如owner）和值（例如John）。
            listBucketsRequest.setTag("owner", "John");
            BucketList bucketList = ossClient.listBuckets(listBucketsRequest);
            for (Bucket o : bucketList.getBucketList()) {
                System.out.println("list result bucket: " + o.getName());
            }

            // 删除Bucket标签。
            ossClient.deleteBucketTagging(new GenericRequest(bucketName));
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
