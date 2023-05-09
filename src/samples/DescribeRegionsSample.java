package samples;

import com.aliyun.oss.model.GetDescribeRegionsRequest;
import com.aliyun.oss.model.GetDescribeRegionsResult;
import com.aliyun.oss.model.RegionInfo;

public class DescribeRegionsSample {
    private static String endpoint = "*** Provide OSS endpoint ***";
    private static String accessKeyId = "*** Provide your AccessKeyId ***";
    private static String accessKeySecret = "*** Provide your AccessKeySecret ***";

    public static void main(String[] args) {

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            GetDescribeRegionsResult result = ossClient.getDescribeRegions(new GetDescribeRegionsRequest());

            for(RegionInfo r : result.getRegionInfoList()){
                System.out.println(r.getRegion());
                System.out.println(r.getInternetEndpoint());
                System.out.println(r.getInternetEndpoint());
                System.out.println(r.getAccelerateEndpoint());
            }
            System.out.println("return status code: " + result.getResponse().getStatusCode());
        } catch (OSSException oe) {
            System.out.println("Error Message: " + oe.getErrorMessage());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message: " + ce.getMessage());
        } finally {
            if(ossClient != null){
                ossClient.shutdown();
            }
        }
    }
}
