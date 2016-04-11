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
import java.io.InputStream;
import java.util.Date;
import java.util.Random;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.UploadFileRequest;

/**
 * 测试请求超时
 */
@Ignore
public class RequestTimeoutTest extends TestBase {
    
    private final static String endpoint = TestConfig.DEFAULT_ENDPOINT;
    private final static String accessId = TestConfig.DEFAULT_ACCESS_ID_1;
    private final static String accessKey = TestConfig.DEFAULT_ACCESS_KEY_1;

    private static String bucketName;
    private final static int requestTimeout = 10 * 1000;
    private static OSSClient ossClient;
    
    @Before
    public void setUp() throws Exception {
        long ticks = new Date().getTime() / 1000 + new Random().nextInt(5000);
        bucketName = TestConfig.BUCKET_NAME_PREFIX + ticks;
        
        if (ossClient == null) {
            ClientConfiguration config = new ClientConfiguration();
            config.setRequestTimeout(requestTimeout);
            config.setRequestTimeoutEnabled(true);
            //config.setMaxConnections(1);

            ossClient = new OSSClient(endpoint, accessId, accessKey, config);
            
            ossClient.createBucket(bucketName);
        }
    }

    @After
    public void tearDown() throws Exception {
        abortAllMultipartUploads(ossClient, bucketName);
        deleteBucketWithObjects(ossClient, bucketName);
        
        if (ossClient != null) {
            ossClient.shutdown();
            ossClient = null;
        }
    }
    
    /**
     * 正常请求时间不变
     */
    @Test
    public void testObjectOperationsNormal() throws Exception {
        String key = "test-object-operation-normal";

        try {
            // get
            ossClient.putObject(bucketName, key, TestUtils.genFixedLengthInputStream(64));
            
            // put
            OSSObject ossObject = ossClient.getObject(bucketName, key);
            ossObject.getObjectContent().close();
            
            // delete
            ossClient.deleteObject(bucketName, key);
            
            // upload
            File file = createSampleFile(key, 1024 * 500);
            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setPartSize(1024 * 100);
            uploadFileRequest.setTaskNum(10);
            uploadFileRequest.setEnableCheckpoint(true);
            
            ossClient.uploadFile(uploadFileRequest);
            
            // download
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(file.getAbsolutePath());
            downloadFileRequest.setTaskNum(10);
            downloadFileRequest.setEnableCheckpoint(true);
            
            ossClient.downloadFile(downloadFileRequest);
            
            ossClient.deleteObject(bucketName, key);
            file.delete();
            
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } catch (Throwable t) {
            Assert.fail(t.getMessage());
        }
    }
    
    
    /**
     * 超时生效
     */
    @Test
    public void testRequestTimeoutEffective() throws Exception {
        String key = "test-request-timeout-effective";
        
        try {
            ossClient.getClientConfiguration().setRequestTimeout(1);
            ossClient.getObject(bucketName, key);
            Assert.fail("Get object should not be successful");
        } catch (ClientException e) {
            Assert.assertEquals(OSSErrorCode.REQUEST_TIMEOUT, e.getErrorCode());
        } finally {
            ossClient.getClientConfiguration().setRequestTimeout(requestTimeout);
        }
    }
    
    
    /**
     * 原来的所有请求正常，各种异常抛出正常
     */
    @Test
    public void testObjectOperationsNegative() throws Exception {
        String bucket = "test-object-operations-negative";
        String key = "test-object-operations-negative";

        try {            
            ossClient.getBucketInfo(bucket);
            Assert.fail("Get bucket info should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }
        
        try {  
            ossClient.getObject(bucketName, key);
            Assert.fail("Get object should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_KEY, e.getErrorCode());
        }
        
        try {
            InputStream inputStream = null;
            ossClient.putObject(bucket, key, inputStream);
            Assert.fail("Put object should not be successful");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof IllegalArgumentException);
        }
        
        try {
            ossClient.getClientConfiguration().setRequestTimeout(1);
            ossClient.getObject(bucketName, key);
            Assert.fail("Get object should not be successful");
        } catch (ClientException e) {
            Assert.assertEquals(OSSErrorCode.REQUEST_TIMEOUT, e.getErrorCode());
        } finally {
            ossClient.getClientConfiguration().setRequestTimeout(requestTimeout);
        }
    }
    
    
    /**
     * 超时后连接重用正常
     */
    @Test
    public void testOperationsNormalAfterTimeout() throws Exception {
        String key = "test-operation-after-timeout";

        try {
            
            try {
                ossClient.getClientConfiguration().setRequestTimeout(1);
                ossClient.putObject(bucketName, key, TestUtils.genFixedLengthInputStream(64));
                Assert.fail("Get object should not be successful");
            } catch (ClientException e) {
                Assert.assertEquals(OSSErrorCode.REQUEST_TIMEOUT, e.getErrorCode());
            } finally {
                ossClient.getClientConfiguration().setRequestTimeout(requestTimeout);
            }

            ossClient.putObject(bucketName, key, TestUtils.genFixedLengthInputStream(64));
            OSSObject ossObject = ossClient.getObject(bucketName, key);
            ossObject.getObjectContent().close();
            ossClient.deleteObject(bucketName, key);
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }
    
    /**
     * 丢包恢复后不超时，Linux下使用iptables模拟，win下用断网模拟
     */
    @Test
    public void testOperationsNormalAfterPacketLoss() throws Exception {
        String key = "test-operation-after-packet-loss";

        try {
            File file = createSampleFile(key, 1024 * 1024 * 200);

            //System.out.println("start disconnect");
            ossClient.getClientConfiguration().setRequestTimeout(60 * 60 * 1000);
            
            try {
                ossClient.putObject(bucketName, key, file);
                Assert.fail("Get object should not be successful");
            } catch (ClientException e) {
                Assert.assertEquals(OSSErrorCode.REQUEST_TIMEOUT, e.getErrorCode());
            } finally {
                ossClient.getClientConfiguration().setRequestTimeout(requestTimeout);
            }

            ObjectListing objectListing = ossClient.listObjects(bucketName, key);
            Assert.assertEquals(objectListing.getObjectSummaries().size(), 1);
            
            ossClient.deleteObject(bucketName, key);
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }
    
    /**
     * 大并发时请求时间不变，正常完成
     */
    @Test
    public void testConcurrentOperationsNormal() throws Exception {
        String key = "test-concurrent-operation-normal";

        try {
            ossClient.getClientConfiguration().setRequestTimeout(100 * 1000);
            
            Thread threads[] = new Thread[100];
            for (int i = 0; i < 100; i++) {
                threads[i] = new OperationThread(key + i);
            }
            
            for (int i = 0; i < 100; i++) {
                threads[i].start();
            }
            
            for (int i = 0; i < 100; i++) {
                threads[i].join();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }
    
    class OperationThread extends Thread {
        private String key;

        public OperationThread(String key) {
            this.key = key;
        }

        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    ossClient.putObject(bucketName, key, TestUtils.genFixedLengthInputStream(1024 * 10));
                    OSSObject ossObject = ossClient.getObject(bucketName, key);
                    ossObject.getObjectContent().close();
                    ossClient.deleteObject(bucketName, key);
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                }

            }
        }
    }
    
    /**
     * 多个OSSClient，丢包的超时，不丢包的正常完成
     */
    @Test
    public void testMultiOssClientIndependent() throws Exception {
        String key = "test-multi-client-independent";

        try {
            ClientConfiguration config = new ClientConfiguration();
            config.setRequestTimeout(1);
            config.setRequestTimeoutEnabled(true);
            config.setMaxConnections(1);

            OSSClient client = new OSSClient(endpoint, accessId, accessKey, config);
                        
            Thread threads[] = new Thread[10];
            for (int i = 0; i < 10; i++) {
                if (i % 2 == 0) {
                    threads[i] = new TimeoutOperationThread(client, key + i);
                } else {
                    threads[i] = new OperationThread(key + i); 
                }
            }
            
            for (int i = 0; i < 10; i++) {
                threads[i].start();
            }
            
            for (int i = 0; i < 10; i++) {
                threads[i].join();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

    }
    
    class TimeoutOperationThread extends Thread {
        private String key;
        private OSSClient client;
        
        public TimeoutOperationThread(OSSClient client, String key) {
            this.client = client;
            this.key = key;
        }

        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    client.putObject(bucketName, key, TestUtils.genFixedLengthInputStream(1024 * 10));
                    Assert.fail("Put object should not be successful");
                } catch (ClientException e) {
                    Assert.assertEquals(OSSErrorCode.REQUEST_TIMEOUT, e.getErrorCode());
                } 
                
                try {
                    client.getObject(bucketName, key);
                    Assert.fail("Get object should not be successful");
                } catch (ClientException e) {
                    Assert.assertEquals(OSSErrorCode.REQUEST_TIMEOUT, e.getErrorCode());
                } 
                
                try {
                    client.deleteObject(bucketName, key);
                    Assert.fail("Delete object should not be successful");
                } catch (ClientException e) {
                    Assert.assertEquals(OSSErrorCode.REQUEST_TIMEOUT, e.getErrorCode());
                }
            }
        }
    }
     
    /**
     * 超时参数不影响原来的参数，测试连接超时
     */
    @Test
    public void testClientConfigIndependent() throws Exception {
        String key = "test-client-config-independent";
        
        ClientConfiguration config = new ClientConfiguration();
        config.setRequestTimeout(requestTimeout);
        config.setRequestTimeoutEnabled(true);
        config.setConnectionTimeout(1);

        OSSClient client = new OSSClient(endpoint, accessId, accessKey, config);

        try {
            client.putObject(bucketName, key, TestUtils.genFixedLengthInputStream(1024));
            Assert.fail("Put object should not be successful");
        } catch (ClientException e) {
            Assert.assertEquals(ClientErrorCode.CONNECTION_TIMEOUT, e.getErrorCode());
        } finally {
            client.shutdown();
        }

    }
    
    /**
     * 超时后正常退出
     */
    @Test
    public void testExitNormalAfterTimeout() throws Exception {
        String key = "test-exit-after-timeout";
        
        ClientConfiguration config = new ClientConfiguration();
        config.setRequestTimeout(requestTimeout);
        config.setRequestTimeoutEnabled(true);
        config.setMaxConnections(1);

        OSSClient client = new OSSClient(endpoint, accessId, accessKey, config);

        try {
            client.putObject(bucketName, key, TestUtils.genFixedLengthInputStream(1024 * 10));
            Assert.fail("Put object should not be successful");
        } catch (ClientException e) {
            Assert.assertEquals(OSSErrorCode.REQUEST_TIMEOUT, e.getErrorCode());
        }
    }
    
}
