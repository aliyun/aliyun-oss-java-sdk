/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.oss.integrationtests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Assert;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;

import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.utils.ResourceUtils;

/**
 * 图片处理服务测试
 */
public class ImageTest extends TestBase {
    
    final private static String originalImage = "oss/example.jpg";
    final private static String newImage = "oss/new-example.jpg";
        
    @Override
    public void setUp() throws Exception {
        super.setUp();
        ossClient.putObject(bucketName, originalImage, new File(ResourceUtils.getTestFilename(originalImage)));        
    }

    @Override
    public void tearDown() throws Exception {
        ossClient.deleteObject(bucketName, originalImage);
        ossClient.deleteObject(bucketName, newImage);
        super.tearDown();
    }

    @Test
    public void testResizeImage() {
        String style = "image/resize,m_fixed,w_100,h_100";  // 缩放
        
        try {
            GetObjectRequest request = new GetObjectRequest(bucketName, originalImage);
            request.addParameter("x-oss-process", style);
            
            OSSObject ossObject = ossClient.getObject(request);
            ossClient.putObject(bucketName, newImage, ossObject.getObjectContent());
            
            ImageInfo imageInfo = getImageInfo(bucketName, newImage);
            Assert.assertEquals(imageInfo.getHeight(), 100);
            Assert.assertEquals(imageInfo.getWidth(), 100); // 3587
            Assert.assertEquals(imageInfo.getFormat(), "jpg");
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testCropImage() {
        String style = "image/crop,w_100,h_100,x_100,y_100,r_1"; // 裁剪
        
        try {
            GetObjectRequest request = new GetObjectRequest(bucketName, originalImage);
            request.addParameter("x-oss-process", style);
            
            OSSObject ossObject = ossClient.getObject(request);
            ossClient.putObject(bucketName, newImage, ossObject.getObjectContent());
            
            ImageInfo imageInfo = getImageInfo(bucketName, newImage);
            Assert.assertEquals(imageInfo.getHeight(), 100);
            Assert.assertEquals(imageInfo.getWidth(), 100);
            Assert.assertEquals(imageInfo.getSize(), 2281);
            Assert.assertEquals(imageInfo.getFormat(), "jpg");
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testRotateImage() {
        String style = "image/rotate,90"; // 旋转

        try {
            GetObjectRequest request = new GetObjectRequest(bucketName,
                    originalImage);
            request.addParameter("x-oss-process", style);

            OSSObject ossObject = ossClient.getObject(request);
            ossClient.putObject(bucketName, newImage, ossObject.getObjectContent());

            ImageInfo imageInfo = getImageInfo(bucketName, originalImage);
            Assert.assertEquals(imageInfo.getHeight(), 267);
            Assert.assertEquals(imageInfo.getWidth(), 400);
            Assert.assertEquals(imageInfo.getSize(), 21839);
            Assert.assertEquals(imageInfo.getFormat(), "jpg");

            imageInfo = getImageInfo(bucketName, newImage);
            Assert.assertEquals(imageInfo.getHeight(), 400);
            Assert.assertEquals(imageInfo.getWidth(), 267);
            Assert.assertEquals(imageInfo.getSize(), 21509);
            Assert.assertEquals(imageInfo.getFormat(), "jpg");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testSharpenImage() {
        String style = "image/sharpen,100"; // 锐化

        try {
            GetObjectRequest request = new GetObjectRequest(bucketName, originalImage);
            request.addParameter("x-oss-process", style);

            OSSObject ossObject = ossClient.getObject(request);
            ossClient.putObject(bucketName, newImage, ossObject.getObjectContent());

            ImageInfo imageInfo = getImageInfo(bucketName, newImage);
            Assert.assertEquals(imageInfo.getHeight(), 267);
            Assert.assertEquals(imageInfo.getWidth(), 400);
            Assert.assertEquals(imageInfo.getSize(), 24183);
            Assert.assertEquals(imageInfo.getFormat(), "jpg");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } 
    }
    
    @Test
    public void testWatermarkImage() {
        String style = "image/watermark,text_SGVsbG8g5Zu-54mH5pyN5YqhIQ"; // 文字水印

        try {
            GetObjectRequest request = new GetObjectRequest(bucketName, originalImage);
            request.addParameter("x-oss-process", style);

            OSSObject ossObject = ossClient.getObject(request);
            ossClient.putObject(bucketName, newImage, ossObject.getObjectContent());

            ImageInfo imageInfo = getImageInfo(bucketName, newImage);
            System.out.println(imageInfo);
            Assert.assertEquals(imageInfo.getHeight(), 267);
            Assert.assertEquals(imageInfo.getWidth(), 400);
            Assert.assertEquals(imageInfo.getSize(), 26953);
            Assert.assertEquals(imageInfo.getFormat(), "jpg");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } 
    }
    
    @Test
    public void testFormatImage() {
        String style = "image/format,png"; // 文字水印

        try {
            GetObjectRequest request = new GetObjectRequest(bucketName, originalImage);
            request.addParameter("x-oss-process", style);

            OSSObject ossObject = ossClient.getObject(request);
            ossClient.putObject(bucketName, newImage, ossObject.getObjectContent());

            ImageInfo imageInfo = getImageInfo(bucketName, newImage);
            System.out.println(imageInfo);
            Assert.assertEquals(imageInfo.getHeight(), 267);
            Assert.assertEquals(imageInfo.getWidth(), 400);
            Assert.assertEquals(imageInfo.getSize(), 160733);
            Assert.assertEquals(imageInfo.getFormat(), "png");

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } 
    }
    
    private static ImageInfo getImageInfo(final String bucket, final String image) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        GetObjectRequest request = new GetObjectRequest(bucketName, image);
        request.addParameter("x-oss-process", "image/info");
        OSSObject ossObject = ossClient.getObject(request);
        
        String jsonStr = IOUtils.readStreamAsString(ossObject.getObjectContent(), "UTF-8");
        ossObject.getObjectContent().close();
        
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        Object bean = JSONObject.toBean(jsonObject); 
        
        long height = Long.parseLong((String) PropertyUtils.getProperty(PropertyUtils.getProperty(bean, "ImageHeight"), "value"));
        long width = Long.parseLong((String) PropertyUtils.getProperty(PropertyUtils.getProperty(bean, "ImageWidth"), "value"));
        long size = Long.parseLong((String) PropertyUtils.getProperty(PropertyUtils.getProperty(bean, "FileSize"), "value"));
        String format = (String) PropertyUtils.getProperty(PropertyUtils.getProperty(bean, "Format"), "value");
        
        return new ImageInfo(height, width, size, format);
    }
    
    static class ImageInfo {
        
        public ImageInfo(long height, long width, long size, String format) {
            super();
            this.height = height;
            this.width = width;
            this.size = size;
            this.format = format;
        }

        public long getHeight() {
            return height;
        }

        public void setHeight(long height) {
            this.height = height;
        }

        public long getWidth() {
            return width;
        }

        public void setWidth(long width) {
            this.width = width;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }
        
        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
        
        public String toString() {
            return "[height:" + this.height + ",width:" + this.width + 
                    ",size:" + this.size + ",format:" + this.format + "]\n";
        }

        private long height;
        private long width;
        private long size;
        private String format;
    }
    
}
