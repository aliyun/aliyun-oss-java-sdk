package com.aliyun.oss.testing;

import java.io.File;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;

public class ImageOperationsSample {
    
    private static final String ENDPOINT = "http://img-cn-beijing.aliyuncs.com";
    private static final String ACCESS_ID = "uxeiej372vj14mnfgkvojpda";
    private static final String ACCESS_KEY = "ztv8m9igkg1tfXcKjxUdLQvzdAE=";

    public static void main(String[] args) throws Exception {
        
        String bucketName = "'jy-bj-img";
        String key = "1280.jpg";
        String filePath = "D:\\pic\\1280.jpg";
        
        try {    
            OSSClient client = new OSSClient(ENDPOINT, ACCESS_ID, ACCESS_KEY);
            client.putObject(bucketName, key, new File(filePath));
            
            OSSObject o = client.getObject(bucketName, key + "@100w");
            System.out.println(o.getObjectMetadata().getContentType());
        } catch (OSSException oe) {
            System.out.println(oe.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
