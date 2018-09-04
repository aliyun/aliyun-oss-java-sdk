package com.aliyun.oss.integrationtests;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.StringUtils;
import com.aliyun.oss.internal.SignParameters;
import com.aliyun.oss.internal.SignV2Utils;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static com.aliyun.oss.integrationtests.TestUtils.genFixedLengthFile;
import static com.aliyun.oss.integrationtests.TestUtils.removeFile;

public class SignTest {

    private static final String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";

    private static final String accessKeyID = "LTAI2pSNlDGMkFrB";

    private static final String accessKeySecret = "VWEukXofmBnajjymqMvYVwG2LdFN4B";

    private static final String bucket = "test-sign";

    @Test
    public void testSignV2() {
        String key = "test-sign-V2";
        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setSignatureVersion(SignParameters.AUTH_V2);
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyID, accessKeySecret, conf);
        ossClient.createBucket(bucket);
        String filePath = null;

        try {
            filePath = genFixedLengthFile(1 * 1024 * 1024); //1MB
            PutObjectRequest request = new PutObjectRequest(bucket, key, new File(filePath));
            request.addHeader("x-oss-head1", "31232");
            request.addHeader("abc", "4fdfsd");
            request.addHeader("ZAbc", "4fde324fsd");
            request.addHeader("XYZ", "4fde324fsd");
            request.addAdditionalHeaderName("ZAbc");
            request.addAdditionalHeaderName("x-oss-head1");
            request.addAdditionalHeaderName("abc");
            request.addParameter("param1", "value1");

            ossClient.putObject(request);
        } catch (IOException e) {
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
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyID, accessKeySecret, conf);
        Date expiration = new Date(new Date().getTime() + 1000 * 60 *10);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key);
        request.setExpiration(expiration);
        URL url = null;

        Set<String> abc = new HashSet<String>();
        List<String> ls = new LinkedList<String>(abc);
        StringUtils.join(";", ls);
        try {
            URI endpointURI = new URI(endpoint);
            String head1 = "test1";
            String head2 = "B\rTest!";

            request.addUserMetadata("user1", "ddd");
            request.addHeader(head1, "aaa");
            request.addHeader("atest", "bbb");
            request.addHeader(head2, "ccc");
            request.addAdditionalHeaderName(head1);
            request.addAdditionalHeaderName(head2);
            request.addQueryParameter("queryParam1", "value1");
            url = ossClient.generatePresignedUrl(request);

            StringBuilder expectedUrlPrefix = new StringBuilder();
            expectedUrlPrefix.append(endpointURI.getScheme()).append("://").append(bucket).append(".").append(endpointURI.getHost()).append("/")
                    .append(key).append("?x-oss-signature-version=OSS2").append("&x-oss-expires=").append(Long.toString(expiration.getTime() / 1000))
                    .append("&x-oss-access-key-id=").append(accessKeyID).append("&x-oss-additional-headers=").append(SignV2Utils.uriEncoding(head2.toLowerCase() + ";" + head1.toLowerCase())).append("&x-oss-signature");

            Assert.assertTrue(url.toString().startsWith(expectedUrlPrefix.toString()));

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

    }
}
