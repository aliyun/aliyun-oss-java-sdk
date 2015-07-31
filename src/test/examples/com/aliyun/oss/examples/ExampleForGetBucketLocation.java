import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;


public class ExampleForGetBucketLocation {
	private static final String ACCESS_ID = "<your access key id>";
    private static final String ACCESS_KEY = "<your access key secret>";
    private static final String OSS_ENDPOINT = "http://oss.aliyuncs.com/";
	public static void main(String [] args){
		OSSClient client=new OSSClient(OSS_ENDPOINT,ACCESS_ID,ACCESS_KEY);
		String bucketName = "<your bucket name>";
        try{
            String location = client.getBucketLocation(bucketName);
            System.out.println(location);
        }catch(OSSException e){
        	e.printStackTrace();
        }
	}
}
