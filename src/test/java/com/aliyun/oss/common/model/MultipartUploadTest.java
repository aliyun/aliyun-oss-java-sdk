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

package com.aliyun.oss.common.model;

import com.aliyun.oss.model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;


public class MultipartUploadTest {

   @Test
   public void testAbortMultipartUploadRequest() {

       final String bucketName = "test-bucket-name";
       final String objectName = "test-object-name";
       final String id = "id";
       AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(bucketName, objectName, id);
       Assertions.assertEquals("id", request.getUploadId());

       request.setUploadId("new upload id");
       Assertions.assertEquals("new upload id", request.getUploadId());
   }

   @Test
   public void testCompleteMultipartUploadRequest() {
       final String bucketName = "test-bucket-name";
       final String objectName = "test-object-name";
       final String id = "id";
       List<PartETag> partETags = null;
       CompleteMultipartUploadRequest  request = new CompleteMultipartUploadRequest (bucketName, objectName, id, partETags);

       Assertions.assertEquals("id", request.getUploadId());
       Assertions.assertEquals(null, request.getPartETags());
       Assertions.assertEquals(null, request.getProcess());

       partETags = new ArrayList<PartETag>();
       partETags.add(new PartETag(1, "1"));
       partETags.add(new PartETag(2, "2"));
       Assertions.assertNotNull(partETags);

       request.setUploadId("new upload id");
       request.setProcess("process");
       request.setPartETags(partETags);
       Assertions.assertEquals("new upload id", request.getUploadId());
       Assertions.assertEquals("process", request.getProcess());
       Assertions.assertEquals(partETags, request.getPartETags());
   }

   @Test
   public void testMultipartUploadListing() {
       final String bucketName = "test-bucket-name";

       MultipartUploadListing list  = new MultipartUploadListing(bucketName);
       Assertions.assertEquals(bucketName, list.getBucketName());

       list.setKeyMarker("key Marker");
       Assertions.assertEquals("key Marker", list.getKeyMarker());

       list.setUploadIdMarker("upload id Marker");
       Assertions.assertEquals("upload id Marker", list.getUploadIdMarker());

       List<String >prefixes = new ArrayList<String>();
       prefixes.add("prefix 1");
       prefixes.add("prefix 2");
       Assertions.assertEquals(0, list.getCommonPrefixes().size());
       list.setCommonPrefixes(prefixes);
       Assertions.assertEquals(prefixes, list.getCommonPrefixes());

       list.setCommonPrefixes(null);
       Assertions.assertEquals(0, list.getCommonPrefixes().size());

       prefixes.clear();
       list.setCommonPrefixes(prefixes);
       Assertions.assertEquals(0, list.getCommonPrefixes().size());

       List<MultipartUpload >uploads = new ArrayList<MultipartUpload>();
       uploads.add(new MultipartUpload());
       list.setMultipartUploads(uploads);
       Assertions.assertEquals(uploads, list.getMultipartUploads());

       list.setMultipartUploads(null);
       Assertions.assertEquals(0, list.getMultipartUploads().size());

       uploads.clear();
       list.setMultipartUploads(uploads);
       Assertions.assertEquals(0, list.getMultipartUploads().size());
   }

   @Test
   public void testPartETag() {
       PartETag partEtag = new PartETag(1, "tag");
       partEtag.setPartNumber(10);
       partEtag.setETag("etag");
       partEtag.setPartSize(100);
       partEtag.setPartCRC((long) 2000);
   }

   @Test
    public void testPartListing() {

       PartListing list = new PartListing();
       list.setPartNumberMarker(1);
       Assertions.assertEquals(new Integer(1), list.getPartNumberMarker());

       List<PartSummary> parts = new ArrayList<PartSummary>();
       parts.add(new PartSummary());
       list.setParts(parts);
       Assertions.assertEquals(1, list.getParts().size());

       list.setParts(null);
       Assertions.assertEquals(0, list.getParts().size());

       list.setParts(parts);
       parts.clear();
       list.setParts(parts);
       Assertions.assertEquals(0, list.getParts().size());
   }

   @Test
   public void testUploadPartCopyRequest() {
       UploadPartCopyRequest request = new UploadPartCopyRequest();
       request.setMd5Digest("md5");
       Assertions.assertEquals("md5", request.getMd5Digest());

       request = new UploadPartCopyRequest("srcbucket", "srckey", "dstbucket", "dstkey", "id");

       List<String> matchingETagConstraints = new ArrayList<String>();
       request.setMatchingETagConstraints(matchingETagConstraints);
       Assertions.assertEquals(0, request.getMatchingETagConstraints().size());

       request.setMatchingETagConstraints(null);
       Assertions.assertEquals(0, request.getMatchingETagConstraints().size());

       matchingETagConstraints.add("etag");
       request.setMatchingETagConstraints(matchingETagConstraints);
       Assertions.assertEquals(1, request.getMatchingETagConstraints().size());

       request.clearMatchingETagConstraints();

       List<String> nonmatchingEtagConstraints = new ArrayList<String>();
       request.setNonmatchingETagConstraints(nonmatchingEtagConstraints);
       Assertions.assertEquals(0, request.getNonmatchingEtagConstraints().size());

       request.setNonmatchingETagConstraints(null);
       Assertions.assertEquals(0, request.getNonmatchingEtagConstraints().size());

       nonmatchingEtagConstraints.add("etag");
       request.setNonmatchingETagConstraints(nonmatchingEtagConstraints);
       Assertions.assertEquals(1, request.getNonmatchingEtagConstraints().size());

       request.clearNonmatchingETagConstraints();

       Date date = new Date();
       request.setUnmodifiedSinceConstraint(date);
       Assertions.assertEquals(date, request.getUnmodifiedSinceConstraint());

       request.setModifiedSinceConstraint(date);
       Assertions.assertEquals(date, request.getModifiedSinceConstraint());
   }

   @Test
   public void testUploadPartRequest() {
       UploadPartRequest request = new UploadPartRequest("bucket", "key");
       Assertions.assertEquals("bucket", request.getBucketName());

       request.setMd5Digest("md5");
       Assertions.assertEquals("md5", request.getMd5Digest());

       request.setUseChunkEncoding(true);
       Assertions.assertEquals(true, request.isUseChunkEncoding());

       request.setPartSize(-1);
       request.setUseChunkEncoding(false);
       Assertions.assertEquals(true, request.isUseChunkEncoding());
   }

}
