import java.util.ArrayList;
import java.util.List;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.UploadPartCopyRequest;
import com.aliyun.oss.model.UploadPartCopyResult;


public class UploadPartCopySample {

    private static final String ACCESS_ID = "<your access key id>";
    private static final String ACCESS_KEY = "<your access key secret>";
    private static final String OSS_ENDPOINT = "http://oss.aliyuncs.com/";
	
	public static void main(String[] args) throws Exception {
		String sourceBucketName = "";//需要拷贝的bucket
		String sourceKey = "";//需要拷贝的key
		String targetBucketName = "";//目标bucket
		String targetKey = "";//目标key
        
        OSSClient client=new OSSClient(OSS_ENDPOINT,ACCESS_ID,ACCESS_KEY);
	
		InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(targetBucketName, targetKey);
		InitiateMultipartUploadResult initiateMultipartUploadResult = client.initiateMultipartUpload(initiateMultipartUploadRequest);
		String uploadId = initiateMultipartUploadResult.getUploadId();
		ObjectMetadata objectMetadata = client.getObjectMetadata(sourceBucketName,sourceKey);

		long partSize = 1024 * 1024 * 100;
		long contentLength = objectMetadata.getContentLength();
		
		// 计算分块数目
		int partCount = (int) (contentLength / partSize);
		if (contentLength % partSize != 0) {
			partCount++;
		}
        System.out.println("total part count:" + partCount);
		List<PartETag> partETags = new ArrayList<PartETag>();

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < partCount; i++) {
			System.out.println("now begin to copy part:" + (i+1));
			long skipBytes = partSize * i;
			long size = partSize < contentLength - skipBytes ? partSize	: contentLength - skipBytes;

			UploadPartCopyRequest uploadPartCopyRequest = new UploadPartCopyRequest();
			uploadPartCopyRequest.setSourceKey("/" + sourceBucketName + "/" + sourceKey);
			uploadPartCopyRequest.setBucketName(targetBucketName);
			uploadPartCopyRequest.setKey(targetKey);
			uploadPartCopyRequest.setUploadId(uploadId);
			uploadPartCopyRequest.setPartSize(size);
			uploadPartCopyRequest.setBeginIndex(skipBytes);
			uploadPartCopyRequest.setPartNumber(i + 1);
			
			UploadPartCopyResult uploadPartCopyResult = client.uploadPartCopy(uploadPartCopyRequest);

			partETags.add(uploadPartCopyResult.getPartETag());
            System.out.println("now end to copy part:" + (i+1));
		}
		
		CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
				targetBucketName, targetKey, uploadId, partETags);

		// 完成拷贝
		CompleteMultipartUploadResult completeMultipartUploadResult 
				= client.completeMultipartUpload(completeMultipartUploadRequest);
		System.out.println("结束........");
		long endTime = System.currentTimeMillis();
		System.out.println("花费时间约：" + (endTime - startTime) + " ms");
		// 打印Object的ETag
		System.out.println(completeMultipartUploadResult.getETag() + "---------------------------------");
	}
}
