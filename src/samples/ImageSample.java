package samples;

import java.io.File;
import java.io.IOException;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GetObjectRequest;

/**
 * 断点续传下载用法示例
 *
 */
public class ImageSample {
    
    private static String endpoint = "<endpoint, http://oss-cn-hangzhou.aliyuncs.com>";
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String bucketName = "<bucketName>";
    private static String key = "example.jpg";
    
    
    public static void main(String[] args) throws IOException {        

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        
        try {
            // 缩放
            String style = "image/resize,m_fixed,w_100,h_100";  
            GetObjectRequest request = new GetObjectRequest(bucketName, key);
            request.setProcess(style);
            
            ossClient.getObject(request, new File("example-resize.jpg"));
            
            // 裁剪
            style = "image/crop,w_100,h_100,x_100,y_100,r_1"; 
            request = new GetObjectRequest(bucketName, key);
            request.setProcess(style);
            
            ossClient.getObject(request, new File("example-crop.jpg"));
            
            // 旋转
            style = "image/rotate,90"; 
            request = new GetObjectRequest(bucketName, key);
            request.setProcess(style);
            
            ossClient.getObject(request, new File("example-rotate.jpg"));
            
            // 锐化
            style = "image/sharpen,100"; 
            request = new GetObjectRequest(bucketName, key);
            request.setProcess(style);
            
            ossClient.getObject(request, new File("example-sharpen.jpg"));
            
            // 水印
            style = "image/watermark,text_SGVsbG8g5Zu-54mH5pyN5YqhIQ"; 
            request = new GetObjectRequest(bucketName, key);
            request.setProcess(style);
            
            ossClient.getObject(request, new File("example-watermark.jpg"));
            
            // 格式转换
            style = "image/format,png"; 
            request = new GetObjectRequest(bucketName, key);
            request.setProcess(style);
            
            ossClient.getObject(request, new File("example-format.png"));
            
            // 图片信息
            style = "image/info"; 
            request = new GetObjectRequest(bucketName, key);
            request.setProcess(style);
            
            ossClient.getObject(request, new File("example-info.txt"));
            
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
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
    }
}
