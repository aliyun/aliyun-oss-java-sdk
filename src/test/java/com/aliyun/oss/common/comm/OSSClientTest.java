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
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;

public class OSSClientTest {
	@Test
	@SuppressWarnings("deprecation")
    public void testGeneratePresignedUrl() throws IOException {
        OSSClient client = new OSSClient("id", "key");
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
    }
}

