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
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.DownloadFileResult;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.UploadFileRequest;
import com.aliyun.oss.model.UploadFileResult;

public class DownloadFileTest extends TestBase {

    @Test
    public void testUploadFileWithoutCheckpoint() {
        final String key = "obj-download-file-wcp";
        
        try {            
            File file = createSampleFile(key, 1024 * 500);
                        
            // upload file
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.addUserMetadata("prop", "propval");
            uploadFileRequest.setObjectMetadata(objMetadata);
            
            UploadFileResult uploadRes = ossClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
            
            // download file
            String filePathNew = key + "-new.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(filePathNew);
            downloadFileRequest.setTaskNum(10);
            
            DownloadFileResult downloadRes = ossClient.downloadFile(downloadFileRequest);
            
            ObjectMetadata objMeta = downloadRes.getObjectMetadata();
            Assert.assertEquals(objMeta.getContentLength(), 102400);
            Assert.assertEquals(objMeta.getObjectType(), "Multipart");
            Assert.assertEquals(objMeta.getUserMetadata().get("prop"), "propval");

            File fileNew = new File(filePathNew);
            Assert.assertTrue("comparte file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            
            ossClient.deleteObject(bucketName, key);
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            ossClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testUploadFileWithCheckpoint() {
        final String key = "obj-download-file-cp";
        
        try {            
            File file = createSampleFile(key, 1024 * 500);
                        
            // upload file
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            uploadFileRequest.setEnableCheckpoint(true);
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.addUserMetadata("prop", "propval");
            uploadFileRequest.setObjectMetadata(objMetadata);
            
            UploadFileResult uploadRes = ossClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);            
            
            // download file
            String filePathNew = key + "-new.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(filePathNew);
            downloadFileRequest.setTaskNum(10);
            downloadFileRequest.setEnableCheckpoint(true);
            
            DownloadFileResult downloadRes = ossClient.downloadFile(downloadFileRequest);
            
            ObjectMetadata objMeta = downloadRes.getObjectMetadata();
            Assert.assertEquals(objMeta.getContentLength(), 102400);
            Assert.assertEquals(objMeta.getObjectType(), "Multipart");
            Assert.assertEquals(objMeta.getUserMetadata().get("prop"), "propval");

            File fileNew = new File(filePathNew);
            Assert.assertTrue("comparte file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            
            ossClient.deleteObject(bucketName, key);
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            ossClient.deleteBucket(bucketName);
        }
    }
  
    @Test
    public void testUploadFileWithCheckpointFile() {
        final String key = "obj-download-file-cpf";
        
        try {            
            File file = createSampleFile(key, 1024 * 500);
                        
            // upload file
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            uploadFileRequest.setEnableCheckpoint(true);
            uploadFileRequest.setCheckpointFile("BingWallpaper.ucp");
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.addUserMetadata("prop", "propval");
            uploadFileRequest.setObjectMetadata(objMetadata);
            
            UploadFileResult uploadRes = ossClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);
            
            // download file
            String filePathNew = key + "-new.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(filePathNew);
            downloadFileRequest.setTaskNum(10);
            downloadFileRequest.setEnableCheckpoint(true);
            downloadFileRequest.setCheckpointFile("BingWallpaper.dcp");
            
            DownloadFileResult downloadRes = ossClient.downloadFile(downloadFileRequest);
            
            ObjectMetadata objMeta = downloadRes.getObjectMetadata();
            Assert.assertEquals(objMeta.getContentLength(), 102400);
            Assert.assertEquals(objMeta.getObjectType(), "Multipart");
            Assert.assertEquals(objMeta.getUserMetadata().get("prop"), "propval");

            File fileNew = new File(filePathNew);
            Assert.assertTrue("comparte file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            
            ossClient.deleteObject(bucketName, key);
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            ossClient.deleteBucket(bucketName);
        }
    }

    static class RangeInfo {
        RangeInfo(long start, long end) {
            this.start = start;
            this.end = end;
        }
        public long start; // start index;
        public long end; // end index;
    }

    @Test
    public void testDownloadFileWithRange() {
        final String key = "obj-download-file-range-cp";

        try {
            File file = createSampleFile(key, 1024 * 500);

            // upload file
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            uploadFileRequest.setEnableCheckpoint(true);
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.addUserMetadata("prop", "propval");
            uploadFileRequest.setObjectMetadata(objMetadata);

            UploadFileResult uploadRes = ossClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);

            //range
            List<RangeInfo> rangeInfo = new ArrayList<RangeInfo>();
            List<RangeInfo> expectRangeInfo = new ArrayList<RangeInfo>();
            // case normal range
            rangeInfo.add(new RangeInfo(20, 200*1024 + 100));
            expectRangeInfo.add(new RangeInfo(20, 200*1024 + 100));

            rangeInfo.add(new RangeInfo(100, 101));
            expectRangeInfo.add(new RangeInfo(100, 101));

            rangeInfo.add(new RangeInfo(200, 200));
            expectRangeInfo.add(new RangeInfo(200, 200));

            //to end
            rangeInfo.add(new RangeInfo(102402, -1));
            expectRangeInfo.add(new RangeInfo(102402, file.length() -1));

            //end > size
            rangeInfo.add(new RangeInfo(102402, file.length() + 20));
            expectRangeInfo.add(new RangeInfo(102402, file.length() -1));

            //start < 0, end > 0 &&  end < size
            rangeInfo.add(new RangeInfo(-1, 300*1024 + 100));
            expectRangeInfo.add(new RangeInfo(0, 300*1024 + 100));

            //start < 0, end > size
            rangeInfo.add(new RangeInfo(-1, file.length()));
            expectRangeInfo.add(new RangeInfo(0, file.length() -1));

            //start < 0, end < 0
            rangeInfo.add(new RangeInfo(-1, -1));
            expectRangeInfo.add(new RangeInfo(0, file.length() -1));

            //start >= size
            rangeInfo.add(new RangeInfo(file.length(), file.length() + 20));
            expectRangeInfo.add(new RangeInfo(0, file.length() -1));

            //start > end
            rangeInfo.add(new RangeInfo(1024, 100));
            expectRangeInfo.add(new RangeInfo(0, file.length() -1));

            Assert.assertEquals(rangeInfo.size(), expectRangeInfo.size());
            Assert.assertEquals(true, rangeInfo.size() > 0);

            for (int i = 0; i < rangeInfo.size(); i++) {
                // download file
                String filePathNew = key + "-" + i +"-new.txt";
                long start = rangeInfo.get(i).start;
                long end = rangeInfo.get(i).end;
                DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
                downloadFileRequest.setDownloadFile(filePathNew);
                downloadFileRequest.setTaskNum(10);
                downloadFileRequest.setEnableCheckpoint(true);
                downloadFileRequest.setRange(start, end);

                DownloadFileResult downloadRes = ossClient.downloadFile(downloadFileRequest);

                ObjectMetadata objMeta = downloadRes.getObjectMetadata();
                //Assert.assertEquals(objMeta.getContentLength(), downloadFileRequest.getPartSize());
                Assert.assertEquals(objMeta.getObjectType(), "Multipart");
                Assert.assertEquals(objMeta.getUserMetadata().get("prop"), "propval");

                File fileNew = new File(filePathNew);
                long expectStart = expectRangeInfo.get(i).start;
                long expectEnd = expectRangeInfo.get(i).end;
                Assert.assertTrue("comparte file", compareFileWithRange(file.getAbsolutePath(), expectStart, expectEnd, fileNew.getAbsolutePath()));
                fileNew.delete();
            }
            ossClient.deleteObject(bucketName, key);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            ossClient.deleteBucket(bucketName);
        }
    }

    @Test
    public void testDownloadEmptyFileWithoutCheckpoint() {
        final String key = "obj-download-empty-file-wcp";

        try {
            File file = createSampleFile(key, 0);
            Assert.assertEquals(0, file.length());

            // upload file
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setTaskNum(10);
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.addUserMetadata("prop", "propval");
            uploadFileRequest.setObjectMetadata(objMetadata);

            UploadFileResult uploadRes = ossClient.uploadFile(uploadFileRequest);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getBucketName(), bucketName);
            Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), key);

            // download file
            String filePathNew = key + "-new.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(filePathNew);
            downloadFileRequest.setTaskNum(10);

            DownloadFileResult downloadRes = ossClient.downloadFile(downloadFileRequest);

            ObjectMetadata objMeta = downloadRes.getObjectMetadata();
            Assert.assertEquals(objMeta.getContentLength(), 0);
            Assert.assertEquals(objMeta.getObjectType(), "Multipart");
            Assert.assertEquals(objMeta.getUserMetadata().get("prop"), "propval");

            File fileNew = new File(filePathNew);
            Assert.assertEquals(0, fileNew.length());
            fileNew.delete();

            //download file with range
            filePathNew = key + "range-new.txt";
            downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(filePathNew);
            downloadFileRequest.setTaskNum(10);
            downloadFileRequest.setRange(0, 0);
            downloadRes = ossClient.downloadFile(downloadFileRequest);

            objMeta = downloadRes.getObjectMetadata();
            Assert.assertEquals(objMeta.getContentLength(), 0);
            Assert.assertEquals(objMeta.getObjectType(), "Multipart");
            Assert.assertEquals(objMeta.getUserMetadata().get("prop"), "propval");

            fileNew = new File(filePathNew);
            Assert.assertEquals(0, fileNew.length());
            fileNew.delete();

            ossClient.deleteObject(bucketName, key);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
            ossClient.deleteBucket(bucketName);
        }
    }
}
