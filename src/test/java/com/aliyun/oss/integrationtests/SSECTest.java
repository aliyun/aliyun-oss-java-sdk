package com.aliyun.oss.integrationtests;

import com.aliyun.oss.common.auth.ServiceSignature;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.HeadObjectRequest;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PartETag;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.SSEAlgorithm;
import com.aliyun.oss.model.SSECustomerKey;
import com.aliyun.oss.model.UploadPartCopyRequest;
import com.aliyun.oss.model.UploadPartCopyResult;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.activation.MimetypesFileTypeMap;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SSECTest extends TestBase {
    private String ssecKey;
    private String anotherSSECKey;
    private List<String> objectName;
    private List<Integer> objectSize;
    private List<Integer> partSize;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ssecKey = BinaryUtil.toBase64String(TestUtils.genRandomString(32).getBytes());
        anotherSSECKey = BinaryUtil.toBase64String(TestUtils.genRandomString(32).getBytes());
        objectSize = Arrays.asList(0, 64 * 1024 - 1, 0, 0, 64 * 1024, 0, 64 * 1024 + 1, 0, 0, 0, 512 * 1024 - 1, 0, 512 * 1024, 0, 512 * 1024 + 1, 0, 0, 10 * 1024 * 1024, 0, 0);
        //objectSize = Arrays.asList(0, 0, 1024, 0);
        objectName = new ArrayList<String>();
        for (int i = 0; i < objectSize.size(); ++i) {
            objectName.add("ssec" + i);
        }
        partSize = new ArrayList<Integer>();
        Random random = new Random();
        for (int i = 0; i < 10; ++i) {
            partSize.add(random.nextInt(5 * 1024 * 1024) + 100 * 1024);
        }
    }

    private void encryptNormalObjectWithSSEC() throws Exception {
        for (int i = 0; i < objectSize.size(); ++i) {
            ObjectMetadata metadata = new ObjectMetadata();
            // 1. invalid algorithm
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.SM4.getAlgorithm());
                ossClient.putObject(bucketName, objectName.get(i), TestUtils.genFixedLengthInputStream(objectSize.get(i)), metadata);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 2. missing customer key
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.AES256.getAlgorithm());
                ossClient.putObject(bucketName, objectName.get(i), TestUtils.genFixedLengthInputStream(objectSize.get(i)), metadata);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 3. missing customer key md5
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey);
                ossClient.putObject(bucketName, objectName.get(i), TestUtils.genFixedLengthInputStream(objectSize.get(i)), metadata);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 4. invalid customer key encoding
            try {
                SSECustomerKey customerKey = new SSECustomerKey(ssecKey + ".random");
                metadata.setSSECustomerKey(customerKey);
                ossClient.putObject(bucketName, objectName.get(i), TestUtils.genFixedLengthInputStream(objectSize.get(i)), metadata);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 5. invalid customer key length
            try {
                SSECustomerKey customerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(16).getBytes()));
                metadata.setSSECustomerKey(customerKey);
                ossClient.putObject(bucketName, objectName.get(i), TestUtils.genFixedLengthInputStream(objectSize.get(i)), metadata);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 6. invalid customer key md5
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.AES256.getAlgorithm());
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey);
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5, "123456");
                ossClient.putObject(bucketName, objectName.get(i), TestUtils.genFixedLengthInputStream(objectSize.get(i)), metadata);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            String fileName = TestUtils.genFixedLengthFile(objectSize.get(i));
            {
                SSECustomerKey customerKey = new SSECustomerKey(ssecKey);
                metadata.setSSECustomerKey(customerKey);
                PutObjectRequest request = new PutObjectRequest(bucketName, objectName.get(i), new File(fileName), metadata);
                request.setProcess("");
                Map<String, String> headers = ossClient.putObject(request).getResponse().getHeaders();

                Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM),
                        SSEAlgorithm.AES256.getAlgorithm());
                Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
                Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5),
                        BinaryUtil.toBase64String(BinaryUtil.calculateMd5(BinaryUtil.fromBase64String(ssecKey))));
            }

            readObject(objectName.get(i), fileName, objectSize.get(i), false);

            ossClient.createSymlink(bucketName, objectName.get(i) + "-symlink", objectName.get(i));
            readObject(objectName.get(i) + "-symlink", fileName, objectSize.get(i), true);
        }
    }

    private void readObject(String objectName, String fileName, int objectSize, boolean symlink) throws Exception {
        readObject(objectName, fileName, objectSize, symlink, ssecKey);
    }

    private void readObject(String objectName, String fileName, int objectSize, boolean symlink, String ssecKey) throws Exception {
        // 1. invalid algorithm
        SSECustomerKey sseCustomerKey = null;
        try {
            sseCustomerKey = new SSECustomerKey("12345", SSEAlgorithm.SM4.getAlgorithm());
            GetObjectRequest request = new GetObjectRequest(bucketName, objectName, sseCustomerKey);
            ossClient.getObject(request);
            Assert.fail("why am I here!");
        } catch (Exception ignored) {
            try {
                HeadObjectRequest request = new HeadObjectRequest(bucketName, objectName, sseCustomerKey);
                ossClient.headObject(request);
                Assert.fail("why am I here!");
            } catch (Exception ignore) { }
        }

        // 2. invalid customer key encoding
        try {
            sseCustomerKey = new SSECustomerKey(ssecKey + ".random");
            GetObjectRequest request = new GetObjectRequest(bucketName, objectName, sseCustomerKey);
            ossClient.getObject(request);
            Assert.fail("why am I here!");
        } catch (Exception ignored) {
            try {
                HeadObjectRequest request = new HeadObjectRequest(bucketName, objectName, sseCustomerKey);
                ossClient.headObject(request);
                Assert.fail("why am I here!");
            } catch (Exception ignore) { }
        }

        // 3. invalid customer key length
        try {
            sseCustomerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(16).getBytes()));
            GetObjectRequest request = new GetObjectRequest(bucketName, objectName, sseCustomerKey);
            ossClient.getObject(request);
            Assert.fail("why am I here!");
        } catch (Exception ignored) {
            try {
                HeadObjectRequest request = new HeadObjectRequest(bucketName, objectName, sseCustomerKey);
                ossClient.headObject(request);
                Assert.fail("why am I here!");
            } catch (Exception ignore) { }
        }

        // 4. invalid customer key md5
        try {
            sseCustomerKey = new SSECustomerKey(ssecKey);
            sseCustomerKey.setBase64EncodedMd5("123456");
            GetObjectRequest request = new GetObjectRequest(bucketName, objectName, sseCustomerKey);
            ossClient.getObject(request);
            Assert.fail("why am I here!");
        } catch (Exception ignored) {
            try {
                HeadObjectRequest request = new HeadObjectRequest(bucketName, objectName, sseCustomerKey);
                ossClient.headObject(request);
                Assert.fail("why am I here!");
            } catch (Exception ignore) { }
        }

        // 5. wrong customer key
        try {
            sseCustomerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(32).getBytes()));
            GetObjectRequest request = new GetObjectRequest(bucketName, objectName, sseCustomerKey);
            ossClient.getObject(request);
            if (symlink && objectSize == 0) {
                HeadObjectRequest head = new HeadObjectRequest(bucketName, objectName, sseCustomerKey);
                ossClient.headObject(head);
            } else {
                Assert.fail("why am I here!");
            }
        } catch (Exception ignored) {
            try {
                HeadObjectRequest request = new HeadObjectRequest(bucketName, objectName, sseCustomerKey);
                ossClient.headObject(request);
                if (symlink) {
                    // pass
                } else {
                    Assert.fail("why am I here!");
                }
            } catch (Exception ignore) { }
        }

        if (!(symlink && objectSize == 0)) {
            try {
                GetObjectRequest get = new GetObjectRequest(bucketName, objectName);
                ossClient.getObject(get);
            } catch (Exception ignored) { }
        }
        SSECustomerKey customerKey = new SSECustomerKey(ssecKey);
        GetObjectRequest get = new GetObjectRequest(bucketName, objectName);
        get.setSseCustomerKey(customerKey);
        OSSObject ossObject = ossClient.getObject(get);
        Map<String, String> headers = ossObject.getResponse().getHeaders();
        byte[] buffer = new byte[objectSize];
        int bytesRead;
        int off = 0;
        while ((bytesRead = ossObject.getObjectContent().read()) != -1) {
            buffer[off++] = (byte)bytesRead;
        }

        FileReader fileReader = new FileReader(fileName);
        char[] chars = new char[objectSize];
        fileReader.read(chars, 0, objectSize);
        Assert.assertEquals(new String(buffer), new String(chars));
        if (symlink) return;
        Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM),
                SSEAlgorithm.AES256.getAlgorithm());
        Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
        Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5),
                BinaryUtil.toBase64String(BinaryUtil.calculateMd5(BinaryUtil.fromBase64String(ssecKey))));

        HeadObjectRequest head = new HeadObjectRequest(bucketName, objectName, customerKey);
        Map<String, Object> rawMetadata = ossClient.headObject(head).getRawMetadata();
        Assert.assertEquals(rawMetadata.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM),
                SSEAlgorithm.AES256.getAlgorithm());
        Assert.assertNull(rawMetadata.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
        Assert.assertEquals(rawMetadata.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5),
                BinaryUtil.toBase64String(BinaryUtil.calculateMd5(BinaryUtil.fromBase64String(ssecKey))));
    }

    @Test
    public void testNormalObjectWithSSEC() throws Exception {
        encryptNormalObjectWithSSEC();
    }

    private void encryptAppendObjectWithSSEC() throws Exception {
        String resultFile = TestUtils.genFixedLengthFile(0);
        int position = 0;
        for (int i = 0; i < objectSize.size(); ++i) {
            ObjectMetadata metadata = new ObjectMetadata();
            // 1. invalid algorithm
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.SM4.getAlgorithm());
                AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName.get(i), new File(TestUtils.genFixedLengthFile(objectSize.get(i))), metadata);
                ossClient.appendObject(request);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 2. missing customer key
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.AES256.getAlgorithm());
                AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName.get(i), new File(TestUtils.genFixedLengthFile(objectSize.get(i))), metadata);
                ossClient.appendObject(request);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 3. missing customer key md5
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey);
                AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName.get(i), new File(TestUtils.genFixedLengthFile(objectSize.get(i))), metadata);
                ossClient.appendObject(request);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 4. invalid customer key encoding
            try {
                SSECustomerKey customerKey = new SSECustomerKey(ssecKey + ".random");
                metadata.setSSECustomerKey(customerKey);
                AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName.get(i), new File(TestUtils.genFixedLengthFile(objectSize.get(i))), metadata);
                ossClient.appendObject(request);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 5. invalid customer key length
            try {
                SSECustomerKey customerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(16).getBytes()));
                metadata.setSSECustomerKey(customerKey);
                AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName.get(i), new File(TestUtils.genFixedLengthFile(objectSize.get(i))), metadata);
                ossClient.appendObject(request);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 6. invalid customer key md5
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.AES256.getAlgorithm());
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey);
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5, "123456");
                AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName.get(i), new File(TestUtils.genFixedLengthFile(objectSize.get(i))), metadata);
                ossClient.appendObject(request);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            String fileName = TestUtils.genFixedLengthFile(objectSize.get(i));
            {
                if (i != 0) {
                    try {
                        AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName.get(0) + ".append", new File(fileName));
                        request.setPosition((long)position);
                        ossClient.appendObject(request);
                        Assert.fail("why am I here!");
                    } catch (Exception ignored) { }
                } else {
                    AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName.get(0) + ".append", new File(fileName));
                    request.setPosition((long)position);
                    ossClient.appendObject(request);

                    SSECustomerKey customerKey = new SSECustomerKey(ssecKey);
                    metadata.setSSECustomerKey(customerKey);
                    request = new AppendObjectRequest(bucketName, objectName.get(0) + ".append", new File(fileName), metadata);
                    request.setPosition((long)position);
                    try {
                        ossClient.appendObject(request);
                        Assert.fail("why am I here!");
                    } catch (Exception ignored) {
                        ossClient.deleteObject(bucketName, objectName.get(0) + ".append");
                    }
                }
                SSECustomerKey customerKey = new SSECustomerKey(ssecKey);
                metadata.setSSECustomerKey(customerKey);
                AppendObjectRequest request = new AppendObjectRequest(bucketName, objectName.get(0) + ".append", new File(fileName), metadata);
                request.setPosition((long)position);
                Map<String, String> headers = ossClient.appendObject(request).getResponse().getHeaders();
                Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM),
                        SSEAlgorithm.AES256.getAlgorithm());
                Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
                Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5),
                        BinaryUtil.toBase64String(BinaryUtil.calculateMd5(BinaryUtil.fromBase64String(ssecKey))));
                position += objectSize.get(i);

                FileInputStream fileInputStream = new FileInputStream(fileName);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                File file = new File(resultFile);
                FileOutputStream fileOutputStream = new FileOutputStream(file, true);

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    fileOutputStream.write(line.getBytes());
                }

                fileInputStream.close();
                fileOutputStream.close();
            }
        }
        readObject(objectName.get(0) + ".append", resultFile, position, false);

        ossClient.createSymlink(bucketName, objectName.get(0) + ".append" + "-symlink", objectName.get(0) + ".append");
        readObject(objectName.get(0) + ".append" + "-symlink", resultFile, position, true);
    }

    @Test
    public void testAppendObjectWithSSEC() throws Exception {
        encryptAppendObjectWithSSEC();
    }

    @Test
    public void testMultipartObjectWithSSEC() throws Exception {
        {
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName.get(0));
            InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);

            UploadPartRequest partRequest = new UploadPartRequest(bucketName, objectName.get(0),
                    new SSECustomerKey(ssecKey), result.getUploadId(), 1,
                    TestUtils.genFixedLengthInputStream(1024), 1024);
            try {
                ossClient.uploadPart(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            SSECustomerKey customerKey = new SSECustomerKey(ssecKey);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setSSECustomerKey(customerKey);

            request.setObjectMetadata(metadata);

            result = ossClient.initiateMultipartUpload(request);
            partRequest = new UploadPartRequest(bucketName, objectName.get(0),
                    result.getUploadId(), 1,
                    TestUtils.genFixedLengthInputStream(1024), 1024);

            try {
                ossClient.uploadPart(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }
        }

        ObjectMetadata metadata = new ObjectMetadata();
        List<PartETag> partETags =  new ArrayList<PartETag>();
        String resultFile = TestUtils.genFixedLengthFile(0);
        // 1. invalid algorithm
        try {
            metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.SM4.getAlgorithm());
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName.get(0));
            request.setObjectMetadata(metadata);
            ossClient.initiateMultipartUpload(request);
            Assert.fail("why am I here!");
        } catch (Exception ignored) { }

        // 2. missing customer key
        try {
            metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.AES256.getAlgorithm());
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName.get(0));
            request.setObjectMetadata(metadata);
            ossClient.initiateMultipartUpload(request);
            Assert.fail("why am I here!");
        } catch (Exception ignored) { }

        // 3. missing customer key md5
        try {
            metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey);
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName.get(0));
            request.setObjectMetadata(metadata);
            ossClient.initiateMultipartUpload(request);
            Assert.fail("why am I here!");
        } catch (Exception ignored) { }

        // 4. invalid customer key encoding
        try {
            SSECustomerKey customerKey = new SSECustomerKey(ssecKey + ".random");
            metadata.setSSECustomerKey(customerKey);
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName.get(0));
            request.setObjectMetadata(metadata);
            ossClient.initiateMultipartUpload(request);
            Assert.fail("why am I here!");
        } catch (Exception ignored) { }

        // 5. invalid customer key length
        try {
            SSECustomerKey customerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(16).getBytes()));
            metadata.setSSECustomerKey(customerKey);
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName.get(0));
            request.setObjectMetadata(metadata);
            ossClient.initiateMultipartUpload(request);
            Assert.fail("why am I here!");
        } catch (Exception ignored) { }

        // 6. invalid customer key md5
        try {
            metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.AES256.getAlgorithm());
            metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey);
            metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5, "123456");
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName.get(0));
            request.setObjectMetadata(metadata);
            ossClient.initiateMultipartUpload(request);
            Assert.fail("why am I here!");
        } catch (Exception ignored) { }

        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName.get(0));
        SSECustomerKey customerKey = new SSECustomerKey(ssecKey);
        metadata.setSSECustomerKey(customerKey);
        request.setObjectMetadata(metadata);
        InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
        int size = 0;
        for (int i = 0; i < partSize.size(); ++i) {
            size += partSize.get(i);
            // 1. invalid algorithm
            SSECustomerKey sseCustomerKey = null;
            try {
                sseCustomerKey = new SSECustomerKey("12345", SSEAlgorithm.SM4.getAlgorithm());
                UploadPartRequest partRequest = new UploadPartRequest(bucketName, objectName.get(0),
                        sseCustomerKey, result.getUploadId(), i + 1,
                        new FileInputStream(TestUtils.genFixedLengthFile(partSize.get(i))), partSize.get(i));
                ossClient.uploadPart(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 2. invalid customer key encoding
            try {
                sseCustomerKey = new SSECustomerKey(ssecKey + ".random");
                UploadPartRequest partRequest = new UploadPartRequest(bucketName, objectName.get(0),
                        sseCustomerKey, result.getUploadId(), i + 1,
                        new FileInputStream(TestUtils.genFixedLengthFile(partSize.get(i))), partSize.get(i));
                ossClient.uploadPart(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 3. invalid customer key length
            try {
                sseCustomerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(16).getBytes()));
                UploadPartRequest partRequest = new UploadPartRequest(bucketName, objectName.get(0),
                        sseCustomerKey, result.getUploadId(), i + 1,
                        new FileInputStream(TestUtils.genFixedLengthFile(partSize.get(i))), partSize.get(i));
                ossClient.uploadPart(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 4. invalid customer key md5
            try {
                sseCustomerKey = new SSECustomerKey(ssecKey);
                sseCustomerKey.setBase64EncodedMd5("123456");
                UploadPartRequest partRequest = new UploadPartRequest(bucketName, objectName.get(0),
                        sseCustomerKey, result.getUploadId(), i + 1,
                        new FileInputStream(TestUtils.genFixedLengthFile(partSize.get(i))), partSize.get(i));
                ossClient.uploadPart(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 5. wrong customer key
            try {
                sseCustomerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(32).getBytes()));
                UploadPartRequest partRequest = new UploadPartRequest(bucketName, objectName.get(0),
                        sseCustomerKey, result.getUploadId(), i + 1,
                        new FileInputStream(TestUtils.genFixedLengthFile(partSize.get(i))), partSize.get(i));
                ossClient.uploadPart(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            String fileName = TestUtils.genFixedLengthFile(partSize.get(i));
            sseCustomerKey = new SSECustomerKey(ssecKey);

            UploadPartRequest partRequest = new UploadPartRequest(bucketName, objectName.get(0),
                    sseCustomerKey, result.getUploadId(), i + 1,
                    new FileInputStream(fileName), partSize.get(i));
            UploadPartResult uploadPartResult = ossClient.uploadPart(partRequest);
            Map<String, String> headers = uploadPartResult.getResponse().getHeaders();
            partETags.add(uploadPartResult.getPartETag());
            Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM),
                    SSEAlgorithm.AES256.getAlgorithm());
            Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
            Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5),
                    BinaryUtil.toBase64String(BinaryUtil.calculateMd5(BinaryUtil.fromBase64String(ssecKey))));

            FileInputStream fileInputStream = new FileInputStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            File file = new File(resultFile);
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileOutputStream.write(line.getBytes());
            }

            fileInputStream.close();
            fileOutputStream.close();
        }

        CompleteMultipartUploadRequest cmu = new CompleteMultipartUploadRequest(bucketName, objectName.get(0), result.getUploadId(), partETags);
        ossClient.completeMultipartUpload(cmu);
        readObject(objectName.get(0), resultFile, size, false);

        ossClient.createSymlink(bucketName, objectName.get(0) + "-symlink", objectName.get(0));
        readObject(objectName.get(0) + "-symlink", resultFile, size, true);
    }

    @Test
    public void testCopyObjectWithSSEC() throws Exception {
        for (int i = 0; i < objectSize.size(); ++i) {
            ObjectMetadata metadata = new ObjectMetadata();
            SSECustomerKey customerKey = new SSECustomerKey(ssecKey);
            metadata.setSSECustomerKey(customerKey);

            String fileName = TestUtils.genFixedLengthFile(objectSize.get(i));
            PutObjectRequest request = new PutObjectRequest(bucketName, objectName.get(i), new File(fileName), metadata);
            request.setProcess("");
            Map<String, String> headers = ossClient.putObject(request).getResponse().getHeaders();

            Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM),
                    SSEAlgorithm.AES256.getAlgorithm());
            Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
            Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5),
                    BinaryUtil.toBase64String(BinaryUtil.calculateMd5(BinaryUtil.fromBase64String(ssecKey))));

            // 1. invalid algorithm
            try {
                customerKey = new SSECustomerKey("12345", SSEAlgorithm.SM4.getAlgorithm());
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 2. invalid customer key encoding
            try {
                customerKey = new SSECustomerKey(ssecKey + ".random");
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 3. invalid customer key length
            try {
                customerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(16).getBytes()));
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 4. invalid customer key md5
            try {
                customerKey = new SSECustomerKey(ssecKey);
                customerKey.setBase64EncodedMd5("123456");
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 5. wrong customer key
            try {
                customerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(32).getBytes()));
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }


            customerKey = new SSECustomerKey(ssecKey);
            metadata = new ObjectMetadata();
            // 1. invalid algorithm
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.SM4.getAlgorithm());
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                copyObjectRequest.setNewObjectMetadata(metadata);
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 2. missing customer key
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.AES256.getAlgorithm());
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                copyObjectRequest.setNewObjectMetadata(metadata);
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 3. missing customer key md5
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey);
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                copyObjectRequest.setNewObjectMetadata(metadata);
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 4. invalid customer key encoding
            try {
                metadata.setSSECustomerKey(new SSECustomerKey(ssecKey + ".random"));
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                copyObjectRequest.setNewObjectMetadata(metadata);
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 5. invalid customer key length
            try {
                metadata.setSSECustomerKey(new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(16).getBytes())));
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                copyObjectRequest.setNewObjectMetadata(metadata);
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 6. invalid customer key md5
            try {
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.AES256.getAlgorithm());
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey);
                metadata.setHeader(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5, "123456");
                CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
                copyObjectRequest.setNewObjectMetadata(metadata);
                ossClient.copyObject(copyObjectRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // target no encryption
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
            headers = ossClient.copyObject(copyObjectRequest).getResponse().getHeaders();
            Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM));
            Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
            Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5));

            // source and target same encryption key
            copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
            metadata = new ObjectMetadata();
            metadata.setSSECustomerKey(customerKey);
            copyObjectRequest.setNewObjectMetadata(metadata);
            headers = ossClient.copyObject(copyObjectRequest).getResponse().getHeaders();
            Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM),
                    SSEAlgorithm.AES256.getAlgorithm());
            Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
            Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5),
                    BinaryUtil.toBase64String(BinaryUtil.calculateMd5(BinaryUtil.fromBase64String(ssecKey))));
            readObject(objectName.get(i) + ".copy", fileName, objectSize.get(i), false);
            ossClient.createSymlink(bucketName, objectName.get(i) + ".copy" + "-symlink", objectName.get(i) + ".copy");
            readObject(objectName.get(i) + ".copy" + "-symlink", fileName, objectSize.get(i), true);

            // source and target different encryption key
            SSECustomerKey anotherCustomerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(32).getBytes()));
            copyObjectRequest = new CopyObjectRequest(bucketName, objectName.get(i), customerKey, bucketName, objectName.get(i) + ".copy");
            metadata = new ObjectMetadata();
            metadata.setSSECustomerKey(anotherCustomerKey);
            copyObjectRequest.setNewObjectMetadata(metadata);
            headers = ossClient.copyObject(copyObjectRequest).getResponse().getHeaders();
            Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM),
                    SSEAlgorithm.AES256.getAlgorithm());
            Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
            Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5),
                    BinaryUtil.toBase64String(BinaryUtil.calculateMd5(BinaryUtil.fromBase64String(anotherCustomerKey.getBase64EncodedKey()))));
            readObject(objectName.get(i) + ".copy", fileName, objectSize.get(i), false, anotherCustomerKey.getBase64EncodedKey());
            ossClient.createSymlink(bucketName, objectName.get(i) + ".copy" + "-symlink", objectName.get(i) + ".copy");
            readObject(objectName.get(i) + ".copy" + "-symlink", fileName, objectSize.get(i), true, anotherCustomerKey.getBase64EncodedKey());
        }
    }

    @Test
    public void testUploadPartCopyWithSSEC() throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        List<PartETag> partETags =  new ArrayList<PartETag>();
        String resultFile = TestUtils.genFixedLengthFile(0);
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectName.get(0));
        SSECustomerKey customerKey = new SSECustomerKey(ssecKey);
        metadata.setSSECustomerKey(customerKey);
        request.setObjectMetadata(metadata);
        InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);

        int size = 0;
        for (int i = 0; i < partSize.size(); ++i) {
            metadata.setSSECustomerKey(new SSECustomerKey(anotherSSECKey));
            String fileName = TestUtils.genFixedLengthFile(partSize.get(i));
            ossClient.putObject(new PutObjectRequest(bucketName, objectName.get(0) + ".source", new File(fileName), metadata));

            size += partSize.get(i);
            // 1. invalid algorithm
            SSECustomerKey sseCustomerKey = null;
            try {
                sseCustomerKey = new SSECustomerKey("12345", SSEAlgorithm.SM4.getAlgorithm());
                UploadPartCopyRequest partRequest = new UploadPartCopyRequest(bucketName, objectName.get(0) + ".source",
                        bucketName, objectName.get(0), result.getUploadId(), i + 1,
                        0L, (long)partSize.get(i));
                partRequest.setSourceSseCustomerKey(sseCustomerKey);
                ossClient.uploadPartCopy(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 2. invalid customer key encoding
            try {
                sseCustomerKey = new SSECustomerKey(anotherSSECKey + ".random");
                UploadPartCopyRequest partRequest = new UploadPartCopyRequest(bucketName, objectName.get(0) + ".source",
                        bucketName, objectName.get(0), result.getUploadId(), i + 1,
                        0L, (long)partSize.get(i));
                partRequest.setSourceSseCustomerKey(sseCustomerKey);
                ossClient.uploadPartCopy(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 3. invalid customer key length
            try {
                sseCustomerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(16).getBytes()));
                UploadPartCopyRequest partRequest = new UploadPartCopyRequest(bucketName, objectName.get(0) + ".source",
                        bucketName, objectName.get(0), result.getUploadId(), i + 1,
                        0L, (long)partSize.get(i));
                partRequest.setSourceSseCustomerKey(sseCustomerKey);
                ossClient.uploadPartCopy(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 4. invalid customer key md5
            try {
                sseCustomerKey = new SSECustomerKey(anotherSSECKey);
                sseCustomerKey.setBase64EncodedMd5("123456");
                UploadPartCopyRequest partRequest = new UploadPartCopyRequest(bucketName, objectName.get(0) + ".source",
                        bucketName, objectName.get(0), result.getUploadId(), i + 1,
                        0L, (long)partSize.get(i));
                partRequest.setSourceSseCustomerKey(sseCustomerKey);
                ossClient.uploadPartCopy(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            // 5. wrong customer key
            try {
                sseCustomerKey = new SSECustomerKey(BinaryUtil.toBase64String(TestUtils.genRandomString(32).getBytes()));
                UploadPartCopyRequest partRequest = new UploadPartCopyRequest(bucketName, objectName.get(0) + ".source",
                        bucketName, objectName.get(0), result.getUploadId(), i + 1,
                        0L, (long)partSize.get(i));
                partRequest.setSourceSseCustomerKey(sseCustomerKey);
                ossClient.uploadPartCopy(partRequest);
                Assert.fail("why am I here!");
            } catch (Exception ignored) { }

            sseCustomerKey = new SSECustomerKey(ssecKey);

            UploadPartCopyRequest partRequest = new UploadPartCopyRequest(bucketName, objectName.get(0) + ".source",
                    bucketName, objectName.get(0), result.getUploadId(), i + 1,
                    0L, (long)partSize.get(i));
            partRequest.setSourceSseCustomerKey(new SSECustomerKey(anotherSSECKey));
            partRequest.setDestinationSSECustomerKey(sseCustomerKey);
            ossClient.uploadPartCopy(partRequest);
            UploadPartCopyResult uploadPartResult = ossClient.uploadPartCopy(partRequest);
            Map<String, String> headers = uploadPartResult.getResponse().getHeaders();
            partETags.add(uploadPartResult.getPartETag());
            Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM),
                    SSEAlgorithm.AES256.getAlgorithm());
            Assert.assertNull(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION));
            Assert.assertEquals(headers.get(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5),
                    BinaryUtil.toBase64String(BinaryUtil.calculateMd5(BinaryUtil.fromBase64String(ssecKey))));

            FileInputStream fileInputStream = new FileInputStream(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            File file = new File(resultFile);
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                fileOutputStream.write(line.getBytes());
            }

            fileInputStream.close();
            fileOutputStream.close();
        }

        CompleteMultipartUploadRequest cmu = new CompleteMultipartUploadRequest(bucketName, objectName.get(0), result.getUploadId(), partETags);
        ossClient.completeMultipartUpload(cmu);
        readObject(objectName.get(0), resultFile, size, false);

        ossClient.createSymlink(bucketName, objectName.get(0) + "-symlink", objectName.get(0));
        readObject(objectName.get(0) + "-symlink", resultFile, size, true);
    }

    private String formUpload(String urlStr, Map<String, String> formFields, String localFile, boolean error, String md5) throws Exception {
        String result;
        HttpURLConnection connection = null;
        String boundary = "9431149156168";
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            OutputStream out = new DataOutputStream(connection.getOutputStream());
            StringBuffer strBuf = new StringBuffer();
            Iterator<Map.Entry<String, String>> itr = formFields.entrySet().iterator();
            int i = 0;
            while (itr.hasNext()) {
                Map.Entry<String, String> entry = itr.next();
                String inputName = entry.getKey();
                String inputValue = entry.getValue();
                if (inputValue == null) {
                    continue;
                }
                if (i == 0) {
                    strBuf.append("--").append(boundary).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                } else {
                    strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                i++;
            }
            out.write(strBuf.toString().getBytes());

            File file = new File(localFile);
            String filename = file.getName();
            String contentType = new MimetypesFileTypeMap().getContentType(file);
            if (contentType == null || contentType.equals("")) {
                contentType = "application/octet-stream";
            }
            strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(boundary)
                    .append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"file\"; "
                    + "filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type: " + contentType + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();
            byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            result = strBuf.toString();
            reader.close();
            Assert.assertFalse(error);
            Assert.assertEquals(connection.getHeaderField(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM), SSEAlgorithm.AES256.name());
            Assert.assertEquals(connection.getHeaderField(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5), md5);
        } catch (Exception e) {
            Assert.assertTrue(error);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            StringBuffer strBuf = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            result = strBuf.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return result;
    }

    @Test
    public void testPostObjectWithSSEC() throws Exception {
        //String urlStr = TestConfig.OSS_TEST_ENDPOINT.replace("http://", "http://" + bucketName+ ".");
        String urlStr = TestConfig.OSS_TEST_ENDPOINT + "/" + bucketName;
        for (int i = 0; i < objectSize.size(); ++i) {
            Map<String, String> formFields = new LinkedHashMap<String, String>();
            formFields.put("key", objectName.get(i));
            String md5 = BinaryUtil.toBase64Md5FromBase64String(ssecKey);
            formFields.put("OSSAccessKeyId", TestConfig.OSS_TEST_ACCESS_KEY_ID);
            String policy = "{\"expiration\": \"2120-01-01T12:00:00.000Z\",\"conditions\": [[\"content-length-range\", 0, 104857600]]}";
            String encodePolicy = new String(Base64.encodeBase64(policy.getBytes()));
            formFields.put("policy", encodePolicy);
            String signature = ServiceSignature.create().computeSignature(TestConfig.OSS_TEST_ACCESS_KEY_SECRET, encodePolicy);
            formFields.put("Signature", signature);

            String fileName = TestUtils.genFixedLengthFile(objectSize.get(i));
            formFields.put("Content-Disposition", "attachment;filename=" + fileName);

            // 1. invalid algorithm
            formFields.put(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.SM4.name());
            String result = formUpload(urlStr, formFields, fileName, true, md5);
            System.out.println(result);

            // 2. missing customer key
            formFields.put(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_ALGORITHM, SSEAlgorithm.AES256.name());
            result = formUpload(urlStr, formFields, fileName, true, md5);
            System.out.println(result);

            // 3. missing customer key md5
            formFields.put(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey);
            result = formUpload(urlStr, formFields, fileName, true, md5);
            System.out.println(result);

            // 4. invalid customer key encoding
            formFields.put(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey + ".random");
            result = formUpload(urlStr, formFields, fileName, true, md5);
            System.out.println(result);

            // 5. invalid customer key length
            formFields.put(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY,
                    BinaryUtil.toBase64String(TestUtils.genRandomString(16).getBytes()));
            result = formUpload(urlStr, formFields, fileName, true, md5);
            System.out.println(result);

            // 6. invalid customer key md5
            formFields.put(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5, "12345");
            result = formUpload(urlStr, formFields, fileName, true, md5);
            System.out.println(result);

            formFields.put(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY, ssecKey);
            formFields.put(OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_CUSTOMER_KEY_MD5, md5);
            result = formUpload(urlStr, formFields, fileName, false, md5);
            System.out.println(result);

            readObject(objectName.get(i), fileName, objectSize.get(i), false);

            ossClient.createSymlink(bucketName, objectName.get(i) + "-symlink", objectName.get(i));
            readObject(objectName.get(i) + "-symlink", fileName, objectSize.get(i), true);
        }
    }
}
