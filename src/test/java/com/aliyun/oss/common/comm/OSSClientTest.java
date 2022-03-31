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

package com.aliyun.oss.common.comm;

import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.internal.OSSConstants;
import com.aliyun.oss.internal.RequestParameters;
import com.aliyun.oss.model.GetObjectRequest;
import org.junit.jupiter.api.*;
import org.junit.Ignore;
import org.junit.Test;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;

public class OSSClientTest {
    @Test
    /**
     * TODO: needs the fix about local time.
     */
    public void testGeneratePresignedUrl() throws IOException {
        OSS client = new OSSClientBuilder().build("oss.aliyuncs.com", "id", "key");
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest("bucket", "key");
        Calendar ex = Calendar.getInstance();
        ex.set(2015, 1, 1, 0, 0, 0);
        Date expiration = ex.getTime();
        request.setExpiration(expiration);
        request.setContentMD5("md5");
        request.setContentType("type");
        Assertions.assertEquals(request.getContentType(), "type");
        Assertions.assertEquals(request.getContentMD5(), "md5");
        URL url = client.generatePresignedUrl(request);
        Assertions.assertEquals(url.getPath(), "/key");
        Assertions.assertEquals(url.getAuthority(), "bucket.oss.aliyuncs.com");
        Assertions.assertEquals(url.getHost(), "bucket.oss.aliyuncs.com");
        Assertions.assertEquals(url.getDefaultPort(), 80);
        Assertions.assertEquals(url.getProtocol(), "http");
        Assertions.assertEquals(url.getQuery(), "Expires=1422720000&OSSAccessKeyId=id&Signature=XA8ThdVKdJQ4vlkoggdzCs5s1RY%3D");
        Assertions.assertEquals(url.getFile(), "/key?Expires=1422720000&OSSAccessKeyId=id&Signature=XA8ThdVKdJQ4vlkoggdzCs5s1RY%3D");
        request.setContentMD5("md5'");
        url = client.generatePresignedUrl(request);
        Assertions.assertTrue(!url.getQuery().equals("Expires=1422720000&OSSAccessKeyId=id&Signature=XA8ThdVKdJQ4vlkoggdzCs5s1RY%3D"));
        request.setContentMD5("md5'");
        url = client.generatePresignedUrl(request);
        Assertions.assertTrue(!url.getQuery().equals("Expires=1422720000&OSSAccessKeyId=id&Signature=XA8ThdVKdJQ4vlkoggdzCs5s1RY%3D"));
        request.setContentType("type'");
        request.setContentMD5("md5");
        url = client.generatePresignedUrl(request);
        Assertions.assertTrue(!url.getQuery().equals("Expires=1422720000&OSSAccessKeyId=id&Signature=XA8ThdVKdJQ4vlkoggdzCs5s1RY%3D"));

        request.setBucketName(null);
        try {
            url = client.generatePresignedUrl(request);
            Assertions.assertTrue(false);
        }catch (Exception e) {
            Assertions.assertTrue(true);
        }

        request.setBucketName("bucket");
        request.setExpiration(null);
        try {
            url = client.generatePresignedUrl(request);
            Assertions.assertTrue(false);
        }catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testProxyHost() {
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        String accessKeyId = "accessKeyId";
        String accessKeySecret = "accessKeySecret";

        ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
        conf.setProxyHost(endpoint);
        conf.setProxyPort(80);
        conf.setProxyUsername("user");
        conf.setProxyPassword("passwd");

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, conf);
        ossClient.shutdown();


        conf = new ClientBuilderConfiguration();
        //conf.setProxyHost(endpoint);
        conf.setProxyPort(80);
        //conf.setProxyUsername("user");
        //conf.setProxyPassword("passwd");

        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, conf);
        ossClient.shutdown();

        conf = new ClientBuilderConfiguration();
        conf.setProxyHost(endpoint);
        conf.setProxyPort(80);
        //conf.setProxyUsername("user");
        //conf.setProxyPassword("passwd");

        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, conf);
        ossClient.shutdown();

        conf = new ClientBuilderConfiguration();
        conf.setProxyHost(endpoint);
        conf.setProxyPort(80);
        conf.setProxyUsername("user");
        //conf.setProxyPassword("passwd");

        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, conf);
        ossClient.shutdown();

        conf = new ClientBuilderConfiguration();
        conf.setProxyHost(endpoint);
        conf.setProxyPort(80);
        //conf.setProxyUsername("user");
        conf.setProxyPassword("passwd");

        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, conf);
        ossClient.shutdown();
    }

    @Test
    public void testClientConfiguration() {
        ClientConfiguration conf = new ClientConfiguration();

        conf.setUserAgent("userAgent");
        Assertions.assertEquals("userAgent", conf.getUserAgent());

        conf.setProxyPort(100);
        Assertions.assertEquals(100, conf.getProxyPort());

        try {
            conf.setProxyPort(-1);
            Assertions.assertTrue(false);
        }catch (Exception e) {
            Assertions.assertTrue(true);
        }

        conf.setProxyDomain("domain");
        Assertions.assertEquals("domain", conf.getProxyDomain());

        conf.setProxyWorkstation("workstation");
        Assertions.assertEquals("workstation", conf.getProxyWorkstation());

        conf.setMaxConnections(100);
        Assertions.assertEquals(100, conf.getMaxConnections());

        conf.setSocketTimeout(100);
        Assertions.assertEquals(100, conf.getSocketTimeout());

        conf.setConnectionRequestTimeout(100);
        Assertions.assertEquals(100, conf.getConnectionRequestTimeout());

        conf.setConnectionTTL(100);
        Assertions.assertEquals(100, conf.getConnectionTTL());

        conf.setUseReaper(true);
        Assertions.assertEquals(true, conf.isUseReaper());

        conf.setIdleConnectionTime(100);
        Assertions.assertEquals(100, conf.getIdleConnectionTime());

        conf.setProtocol(Protocol.HTTP);
        Assertions.assertEquals(Protocol.HTTP, conf.getProtocol());

        conf.setRequestTimeoutEnabled(true);
        Assertions.assertEquals(true, conf.isRequestTimeoutEnabled());

        conf.setRequestTimeout(100);
        Assertions.assertEquals(100, conf.getRequestTimeout());

        conf.setSlowRequestsThreshold(100);
        Assertions.assertEquals(100, conf.getSlowRequestsThreshold());

        conf.addDefaultHeader("k", "v");
        Map<String, String> defaultHeaders = new HashMap<String, String>();
        defaultHeaders.put("key", "value");
        conf.setDefaultHeaders(defaultHeaders);
        Assertions.assertEquals(defaultHeaders , conf.getDefaultHeaders());

        conf.setCrcCheckEnabled(true);
        Assertions.assertEquals(true, conf.isCrcCheckEnabled());

        conf.setSignerHandlers(null);

        List<String> cnameList = conf.getCnameExcludeList();
        Assertions.assertEquals(3, cnameList.size());
        Assertions.assertEquals(true, cnameList.contains("aliyuncs.com"));
        Assertions.assertEquals(true, cnameList.contains("aliyun-inc.com"));
        Assertions.assertEquals(true, cnameList.contains("aliyun.com"));

        cnameList = new ArrayList<String>();
        cnameList.add("");
        cnameList.add("cname");
        cnameList.add("cname1");
        cnameList.add("aliyuncs.com");
        conf.setCnameExcludeList(cnameList);
        List<String> gCnameList = conf.getCnameExcludeList();
        Assertions.assertEquals(5, gCnameList.size());
        Assertions.assertEquals(true, gCnameList.contains("cname"));
        Assertions.assertEquals(true, gCnameList.contains("cname1"));
        Assertions.assertEquals(true, gCnameList.contains("aliyun-inc.com"));

        cnameList = new ArrayList<String>();
        conf.setCnameExcludeList(cnameList);
        gCnameList = conf.getCnameExcludeList();
        Assertions.assertEquals(3, gCnameList.size());
        Assertions.assertEquals(true, gCnameList.contains("aliyuncs.com"));
        Assertions.assertEquals(true, gCnameList.contains("aliyun-inc.com"));
        Assertions.assertEquals(true, gCnameList.contains("aliyun.com"));

        try {
            conf.setCnameExcludeList(null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSwitchFuncWithException() {

        OSS client = new OSSClientBuilder().build("oss-cn-hangzhou.aliyuncs.com", "ak", "sk", "");

        try {
            client.switchCredentials(null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            client.switchSignatureVersion(null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDeprecationFunction() {

        OSSClient client = new OSSClient("ak", "sk");
        Assertions.assertEquals(OSSConstants.DEFAULT_OSS_ENDPOINT, client.getEndpoint().toString());

        client = new OSSClient("oss-cn-hangzhou.aliyuncs.com", "ak", "sk", "sts");
        Assertions.assertEquals("http://oss-cn-hangzhou.aliyuncs.com", client.getEndpoint().toString());


        client = new OSSClient("oss-cn-shenzhen.aliyuncs.com", "ak", "sk", new ClientConfiguration());
        Assertions.assertEquals("http://oss-cn-shenzhen.aliyuncs.com", client.getEndpoint().toString());

        client = new OSSClient("oss-cn-zhangjiakou.aliyuncs.com", "ak", "sk", "sts", new ClientConfiguration());
        Assertions.assertEquals("http://oss-cn-zhangjiakou.aliyuncs.com", client.getEndpoint().toString());

        try {
            client.isBucketExist("bucketName");
        } catch (Exception e){}
    }

    @Test
    public void testValidateEndpoint() {
        final String endpoint = "oss-cn-shenzhen.aliyuncs.com";

        // true
        try {
            OSS client = new OSSClientBuilder().build(endpoint, "id", "key");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // true
        try {
            OSS client = new OSSClientBuilder().build("http://" + endpoint, "id", "key");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // true
        try {
            OSS client = new OSSClientBuilder().build("https://" + endpoint, "id", "key");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // true
        try {
            OSS client = new OSSClientBuilder().build("11.11.11.11", "id", "key");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // true
        try {
            OSS client = new OSSClientBuilder().build("http://11.11.11.11", "id", "key");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // true
        try {
            OSS client = new OSSClientBuilder().build("https://11.11.11.11", "id", "key");
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // false
        try {
            OSS client = new OSSClientBuilder().build("https://www.alibabacloud.com\\www.aliyun.com", "id", "key");
            Assertions.fail("should be failed here.");
        } catch (Exception e) {
        }

        // false
        try {
            OSS client = new OSSClientBuilder().build("https://www.alibabacloud.com#www.aliyun.com", "id", "key");
        } catch (Exception e) {
            Assertions.fail("should be failed here.");
        }
    }

    private class NullCredentialProvider implements CredentialsProvider {
        private volatile Credentials creds = null;
        @Override
        public synchronized void setCredentials(Credentials creds) {
            this.creds = creds;
        }

        @Override
        public Credentials getCredentials() {
            return this.creds;
        }
    }

    @Test
    public void testNullCredential(){
        OSS client = new OSSClientBuilder().build("oss-cn-hangzhou.aliyuncs.com", new NullCredentialProvider());
        try {
            client.getObject("bucket","objedct");
            Assertions.fail("should be failed here.");
        } catch (NullPointerException e) {
        } catch (Exception e1) {
            Assertions.fail("should be failed here.");
        }
    }
}

