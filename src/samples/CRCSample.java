package samples;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.InconsistentException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.UploadFileRequest;
import junit.framework.Assert;

/**
 * 上传/下载数据校验用法示例
 *
 */
public class CRCSample {
    
    private static String endpoint = "<endpoint, http://oss-cn-hangzhou.aliyuncs.com>";
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String bucketName = "<bucketName>";
    private static String uploadFile = "<uploadFile>";
    private static String key = "crc-sample.txt";    

    
    public static void main(String[] args) throws IOException { 
    	
    	String content = "Hello OSS, Hi OSS, OSS OK.";

    	// 上传/下载默认开启CRC校验，如果不需要，请关闭CRC校验功能
    	// ClientConfiguration config = new ClientConfiguration();
    	// config.setCrcCheckEnabled(false);    	
    	// OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        
        try {
        	
        	// 开启CRC校验后，上传(putObject/uploadPart/uploadFile)自动开启CRC校验，使用方法与原来相同。
        	// appendObject需要出AppendObjectRequest.setInitCRC才会CRC校验
            ossClient.putObject(bucketName, key, new ByteArrayInputStream(content.getBytes()));
            ossClient.deleteObject(bucketName, key);
            
            // 追加上传，第一次追加
            AppendObjectRequest appendObjectRequest = new AppendObjectRequest(bucketName, key, 
                    new ByteArrayInputStream(content.getBytes())).withPosition(0L);
            
            appendObjectRequest.setInitCRC(0L);
            AppendObjectResult appendObjectResult = ossClient.appendObject(appendObjectRequest);
            
            // 追加上传，第二次追加
            appendObjectRequest = new AppendObjectRequest(bucketName, key, 
            		new ByteArrayInputStream(content.getBytes()));
            appendObjectRequest.setPosition(appendObjectResult.getNextPosition());
            appendObjectRequest.setInitCRC(appendObjectResult.getClientCRC());
            appendObjectResult = ossClient.appendObject(appendObjectRequest);
            
            ossClient.deleteObject(bucketName, key);
            
            // 断点续传上传，支持CRC校验
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            // 待上传的本地文件
            uploadFileRequest.setUploadFile(uploadFile);
            // 设置并发下载数，默认1
            uploadFileRequest.setTaskNum(5);
            // 设置分片大小，默认100KB
            uploadFileRequest.setPartSize(1024 * 1024 * 1);
            // 开启断点续传，默认关闭
            uploadFileRequest.setEnableCheckpoint(true);
            
            ossClient.uploadFile(uploadFileRequest);
            
            // 下载CRC校验，注意范围下载不支持CRC校验，downloadFile不支持CRC校验
            OSSObject ossObject = ossClient.getObject(bucketName, key);
            Assert.assertNull(ossObject.getClientCRC());
            Assert.assertNotNull(ossObject.getServerCRC());
            
            InputStream stream = ossObject.getObjectContent();
            while (stream.read() != -1) {
            }
            stream.close();
            
            // 校验CRC是否一致
            OSSUtils.checkChecksum(IOUtils.getCRCValue(stream), ossObject.getServerCRC(), ossObject.getRequestId());
            
            ossClient.deleteObject(bucketName, key);
                        
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        } catch (InconsistentException ie) {
        	System.out.println("Caught an OSSException");
        	System.out.println("Request ID:      " + ie.getRequestId());
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }
}
