import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;


public class BlockDownloadSample {

    private static OSSClient client = null;
    private static ExecutorService executorService;
    private static final String ACCESS_ID = "<your access key id>";
    private static final String ACCESS_KEY = "<your access key secret>";
    private static final String OSS_ENDPOINT = "http://oss.aliyuncs.com/";
    
	static{
		 client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		final String localFilePath = "<your localFilePath>";
		final String bucketName = "<your bucketName>";
		final String fileKey = "<your fileKey>";
        
	    ObjectMetadata objectMetadata=	client.getObjectMetadata(bucketName, fileKey);
        long partSize = 1024 *1024 * 5; 
        long  fileLength = objectMetadata.getContentLength();
        RandomAccessFile file = new RandomAccessFile(localFilePath, "rw");
        file.setLength(fileLength);
        file.close();
	
        int partCount=calPartCount(fileLength, partSize);
        System.out.println("需要下载的文件分块数：" + partCount);
        executorService = Executors.newFixedThreadPool(5);
        List<String> eTags = Collections.synchronizedList(new ArrayList<String>());
        
        for (int i = 0; i < partCount; i++) {
          final  long startPos = partSize * i;
          final  long endPos = partSize * i +( partSize < (fileLength - startPos) ? partSize : (fileLength - startPos)) - 1;

          executorService.execute(new BlockDownloadThread(startPos, endPos, localFilePath, bucketName, fileKey,eTags));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
        	executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
        
        if (eTags.size() != partCount)
        {
            throw new IllegalStateException("下载失败，有Part未下载成功。");
        }
	}
	
    private static int calPartCount(long fileLength,long partSize) {
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0){
            partCount++;
        }
        return partCount;
    }

    private static class BlockDownloadThread extends Thread
    {
        //当前线程的下载开始位置
        private long startPos;
        
        //当前线程的下载结束位置
        private long endPos;
        
        //保存文件路径
        private String localFilePath;
        
        private String bucketName;
        private String fileKey;
        private List<String> eTags;

        public BlockDownloadThread(long startPos, long endPos,String localFilePath,String bucketName,String fileKey,List<String> eTags)
        {
            this.startPos = startPos;
            this.endPos = endPos;
            this.localFilePath = localFilePath;
            this.bucketName = bucketName;
            this.fileKey = fileKey;
            this.eTags = eTags;
        }

        @Override
        public void run()
        {
            try
            {
            	RandomAccessFile file = new RandomAccessFile(localFilePath, "rw");
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileKey);
                getObjectRequest.setRange(startPos, endPos);
                OSSObject ossObject = client.getObject(getObjectRequest);
                file.seek(startPos);
                int bufSize = 1024;
                try {
                    byte[] buffer = new byte[bufSize];
                    int bytesRead;
                    while ((bytesRead = ossObject.getObjectContent().read(buffer)) > -1) {
                        file.write(buffer, 0, bytesRead);
                    }
                    eTags.add(ossObject.getObjectMetadata().getETag());
                } catch (IOException e) {
                   
                } finally {
                    IOUtils.safeClose(ossObject.getObjectContent());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
