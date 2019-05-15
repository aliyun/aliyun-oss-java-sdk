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

package com.aliyun.oss.internal;

import static com.aliyun.oss.common.parser.RequestMarshallers.*;
import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.internal.OSSUtils.OSS_RESOURCE_MANAGER;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;
import static com.aliyun.oss.internal.RequestParameters.*;
import static com.aliyun.oss.internal.ResponseParsers.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ResponseHandler;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.ExceptionFactory;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.model.*;
import org.apache.http.HttpStatus;

/**
 * Bucket operation.
 */
public class OSSBucketOperation extends OSSOperation {

    public OSSBucketOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }

    /**
     * Create a bucket.
     */
    public Bucket createBucket(CreateBucketRequest createBucketRequest) throws OSSException, ClientException {

        assertParameterNotNull(createBucketRequest, "createBucketRequest");

        String bucketName = bucketNameCheck(createBucketRequest);

        Map<String, String> headers = new HashMap<String, String>();
        addOptionalACLHeader(headers, createBucketRequest.getCannedACL());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setHeaders(headers)
                .setInputStreamWithLength(createBucketRequestMarshaller.marshall(createBucketRequest))
                .setOriginalRequest(createBucketRequest).build();

        ResponseMessage result = doOperation(request, emptyResponseParser, bucketName, null);
        return new Bucket(bucketName, result.getRequestId());
    }

    /**
     * Delete a bucket.
     */
    public void deleteBucket(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setOriginalRequest(genericRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * List all my buckets.
     */
    public List<Bucket> listBuckets() throws OSSException, ClientException {
        BucketList bucketList = listBuckets(new ListBucketsRequest(null, null, null));
        List<Bucket> buckets = bucketList.getBucketList();
        while (bucketList.isTruncated()) {
            bucketList = listBuckets(new ListBucketsRequest(null, bucketList.getNextMarker(), null));
            buckets.addAll(bucketList.getBucketList());
        }
        return buckets;
    }

    /**
     * List all my buckets.
     */
    public BucketList listBuckets(ListBucketsRequest listBucketRequest) throws OSSException, ClientException {

        assertParameterNotNull(listBucketRequest, "listBucketRequest");

        Map<String, String> params = new LinkedHashMap<String, String>();
        if (listBucketRequest.getPrefix() != null) {
            params.put(PREFIX, listBucketRequest.getPrefix());
        }
        if (listBucketRequest.getMarker() != null) {
            params.put(MARKER, listBucketRequest.getMarker());
        }
        if (listBucketRequest.getMaxKeys() != null) {
            params.put(MAX_KEYS, Integer.toString(listBucketRequest.getMaxKeys()));
        }
        if (listBucketRequest.getBid() != null) {
            params.put(BID, listBucketRequest.getBid());
        }
        if (listBucketRequest.getTagSet() != null) {
            String tagging = "";
            TagSet tagSet = listBucketRequest.getTagSet();
            if (tagSet.getAllTags().size() > 0) {
                tagging = JSON.toJSONString(tagSet.getAllTags());
                int length = tagging.length();
                tagging = tagging.substring(1, length - 1);
            }
            params.put(TAGGING, tagging);
        }
        if (listBucketRequest.isRegionList()) {
            params.put(SUBRESOURCE_REGION_LIST, null);
        }

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).setOriginalRequest(listBucketRequest).build();

        return doOperation(request, listBucketResponseParser, null, null, true);
    }

    /**
     * Set bucket's canned ACL.
     */
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketAclRequest, "setBucketAclRequest");

        String bucketName = bucketNameCheck(setBucketAclRequest);

        Map<String, String> headers = new HashMap<String, String>();
        addOptionalACLHeader(headers, setBucketAclRequest.getCannedACL());

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setHeaders(headers).setParameters(params)
                .setOriginalRequest(setBucketAclRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket's ACL.
     */
    public AccessControlList getBucketAcl(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketAclResponseParser, bucketName, null, true);
    }

    public BucketMetadata getBucketMetadata(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.HEAD).setBucket(bucketName).setOriginalRequest(genericRequest).build();

        List<ResponseHandler> reponseHandlers = new ArrayList<ResponseHandler>();
        reponseHandlers.add(new ResponseHandler() {
            @Override
            public void handle(ResponseMessage response) throws ServiceException, ClientException {
                if (response.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                    safeCloseResponse(response);
                    throw ExceptionFactory.createOSSException(
                            response.getHeaders().get(OSSHeaders.OSS_HEADER_REQUEST_ID), OSSErrorCode.NO_SUCH_BUCKET,
                            OSS_RESOURCE_MANAGER.getString("NoSuchBucket"));
                }
            }
        });

        return doOperation(request, ResponseParsers.getBucketMetadataResponseParser, bucketName, null, true, null,
                reponseHandlers);
    }

    /**
     * Set bucket referer.
     */
    public void setBucketReferer(SetBucketRefererRequest setBucketRefererRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketRefererRequest, "setBucketRefererRequest");

        String bucketName = bucketNameCheck(setBucketRefererRequest);

        BucketReferer referer = setBucketRefererRequest.getReferer();
        if (referer == null) {
            referer = new BucketReferer();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_REFERER, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(bucketRefererMarshaller.marshall(referer))
                .setOriginalRequest(setBucketRefererRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket referer.
     */
    public BucketReferer getBucketReferer(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_REFERER, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketRefererResponseParser, bucketName, null, true);
    }

    /**
     * Get bucket location.
     */
    public String getBucketLocation(GenericRequest genericRequest) {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOCATION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketLocationResponseParser, bucketName, null, true);
    }

    /**
     * Determine whether a bucket exists or not.
     */
    public boolean doesBucketExists(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        try {
            getBucketAcl(new GenericRequest(bucketName));
        } catch (OSSException oe) {
            if (oe.getErrorCode().equals(OSSErrorCode.INTERNAL_ERROR)) {
                throw oe;
            }
            if (oe.getErrorCode().equals(OSSErrorCode.NO_SUCH_BUCKET)) {
                return false;
            }
        }
        return true;
    }

    /**
     * List objects under the specified bucket.
     */
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws OSSException, ClientException {

        assertParameterNotNull(listObjectsRequest, "listObjectsRequest");

        String bucketName = bucketNameCheck(listObjectsRequest);

        Map<String, String> params = new LinkedHashMap<String, String>();
        populateListObjectsRequestParameters(listObjectsRequest, params);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(listObjectsRequest).build();

        return doOperation(request, listObjectsReponseParser, bucketName, null, true);
    }

    /**
     * Set bucket logging.
     */
    public void setBucketLogging(SetBucketLoggingRequest setBucketLoggingRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketLoggingRequest, "setBucketLoggingRequest");

        String bucketName = bucketNameCheck(setBucketLoggingRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(setBucketLoggingRequestMarshaller.marshall(setBucketLoggingRequest))
                .setOriginalRequest(setBucketLoggingRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Put bucket image
     */
    public void putBucketImage(PutBucketImageRequest putBucketImageRequest) {
        assertParameterNotNull(putBucketImageRequest, "putBucketImageRequest");
        String bucketName = bucketNameCheck(putBucketImageRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_IMG, null);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(putBucketImageRequest)
                .setInputStreamWithLength(putBucketImageRequestMarshaller.marshall(putBucketImageRequest)).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket image
     */
    public GetBucketImageResult getBucketImage(String bucketName, GenericRequest genericRequest) {
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_IMG, null);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();
        return doOperation(request, getBucketImageResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket image attributes.
     */
    public void deleteBucketImage(String bucketName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_IMG, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * put image style
     */
    public void putImageStyle(PutImageStyleRequest putImageStyleRequest) throws OSSException, ClientException {
        assertParameterNotNull(putImageStyleRequest, "putImageStyleRequest");
        String bucketName = putImageStyleRequest.GetBucketName();
        String styleName = putImageStyleRequest.GetStyleName();
        assertParameterNotNull(styleName, "styleName");
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_STYLE, null);
        params.put(STYLE_NAME, styleName);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(putImageStyleRequest)
                .setInputStreamWithLength(putImageStyleRequestMarshaller.marshall(putImageStyleRequest)).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    public void deleteImageStyle(String bucketName, String styleName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(styleName, "styleName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_STYLE, null);
        params.put(STYLE_NAME, styleName);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    public GetImageStyleResult getImageStyle(String bucketName, String styleName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(styleName, "styleName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_STYLE, null);
        params.put(STYLE_NAME, styleName);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getImageStyleResponseParser, bucketName, null, true);
    }

    /**
     * List image style.
     */
    public List<Style> listImageStyle(String bucketName, GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        ;
        params.put(SUBRESOURCE_STYLE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, listImageStyleResponseParser, bucketName, null, true);
    }

    public void setBucketProcess(SetBucketProcessRequest setBucketProcessRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketProcessRequest, "setBucketProcessRequest");

        ImageProcess imageProcessConf = setBucketProcessRequest.getImageProcess();
        assertParameterNotNull(imageProcessConf, "imageProcessConf");

        String bucketName = bucketNameCheck(setBucketProcessRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_PROCESS_CONF, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(bucketImageProcessConfMarshaller.marshall(imageProcessConf))
                .setOriginalRequest(setBucketProcessRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    public BucketProcess getBucketProcess(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_PROCESS_CONF, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketImageProcessConfResponseParser, bucketName, null, true);
    }

    /**
     * Get bucket logging.
     */
    public BucketLoggingResult getBucketLogging(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketLoggingResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket logging.
     */
    public void deleteBucketLogging(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Set bucket website.
     */
    public void setBucketWebsite(SetBucketWebsiteRequest setBucketWebSiteRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketWebSiteRequest, "setBucketWebSiteRequest");

        String bucketName = bucketNameCheck(setBucketWebSiteRequest);

        if (setBucketWebSiteRequest.getIndexDocument() == null && setBucketWebSiteRequest.getErrorDocument() == null
                && setBucketWebSiteRequest.getRoutingRules().size() == 0) {
            throw new IllegalArgumentException(String.format("IndexDocument/ErrorDocument/RoutingRules must have one"));
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(setBucketWebsiteRequestMarshaller.marshall(setBucketWebSiteRequest))
                .setOriginalRequest(setBucketWebSiteRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket website.
     */
    public BucketWebsiteResult getBucketWebsite(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketWebsiteResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket website.
     */
    public void deleteBucketWebsite(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Set bucket lifecycle.
     */
    public void setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(setBucketLifecycleRequest, "setBucketLifecycleRequest");

        String bucketName = bucketNameCheck(setBucketLifecycleRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(setBucketLifecycleRequestMarshaller.marshall(setBucketLifecycleRequest))
                .setOriginalRequest(setBucketLifecycleRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket lifecycle.
     */
    public List<LifecycleRule> getBucketLifecycle(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketLifecycleResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket lifecycle.
     */
    public void deleteBucketLifecycle(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Set bucket tagging.
     */
    public void setBucketTagging(SetBucketTaggingRequest setBucketTaggingRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketTaggingRequest, "setBucketTaggingRequest");

        String bucketName = bucketNameCheck(setBucketTaggingRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(setBucketTaggingRequestMarshaller.marshall(setBucketTaggingRequest))
                .setOriginalRequest(setBucketTaggingRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket tagging.
     */
    public TagSet getBucketTagging(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketTaggingResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket tagging.
     */
    public void deleteBucketTagging(DeleteBucketTaggingRequest deleteBucketTaggingRequest) throws OSSException, ClientException {

        assertParameterNotNull(deleteBucketTaggingRequest, "genericRequest");

        String bucketName = bucketNameCheck(deleteBucketTaggingRequest);

        Map<String, String> params = new HashMap<String, String>();
        String keys = deleteBucketTaggingRequest.getTags();
        params.put(SUBRESOURCE_TAGGING, keys);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(deleteBucketTaggingRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Add bucket replication.
     */
    public void addBucketReplication(AddBucketReplicationRequest addBucketReplicationRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(addBucketReplicationRequest, "addBucketReplicationRequest");
        assertParameterNotNull(addBucketReplicationRequest.getTargetBucketName(), "targetBucketName");
        assertParameterNotNull(addBucketReplicationRequest.getTargetBucketLocation(), "targetBucketLocation");

        String bucketName = bucketNameCheck(addBucketReplicationRequest);

        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_ADD);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(addBucketReplicationRequestMarshaller.marshall(addBucketReplicationRequest))
                .setOriginalRequest(addBucketReplicationRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket replication.
     */
    public List<ReplicationRule> getBucketReplication(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketReplicationResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket replication.
     */
    public void deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(deleteBucketReplicationRequest, "deleteBucketReplicationRequest");
        assertParameterNotNull(deleteBucketReplicationRequest.getReplicationRuleID(), "replicationRuleID");

        String bucketName = deleteBucketReplicationRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_DELETE);

        byte[] rawContent = deleteBucketReplicationRequestMarshaller.marshall(deleteBucketReplicationRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(deleteBucketReplicationRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    private static void addRequestRequiredHeaders(Map<String, String> headers, byte[] rawContent) {
        headers.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(rawContent.length));

        byte[] md5 = BinaryUtil.calculateMd5(rawContent);
        String md5Base64 = BinaryUtil.toBase64String(md5);
        headers.put(HttpHeaders.CONTENT_MD5, md5Base64);
    }

    /**
     * Get bucket replication progress.
     */
    public BucketReplicationProgress getBucketReplicationProgress(
            GetBucketReplicationProgressRequest getBucketReplicationProgressRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(getBucketReplicationProgressRequest, "getBucketReplicationProgressRequest");
        assertParameterNotNull(getBucketReplicationProgressRequest.getReplicationRuleID(), "replicationRuleID");

        String bucketName = bucketNameCheck(getBucketReplicationProgressRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION_PROGRESS, null);
        params.put(RequestParameters.RULE_ID, getBucketReplicationProgressRequest.getReplicationRuleID());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(getBucketReplicationProgressRequest).build();

        return doOperation(request, getBucketReplicationProgressResponseParser, bucketName, null, true);
    }

    /**
     * Get bucket replication progress.
     */
    public List<String> getBucketReplicationLocation(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION_LOCATION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketReplicationLocationResponseParser, bucketName, null, true);
    }

    public void addBucketCname(AddBucketCnameRequest addBucketCnameRequest) throws OSSException, ClientException {

        assertParameterNotNull(addBucketCnameRequest, "addBucketCnameRequest");
        assertParameterNotNull(addBucketCnameRequest.getDomain(), "addBucketCnameRequest.domain");

        String bucketName = bucketNameCheck(addBucketCnameRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_CNAME, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_ADD);

        byte[] rawContent = addBucketCnameRequestMarshaller.marshall(addBucketCnameRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(addBucketCnameRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    public List<CnameConfiguration> getBucketCname(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_CNAME, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketCnameResponseParser, bucketName, null, true);
    }

    public void deleteBucketCname(DeleteBucketCnameRequest deleteBucketCnameRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(deleteBucketCnameRequest, "deleteBucketCnameRequest");
        assertParameterNotNull(deleteBucketCnameRequest.getDomain(), "deleteBucketCnameRequest.domain");

        String bucketName = bucketNameCheck(deleteBucketCnameRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_CNAME, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_DELETE);

        byte[] rawContent = deleteBucketCnameRequestMarshaller.marshall(deleteBucketCnameRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(deleteBucketCnameRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    public BucketInfo getBucketInfo(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_BUCKET_INFO, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketInfoResponseParser, bucketName, null, true);
    }

    public BucketStat getBucketStat(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_STAT, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketStatResponseParser, bucketName, null, true);
    }

    public void setBucketStorageCapacity(SetBucketStorageCapacityRequest setBucketStorageCapacityRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(setBucketStorageCapacityRequest, "setBucketStorageCapacityRequest");
        assertParameterNotNull(setBucketStorageCapacityRequest.getUserQos(), "setBucketStorageCapacityRequest.userQos");

        String bucketName = bucketNameCheck(setBucketStorageCapacityRequest);

        UserQos userQos = setBucketStorageCapacityRequest.getUserQos();
        assertParameterNotNull(userQos.getStorageCapacity(), "StorageCapacity");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_QOS, null);

        byte[] rawContent = setBucketQosRequestMarshaller.marshall(userQos);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(setBucketStorageCapacityRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);

    }

    public UserQos getBucketStorageCapacity(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_QOS, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketQosResponseParser, bucketName, null, true);

    }

    private static void populateListObjectsRequestParameters(ListObjectsRequest listObjectsRequest,
            Map<String, String> params) {

        if (listObjectsRequest.getPrefix() != null) {
            params.put(PREFIX, listObjectsRequest.getPrefix());
        }

        if (listObjectsRequest.getMarker() != null) {
            params.put(MARKER, listObjectsRequest.getMarker());
        }

        if (listObjectsRequest.getDelimiter() != null) {
            params.put(DELIMITER, listObjectsRequest.getDelimiter());
        }

        if (listObjectsRequest.getMaxKeys() != null) {
            params.put(MAX_KEYS, Integer.toString(listObjectsRequest.getMaxKeys()));
        }

        if (listObjectsRequest.getEncodingType() != null) {
            params.put(ENCODING_TYPE, listObjectsRequest.getEncodingType());
        }
    }

    private static void populateListObjectVersionsRequestParameters(ListObjectVersionsRequest listObjectVersionsRequest,
                                                             Map<String, String> params) {

        if (listObjectVersionsRequest.getPrefix() != null) {
            params.put(PREFIX, listObjectVersionsRequest.getPrefix());
        }

        if (listObjectVersionsRequest.getKeyMarker() != null) {
            params.put(KEY_MARKER, listObjectVersionsRequest.getKeyMarker());
        }

        if (listObjectVersionsRequest.getVersionIdMarker() != null) {
            params.put(VERSION_ID_MARKER, listObjectVersionsRequest.getVersionIdMarker());
        }

        if (listObjectVersionsRequest.getDelimiter() != null) {
            params.put(DELIMITER, listObjectVersionsRequest.getDelimiter());
        }

        if (listObjectVersionsRequest.getMaxKeys() != null) {
            params.put(MAX_KEYS, Integer.toString(listObjectVersionsRequest.getMaxKeys()));
        }

        if (listObjectVersionsRequest.getEncodingType() != null) {
            params.put(ENCODING_TYPE, listObjectVersionsRequest.getEncodingType());
        }
    }


    private static void addOptionalACLHeader(Map<String, String> headers, CannedAccessControlList cannedAcl) {
        if (cannedAcl != null) {
            headers.put(OSSHeaders.OSS_CANNED_ACL, cannedAcl.toString());
        }
    }

    public void putBucketRequestPayment(PutBucketRequestPaymentRequest requestPaymentRequest) throws OSSException, ClientException {
        assertParameterNotNull(requestPaymentRequest, "requestPaymentRequest");
        assertParameterNotNull(requestPaymentRequest.getPayer(), "requestPaymentRequestPayer");

        String bucketName = bucketNameCheck(requestPaymentRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_REQUEST_PAYMENT, null);

        byte[] rawContent = putBucketRequestPaymentMarshaller.marshall(requestPaymentRequest);

        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
            .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
            .setOriginalRequest(requestPaymentRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    public RequestPayer getBucketRequestPayment(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_REQUEST_PAYMENT, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
            .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketRequestPaymentResponseParser, bucketName, null, true);
    }

    public String initiateWormConfiguration(InitiateWormConfigurationRequest initiateWormConfigurationRequest) throws OSSException, ClientException {

        assertParameterNotNull(initiateWormConfigurationRequest, "initiateWormConfigurationRequest");

        String bucketName = bucketNameCheck(initiateWormConfigurationRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WORM, null);

        byte[] rawContent = initiateWormConfigurationRequestMarshaller.marshall(initiateWormConfigurationRequest);

        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
            .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
            .setOriginalRequest(initiateWormConfigurationRequest).build();

        return doOperation(request, initiateWormConfigurationResponseParser, bucketName, null, true);
    }

    public void abortBucketWorm(GenericRequest abortWormRequest) throws OSSException, ClientException {
        assertParameterNotNull(abortWormRequest, "abortWormRequest");

        String bucketName = bucketNameCheck(abortWormRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WORM, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
            .setOriginalRequest(abortWormRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null, true);
    }

    public void completeBucketWorm(CompleteWormConfigurationRequest completeWormConfigurationRequest) throws OSSException, ClientException {
        assertParameterNotNull(completeWormConfigurationRequest, "completeWormConfigurationRequest");

        String bucketName = bucketNameCheck(completeWormConfigurationRequest);

        String wormId = completeWormConfigurationRequest.getWormId();
        assertParameterNotNull(wormId, "wormId");

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WORM_ID, wormId);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params)
            .setInputStream(new ByteArrayInputStream(new byte[0])).setInputSize(0)
            .setOriginalRequest(completeWormConfigurationRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null, true);
    }

    public void extendBucketWorm(ExtendWormConfigurationRequest extendWormConfigurationRequest) throws OSSException, ClientException {

        assertParameterNotNull(extendWormConfigurationRequest, "extendWormConfigurationRequest");

        String bucketName = bucketNameCheck(extendWormConfigurationRequest);

        String wormId = extendWormConfigurationRequest.getWormId();
        assertParameterNotNull(wormId, "wormId");

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WORM_ID, wormId);
        params.put(SUBRESOURCE_WORM_EXTEND, null);

        byte[] rawContent = extendWormConfigurationRequestMarshaller.marshall(extendWormConfigurationRequest);

        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
            .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
            .setOriginalRequest(extendWormConfigurationRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null, true);
    }

    public WormConfiguration getBucketWorm(GenericRequest getBucketWormRequest) throws OSSException, ClientException {
        assertParameterNotNull(getBucketWormRequest, "getBucketWormRequest");

        String bucketName = bucketNameCheck(getBucketWormRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WORM, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
            .setOriginalRequest(getBucketWormRequest).build();

        return doOperation(request, getWormConfigurationResponseParser, bucketName, null, true);
    }

    public ListUserRegionsResult listUserRegions() throws OSSException, ClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_USER_REGION, null);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).build();

        return doOperation(request, listUserRegionResponseParser, null, null, true);
    }

    /**
     * Get bucket event notification
     */
    public NotificationConfiguration getBucketEventNotification(GenericRequest genericRequest)
        throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_NOTIFICATION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
            .setEndpoint(getEndpoint())
            .setMethod(HttpMethod.GET)
            .setBucket(bucketName)
            .setParameters(params)
            .setOriginalRequest(genericRequest)
            .build();

        return doOperation(request, getBucketNotificationResponseParser, bucketName, null, true);
    }

    /**
     * Get bucket versioning status
     */

    public String getBucketVersioning(GenericRequest genericRequest) throws OSSException, ClientException  {
        assertParameterNotNull(genericRequest, "genericRequest");
        String bucketName = genericRequest.getBucketName();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_VERSIONING, null);


        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();

        BucketVersion version = doOperation(request, getBucketVersioningResponseParser, bucketName, null, true);

        return version.getBucketVersion();
    }

    /**
     * Put bucket versioning
     */

    public void putBucketVersioning(PutBucketVersioningRequest putBucketVersioningRequest) throws OSSException, ClientException {
        assertParameterNotNull(putBucketVersioningRequest, "putBucketVersioningRequest");
        assertParameterNotNull(putBucketVersioningRequest.getBucketVersion(), "putBucketVersioningRequest");

        String bucketName = putBucketVersioningRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_VERSIONING, null);

        byte[] rawContent = putBucketVersioningMarshaller.marshall(putBucketVersioningRequest);

        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(putBucketVersioningRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * getBucketVersions || listObjectVersions
     * @param listObjectVersionsRequest
     * @return
     * @throws OSSException
     * @throws ClientException
     */
    public ObjectVersionsListing listObjectVersions(ListObjectVersionsRequest listObjectVersionsRequest) throws OSSException, ClientException {

        assertParameterNotNull(listObjectVersionsRequest, "listObjectVersionsRequest");

        String bucketName = listObjectVersionsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(SUBRESOURCE_VERSIONS, null);
        populateListObjectVersionsRequestParameters(listObjectVersionsRequest, params);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(listObjectVersionsRequest).build();

        return doOperation(request, listObjectVersionsReponseParser, bucketName, null, true);
    }
    public void putBucketVpcId(PutBucketVpcIdRequest putBucketVpcIdRequest) throws OSSException, ClientException {
        assertParameterNotNull(putBucketVpcIdRequest, "putBucketVpcIdRequest");

        String bucketName = bucketNameCheck(putBucketVpcIdRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_VPC_ID, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(putBucketVpcIdRequestMarshaller.marshall(putBucketVpcIdRequest))
                .setOriginalRequest(putBucketVpcIdRequest).build();

        doOperation(request, emptyResponseParser, bucketName, null);
    }
    public BucketVpcIdList listBucketVpcId(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = bucketNameCheck(genericRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_VPC_ID, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
            .setEndpoint(getEndpoint())
            .setMethod(HttpMethod.GET)
            .setBucket(bucketName)
            .setParameters(params)
            .setOriginalRequest(genericRequest)
            .build();

        return doOperation(request, listBucketVpcIdResponseParser, bucketName, null, true);
    }
    public void deleteBucketVpcId(DeleteBucketVpcIdRequest deleteBucketVpcIdRequest) throws OSSException, ClientException {
        assertParameterNotNull(deleteBucketVpcIdRequest, "deleteBucketVpcIdRequest");

        String bucketName = bucketNameCheck(deleteBucketVpcIdRequest);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_VPC_ID, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
            .setInputStreamWithLength(deleteBucketVpcIdRequestMarshaller.marshall(deleteBucketVpcIdRequest))
            .setOriginalRequest(deleteBucketVpcIdRequest).build();
        doOperation(request, emptyResponseParser, bucketName, null);
    }
}
