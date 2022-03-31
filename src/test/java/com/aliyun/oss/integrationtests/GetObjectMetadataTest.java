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

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;

import java.io.ByteArrayInputStream;

public class GetObjectMetadataTest extends TestBase {

    @Test
    public void testGetObjectMetaDataNormal() {
        String objectName = "test-obj";
        PutObjectRequest request = new PutObjectRequest(bucketName,objectName, new ByteArrayInputStream("123".getBytes()));
        ObjectMetadata meta = new ObjectMetadata();
        meta.addUserMetadata("my-key", "my-value");
        request.setMetadata(meta);

        ossClient.putObject(request);

        ObjectMetadata metaResult = ossClient.getObjectMetadata(bucketName, objectName);
        Assertions.assertEquals(3, metaResult.getContentLength());
        Assertions.assertNotNull(metaResult.getContentMD5());
        Assertions.assertNotNull(metaResult.getETag());
        Assertions.assertNotNull(metaResult.getObjectStorageClass());
        Assertions.assertNotNull(metaResult.getLastModified());
        Assertions.assertNotNull(metaResult.getRequestId());
        Assertions.assertEquals("my-value", metaResult.getUserMetadata().get("my-key"));
    }

    @Test
    public void testGetObjectUnNormal() {
        String objectName = "test-obj";
        PutObjectRequest request = new PutObjectRequest(bucketName,objectName, new ByteArrayInputStream("123".getBytes()));
        ObjectMetadata meta = new ObjectMetadata();
        meta.addUserMetadata("my-key", "my-value");

        try {
            ossClient.getObjectMetadata(bucketName + "non-exist", objectName);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_KEY, e.getErrorCode());
        }

        try {
            ossClient.getObjectMetadata(bucketName, objectName + "non-exist");
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.NO_SUCH_KEY, e.getErrorCode());
        }

        // Forbidden
        OSS client = new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT, TestConfig.OSS_TEST_ACCESS_KEY_ID,
                TestConfig.OSS_TEST_ACCESS_KEY_SECRET + " ");
        try {
            client.getObjectMetadata(bucketName, objectName + "non-exist");
            Assertions.fail("Get simplified object meta should not be successful");
        } catch (OSSException ex) {
            Assertions.assertEquals(OSSErrorCode.ACCESS_FORBIDDEN, ex.getErrorCode());
        } finally {
            client.shutdown();
        }
    }
}
