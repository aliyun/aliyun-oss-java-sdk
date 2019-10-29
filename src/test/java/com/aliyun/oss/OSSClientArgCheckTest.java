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

package com.aliyun.oss;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;

import com.aliyun.oss.internal.OSSConstants;
import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ObjectMetadata;

public class OSSClientArgCheckTest {
    private static final String validBucketName = "bucket";
    private static final String invalidBucketName = "bucket*";
    private static final String objectKey = "object";

    @Test
    public void testValidateObjectKey(){
        // 1023 chars
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 1023; ++i){
            builder.append("o");
        }

        assertTrue(OSSUtils.validateObjectKey(builder.toString()));

        builder.append("o");
        assertFalse(OSSUtils.validateObjectKey(builder.toString()));
        
        // Legal key
        assertTrue(OSSUtils.validateObjectKey((char)9 + "" + (char)0x20 + "123_.*  中文-!@#$%^&*()_+-=;'\"~`><?/':[]|\\"));
    }

    @Test
    public void testValidBucketName() {
        String bucketName = "123456789012345678901234567890123456789012345678901234567890123";
        Assert.assertTrue(OSSUtils.validateBucketName(bucketName));
        Assert.assertFalse(OSSUtils.validateBucketName(bucketName + "4"));

        bucketName = "lyz-jdifd";
        Assert.assertTrue(OSSUtils.validateBucketName(bucketName));
        Assert.assertFalse(OSSUtils.validateBucketName(bucketName + "dd-"));
        Assert.assertFalse(OSSUtils.validateBucketName(bucketName + "Ldfd"));
        Assert.assertFalse(OSSUtils.validateBucketName(bucketName + "~dd"));
        Assert.assertFalse(OSSUtils.validateBucketName(bucketName + "_dd"));
        Assert.assertFalse(OSSUtils.validateBucketName(bucketName + "\\dd"));
    }

    @Test
    public void testBucketArgChecking() throws Exception{

        OSS client = getOSSClient();

        // invalid bucket name
        try{
            client.createBucket(invalidBucketName);
            fail();
        } catch (IllegalArgumentException e) {}

        try{
            client.deleteBucket(invalidBucketName);
            fail();
        } catch (IllegalArgumentException e) {}

        try{
            client.setBucketAcl(invalidBucketName, CannedAccessControlList.PublicRead);
            fail();
        } catch (IllegalArgumentException e) {}

        try{
            client.getBucketAcl(invalidBucketName);
            fail();
        } catch (IllegalArgumentException e) {}

        try{
            client.listObjects(invalidBucketName);
            fail();
        } catch (IllegalArgumentException e) {}
        // max-keys exceeds
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(validBucketName);
        try{
            listObjectsRequest.setMaxKeys(1001);
            fail();
        } catch (IllegalArgumentException e) {}

        // valid max-keys
        listObjectsRequest.setMaxKeys(100);
    }

    @Test
    public void testPutObjectArgChecking() throws Exception{

        OSS client = getOSSClient();

        String content = "中English混合的Content。\n" + "This is the 2nd line.";
        byte[] contentBuffer = null;
        try {
            contentBuffer = content.getBytes(OSSConstants.DEFAULT_CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            fail(e.getMessage());
        }
        final ByteArrayInputStream input =
                new ByteArrayInputStream(contentBuffer);

        final ObjectMetadata metadata = new ObjectMetadata();

        // no input stream
        try{
            InputStream instream = null;
            client.putObject(validBucketName, objectKey, instream, metadata);
        } catch (IllegalArgumentException e) {}
        // too big content-length
        final long G5 = 5 * 1024 * 1024 * 1024L;
        metadata.setContentLength(G5);

        // Invalid bucket name
        metadata.setContentLength(contentBuffer.length);
        try{
            client.putObject(invalidBucketName, objectKey, input, metadata);
            fail();
        } catch (IllegalArgumentException e) {}
    }
    
    @Test
    public void testGetObjectArgChecking() throws Exception{
        OSS client = getOSSClient();

        // invalid bucket name
        try{
            client.getObject(invalidBucketName, objectKey);
            fail();
        } catch (IllegalArgumentException e) {}
        try{
            client.getObjectMetadata(invalidBucketName, objectKey);
            fail();
        } catch (IllegalArgumentException e) {}
        try{
            client.deleteObject(invalidBucketName, objectKey);
            fail();
        } catch (IllegalArgumentException e) {}
    }

    private static OSS getOSSClient(){
        return new OSSClientBuilder().build("http://localhost", "id", "key");
    }
}
