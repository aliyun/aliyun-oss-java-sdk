package samples;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

/**
 * This sample demonstrates how to list objects under specfied bucket 
 * from Aliyun OSS using the OSS SDK for Java.
 */
public class ListObjectsSample {
    
    private static String endpoint = "<endpoint>";
    private static String accessKeyId = "<accessKeyId>";
    private static String accessKeySecret = "<accessKeySecret>";
    private static String bucketName = "<bucketName>";
    
    public static void main(String[] args) throws IOException {        

        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        
        try {
            final String content = "Hello OSS";
            final String keyPrefix = "MyObjectKey";
            
            if (!client.doesBucketExist(bucketName)) {
                client.createBucket(bucketName);
            }
            
            // 准备环境，插入100个测试Object
            List<String> keys = new ArrayList<String>();
            for (int i = 0; i < 100; i++) {
                String key = keyPrefix + i;
                InputStream instream = new ByteArrayInputStream(content.getBytes());
                client.putObject(bucketName, key, instream);
                keys.add(key);
            }
            System.out.println("Put " + keys.size() + " objects completed." );
            
            ObjectListing objectListing = null;
            String nextMarker = null;
            final int maxKeys = 30;
            List<OSSObjectSummary> sums = null;
            
            // 使用默认参数获取存储空间的文件列表，默认最多返回100条
            System.out.println("Default paramters:");
            objectListing = client.listObjects(bucketName);
            sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
            }
            
            // 指定最大返回数量，最多不能超过1000条
            System.out.println("With max keys:");
            objectListing = client.listObjects(new ListObjectsRequest(bucketName).
                    withMaxKeys(200));
            
            sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
            }
            
            // 返回指定前缀的Object，默认最多返回100条
            System.out.println("With prefix:");
            objectListing = client.listObjects(new ListObjectsRequest(bucketName).withPrefix(keyPrefix));
            
            sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
            }
            
            // 从指定的某Object后返回，默认最多100条
            System.out.println("With marker: ");
            objectListing = client.listObjects(new ListObjectsRequest(bucketName).withMarker(keyPrefix + "11"));
            
            sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                System.out.println("\t" + s.getKey());
            }
            
            // 分页获取所有Object，每页maxKeys条Object
            System.out.println("List all objects:");
            nextMarker = null;
            do {
                objectListing = client.listObjects(new ListObjectsRequest(bucketName).
                        withMarker(nextMarker).withMaxKeys(maxKeys));
                
                sums = objectListing.getObjectSummaries();
                for (OSSObjectSummary s : sums) {
                    System.out.println("\t" + s.getKey());
                }
                
                nextMarker = objectListing.getNextMarker();
                
            } while (objectListing.isTruncated());
            
            
            // 分页所有获取从特定Object后的所有的Object，每页maxKeys条Object
            System.out.println("List all objects after marker:");
            nextMarker = keyPrefix + "11";
            do {
                objectListing = client.listObjects(new ListObjectsRequest(bucketName).
                        withMarker(nextMarker).withMaxKeys(maxKeys));
                
                sums = objectListing.getObjectSummaries();
                for (OSSObjectSummary s : sums) {
                    System.out.println("\t" + s.getKey());
                }
                
                nextMarker = objectListing.getNextMarker();
                
            } while (objectListing.isTruncated());
            
            // 分页所有获取指定前缀的Object，每页maxKeys条Object
            System.out.println("List all objects with prefix:");
            nextMarker = null;
            do {
                objectListing = client.listObjects(new ListObjectsRequest(bucketName).
                        withPrefix(keyPrefix + "1").withMarker(nextMarker).withMaxKeys(maxKeys));
                
                sums = objectListing.getObjectSummaries();
                for (OSSObjectSummary s : sums) {
                    System.out.println("\t" + s.getKey());
                }
                
                nextMarker = objectListing.getNextMarker();
                
            } while (objectListing.isTruncated());
            
            // 清理测试环境，删除创建的Object
            System.out.println("Deleting all objects:");
            DeleteObjectsResult deleteObjectsResult = client.deleteObjects(
                    new DeleteObjectsRequest(bucketName).withKeys(keys));
            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
            for (String object : deletedObjects) {
                System.out.println("\t" + object);
            }
            
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
        } finally {
            /*
             * Do not forget to shut down the client finally to release all allocated resources.
             */
            client.shutdown();
        }
    }
}
