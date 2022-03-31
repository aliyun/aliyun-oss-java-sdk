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

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import org.junit.Test;

import java.net.URL;
import java.util.*;

import org.junit.jupiter.api.Assertions;


public class ObjectRelatedTest {

    @Test
    public void testCallback() {
        Callback callback = new Callback();
        Assertions.assertEquals(false, callback.hasCallbackVar());

        Map<String, String> callbackVar = new HashMap<String, String>();
        callback.setCallbackVar(callbackVar);
        Assertions.assertEquals(false, callback.hasCallbackVar());

        callbackVar.put("key1", "value1");
        callback.setCallbackVar(callbackVar);
        Assertions.assertEquals(true, callback.hasCallbackVar());

        callback.setCallbackVar(null);
        Assertions.assertEquals(false, callback.hasCallbackVar());

        Assertions.assertEquals("2", Callback.CalbackBodyType.JSON.toString());
    }

    @Test
    public void testCopyObjectRequest() {
        CopyObjectRequest request = new CopyObjectRequest("src-bucket", "src-key", "dst-bucket", "dst-key");

        List<String> matchingETagConstraints = new ArrayList<String>();
        Assertions.assertEquals(0, request.getMatchingETagConstraints().size());
        request.setMatchingETagConstraints(matchingETagConstraints);
        Assertions.assertEquals(0, request.getMatchingETagConstraints().size());
        matchingETagConstraints.add("123");
        request.setMatchingETagConstraints(matchingETagConstraints);
        Assertions.assertEquals(matchingETagConstraints, request.getMatchingETagConstraints());
        request.setMatchingETagConstraints(null);
        Assertions.assertEquals(0, request.getMatchingETagConstraints().size());

        List<String> nonmatchingEtagConstraints = new ArrayList<String>();
        Assertions.assertEquals(0, request.getNonmatchingEtagConstraints().size());
        request.setNonmatchingETagConstraints(nonmatchingEtagConstraints);
        Assertions.assertEquals(0, request.getNonmatchingEtagConstraints().size());
        nonmatchingEtagConstraints.add("123");
        request.setNonmatchingETagConstraints(nonmatchingEtagConstraints);
        Assertions.assertEquals(nonmatchingEtagConstraints, request.getNonmatchingEtagConstraints());
        request.setNonmatchingETagConstraints(null);
        Assertions.assertEquals(0, request.getNonmatchingEtagConstraints().size());

        request.setNonmatchingETagConstraints(nonmatchingEtagConstraints);
        request.clearNonmatchingETagConstraints();
        Assertions.assertEquals(0, request.getNonmatchingEtagConstraints().size());

        request = new CopyObjectRequest("src-bucket", "src-key", "id", "dst-bucket", "dst-key");
        request.clearMatchingETagConstraints();
    }

    @Test
    public void testDeleteObjectsRequest() {
        List<String> keys = new ArrayList<String>();
        keys.add("key1");
        keys.add("key2");

        DeleteObjectsRequest request = new DeleteObjectsRequest("bucket")
                .withQuiet(true).withEncodingType("type").withKeys(keys);
        Assertions.assertTrue(request.isQuiet());
        Assertions.assertEquals("type", request.getEncodingType());
        Assertions.assertEquals(keys, request.getKeys());

        try {
            request.setKeys(null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            keys = new ArrayList<String>();
            request.setKeys(keys);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            keys = new ArrayList<String>();
            keys.add(null);
            request.setKeys(keys);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            keys = new ArrayList<String>();
            keys.add("");
            request.setKeys(keys);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            keys = new ArrayList<String>();
            keys.add("//aaaa");
            request.setKeys(keys);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void testDownloadFileRequest() {
        DownloadFileRequest request = new DownloadFileRequest("bucket", "key", "filePath", 0);
        request.setTaskNum(1);
        request = new DownloadFileRequest("bucket", "key", "filePath", 0, 1, false);
        request.setTaskNum(1);
        request = new DownloadFileRequest("bucket", "key", "filePath", 0, 1, true, "checkfile");

        List<String> eTagList = new ArrayList<String>();
        eTagList.add("item1");
        request.setMatchingETagConstraints(eTagList);
        Assertions.assertEquals(eTagList, request.getMatchingETagConstraints());
        request.clearMatchingETagConstraints();
        Assertions.assertEquals(0, request.getMatchingETagConstraints().size());

        request.setMatchingETagConstraints(eTagList);
        Assertions.assertEquals(eTagList, request.getMatchingETagConstraints());
        request.setMatchingETagConstraints(null);
        Assertions.assertEquals(0, request.getMatchingETagConstraints().size());

        request.setMatchingETagConstraints(eTagList);
        Assertions.assertEquals(eTagList, request.getMatchingETagConstraints());
        eTagList.clear();
        request.setMatchingETagConstraints(eTagList);
        Assertions.assertEquals(0, request.getMatchingETagConstraints().size());

        eTagList.clear();
        eTagList.add("item1");
        request.setNonmatchingETagConstraints(eTagList);
        Assertions.assertEquals(eTagList, request.getNonmatchingETagConstraints());
        request.clearNonmatchingETagConstraints();
        Assertions.assertEquals(0, request.getNonmatchingETagConstraints().size());

        request.setNonmatchingETagConstraints(eTagList);
        Assertions.assertEquals(eTagList, request.getNonmatchingETagConstraints());
        request.setNonmatchingETagConstraints(null);
        Assertions.assertEquals(0, request.getNonmatchingETagConstraints().size());

        request.setNonmatchingETagConstraints(eTagList);
        Assertions.assertEquals(eTagList, request.getNonmatchingETagConstraints());
        eTagList.clear();
        request.setNonmatchingETagConstraints(eTagList);
        Assertions.assertEquals(0, request.getNonmatchingETagConstraints().size());

        request.setUnmodifiedSinceConstraint(null);
        request.setModifiedSinceConstraint(null);
        request.setResponseHeaders(null);
    }

    @Test
    public void testGetObjectRequest() {
        GetObjectRequest request = new GetObjectRequest("bucket", "key").withRange(10, 20);
        Assertions.assertEquals(10, request.getRange()[0]);
        Assertions.assertEquals(20, request.getRange()[1]);

        request.setUseUrlSignature(true);
        Assertions.assertEquals(true, request.isUseUrlSignature());

        List<String> eTagList = new ArrayList<String>();
        eTagList.add("tag1");
        request.setNonmatchingETagConstraints(eTagList);
        Assertions.assertEquals(eTagList, request.getNonmatchingETagConstraints());
        request.clearNonmatchingETagConstraints();
        Assertions.assertEquals(0, request.getNonmatchingETagConstraints().size());

        eTagList = new ArrayList<String>();
        eTagList.add("tag2");
        request.setMatchingETagConstraints(eTagList);
        Assertions.assertEquals(eTagList, request.getMatchingETagConstraints());
        request.clearMatchingETagConstraints();
        Assertions.assertEquals(0, request.getMatchingETagConstraints().size());

        request = new GetObjectRequest((URL) null, null);
        Assertions.assertEquals(null, request.getAbsoluteUri());

        Map<String, String> requestHeaders = new HashMap<String, String>();
        request = new GetObjectRequest((URL) null, requestHeaders);
        Assertions.assertEquals(null, request.getAbsoluteUri());
    }

    @Test
    public void testHeadObjectRequest() {
        HeadObjectRequest request = new HeadObjectRequest("bucket", "key");
        request.setBucketName("new-bucket");
        request.setKey("new-key");
        request.setVersionId("new-id");
        Assertions.assertEquals("new-bucket", request.getBucketName());
        Assertions.assertEquals("new-key", request.getKey());
        Assertions.assertEquals("new-id", request.getVersionId());

        List<String> eTagList = new ArrayList<String>();
        request.setMatchingETagConstraints(null);
        request.setMatchingETagConstraints(eTagList);
        request.setNonmatchingETagConstraints(null);
        request.setNonmatchingETagConstraints(eTagList);
    }

    @Test
    public void testObjectMetadata() {
        ObjectMetadata meta = new ObjectMetadata();
        Map<String, String> userMetadata = new HashMap<String, String>();
        Date date = new Date();

        meta.setUserMetadata(null);
        meta.setUserMetadata(userMetadata);
        meta.setLastModified(date);

        Assertions.assertEquals(date, meta.getLastModified());
        Assertions.assertNull(meta.getContentMD5());
        Assertions.assertNull(meta.getContentDisposition());
        Assertions.assertNull(meta.getServerSideEncryptionKeyId());

        try {
            meta.getExpirationTime();
        } catch (Exception e) {
        }
        meta.setExpirationTime(date);

        try {
            meta.getServerCRC();
        } catch (Exception e) {
        }

        try {
            meta.getObjectStorageClass();
        } catch (Exception e) {
        }

        try {
            meta.isRestoreCompleted();
        } catch (Exception e) {
        }
        meta.setHeader(OSSHeaders.OSS_RESTORE, "");
        Assertions.assertEquals(true, meta.isRestoreCompleted());

        meta.setHeader(OSSHeaders.OSS_RESTORE, OSSHeaders.OSS_ONGOING_RESTORE);
        Assertions.assertEquals(false, meta.isRestoreCompleted());

        meta.setContentDisposition("disposition");
        Assertions.assertEquals("disposition", meta.getContentDisposition());

        Map<String, String> tags = new HashMap<String, String>();
        meta.setObjectTagging(null);
        Assertions.assertEquals(null, meta.getRawMetadata().get(OSSHeaders.OSS_TAGGING));

        meta.setObjectTagging(tags);
        Assertions.assertEquals(null, meta.getRawMetadata().get(OSSHeaders.OSS_TAGGING));

        try {
            tags.clear();
            tags.put(null, "value");
            meta.setObjectTagging(tags);
        } catch (Exception e) {
        }

        try {
            tags.clear();
            tags.put("key", null);
            meta.setObjectTagging(tags);
        } catch (Exception e) {
        }

        try {
            tags.clear();
            tags.put("", "");
            meta.setObjectTagging(tags);
        } catch (Exception e) {
        }

        try {
            tags.clear();
            tags.put("key", "");
            meta.setObjectTagging(tags);
        } catch (Exception e) {
        }
    }

    @Test
    public void testListObjectsRequest() {
        ListObjectsRequest request = new ListObjectsRequest().withDelimiter("#").withEncodingType("url").withMarker("marker")
                .withPrefix("prefix").withMaxKeys(30);
        request.setBucketName("bucket");
        Assertions.assertEquals("#", request.getDelimiter());
        Assertions.assertEquals("prefix", request.getPrefix());

        ObjectListing list = new ObjectListing();
        List<OSSObjectSummary> objectSummaries = new ArrayList<OSSObjectSummary>();
        List<String> commonPrefixes = new ArrayList<String>();
        list.setObjectSummaries(null);
        list.setObjectSummaries(objectSummaries);
        objectSummaries.add(new OSSObjectSummary());
        list.setObjectSummaries(objectSummaries);

        list.setCommonPrefixes(null);
        list.setCommonPrefixes(commonPrefixes);
        commonPrefixes.add("prefix");
        list.setCommonPrefixes(commonPrefixes);
    }

    @Test
    public void testPayer() {
        Payer payer = Payer.parse("BucketOwner");
        Assertions.assertEquals(Payer.BucketOwner, payer);
        try {
            payer = Payer.parse("UN");
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testDeleteVersionRequest() {
        DeleteVersionRequest reqeust = new DeleteVersionRequest("bucket", "key", "id");
        Assertions.assertEquals("id", reqeust.getVersionId());

        reqeust.setVersionId("new id");
        Assertions.assertEquals("new id", reqeust.getVersionId());

        reqeust = new DeleteVersionRequest("bucket", "key", "id").withVersionId("new id1");
        Assertions.assertEquals("new id1", reqeust.getVersionId());
    }

    @Test
    public void testDeleteVersionsRequest() {
        DeleteVersionsRequest reqeust = new DeleteVersionsRequest("bucket").withQuiet(true);
        Assertions.assertEquals(true, reqeust.getQuiet());

        reqeust.setQuiet(false);
        Assertions.assertEquals(false, reqeust.getQuiet());

        reqeust = reqeust.withBucketName("new-bucket");
        Assertions.assertEquals("new-bucket", reqeust.getBucketName());

        DeleteVersionsRequest.KeyVersion keyVersion = new DeleteVersionsRequest.KeyVersion("key");
        Assertions.assertEquals(null, keyVersion.getVersion());
        Assertions.assertEquals("key", keyVersion.getKey());
    }

    @Test
    public void testDeleteVersionsResult() {
        DeleteVersionsResult.DeletedVersion version = new DeleteVersionsResult.DeletedVersion();
        version.setDeleteMarker(false);
        version.setDeleteMarkerVersionId("markerId");
        version.setKey("key");
        version.setVersionId("versionid");

        Assertions.assertEquals(false, version.isDeleteMarker());
        Assertions.assertEquals("markerId", version.getDeleteMarkerVersionId());
        Assertions.assertEquals("key", version.getKey());
        Assertions.assertEquals("versionid", version.getVersionId());
    }

    @Test
    public void testOSSVersionSummary() {
        OSSVersionSummary summary = new OSSVersionSummary();
        summary.setBucketName("bucket");
        summary.setETag("etag");
        summary.setIsLatest(true);
        summary.setIsDeleteMarker(true);
        summary.setSize(100);
        summary.setStorageClass("IA");

        Assertions.assertEquals("bucket", summary.getBucketName());
        Assertions.assertEquals("etag", summary.getETag());
        Assertions.assertEquals(true, summary.isLatest());
        Assertions.assertEquals(true, summary.isDeleteMarker());
        Assertions.assertEquals(100, summary.getSize());
        Assertions.assertEquals("IA", summary.getStorageClass());
    }

    @Test
    public void testOptionsRequest() {
        OptionsRequest request = new OptionsRequest().withOrigin("origin").withRequestMethod(HttpMethod.DELETE).withRequestHeaders("header");
        Assertions.assertEquals("origin", request.getOrigin());
        Assertions.assertEquals(HttpMethod.DELETE, request.getRequestMethod());
        Assertions.assertEquals("header", request.getRequestHeaders());
    }

    @Test
    public void testCSVFormat() {
        CSVFormat format = new CSVFormat();
        format.setAllowQuotedRecordDelimiter(false);
        Assertions.assertEquals(false, format.isAllowQuotedRecordDelimiter());

        format = new CSVFormat().withAllowQuotedRecordDelimiter(false);
        Assertions.assertEquals(false, format.isAllowQuotedRecordDelimiter());

        format.setCommentChar(null);
        Assertions.assertEquals(null, format.getCommentChar());
        format.setCommentChar("");
        Assertions.assertEquals(null, format.getCommentChar());

        format.setFieldDelimiter(null);
        Assertions.assertEquals(null, format.getFieldDelimiter());
        format.setFieldDelimiter("");
        Assertions.assertEquals(null, format.getFieldDelimiter());

        format.setQuoteChar(null);
        Assertions.assertEquals(null, format.getQuoteChar());
        format.setQuoteChar("");
        Assertions.assertEquals(null, format.getQuoteChar());
    }

    @Test
    public void testCreateSelectObjectMetadataRequest() {
        CreateSelectObjectMetadataRequest request =
                new CreateSelectObjectMetadataRequest("bucket", "key").withProcess("process");
        Assertions.assertEquals("process", request.getProcess());

        SelectObjectMetadata meta = new SelectObjectMetadata();
        meta.setContentType("type");
        Assertions.assertEquals("type", meta.getContentType());
    }

    @Test
    public void testJsonFormat() {
        JsonFormat format = new JsonFormat();
        format.setRecordDelimiter("#");
        Assertions.assertEquals("#", format.getRecordDelimiter());
    }

    @Test
    public void testSelectObjectException() {
        SelectObjectException e = new SelectObjectException("error", "message", "id");
        Assertions.assertEquals("error", e.getErrorCode());
        Assertions.assertEquals("id", e.getRequestId());
        Assertions.assertFalse(e.toString().isEmpty());
    }

    @Test
    public void testSelectObjectRequest() {
        SelectObjectRequest request = new SelectObjectRequest("bucket", "key").withMaxSkippedRecordsAllowed(0);
        long[] range = new long[2];

        range[0] = 10;
        range[1] = 20;
        String value = request.splitRangeToString(range);
        System.out.println(value);
        Assertions.assertEquals("split-range=10-20", value);

        range[0] = -1;
        range[1] = 10;
        value = request.splitRangeToString(range);
        System.out.println(value);
        Assertions.assertEquals("split-range=-10", value);

        range[0] = 10;
        range[1] = -1;
        value = request.splitRangeToString(range);
        System.out.println(value);
        Assertions.assertEquals("split-range=10-", value);
    }

    @Test
    public void testGeneratePresignedUrlRequest() {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest("bucket", "key");
        Map<String, String> userMetadata = new HashMap<String, String>();
        Map<String, String> queryParam = new HashMap<String, String>();
        Map<String, String> headers = new HashMap<String, String>();

        try {
            request.setMethod(HttpMethod.DELETE);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        request.setKey("new-key");
        request.setBucketName("new-bucket");
        request.setContentMD5("md5");
        request.setUserMetadata(userMetadata);
        request.setQueryParameter(queryParam);
        request.setHeaders(headers);
        request.setAdditionalHeaderNames(null);

        Assertions.assertEquals("new-key", request.getKey());
        Assertions.assertEquals("new-bucket", request.getBucketName());
        Assertions.assertEquals("md5", request.getContentMD5());
        Assertions.assertEquals(userMetadata, request.getUserMetadata());
        Assertions.assertEquals(queryParam, request.getQueryParameter());
        Assertions.assertEquals(headers, request.getHeaders());
        Assertions.assertEquals(null, request.getAdditionalHeaderNames());

        try {
            request.setUserMetadata(null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            request.setQueryParameter(null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            request.setHeaders(null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testCreateSymlinkRequest() {
        CreateSymlinkRequest request = new CreateSymlinkRequest("bucket", "symlink", "target");
        Assertions.assertEquals("symlink", request.getSymlink());
        Assertions.assertEquals("target", request.getTarget());

        request.setSymlink("new symlink");
        request.setTarget("new target");
        Assertions.assertEquals("new symlink", request.getSymlink());
        Assertions.assertEquals("new target", request.getTarget());

        OSSSymlink symlink = new OSSSymlink("symlink", "target");
        symlink.setTarget("new target");
        Assertions.assertEquals("new target", symlink.getTarget());
        Assertions.assertFalse(symlink.toString().isEmpty());
    }

    @Test
    public void testDeleteObjectsResult() {
        List<String> deletedObjects = new ArrayList<String>();
        DeleteObjectsResult resutl = new DeleteObjectsResult(null);
        Assertions.assertEquals(null, resutl.getEncodingType());

        resutl = new DeleteObjectsResult(deletedObjects);
        Assertions.assertEquals(null, resutl.getEncodingType());

        deletedObjects.add("key");
        resutl = new DeleteObjectsResult(deletedObjects);
        Assertions.assertEquals(1, resutl.getDeletedObjects().size());
    }

    @Test
    public void testOSSObject() {
        OSSObject object = new OSSObject();
        object.setKey("");
        Assertions.assertEquals(true, object.toString().contains("Unknown"));

        object.setBucketName("test-bucket");
        Assertions.assertEquals(true, object.toString().contains("test-bucket"));

        try {
            object.close();
        } catch (Exception e) {}
    }

    @Test
    public void testResponseHeaderOverrides() {
        ResponseHeaderOverrides overides = new ResponseHeaderOverrides();
        overides.setContentType("content");
        Assertions.assertEquals("content", overides.getContentType());

        overides.setContentLangauge("lang");
        Assertions.assertEquals("lang", overides.getContentLangauge());

        overides.setExpires("expire");
        Assertions.assertEquals("expire", overides.getExpires());

        overides.setContentDisposition("contentDisposition");
        Assertions.assertEquals("contentDisposition", overides.getContentDisposition());

        overides.setContentEncoding("contentEncoding");
        Assertions.assertEquals("contentEncoding", overides.getContentEncoding());
    }

    @Test
    public void testVersionListing() {
        VersionListing list = new VersionListing();
        Assertions.assertEquals(0, list.getVersionSummaries().size());

        list.setVersionSummaries(null);
        Assertions.assertEquals(null, list.getVersionSummaries());

        list.setKeyMarker("marker");
        Assertions.assertEquals("marker", list.getKeyMarker());

        list.setVersionIdMarker("idmarker");
        Assertions.assertEquals("idmarker", list.getVersionIdMarker());
    }
}
