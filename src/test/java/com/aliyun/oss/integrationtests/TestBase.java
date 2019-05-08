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

import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;
import static com.aliyun.oss.model.DeleteObjectsRequest.DELETE_OBJECTS_ONETIME_LIMIT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.aliyun.oss.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.common.utils.HttpUtil;

public class TestBase {
    
    protected static String bucketName;
    protected static OSSClient ossClient;
    
    protected static final String DEFAULT_ENCODING_TYPE = "url";
    protected static final String APPENDABLE_OBJECT_TYPE = "Appendable";
    protected static final int LIST_PART_MAX_RETURNS = 1000;
    protected static final String INVALID_ENDPOINT = "http://InvalidEndpoint";
    protected static final String INVALID_ACCESS_ID = "InvalidAccessId";
    protected static final String INVALID_ACCESS_KEY = "InvalidAccessKey";
    
    protected static final String BUCKET_NAME_PREFIX = "oss-java-sdk-";
    protected static final String USER_DIR = System.getProperty("user.dir");
    protected static final String UPLOAD_DIR = USER_DIR + File.separator + "upload" + File.separator;
    protected static final String DOWNLOAD_DIR = USER_DIR + File.separator + "download" + File.separator;

    protected static final int REQUEST_ID_LEN = "5A016E35CB3DB13FD2BAAB3A".length();
   
    @BeforeClass
    public static void oneTimeSetUp() {
        cleanUpAllBuckets(getOSSClient(), BUCKET_NAME_PREFIX);
    }
    
    @Before
    public void setUp() throws Exception {
        bucketName = createBucket();
    }

    @After
    public void tearDown() throws Exception {
        deleteBucket(bucketName);
        cleanUp();
    }
    
    public static OSSClient getOSSClient() {
        if (ossClient == null) {
            resetTestConfig();
            ClientConfiguration conf = new ClientConfiguration().setSupportCname(false);
            Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
            ossClient = new OSSClient(TestConfig.OSS_TEST_ENDPOINT, new DefaultCredentialProvider(credentials), conf);
        }
        return ossClient;
    }
    
    public static String createBucket() {
        long ticks = new Date().getTime() / 1000 + new Random().nextInt(5000);
        String bucketName = BUCKET_NAME_PREFIX + ticks;
        getOSSClient().createBucket(bucketName);
        waitForCacheExpiration(2);
        return bucketName;
    }
    
    public static void deleteBucket(String bucketName) {
        abortAllMultipartUploads(getOSSClient(), bucketName);
        deleteBucketWithObjects(getOSSClient(), bucketName);
    }
    
    protected static void deleteBucketWithObjects(OSSClient client, String bucketName) {
        if (!client.doesBucketExist(bucketName)) {
            return;
        }

        // delete objects
        List<DeleteObjectsRequest.KeyVersion> allObjects = listAllObjects(client, bucketName);
        int total = allObjects.size();
        if (total > 0) {
            int opLoops = total / DELETE_OBJECTS_ONETIME_LIMIT;
            if (total % DELETE_OBJECTS_ONETIME_LIMIT != 0) {
                opLoops++;
            }
            
            List<DeleteObjectsRequest.KeyVersion> objectsToDel = null;
            for (int i = 0; i < opLoops; i++) {
                int fromIndex = i * DELETE_OBJECTS_ONETIME_LIMIT;
                int len = 0;
                if (total <= DELETE_OBJECTS_ONETIME_LIMIT) {
                    len = total;
                } else {
                    len = (i + 1 == opLoops) ? (total - fromIndex) : DELETE_OBJECTS_ONETIME_LIMIT;                    
                }
                objectsToDel = allObjects.subList(fromIndex, fromIndex + len);
                
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
                deleteObjectsRequest.setEncodingType(DEFAULT_ENCODING_TYPE);
                deleteObjectsRequest.setKeys(objectsToDel);
                client.deleteObjects(deleteObjectsRequest);
            }
        }
        
        // delete live channels
        List<LiveChannel> channels = ossClient.listLiveChannels(bucketName);
        for (LiveChannel channel : channels) {
            ossClient.deleteLiveChannel(bucketName, channel.getName());
        }
        
        // delete bucket
        client.deleteBucket(bucketName);
    }


    protected static void deleteBucketWithObjectVersions(OSSClient client, String bucketName) {
        if (!client.doesBucketExist(bucketName)) {
            return;
        }

        // delete objects
        List<DeleteObjectsRequest.KeyVersion> allObjects = listAllObjectsWithVersions(client, bucketName);
        int total = allObjects.size();
        if (total > 0) {
            int opLoops = total / DELETE_OBJECTS_ONETIME_LIMIT;
            if (total % DELETE_OBJECTS_ONETIME_LIMIT != 0) {
                opLoops++;
            }

            List<DeleteObjectsRequest.KeyVersion> objectsToDel = null;
            for (int i = 0; i < opLoops; i++) {
                int fromIndex = i * DELETE_OBJECTS_ONETIME_LIMIT;
                int len = 0;
                if (total <= DELETE_OBJECTS_ONETIME_LIMIT) {
                    len = total;
                } else {
                    len = (i + 1 == opLoops) ? (total - fromIndex) : DELETE_OBJECTS_ONETIME_LIMIT;
                }
                objectsToDel = allObjects.subList(fromIndex, fromIndex + len);

                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
                deleteObjectsRequest.setEncodingType(DEFAULT_ENCODING_TYPE);
                deleteObjectsRequest.setKeys(objectsToDel);

                client.deleteObjects(deleteObjectsRequest);
            }
        }

        // delete live channels
        List<LiveChannel> channels = ossClient.listLiveChannels(bucketName);
        for (LiveChannel channel : channels) {
            ossClient.deleteLiveChannel(bucketName, channel.getName());
        }

        // delete bucket
        client.deleteBucket(bucketName);
    }
    
    protected static void abortAllMultipartUploads(OSSClient client, String bucketName) {        
        if (!client.doesBucketExist(bucketName)) {
            return;
        }
        
        String keyMarker = null;
        String uploadIdMarker = null;
        ListMultipartUploadsRequest listMultipartUploadsRequest = null;
        MultipartUploadListing multipartUploadListing = null;
        List<MultipartUpload> multipartUploads = null;
        do {
            listMultipartUploadsRequest = new ListMultipartUploadsRequest(bucketName);
            listMultipartUploadsRequest.setKeyMarker(keyMarker);
            listMultipartUploadsRequest.setUploadIdMarker(uploadIdMarker);
            
            multipartUploadListing = client.listMultipartUploads(listMultipartUploadsRequest);
            multipartUploads = multipartUploadListing.getMultipartUploads();
            for (MultipartUpload mu : multipartUploads) {
                String key = mu.getKey();
                String uploadId = mu.getUploadId();
                client.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, key, uploadId));
            }
            
            keyMarker = multipartUploadListing.getKeyMarker();
            uploadIdMarker = multipartUploadListing.getUploadIdMarker();
        } while (multipartUploadListing != null && multipartUploadListing.isTruncated());
    }
    
    protected static List<DeleteObjectsRequest.KeyVersion> listAllObjects(OSSClient client, String bucketName) {
        List<DeleteObjectsRequest.KeyVersion> objs = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        ObjectListing objectListing = null;
        String nextMarker = null;
        
        do {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName, null, nextMarker, null, 
                    DELETE_OBJECTS_ONETIME_LIMIT);
            listObjectsRequest.setEncodingType(DEFAULT_ENCODING_TYPE);
            objectListing = client.listObjects(listObjectsRequest);
            if (DEFAULT_ENCODING_TYPE.equals(objectListing.getEncodingType())) {
                nextMarker = HttpUtil.urlDecode(objectListing.getNextMarker(), "UTF-8");
            } else {
                nextMarker = objectListing.getNextMarker();
            }
            
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                if (DEFAULT_ENCODING_TYPE.equals(objectListing.getEncodingType())) {
                    DeleteObjectsRequest.KeyVersion keyVersion = new DeleteObjectsRequest.KeyVersion(HttpUtil.urlDecode(s.getKey(), "UTF-8"));
                    objs.add(keyVersion);
                } else {
                    DeleteObjectsRequest.KeyVersion keyVersion = new DeleteObjectsRequest.KeyVersion(s.getKey());
                    objs.add(keyVersion);
                }
            }
        } while (objectListing.isTruncated());
        
        return objs;
    }

    protected static List<DeleteObjectsRequest.KeyVersion> listAllObjectsWithVersions(OSSClient client, String bucketName) {
        List<DeleteObjectsRequest.KeyVersion> objs = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        ObjectVersionsListing objectListing = null;
        String keyMarker = null;
        String versionIdMarker = null;

        do {
            ListObjectVersionsRequest listObjectVersionsRequest = new ListObjectVersionsRequest(bucketName, null, keyMarker, versionIdMarker, null,
                    DELETE_OBJECTS_ONETIME_LIMIT);
            listObjectVersionsRequest.setEncodingType(DEFAULT_ENCODING_TYPE);
            objectListing = client.listObjectVersions(listObjectVersionsRequest);
            if (DEFAULT_ENCODING_TYPE.equals(objectListing.getEncodingType())) {
                keyMarker = HttpUtil.urlDecode(objectListing.getNextKeyMarker(), "UTF-8");
                versionIdMarker = HttpUtil.urlDecode(objectListing.getNextVersionIdMarker(), "UTF-8");

            } else {
                keyMarker = objectListing.getNextKeyMarker();
                versionIdMarker = objectListing.getNextVersionIdMarker();
            }

            List<OSSObjectVersionSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectVersionSummary s : sums) {
                if (DEFAULT_ENCODING_TYPE.equals(objectListing.getEncodingType())) {
                    DeleteObjectsRequest.KeyVersion keyVersion = new DeleteObjectsRequest.KeyVersion(HttpUtil.urlDecode(s.getKey(), "UTF-8"),s.getVersionId());
                    objs.add(keyVersion);
                } else {
                    DeleteObjectsRequest.KeyVersion keyVersion = new DeleteObjectsRequest.KeyVersion(s.getKey());
                    objs.add(keyVersion);
                }
            }
        } while (objectListing.isTruncated());

        return objs;
    }
    
    protected static List<String> listAllBuckets(OSSClient client, String bucketPrefix) {
        List<String> bkts = new ArrayList<String>();
        String nextMarker = null;
        BucketList bucketList = null;
        
        do {
            ListBucketsRequest listBucketsRequest = new ListBucketsRequest(bucketPrefix, nextMarker, 
                    ListBucketsRequest.MAX_RETURNED_KEYS);
            bucketList = client.listBuckets(listBucketsRequest);
            nextMarker = bucketList.getNextMarker();
            for (Bucket b : bucketList.getBucketList()) {
                bkts.add(b.getName());
            }
        } while (bucketList.isTruncated());
        
        return bkts;
    }
    
    protected static void cleanUpAllBuckets(OSSClient client, String bucketPrefix) {
        List<String> bkts = listAllBuckets(client, bucketPrefix);
        for (String b : bkts) {
            abortAllMultipartUploads(client, b);
            deleteBucketWithObjects(client, b);
        }
    }

    protected static void cleanUpAllBucketsWithVersion(OSSClient client, String bucketPrefix) {
        List<String> bkts = listAllBuckets(client, bucketPrefix);
        for (String b : bkts) {
            abortAllMultipartUploads(client, b);
            deleteBucketWithObjectVersions(client, b);
        }
    }



    public static void cleanUp() {
        if (ossClient != null) {
            ossClient.shutdown();
            ossClient = null;
        }
    }
     
    public static boolean compareFile(String fileNameLeft, String fileNameRight) throws IOException {
        FileInputStream fisLeft = null;
        FileInputStream fisRight = null;
        
        try {
            fisLeft = new FileInputStream(fileNameLeft);
            fisRight = new FileInputStream(fileNameRight);

            int len1 = fisLeft.available();
            int len2 = fisRight.available();

            if (len1 == len2) { 
                byte[] data1 = new byte[len1];
                byte[] data2 = new byte[len2];

                fisLeft.read(data1);
                fisRight.read(data2);

                for (int i = 0; i < len1; i++) {
                    if (data1[i] != data2[i]) {
                        return false;
                    }
                }
                
                return true;
            } else {
                return false;
            }
        } finally {
            if (fisLeft != null) {
                try {
                    fisLeft.close();
                } catch (IOException e) {
                }
            }
            
            if (fisRight != null) {
                try {
                    fisRight.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    public static File createSampleFile(String fileName, long size) throws IOException {
        File file = File.createTempFile(fileName, ".txt");
        file.deleteOnExit();
        String context = "abcdefghijklmnopqrstuvwxyz0123456789011234567890\n";

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i < size / context.length(); i++) {
            writer.write(context);
        }
        writer.close();

        return file;
    }
    
    public static void resetTestConfig() {
      // test config
      if (TestConfig.OSS_TEST_ENDPOINT == null) {
          TestConfig.OSS_TEST_ENDPOINT = System.getenv().get("OSS_TEST_ENDPOINT");
      }
      
      if (TestConfig.OSS_TEST_REGION == null) {
          TestConfig.OSS_TEST_REGION = System.getenv().get("OSS_TEST_REGION");
      }   
      
      if (TestConfig.OSS_TEST_ACCESS_KEY_ID == null) {
          TestConfig.OSS_TEST_ACCESS_KEY_ID = System.getenv().get("OSS_TEST_ACCESS_KEY_ID");
      }
      
      if (TestConfig.OSS_TEST_ACCESS_KEY_SECRET == null) {
          TestConfig.OSS_TEST_ACCESS_KEY_SECRET = System.getenv().get("OSS_TEST_ACCESS_KEY_SECRET");
      }
      
      if (TestConfig.OSS_TEST_ACCESS_KEY_ID_1 == null) {
          TestConfig.OSS_TEST_ACCESS_KEY_ID_1 = System.getenv().get("OSS_TEST_ACCESS_KEY_ID_1");
          if (TestConfig.OSS_TEST_ACCESS_KEY_ID_1 == null) {
              TestConfig.OSS_TEST_ACCESS_KEY_ID_1 = TestConfig.OSS_TEST_ACCESS_KEY_ID;
          }
      }
      
      if (TestConfig.OSS_TEST_ACCESS_KEY_SECRET_1 == null) {
          TestConfig.OSS_TEST_ACCESS_KEY_SECRET_1 = System.getenv().get("OSS_TEST_ACCESS_KEY_SECRET_1");
          if (TestConfig.OSS_TEST_ACCESS_KEY_SECRET_1 == null) {
              TestConfig.OSS_TEST_ACCESS_KEY_SECRET_1 = TestConfig.OSS_TEST_ACCESS_KEY_SECRET;
          }
      }
      
      // replacation config
      if (TestConfig.OSS_TEST_REPLICATION_ENDPOINT == null) {
          TestConfig.OSS_TEST_REPLICATION_ENDPOINT = System.getenv().get("OSS_TEST_REPLICATION_ENDPOINT");
      }
      
      if (TestConfig.OSS_TEST_REPLICATION_ACCESS_KEY_ID == null) {
          TestConfig.OSS_TEST_REPLICATION_ACCESS_KEY_ID = System.getenv().get("OSS_TEST_REPLICATION_ACCESS_KEY_ID");
      }
      
      if (TestConfig.OSS_TEST_REPLICATION_ACCESS_KEY_SECRET == null) {
          TestConfig.OSS_TEST_REPLICATION_ACCESS_KEY_SECRET = System.getenv().get("OSS_TEST_REPLICATION_ACCESS_KEY_SECRET");
      }
      
      // sts test
      if (TestConfig.STS_TEST_ENDPOINT == null) {
          TestConfig.STS_TEST_ENDPOINT = System.getenv().get("OSS_TEST_STS_ENDPOINT");
      }
      
      if (TestConfig.STS_TEST_ROLE == null) {
          TestConfig.STS_TEST_ROLE = System.getenv().get("OSS_TEST_STS_ROLE");
      }
      
      if (TestConfig.STS_TEST_BUCKET == null) {
          TestConfig.STS_TEST_BUCKET = System.getenv().get("OSS_TEST_STS_BUCKET");
      }
      
      // proxy test
      if (TestConfig.PROXY_HOST == null) {
          TestConfig.PROXY_HOST = System.getenv().get("OSS_TEST_PROXY_HOST");
      }
      
      if (TestConfig.PROXY_PORT == -1) {
         TestConfig.PROXY_PORT = 3128;
         String portStr = System.getenv().get("OSS_TEST_PROXY_PORT");
         if (portStr != null) {
             TestConfig.PROXY_PORT = Integer.parseInt(portStr);
         } 
      }
      
      if (TestConfig.PROXY_USER == null) {
          TestConfig.PROXY_USER = System.getenv().get("OSS_TEST_PROXY_USER");
      }
      
      if (TestConfig.PROXY_PASSWORD == null) {
          TestConfig.PROXY_PASSWORD = System.getenv().get("OSS_TEST_PROXY_PASSWORD");
      }
    }

}

