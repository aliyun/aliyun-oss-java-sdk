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

import com.aliyun.kms.KmsTransferAcsClient;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSEncryptionClient;
import com.aliyun.oss.OSSEncryptionClientBuilder;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.crypto.ContentCryptoMaterialRW;
import com.aliyun.oss.crypto.EncryptionMaterials;
import com.aliyun.oss.crypto.MultipartUploadCryptoContext;
import com.aliyun.oss.model.*;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.kms.model.v20160120.DecryptRequest;
import com.aliyuncs.kms.model.v20160120.DecryptResponse;
import com.aliyuncs.kms.model.v20160120.EncryptRequest;
import com.aliyuncs.kms.model.v20160120.EncryptResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

@Ignore
public class EncryptionClientKmsTest extends TestBase {
    private OSSEncryptionClient ossEncryptionClient;
    private Map<String, String> matDesc;
    private String kmsRegion;

    public void setUp() throws Exception {
        super.setUp();

        matDesc = new HashMap<String, String>();
        matDesc.put("Desc1-Key1", "Desc1-Value1");
        kmsRegion = TestConfig.KMS_REGION;
        KmsEncryptionMaterialsV3 encryptionMaterials = new KmsEncryptionMaterialsV3(kmsRegion, TestConfig.KMS_CMK_ID, matDesc);
        Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID,
                TestConfig.OSS_TEST_ACCESS_KEY_SECRET);

        encryptionMaterials.setKmsCredentialsProvider(new DefaultCredentialProvider(credentials));
        ossEncryptionClient = new OSSEncryptionClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                new DefaultCredentialProvider(credentials), encryptionMaterials);
    }

    public void tearDown() throws Exception {
        if (ossEncryptionClient != null) {
            ossEncryptionClient.shutdown();
            ossEncryptionClient = null;
        }
        super.tearDown();
    }

    @Test
    public void testPutAndGetFromInputStream() {
        try {
            String key = "encryption-client-test-put-get-inputstream";
            final String content = "qwertyuuihonkffttdctgvbkhiijojilkmkeowirnskdnsiwi93729741084084875dfdf212fa";

            PutObjectRequest req = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(content.getBytes()));
            ossEncryptionClient.putObject(req);

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            OSSObject ossObject = ossEncryptionClient.getObject(getObjectRequest);
            String readContent = readOSSObject(ossObject);
            Assert.assertEquals(content, readContent.toString());
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetUnEncryptedObject() {
        try {
            String key = "test-get-unencryted-object";
            final String content = "qwertyuuihonkffttdctgvbkhiijojilkmkeowirnskdnsiwi93729741084084875dfdf212fa";

            PutObjectRequest req = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(content.getBytes()));
            ossClient.putObject(req);

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            OSSObject ossObject = ossEncryptionClient.getObject(getObjectRequest);
            String readContent = readOSSObject(ossObject);
            Assert.assertEquals(content, readContent.toString());
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPutGetObjectWithFile() {
        try {
            String key = "encryption-client-test-put-get-file.txt";
            String filePathNew = key + "-new.txt";
            File file = createSampleFile(key, 1024 * 500);

            PutObjectRequest req = new PutObjectRequest(bucketName, key, file);
            ossEncryptionClient.putObject(req);

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            ossEncryptionClient.getObject(getObjectRequest, new File(filePathNew));

            File fileNew = new File(filePathNew);
            Assert.assertTrue("comparte file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetObjectToFileWithRangeGet() {
        try {
            String key = "encryption-client-test-get-file-range-get";
            String filePathNew = key + "-new.txt";
            File file = createSampleFile(key, 1024 * 500);

            PutObjectRequest req = new PutObjectRequest(bucketName, key, file);
            ossEncryptionClient.putObject(req);
            int rangeLower = 1321;
            int rangeUpper = 1500;
            int range = rangeUpper - rangeLower + 1;
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            getObjectRequest.setRange(rangeLower, rangeUpper);
            ossEncryptionClient.getObject(getObjectRequest, new File(filePathNew));

            InputStream is1 = new FileInputStream(file);
            byte[] buffer1 = new byte[1024];
            is1.skip(rangeLower);
            int len1 = is1.read(buffer1, 0, range);
            is1.close();
            Assert.assertEquals(range, len1);

            File fileNew = new File(filePathNew);
            InputStream is2 = new FileInputStream(fileNew);
            byte[] buffer2 = new byte[1024];
            int len2 = is2.read(buffer2);
            is2.close();
            Assert.assertEquals(range, len2);
            Assert.assertTrue(Arrays.equals(buffer1, buffer2));
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetObjectWithRangeGet() {
        try {
            final String content = "qwertyuuihonkffttdctgvbkhiijojilkmkeowirnskdnsiwi93729741084084875vydrdrrdrwdfdfds"
                    + "qwertyuiopdfghjklcvbnm,rertyufghcvb";
            Assert.assertEquals(content.length(), 117);
            int maxRange = content.length() - 1;

            try {
                checkRangeGet(content, 20, 10);
                Assert.fail("range[0] > range[1] is not allowed.");
            } catch (ClientException e) {
                // Excepted excption.
            }

            try {
                checkRangeGet(content, -1, 3);
                Assert.fail("range[0] < 0 is not allowed");
            } catch (ClientException e) {
                // Excepted excption.
            }

            try {
                checkRangeGet(content, 20, -1);
                Assert.fail("range[1] < 0 is not allowed");
            } catch (ClientException e) {
                // Excepted excption.
            }

            checkRangeGet(content, 40, maxRange - 20);
            checkRangeGet(content, 40, maxRange);
            checkRangeGet(content, 40, maxRange + 100);
            checkRangeGet(content, maxRange, maxRange + 100);
            checkRangeGet(content, maxRange + 100, maxRange + 200);
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private void checkRangeGet(final String content, final int lowRange, final int highRange) throws Throwable {
        // oss encryption client do range-get
        String key = "encrytion-client-test-range-get-" + String.valueOf(lowRange) + "~" + String.valueOf(highRange);
        PutObjectRequest req = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(content.getBytes()));
        ossEncryptionClient.putObject(req);
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        getObjectRequest.setRange(lowRange, highRange);
        OSSObject ossObject = ossEncryptionClient.getObject(getObjectRequest);
        String result1 = readOSSObject(ossObject);

        // oss client do range-get
        key = "normal-client-test-range-get-" + String.valueOf(lowRange) + "~" + String.valueOf(highRange);
        req = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(content.getBytes()));
        ossClient.putObject(req);
        getObjectRequest = new GetObjectRequest(bucketName, key);
        getObjectRequest.setRange(lowRange, highRange);
        ossObject = ossClient.getObject(getObjectRequest);
        String result2 = readOSSObject(ossObject);

        Assert.assertEquals(result2, result1);
    }

    private String readOSSObject(OSSObject ossObject) throws Throwable {
        BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        return buffer.toString();
    }

    @Test
    public void testUploadPart() throws Throwable {
        String key = "requestpayment-test-upload-part-object";
        File file = createSampleFile(key, 500 * 1024);

        long fileLength = file.length();
        final long partSize = 100 * 1024L; // 100K
        MultipartUploadCryptoContext context = new MultipartUploadCryptoContext();
        context.setPartSize(partSize);
        context.setDataSize(fileLength);
        // Upload part by ossEncryptionClient
        try { 
            InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(bucketName, key);
            InitiateMultipartUploadResult upresult = ossEncryptionClient.initiateMultipartUpload(initiateMultipartUploadRequest, context);
            String uploadId = upresult.getUploadId();

            Assert.assertEquals(context.getUploadId(), uploadId);
            Assert.assertEquals(context.getPartSize(), partSize);
            Assert.assertNotNull(context.getUploadId());
            Assert.assertNotNull(context.getContentCryptoMaterial());

            // Create partETags
            List<PartETag> partETags = new ArrayList<PartETag>();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            // Upload part
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                InputStream instream = new FileInputStream(file);
                instream.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(key);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(instream);
                uploadPartRequest.setPartSize(curPartSize);
                uploadPartRequest.setPartNumber(i + 1);
                UploadPartResult uploadPartResult = ossEncryptionClient.uploadPart(uploadPartRequest, context);
                partETags.add(uploadPartResult.getPartETag());
            }
            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
                    bucketName, key, uploadId, partETags);
            ossEncryptionClient.completeMultipartUpload(completeMultipartUploadRequest);

            String filePathNew = key + "-new.txt";
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            ossEncryptionClient.getObject(getObjectRequest, new File(filePathNew));
            File fileNew = new File(filePathNew);
            Assert.assertTrue("compare file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            fileNew.delete();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            ossClient.deleteObject(bucketName, key);
        }
    }

    @Test
    public void testFindKeyByDescirtion() throws Throwable {
        try {
            String key = "encryption-client-test-put-get-inputstream";
            final String content = "qwertyuuihonkffttdctgvbkhiijojilkmkeowirnskdnsiwi93729741084084875dfdf212fa";

            // Put get object by default encryption client
            PutObjectRequest req = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(content.getBytes()));
            ossEncryptionClient.putObject(req);
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            OSSObject ossObject = ossEncryptionClient.getObject(getObjectRequest);
            String readContent = readOSSObject(ossObject);
            Assert.assertEquals(content, readContent.toString());

            // Create new encryption materials.
            String newRegion = TestConfig.KMS_REGION_1;
            String newCmkId = TestConfig.KMS_CMK_ID_1;
            KmsEncryptionMaterialsV3 encryptionMaterials = new KmsEncryptionMaterialsV3(newRegion, newCmkId);
            Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);

            encryptionMaterials.setKmsCredentialsProvider(new DefaultCredentialProvider(credentials));
            // New encryption client has no object encryption materials, should be get
            // object faild.
            OSSEncryptionClient ossEncryptionClient2 = new OSSEncryptionClientBuilder().build(
                    TestConfig.OSS_TEST_ENDPOINT, new DefaultCredentialProvider(credentials), encryptionMaterials);
            try {
                getObjectRequest = new GetObjectRequest(bucketName, key);
                ossEncryptionClient2.getObject(getObjectRequest);
                Assert.fail("Should be failed here.");
            } catch (Exception e) {
                // Expected exception.
            } finally {
                ossEncryptionClient2.shutdown();
            }

            // New encryption client has the object key, should be get object success.
            encryptionMaterials.addKmsDescMaterial(kmsRegion, matDesc);
            ossEncryptionClient2 = new OSSEncryptionClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(credentials), encryptionMaterials);

            getObjectRequest = new GetObjectRequest(bucketName, key);
            ossObject = ossEncryptionClient2.getObject(getObjectRequest);
            readContent = readOSSObject(ossObject);
            ossEncryptionClient2.shutdown();
            Assert.assertEquals(content, readContent.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMatDescNull() {
        String key = "encryption-client-test-desc-null";
        final String content = "qwertyuuihonkffttdctgvbkhiijojilkmkeowirnskdnsiwi93729741084084875dfdf212fa";
        String region2 = null;
        Credentials credentials = new DefaultCredentials(TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
        
        try {
            // Create new encryption materials.
            region2 = TestConfig.KMS_REGION;
            String cmk2 = TestConfig.KMS_CMK_ID;
            KmsEncryptionMaterialsV3 encryptionMaterials = new KmsEncryptionMaterialsV3(region2, cmk2, null);
            encryptionMaterials.setKmsCredentialsProvider(new DefaultCredentialProvider(credentials));

            OSSEncryptionClient encryptionClient2 = new OSSEncryptionClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(credentials), encryptionMaterials);

            PutObjectRequest req = new PutObjectRequest(bucketName, key, new ByteArrayInputStream(content.getBytes()));
            encryptionClient2.putObject(req);
            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            OSSObject ossObject = encryptionClient2.getObject(getObjectRequest);
            String readContent = readOSSObject(ossObject);
            Assert.assertEquals(content, readContent.toString());
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }

        try {
            String newRegion = TestConfig.KMS_REGION_1;
            String newCmkId = TestConfig.KMS_CMK_ID_1;
            Map<String, String> desc = new HashMap<String, String>();
            desc.put("desc", "test-kms");
            KmsEncryptionMaterialsV3 encryptionMaterials = new KmsEncryptionMaterialsV3(newRegion, newCmkId, desc);
            encryptionMaterials.setKmsCredentialsProvider(new DefaultCredentialProvider(credentials));
            OSSEncryptionClient encryptionClient3 = new OSSEncryptionClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                    new DefaultCredentialProvider(credentials), encryptionMaterials);

            try {
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
                encryptionClient3.getObject(getObjectRequest);
                Assert.fail("Has no correct region, should be failed.");
            } catch (Exception e) {
                // Expected exception.
            } finally {
                encryptionClient3.shutdown();
            }

            // new encryption client has the correct key pair, should be get object success.
            try {
                encryptionMaterials.addKmsDescMaterial(region2, null);
                encryptionClient3 = new OSSEncryptionClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT,
                        new DefaultCredentialProvider(credentials), encryptionMaterials);
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
                OSSObject ossObject = encryptionClient3.getObject(getObjectRequest);
                String readContent = readOSSObject(ossObject);
                Assert.assertEquals(content, readContent.toString());
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(e.getMessage());
            } finally {
                encryptionClient3.shutdown();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testUploadFile() {
        try {
            String key = "encryption-client-uploadfile.txt";
            String filePathNew = key + "-new.txt";
            File file = createSampleFile(key, 1024 * 500);

            UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, key);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
            uploadFileRequest.setPartSize(1 * 100 * 1024);
            uploadFileRequest.setEnableCheckpoint(true);
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.addUserMetadata("prop", "propval");
            uploadFileRequest.setObjectMetadata(objMetadata);

            ossEncryptionClient.uploadFile(uploadFileRequest);

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            ObjectMetadata objectMetadata = ossEncryptionClient.getObject(getObjectRequest, new File(filePathNew));
            Assert.assertEquals(file.length(), objectMetadata.getContentLength());
            Assert.assertEquals("propval", objectMetadata.getUserMetadata().get("prop"));

            File fileNew = new File(filePathNew);
            Assert.assertTrue("comparte file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDownloadBigFile() {
        try {
            String key = "encryption-client-downloadfile.txt";
            String filePathNew = key + "-new.txt";
            File file = createSampleFile(key, 2 * 1024 * 1024);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
            ObjectMetadata objMetadata = new ObjectMetadata();
            objMetadata.addUserMetadata("prop", "propval");
            putObjectRequest.setMetadata(objMetadata);
            ossEncryptionClient.putObject(putObjectRequest);

            String downloadFile = key + "-down.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(downloadFile);
            downloadFileRequest.setPartSize(1 * 100 * 1024);
            downloadFileRequest.setTaskNum(5);
            downloadFileRequest.setEnableCheckpoint(true);

            DownloadFileResult result = ossEncryptionClient.downloadFile(downloadFileRequest);
            Assert.assertEquals("propval", result.getObjectMetadata().getUserMetadata().get("prop"));

            File fileNew = new File(downloadFile);
            Assert.assertTrue("compare file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            fileNew.delete();
        } catch (Throwable e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDownloadSmallFile() throws Throwable {
        try {
            String key = "encryption-client-downloadfile.txt";
            String filePathNew = key + "-new.txt";
            File file = createSampleFile(key, 200 * 1024);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
            ossEncryptionClient.putObject(putObjectRequest);

            String downloadFile = key + "-down.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(downloadFile);
            downloadFileRequest.setPartSize(500 * 1024);
            downloadFileRequest.setTaskNum(5);
            downloadFileRequest.setEnableCheckpoint(true);

            DownloadFileResult result = ossEncryptionClient.downloadFile(downloadFileRequest);
            Assert.assertEquals(file.length(), result.getObjectMetadata().getContentLength());

            File fileNew = new File(downloadFile);
            Assert.assertTrue("compare file", compareFile(file.getAbsolutePath(), fileNew.getAbsolutePath()));
            fileNew.delete();
        } catch (Throwable e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDownloadFileWithWrongPartSize() throws Throwable {
        try {
            String key = "encryption-client-downloadfile.txt";
            String filePathNew = key + "-new.txt";
            File file = createSampleFile(key, 2 * 1024 * 1024);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
            ossEncryptionClient.putObject(putObjectRequest);

            String downloadFile = key + "-down.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(downloadFile);
            downloadFileRequest.setPartSize(1 * 100 * 1024 + 1);
            downloadFileRequest.setTaskNum(5);
            downloadFileRequest.setEnableCheckpoint(true);

            ossEncryptionClient.downloadFile(downloadFileRequest);
            Assert.fail("the part size is not 16 bytes alignment. should be failed.");
        } catch (IllegalArgumentException e) {
            // Expected exception.
        } catch (Throwable e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDownloadBigFileWithRangeGet() throws Throwable {
        try {
            String key = "encryption-client-download-big-file-range-get.txt";
            File file = createSampleFile(key, 5 * 500 * 1024);

            int rangeLower = 500;
            int rangeUpper = rangeLower + 500 * 1024;
            int range = rangeUpper - rangeLower + 1;

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
            ossEncryptionClient.putObject(putObjectRequest);

            String downloadFile = key + "-down.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(downloadFile);
            downloadFileRequest.setPartSize(500 * 1024);
            downloadFileRequest.setTaskNum(5);
            downloadFileRequest.setEnableCheckpoint(true);
            downloadFileRequest.setRange(rangeLower, rangeUpper);

            DownloadFileResult result = ossEncryptionClient.downloadFile(downloadFileRequest);

            InputStream is1 = new FileInputStream(file);
            byte[] buffer1 = new byte[1024 * 600];
            is1.skip(rangeLower);
            int len1 = is1.read(buffer1, 0, range);
            is1.close();
            Assert.assertEquals(range, len1);

            File fileNew = new File(downloadFile);
            InputStream is2 = new FileInputStream(fileNew);
            byte[] buffer2 = new byte[1024 * 600];
            int len2 = is2.read(buffer2);
            is2.close();
            Assert.assertEquals(range, len2);
            Assert.assertTrue(Arrays.equals(buffer1, buffer2));
            fileNew.delete();
        } catch (Throwable e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDownloadSmallFileWithRangeGet() throws Throwable {
        try {
            String key = "encryption-client-download-smal-file-range-get.txt";
            File file = createSampleFile(key, 200 * 1024);

            int rangeLower = 18;
            int rangeUpper = 100;
            int range = rangeUpper - rangeLower + 1;

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);
            ossEncryptionClient.putObject(putObjectRequest);

            String downloadFile = key + "-down.txt";
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, key);
            downloadFileRequest.setDownloadFile(downloadFile);
            downloadFileRequest.setPartSize(500 * 1024);
            downloadFileRequest.setTaskNum(5);
            downloadFileRequest.setEnableCheckpoint(true);
            downloadFileRequest.setRange(rangeLower, rangeUpper);

            DownloadFileResult result = ossEncryptionClient.downloadFile(downloadFileRequest);

            InputStream is1 = new FileInputStream(file);
            byte[] buffer1 = new byte[1024];
            is1.skip(rangeLower);
            int len1 = is1.read(buffer1, 0, range);
            is1.close();
            Assert.assertEquals(range, len1);

            File fileNew = new File(downloadFile);
            InputStream is2 = new FileInputStream(fileNew);
            byte[] buffer2 = new byte[1024];
            int len2 = is2.read(buffer2);
            is2.close();
            Assert.assertEquals(range, len2);
            Assert.assertTrue(Arrays.equals(buffer1, buffer2));
            fileNew.delete();
        } catch (Throwable e){
            Assert.fail(e.getMessage());
        }
    }

    static class KmsEncryptionMaterialsV3 implements EncryptionMaterials {
        private static final String KEY_WRAP_ALGORITHM = "KMS/ALICLOUD";
        private String region;
        private String cmk;
        CredentialsProvider credentialsProvider;

        private final Map<String, String> desc;
        private final LinkedHashMap<KmsEncryptionMaterialsV3.KmsClientSuite, Map<String, String>> kmsDescMaterials =
                new LinkedHashMap<KmsEncryptionMaterialsV3.KmsClientSuite, Map<String, String>>();

        public KmsEncryptionMaterialsV3(String region, String cmk) {
            assertParameterNotNull(region, "kms region");
            assertParameterNotNull(cmk, "kms cmk");
            this.region = region;
            this.cmk = cmk;
            this.desc = new HashMap<String, String>();
        }

        public KmsEncryptionMaterialsV3(String region, String cmk, Map<String, String> desc) {
            assertParameterNotNull(region, "kms region");
            assertParameterNotNull(region, "kms cmk");
            this.region = region;
            this.cmk = cmk;
            this.desc = (desc == null) ? new HashMap<String, String>() : new HashMap<String, String>(desc);
        }

        private final class KmsClientSuite {
            private String region;
            private CredentialsProvider credentialsProvider;
            KmsClientSuite(String region, CredentialsProvider credentialsProvider) {
                this.region = region;
                this.credentialsProvider = credentialsProvider;
            }
        }

        public void setKmsCredentialsProvider(CredentialsProvider credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
            kmsDescMaterials.put(new KmsEncryptionMaterialsV3.KmsClientSuite(region, credentialsProvider), desc);
        }

        private DefaultAcsClient createKmsClient(String region, CredentialsProvider credentialsPorvider) {
            Credentials credentials = credentialsPorvider.getCredentials();
            IClientProfile profile = DefaultProfile.getProfile(region, credentials.getAccessKeyId(),
                    credentials.getSecretAccessKey(), credentials.getSecurityToken());
            return new KmsTransferAcsClient(profile);
        }

        private EncryptResponse encryptPlainText(String keyId, String plainText) throws ClientException {
            DefaultAcsClient kmsClient = createKmsClient(region, credentialsProvider);
            final EncryptRequest encReq = new EncryptRequest();
            encReq.setSysProtocol(ProtocolType.HTTPS);
            encReq.setAcceptFormat(FormatType.JSON);
            encReq.setSysMethod(MethodType.POST);
            encReq.setKeyId(keyId);
            encReq.setPlaintext(plainText);

            final EncryptResponse encResponse;
            try {
                encResponse = kmsClient.getAcsResponse(encReq);
            } catch (Exception e) {
                throw new ClientException("the kms client encrypt data failed." + e.getMessage(), e);
            }
            return encResponse;
        }

        private DecryptResponse decryptCipherBlob(KmsEncryptionMaterialsV3.KmsClientSuite kmsClientSuite, String cipherBlob)
                throws ClientException {
            final DefaultAcsClient kmsClient = createKmsClient(kmsClientSuite.region, kmsClientSuite.credentialsProvider);
            final DecryptRequest decReq = new DecryptRequest();
            decReq.setSysProtocol(ProtocolType.HTTPS);
            decReq.setAcceptFormat(FormatType.JSON);
            decReq.setSysMethod(MethodType.POST);
            decReq.setCiphertextBlob(cipherBlob);

            final DecryptResponse decResponse;
            try {
                decResponse = kmsClient.getAcsResponse(decReq);
            } catch (Exception e) {
                throw new ClientException("The kms client decrypt data faild." + e.getMessage(), e);
            }
            return decResponse;
        }

        public void addKmsDescMaterial(String region, Map<String, String> description) {
            addKmsDescMaterial(region, credentialsProvider, description);
        }

        public synchronized void addKmsDescMaterial(String region, CredentialsProvider credentialsProvider, Map<String, String> description) {
            assertParameterNotNull(region, "region");
            assertParameterNotNull(credentialsProvider, "credentialsProvider");
            KmsEncryptionMaterialsV3.KmsClientSuite kmsClientSuite = new KmsEncryptionMaterialsV3.KmsClientSuite(region, credentialsProvider);
            if (description != null) {
                kmsDescMaterials.put(kmsClientSuite, new HashMap<String, String>(description));
            } else {
                kmsDescMaterials.put(kmsClientSuite, new HashMap<String, String>());
            }
        }

        private KmsEncryptionMaterialsV3.KmsClientSuite findKmsClientSuiteByDescription(Map<String, String> desc) {
            if (desc == null) {
                return null;
            }
            for (Map.Entry<KmsEncryptionMaterialsV3.KmsClientSuite, Map<String, String>> entry : kmsDescMaterials.entrySet()) {
                if (desc.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
            return null;
        }

        private <K, V> Map.Entry<K, V> getTailByReflection(LinkedHashMap<K, V> map)
                throws NoSuchFieldException, IllegalAccessException {
            Field tail = map.getClass().getDeclaredField("tail");
            tail.setAccessible(true);
            return (Map.Entry<K, V>) tail.get(map);
        }

        @Override
        public void encryptCEK(ContentCryptoMaterialRW contentMaterialRW) {
            try {
                assertParameterNotNull(contentMaterialRW, "contentMaterialRW");
                assertParameterNotNull(contentMaterialRW.getIV(), "contentMaterialRW#getIV");
                assertParameterNotNull(contentMaterialRW.getCEK(), "contentMaterialRW#getCEK");

                byte[] iv = contentMaterialRW.getIV();
                EncryptResponse encryptresponse = encryptPlainText(cmk, BinaryUtil.toBase64String(iv));
                byte[] encryptedIV = BinaryUtil.fromBase64String(encryptresponse.getCiphertextBlob());

                SecretKey cek = contentMaterialRW.getCEK();
                encryptresponse = encryptPlainText(cmk, BinaryUtil.toBase64String(cek.getEncoded()));
                byte[] encryptedCEK = BinaryUtil.fromBase64String(encryptresponse.getCiphertextBlob());

                contentMaterialRW.setEncryptedCEK(encryptedCEK);
                contentMaterialRW.setEncryptedIV(encryptedIV);
                contentMaterialRW.setKeyWrapAlgorithm(KEY_WRAP_ALGORITHM);
                contentMaterialRW.setMaterialsDescription(desc);
            } catch (Exception e) {
                throw new ClientException("Kms encrypt CEK IV error. "
                        + "Please check your cmk, region, accessKeyId and accessSecretId." + e.getMessage(), e);
            }
        }

        @Override
        public void decryptCEK(ContentCryptoMaterialRW contentMaterialRW) {
            assertParameterNotNull(contentMaterialRW, "ContentCryptoMaterialRW");
            assertParameterNotNull(contentMaterialRW.getEncryptedCEK(), "ContentCryptoMaterialRW#getEncryptedCEK");
            assertParameterNotNull(contentMaterialRW.getEncryptedIV(), "ContentCryptoMaterialRW#getEncryptedIV");
            assertParameterNotNull(contentMaterialRW.getKeyWrapAlgorithm(), "ContentCryptoMaterialRW#getKeyWrapAlgorithm");

            if (!contentMaterialRW.getKeyWrapAlgorithm().toLowerCase().equals(KEY_WRAP_ALGORITHM.toLowerCase())) {
                throw new ClientException(
                        "Unrecognize your object key wrap algorithm: " + contentMaterialRW.getKeyWrapAlgorithm());
            }

            try {
                KmsEncryptionMaterialsV3.KmsClientSuite kmsClientSuite = findKmsClientSuiteByDescription(contentMaterialRW.getMaterialsDescription());
                if (kmsClientSuite == null) {
                    Map.Entry<KmsEncryptionMaterialsV3.KmsClientSuite, Map<String, String>> entry = getTailByReflection(kmsDescMaterials);
                    kmsClientSuite = entry.getKey();
                }

                DecryptResponse decryptIvResp = decryptCipherBlob(kmsClientSuite,
                        BinaryUtil.toBase64String(contentMaterialRW.getEncryptedIV()));
                byte[] iv = BinaryUtil.fromBase64String(decryptIvResp.getPlaintext());

                DecryptResponse decryptCEKResp = decryptCipherBlob(kmsClientSuite,
                        BinaryUtil.toBase64String(contentMaterialRW.getEncryptedCEK()));
                byte[] cekBytes = BinaryUtil.fromBase64String(decryptCEKResp.getPlaintext());
                SecretKey cek = new SecretKeySpec(cekBytes, "");

                contentMaterialRW.setCEK(cek);
                contentMaterialRW.setIV(iv);
            } catch (Exception e) {
                throw new ClientException("Unable to decrypt content secured key and iv. "
                        + "Please check your kms region and materails description." + e.getMessage(), e);
            }
        }

        private void assertParameterNotNull(Object parameterValue, String errorMessage) {
            if (parameterValue == null)
                throw new IllegalArgumentException(errorMessage);
        }
    }
}
