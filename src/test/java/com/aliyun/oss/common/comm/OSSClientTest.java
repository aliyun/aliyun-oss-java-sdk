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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import com.aliyun.oss.*;
import com.aliyun.oss.internal.OSSConstants;
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
        assertEquals(request.getContentType(), "type");
        assertEquals(request.getContentMD5(), "md5");
        URL url = client.generatePresignedUrl(request);
        assertEquals(url.getPath(), "/key");
        assertEquals(url.getAuthority(), "bucket.oss.aliyuncs.com");
        assertEquals(url.getHost(), "bucket.oss.aliyuncs.com");
        assertEquals(url.getDefaultPort(), 80);
        assertEquals(url.getProtocol(), "http");
        assertEquals(url.getQuery(), "Expires=1422720000&OSSAccessKeyId=id&Signature=XA8ThdVKdJQ4vlkoggdzCs5s1RY%3D");
        assertEquals(url.getFile(), "/key?Expires=1422720000&OSSAccessKeyId=id&Signature=XA8ThdVKdJQ4vlkoggdzCs5s1RY%3D");
        request.setContentMD5("md5'");
        url = client.generatePresignedUrl(request);
        assertTrue(!url.getQuery().equals("Expires=1422720000&OSSAccessKeyId=id&Signature=XA8ThdVKdJQ4vlkoggdzCs5s1RY%3D"));
        request.setContentMD5("md5'");
        url = client.generatePresignedUrl(request);
        assertTrue(!url.getQuery().equals("Expires=1422720000&OSSAccessKeyId=id&Signature=XA8ThdVKdJQ4vlkoggdzCs5s1RY%3D"));
        request.setContentType("type'");
        request.setContentMD5("md5");
        url = client.generatePresignedUrl(request);
        assertTrue(!url.getQuery().equals("Expires=1422720000&OSSAccessKeyId=id&Signature=XA8ThdVKdJQ4vlkoggdzCs5s1RY%3D"));

        request.setBucketName(null);
        try {
            url = client.generatePresignedUrl(request);
            assertTrue(false);
        }catch (Exception e) {
            assertTrue(true);
        }

        request.setBucketName("bucket");
        request.setExpiration(null);
        try {
            url = client.generatePresignedUrl(request);
            assertTrue(false);
        }catch (Exception e) {
            assertTrue(true);
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
        assertEquals("userAgent", conf.getUserAgent());

        conf.setProxyPort(100);
        assertEquals(100, conf.getProxyPort());

        try {
            conf.setProxyPort(-1);
            assertTrue(false);
        }catch (Exception e) {
            assertTrue(true);
        }

        conf.setProxyDomain("domain");
        assertEquals("domain", conf.getProxyDomain());

        conf.setProxyWorkstation("workstation");
        assertEquals("workstation", conf.getProxyWorkstation());

        conf.setMaxConnections(100);
        assertEquals(100, conf.getMaxConnections());

        conf.setSocketTimeout(100);
        assertEquals(100, conf.getSocketTimeout());

        conf.setConnectionRequestTimeout(100);
        assertEquals(100, conf.getConnectionRequestTimeout());

        conf.setConnectionTTL(100);
        assertEquals(100, conf.getConnectionTTL());

        conf.setUseReaper(true);
        assertEquals(true, conf.isUseReaper());

        conf.setIdleConnectionTime(100);
        assertEquals(100, conf.getIdleConnectionTime());

        conf.setProtocol(Protocol.HTTP);
        assertEquals(Protocol.HTTP, conf.getProtocol());

        conf.setRequestTimeoutEnabled(true);
        assertEquals(true, conf.isRequestTimeoutEnabled());

        conf.setRequestTimeout(100);
        assertEquals(100, conf.getRequestTimeout());

        conf.setSlowRequestsThreshold(100);
        assertEquals(100, conf.getSlowRequestsThreshold());

        conf.addDefaultHeader("k", "v");
        Map<String, String> defaultHeaders = new HashMap<String, String>();
        defaultHeaders.put("key", "value");
        conf.setDefaultHeaders(defaultHeaders);
        assertEquals(defaultHeaders , conf.getDefaultHeaders());

        conf.setCrcCheckEnabled(true);
        assertEquals(true, conf.isCrcCheckEnabled());

        conf.setSignerHandlers(null);

        List<String> cnameList = conf.getCnameExcludeList();
        assertEquals(3, cnameList.size());
        assertEquals(true, cnameList.contains("aliyuncs.com"));
        assertEquals(true, cnameList.contains("aliyun-inc.com"));
        assertEquals(true, cnameList.contains("aliyun.com"));

        cnameList = new ArrayList<String>();
        cnameList.add("");
        cnameList.add("cname");
        cnameList.add("cname1");
        cnameList.add("aliyuncs.com");
        conf.setCnameExcludeList(cnameList);
        List<String> gCnameList = conf.getCnameExcludeList();
        assertEquals(5, gCnameList.size());
        assertEquals(true, gCnameList.contains("cname"));
        assertEquals(true, gCnameList.contains("cname1"));
        assertEquals(true, gCnameList.contains("aliyun-inc.com"));

        cnameList = new ArrayList<String>();
        conf.setCnameExcludeList(cnameList);
        gCnameList = conf.getCnameExcludeList();
        assertEquals(3, gCnameList.size());
        assertEquals(true, gCnameList.contains("aliyuncs.com"));
        assertEquals(true, gCnameList.contains("aliyun-inc.com"));
        assertEquals(true, gCnameList.contains("aliyun.com"));

        try {
            conf.setCnameExcludeList(null);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSwitchFuncWithException() {

        OSS client = new OSSClientBuilder().build("endpoint", "ak", "sk", "");

        try {
            client.switchCredentials(null);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            client.switchSignatureVersion(null);
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDeprecationFunction() {

        OSSClient client = new OSSClient("ak", "sk");
        assertEquals(OSSConstants.DEFAULT_OSS_ENDPOINT, client.getEndpoint().toString());

        client = new OSSClient("endpoint", "ak", "sk", "sts");
        assertEquals("http://endpoint", client.getEndpoint().toString());


        client = new OSSClient("endpoint1", "ak", "sk", new ClientConfiguration());
        assertEquals("http://endpoint1", client.getEndpoint().toString());

        client = new OSSClient("endpoint2", "ak", "sk", "sts", new ClientConfiguration());
        assertEquals("http://endpoint2", client.getEndpoint().toString());

        try {
            client.isBucketExist("bucketName");
        } catch (Exception e){}
    }
}

