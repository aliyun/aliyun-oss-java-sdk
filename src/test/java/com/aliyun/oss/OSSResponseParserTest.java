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

import org.junit.jupiter.api.Assertions;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.Test;

import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.internal.ResponseParsers;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.Grant;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.MultipartUploadListing;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PartListing;
import com.aliyun.oss.model.PartSummary;
import com.aliyun.oss.model.Permission;
import com.aliyun.oss.utils.ResourceUtils;

/**
 * Testing OSSResponseParser class
 */
public class OSSResponseParserTest {

    // Test file name.
    private static final String FILE_FOLDER = "oss"; 

    /**
     * Gets test file stream.
     * **/
    private InputStream getInputStream(String filename) throws Exception {
        return ResourceUtils.getTestInputStream(FILE_FOLDER + "/" + filename);
    }
    
    @Test 
    public void testParserGetBucketLocation() throws Exception {

        String filename = "getBucketLocation.xml";
        InputStream in = getInputStream(filename);
        String location = ResponseParsers.parseGetBucketLocation(in);
        Assertions.assertEquals("oss-cn-qingdao-a", location);
    }

    @Test
    public void testParseListObjects() throws Exception {

        String filename = "listObjects.xml";
        InputStream in = getInputStream(filename);

        ObjectListing objectListing = ResponseParsers.parseListObjects(in);

        Assertions.assertFalse(objectListing.isTruncated());
        Assertions.assertEquals("pacjux7y1b86pmtu7g8d6b7z-test-bucket",
                objectListing.getBucketName());
        Assertions.assertNull(objectListing.getPrefix());
        Assertions.assertNull(objectListing.getMarker());
        Assertions.assertEquals(100, objectListing.getMaxKeys());
        Assertions.assertNull(objectListing.getDelimiter());

        for (OSSObjectSummary summary : objectListing.getObjectSummaries()) {

            Assertions.assertEquals("pacjux7y1b86pmtu7g8d6b7z-test-object",
                    summary.getKey());

            Assertions.assertEquals(
                    DateUtil.parseIso8601Date("2012-02-09T01:49:38.000Z"),
                    summary.getLastModified());

            Assertions.assertEquals(10, summary.getSize());
            Assertions.assertEquals("9BF156C2C16BB90B9EC81C96FE37EF1B",
                    summary.getETag());
            Assertions.assertEquals("Standard", summary.getStorageClass());

            Assertions.assertEquals("51744", summary.getOwner().getId());
            Assertions.assertEquals("51744", summary.getOwner().getDisplayName());
        }

        in.close();

    }

    @SuppressWarnings("deprecation")
	@Test
    public void testParseGetBucketAcl() throws Exception {

        String filename = "getBucketAcl.xml";
        InputStream in = getInputStream(filename);
        AccessControlList accessControlList = ResponseParsers.parseGetBucketAcl(in);

        Assertions.assertEquals("51744", accessControlList.getOwner().getId());
        Assertions.assertEquals("51744", accessControlList.getOwner().getDisplayName());
        Assertions.assertEquals(Permission.FullControl, 
                ((Grant)(accessControlList.getGrants().toArray()[0])).getPermission());

        in.close();
    }

    @Test
    public void testParseListBucket() throws Exception {

        String filename = "listBucket.xml";
        InputStream in = getInputStream(filename);

        BucketList bucketList = ResponseParsers.parseListBucket(in);
        Assertions.assertEquals(null, bucketList.getPrefix());
        Assertions.assertEquals(null, bucketList.getMarker());
        Assertions.assertEquals(null, bucketList.getMaxKeys());
        Assertions.assertEquals(false, bucketList.isTruncated());
        Assertions.assertEquals(null, bucketList.getNextMarker());
        List<Bucket> buckets = bucketList.getBucketList();
        
        Bucket bucket1 = buckets.get(0);
        Assertions.assertEquals("51744", bucket1.getOwner().getId());
        Assertions.assertEquals("51744", bucket1.getOwner().getDisplayName());
        Assertions.assertEquals("pacjux7y1b86pmtu7g8d6b7z-test-bucket",
                bucket1.getName());
        Assertions.assertEquals(DateUtil.parseIso8601Date("2012-02-09T01:49:38.000Z"),
                bucket1.getCreationDate());

        Bucket bucket2 = buckets.get(1);
        Assertions.assertEquals("51744", bucket2.getOwner().getId());
        Assertions.assertEquals("51744", bucket2.getOwner().getDisplayName());
        Assertions.assertEquals("ganshumantest", bucket2.getName());
        Assertions.assertEquals(DateUtil.parseIso8601Date("2012-02-09T06:38:47.000Z"),
                bucket2.getCreationDate());

        in.close();
 
        filename = "listBucketTruncated.xml";
        in = getInputStream(filename);
        bucketList = ResponseParsers.parseListBucket(in);
        Assertions.assertEquals("asdasdasdasd", bucketList.getPrefix());
        Assertions.assertEquals("asdasdasd", bucketList.getMarker());
        Assertions.assertEquals(Integer.valueOf(1), bucketList.getMaxKeys());
        Assertions.assertEquals(true, bucketList.isTruncated());
        Assertions.assertEquals("asdasdasdasdasd", bucketList.getNextMarker());
        buckets = bucketList.getBucketList();
        bucket1 = buckets.get(0);
        Assertions.assertEquals("51744", bucket1.getOwner().getId());
        Assertions.assertEquals("51744", bucket1.getOwner().getDisplayName());
        Assertions.assertEquals("asdasdasdasd", bucket1.getName());
        Assertions.assertEquals("osslocation", bucket1.getLocation());
        Assertions.assertEquals(DateUtil.parseIso8601Date("2014-05-15T11:18:32.000Z"),
           bucket1.getCreationDate());
        
        in.close();
    }

    @Test
    public void testCopyObjectResult() throws Exception {

        String filename = "copyObject.xml";
        InputStream in = getInputStream(filename);
        
        CopyObjectResult result = ResponseParsers.parseCopyObjectResult(in);
        Assertions.assertEquals("4F62D1D6EF439E057D4BD20F43DC2C84", result.getETag());
        Assertions.assertEquals("Wed, 27 Jun 2012 07:28:49 GMT",
                DateUtil.formatRfc822Date(result.getLastModified()));
    }

    @Test
    public void testParseInitiateMultipartUpload() throws Exception {

        String filename = "initiateMultipartUpload.xml";
        InputStream in = getInputStream(filename);

        InitiateMultipartUploadResult initiateMultipartUploadResult = ResponseParsers
                .parseInitiateMultipartUpload(in);

        Assertions.assertEquals("dp7d8j2xfec1m984em9xmkgc_gan",
                initiateMultipartUploadResult.getBucketName());
        Assertions.assertEquals("test.rar", initiateMultipartUploadResult.getKey());

        Assertions.assertEquals("0004B9847F209E446A8BA24F84C6881D",
                initiateMultipartUploadResult.getUploadId());

        in.close();

    }

    @Test
    public void testParseListMultipartUploads() throws Exception {

        String filename = "listMultipartUploads.xml";
        InputStream in = getInputStream(filename);

        MultipartUploadListing multipartUploadListing = ResponseParsers
                .parseListMultipartUploads(in);

        Assertions.assertEquals("dp7d8j2xfec1m984em9xmkgc_gan",
                multipartUploadListing.getBucketName());
        Assertions.assertNull(multipartUploadListing.getKeyMarker());
        Assertions.assertNull(multipartUploadListing.getUploadIdMarker());
        Assertions.assertEquals("test.rar",
                multipartUploadListing.getNextKeyMarker());
        Assertions.assertEquals("0004B984CE74CF65B555A38F98CCFF96",
                multipartUploadListing.getNextUploadIdMarker());
        Assertions.assertNull(multipartUploadListing.getDelimiter());
        Assertions.assertNull(multipartUploadListing.getPrefix());
        Assertions.assertEquals(1000, multipartUploadListing.getMaxUploads());
        Assertions.assertEquals(false, multipartUploadListing.isTruncated());

        Assertions.assertEquals("test.rar", multipartUploadListing
                .getMultipartUploads().get(0).getKey());
        Assertions.assertEquals("0004B9846579745A77D988FFFDDEAFC3",
                multipartUploadListing.getMultipartUploads().get(0)
                        .getUploadId());
        Assertions.assertEquals("Standard", multipartUploadListing
                .getMultipartUploads().get(0).getStorageClass());
        Assertions.assertEquals(DateUtil.parseIso8601Date("2012-02-22T02:36:36.000Z"),
                multipartUploadListing.getMultipartUploads().get(0)
                        .getInitiated());

        in.close();
    }

    @Test
    public void testParseListParts() throws Exception {

        String filename = "listParts.xml";
        InputStream in = getInputStream(filename);

        PartListing partListing = ResponseParsers.parseListParts(in);

        Assertions.assertEquals("dp7d8j2xfec1m984em9xmkgc_gan",
                partListing.getBucketName());
        Assertions.assertEquals("test.rar", partListing.getKey());
        Assertions.assertEquals("0004B98692BB2A28C897B642CFAC1DCE",
                partListing.getUploadId());
        Assertions.assertEquals("Standard", partListing.getStorageClass());
        Assertions.assertEquals(3, partListing.getNextPartNumberMarker().intValue());
        Assertions.assertEquals(1000, partListing.getMaxParts().intValue());
        Assertions.assertEquals(false, partListing.isTruncated());
        
        List<PartSummary> parts = partListing.getParts();
        Assertions.assertNotNull(parts);
        Assertions.assertTrue(parts.size() == 1);
        PartSummary part = parts.get(0);
        Assertions.assertEquals(3, part.getPartNumber());
        Assertions.assertEquals(
                DateUtil.parseIso8601Date("2012-02-22T05:12:29.000Z"),
                part.getLastModified());
        Assertions.assertEquals("4B4BEAF5BC622FC89D29BF0E3B70B730", part.getETag());
        Assertions.assertEquals(3996796L, part.getSize());

        in.close();
    }

    @Test
    public void testParseCompleteMultipartUpload() throws Exception {

        String filename = "completeMultipartUpload.xml";
        InputStream in = getInputStream(filename);

        CompleteMultipartUploadResult completeMultipartUploadResult = ResponseParsers
                .parseCompleteMultipartUpload(in);

        Assertions.assertEquals(
                "http://oss-test.aliyun-inc.com/dp7d8j2xfec1m984em9xmkgc_gan/test.rar",
                completeMultipartUploadResult.getLocation());
        Assertions.assertEquals("dp7d8j2xfec1m984em9xmkgc_gan",
                completeMultipartUploadResult.getBucketName());
        Assertions.assertEquals("test.rar", completeMultipartUploadResult.getKey());
        Assertions.assertEquals("894061A784C63D244E6E18B80E36076A-1",
                completeMultipartUploadResult.getETag());

        in.close();
    }
}

