import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.CannedAccessControlList;


public class ExampleForBucketAcl {
	private static final String ACCESS_ID = "<your access key id>";
    private static final String ACCESS_KEY = "<your access key secret>";
    private static final String OSS_ENDPOINT = "http://oss.aliyuncs.com/";
	public static void main(String [] args){
		OSSClient client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);
		String bucketName = "<your bucket name>";
		client.setBucketAcl(bucketName, CannedAccessControlList.PublicReadWrite); 
		AccessControlList accessControlList = client.getBucketAcl(bucketName);
		//可以打印出来看结果,也可以从控制台确认
		System.out.println(accessControlList.toString());
	}
}

