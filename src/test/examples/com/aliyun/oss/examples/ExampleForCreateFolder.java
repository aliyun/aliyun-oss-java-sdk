import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;

public class ExampleForCreateFolder {
	private static final String ACCESS_ID = "<your access key id>";
    private static final String ACCESS_KEY = "<your access key secret>";
    private static final String OSS_ENDPOINT = "http://oss.aliyuncs.com/";
	public static void main(String [] args) throws IOException{
		String bucketName = "<your bucket name>";
		//要创建的文件夹名称,在满足Object命名规则的情况下以"/"结尾
		String objectName = "folder_name/"; 
		OSSClient client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);
		ObjectMetadata objectMeta = new ObjectMetadata();
        /*这里的size为0,注意OSS本身没有文件夹的概念,这里创建的文件夹本质上是一个size为0的Object,dataStream仍然可以有数据
         *照样可以上传下载,只是控制台会对以"/"结尾的Object以文件夹的方式展示,用户可以利用这种方式来实现
         *文件夹模拟功能,创建形式上的文件夹
         */
		byte[] buffer = new byte[0];
		ByteArrayInputStream in = new ByteArrayInputStream(buffer);  
        objectMeta.setContentLength(0);
        try {
            client.putObject(bucketName, objectName, in, objectMeta);
        } finally {
            in.close();
        }
	}
}

