package com.aliyun.oss.integrationtests;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.SignParameters;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static com.aliyun.oss.integrationtests.TestUtils.genFixedLengthFile;
import static com.aliyun.oss.integrationtests.TestUtils.removeFile;

public class SignTest {

    @Test
    public void testSignV2() {
        String key = "test-sign-V2";
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignParameters.AUTH_V2);
        OSS ossClient = new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT, TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET, conf);
        long ticks = new Date().getTime() / 1000 + new Random().nextInt(5000);
        String bucket = TestBase.BUCKET_NAME_PREFIX + ticks;
        ossClient.createBucket(bucket);
        String filePath = null;

        try {
            filePath = genFixedLengthFile(1 * 1024 * 1024); //1MB
            PutObjectRequest request = new PutObjectRequest(bucket, key, new File(filePath));
            request.addHeader("x-oss-head1", "test1");
            request.addHeader("abc", "4fdfsd");
            request.addHeader("ZAbc", "4fde324fsd");
            request.addHeader("XYZ", "4fde324fsd");
            request.addAdditionalHeaderName("ZAbc");
            request.addAdditionalHeaderName("x-oss-head1");
            request.addAdditionalHeaderName("abc");
            request.addParameter("param1", "value1");

            ossClient.putObject(request);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (filePath != null) {
                removeFile(filePath);
            }
        }
    }

    @Test
    public void testGenerateSignedV2URL() {
        String key = "test-sign-v2-url";
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignParameters.AUTH_V2);
        OSS ossClient = new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT, TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET, conf);
        Date expiration = new Date(new Date().getTime() + 1000 * 60 *10);
        long ticks = new Date().getTime() / 1000 + new Random().nextInt(5000);
        String bucket = TestBase.BUCKET_NAME_PREFIX + ticks;

        ossClient.createBucket(bucket);
        URL url;
        String filePath;

        try {
            filePath = genFixedLengthFile(1 * 1024 * 1024); //1MB
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, new File(filePath));

            ossClient.putObject(putObjectRequest);

            URI endpointURI = new URI(TestConfig.OSS_TEST_ENDPOINT);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key);
            request.setExpiration(expiration);
            url = ossClient.generatePresignedUrl(request);

            StringBuilder expectedUrlPrefix = new StringBuilder();

            expectedUrlPrefix.append(endpointURI.getScheme()).append("://").append(bucket).append(".").append(endpointURI.getHost()).append("/")
                    .append(key).append("?x-oss-");

            Assert.assertTrue(url.toString().startsWith(expectedUrlPrefix.toString()));
            Assert.assertTrue(url.toString().contains("x-oss-signature"));
            Assert.assertTrue(url.toString().contains("x-oss-signature-version"));
            Assert.assertTrue(url.toString().contains("x-oss-expires"));
            Assert.assertTrue(url.toString().contains("x-oss-access-key-id"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSwitchSignatureVersion() {
        String key = "test-switch-signature-version";
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignParameters.AUTH_V2);
        OSS ossClient = new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT, TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET, conf);
        long ticks = new Date().getTime() / 1000 + new Random().nextInt(5000);
        String bucket = TestBase.BUCKET_NAME_PREFIX + ticks;
        ossClient.createBucket(bucket);
        String filePath;

        try {
            filePath = genFixedLengthFile(1 * 1024 * 1024); //1MB

            ossClient.putObject(bucket, key, new File(filePath));

            ossClient.switchSignatureVersion(SignParameters.AUTH_V1);

            ossClient.putObject(bucket, key, new File(filePath));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSwitchUnsupportedSignatureVersion() {
        String unsupportedSignatureVersion = "unsupported";
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignParameters.AUTH_V2);
        OSS ossClient = new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT, TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET, conf);
        try {
            ossClient.switchSignatureVersion(unsupportedSignatureVersion);
            Assert.fail("switch unsupported signature version should not be successful");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().startsWith("unsupported signature version"));
        }

    }
}
