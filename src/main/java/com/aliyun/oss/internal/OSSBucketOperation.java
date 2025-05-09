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
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameCreationValid;
import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;
import static com.aliyun.oss.internal.RequestParameters.*;
import static com.aliyun.oss.internal.ResponseParsers.*;
import static com.aliyun.oss.internal.ResponseParsers.listAccessPointsResponseParser;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        String bucketName = createBucketRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameCreationValid(bucketName);

        Map<String, String> headers = new HashMap<String, String>();
        addOptionalACLHeader(headers, createBucketRequest.getCannedACL());
        addOptionalHnsHeader(headers, createBucketRequest.getHnsStatus());
        addOptionalResourceGroupIdHeader(headers, createBucketRequest.getResourceGroupId());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(createBucketRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setHeaders(headers)
                .setInputStreamWithLength(createBucketRequestMarshaller.marshall(createBucketRequest))
                .setOriginalRequest(createBucketRequest).build();

        ResponseMessage result = doOperation(request, emptyResponseParser, bucketName, null);
        return new Bucket(bucketName, result.getRequestId());
    }

    /**
     * Delete a bucket.
     */
    public VoidResult deleteBucket(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
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

        if (listBucketRequest.getTagKey() != null && listBucketRequest.getTagValue() != null) {
            params.put(TAG_KEY, listBucketRequest.getTagKey());
            params.put(TAG_VALUE, listBucketRequest.getTagValue());
        }

        if (listBucketRequest.getParameters() != null && listBucketRequest.getParameters().containsKey(SUBRESOURCE_REGION_LIST)) {
            params.put(SUBRESOURCE_REGION_LIST, listBucketRequest.getParameters().get(SUBRESOURCE_REGION_LIST));
        }

        Map<String, String> headers = new HashMap<String, String>();
        addOptionalResourceGroupIdHeader(headers, listBucketRequest.getResourceGroupId());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(listBucketRequest))
                .setMethod(HttpMethod.GET).setHeaders(headers).setParameters(params).setOriginalRequest(listBucketRequest).build();

        return doOperation(request, listBucketResponseParser, null, null, true);
    }

    /**
     * Set bucket's canned ACL.
     */
    public VoidResult setBucketAcl(SetBucketAclRequest setBucketAclRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketAclRequest, "setBucketAclRequest");

        String bucketName = setBucketAclRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> headers = new HashMap<String, String>();
        addOptionalACLHeader(headers, setBucketAclRequest.getCannedACL());

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketAclRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setHeaders(headers).setParameters(params)
                .setOriginalRequest(setBucketAclRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket's ACL.
     */
    public AccessControlList getBucketAcl(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketAclResponseParser, bucketName, null, true);
    }

    public BucketMetadata getBucketMetadata(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
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
    public VoidResult setBucketReferer(SetBucketRefererRequest setBucketRefererRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketRefererRequest, "setBucketRefererRequest");

        String bucketName = setBucketRefererRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        BucketReferer referer = setBucketRefererRequest.getReferer();
        if (referer == null) {
            referer = new BucketReferer();
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_REFERER, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketRefererRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(bucketRefererMarshaller.marshall(referer))
                .setOriginalRequest(setBucketRefererRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket referer.
     */
    public BucketReferer getBucketReferer(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_REFERER, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketRefererResponseParser, bucketName, null, true);
    }

    /**
     * Get bucket location.
     */
    public String getBucketLocation(GenericRequest genericRequest) {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOCATION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketLocationResponseParser, bucketName, null, true);
    }

    /**
     * Determine whether a bucket exists or not.
     */
    public boolean doesBucketExists(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        try {
            getBucketAcl(new GenericRequest(bucketName));
        } catch (OSSException oe) {
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

        String bucketName = listObjectsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new LinkedHashMap<String, String>();
        populateListObjectsRequestParameters(listObjectsRequest, params);

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, listObjectsRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(listObjectsRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setHeaders(headers).setParameters(params)
                .setOriginalRequest(listObjectsRequest).build();

        return doOperation(request, listObjectsReponseParser, bucketName, null, true);
    }

    /**
     * List objects under the specified bucket.
     */
    public ListObjectsV2Result listObjectsV2(ListObjectsV2Request listObjectsV2Request) throws OSSException, ClientException {

        assertParameterNotNull(listObjectsV2Request, "listObjectsRequest");

        String bucketName = listObjectsV2Request.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new LinkedHashMap<String, String>();
        populateListObjectsV2RequestParameters(listObjectsV2Request, params);

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, listObjectsV2Request.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(listObjectsV2Request))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setHeaders(headers).setParameters(params)
                .setOriginalRequest(listObjectsV2Request).build();

        return doOperation(request, listObjectsV2ResponseParser, bucketName, null, true);
    }

    /**
     * List versions under the specified bucket.
     */
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest) throws OSSException, ClientException {

        assertParameterNotNull(listVersionsRequest, "listVersionsRequest");

        String bucketName = listVersionsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new LinkedHashMap<String, String>();
        populateListVersionsRequestParameters(listVersionsRequest, params);

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, listVersionsRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(listVersionsRequest))
            .setMethod(HttpMethod.GET).setBucket(bucketName).setHeaders(headers).setParameters(params)
            .setOriginalRequest(listVersionsRequest).build();

        return doOperation(request, listVersionsReponseParser, bucketName, null, true);
    }

    /**
     * Set bucket logging.
     */
    public VoidResult setBucketLogging(SetBucketLoggingRequest setBucketLoggingRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketLoggingRequest, "setBucketLoggingRequest");

        String bucketName = setBucketLoggingRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketLoggingRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(setBucketLoggingRequestMarshaller.marshall(setBucketLoggingRequest))
                .setOriginalRequest(setBucketLoggingRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Put bucket image
     */
    public VoidResult putBucketImage(PutBucketImageRequest putBucketImageRequest) {
        assertParameterNotNull(putBucketImageRequest, "putBucketImageRequest");
        String bucketName = putBucketImageRequest.GetBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_IMG, null);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(putBucketImageRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(putBucketImageRequest)
                .setInputStreamWithLength(putBucketImageRequestMarshaller.marshall(putBucketImageRequest)).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket image
     */
    public GetBucketImageResult getBucketImage(String bucketName, GenericRequest genericRequest) {
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_IMG, null);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();
        return doOperation(request, getBucketImageResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket image attributes.
     */
    public VoidResult deleteBucketImage(String bucketName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_IMG, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * put image style
     */
    public VoidResult putImageStyle(PutImageStyleRequest putImageStyleRequest) throws OSSException, ClientException {
        assertParameterNotNull(putImageStyleRequest, "putImageStyleRequest");
        String bucketName = putImageStyleRequest.GetBucketName();
        String styleName = putImageStyleRequest.GetStyleName();
        assertParameterNotNull(styleName, "styleName");
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_STYLE, null);
        params.put(STYLE_NAME, styleName);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(putImageStyleRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(putImageStyleRequest)
                .setInputStreamWithLength(putImageStyleRequestMarshaller.marshall(putImageStyleRequest)).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public VoidResult deleteImageStyle(String bucketName, String styleName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(styleName, "styleName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_STYLE, null);
        params.put(STYLE_NAME, styleName);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public GetImageStyleResult getImageStyle(String bucketName, String styleName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(styleName, "styleName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_STYLE, null);
        params.put(STYLE_NAME, styleName);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
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
        params.put(SUBRESOURCE_STYLE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, listImageStyleResponseParser, bucketName, null, true);
    }

    public VoidResult setBucketProcess(SetBucketProcessRequest setBucketProcessRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketProcessRequest, "setBucketProcessRequest");

        ImageProcess imageProcessConf = setBucketProcessRequest.getImageProcess();
        assertParameterNotNull(imageProcessConf, "imageProcessConf");

        String bucketName = setBucketProcessRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_PROCESS_CONF, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketProcessRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(bucketImageProcessConfMarshaller.marshall(imageProcessConf))
                .setOriginalRequest(setBucketProcessRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public BucketProcess getBucketProcess(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_PROCESS_CONF, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketImageProcessConfResponseParser, bucketName, null, true);
    }

    /**
     * Get bucket logging.
     */
    public BucketLoggingResult getBucketLogging(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketLoggingResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket logging.
     */
    public VoidResult deleteBucketLogging(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Set bucket website.
     */
    public VoidResult setBucketWebsite(SetBucketWebsiteRequest setBucketWebSiteRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketWebSiteRequest, "setBucketWebSiteRequest");

        String bucketName = setBucketWebSiteRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        if (setBucketWebSiteRequest.getIndexDocument() == null && setBucketWebSiteRequest.getErrorDocument() == null
                && setBucketWebSiteRequest.getRoutingRules().size() == 0) {
            throw new IllegalArgumentException(String.format("IndexDocument/ErrorDocument/RoutingRules must have one"));
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketWebSiteRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(setBucketWebsiteRequestMarshaller.marshall(setBucketWebSiteRequest))
                .setOriginalRequest(setBucketWebSiteRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket website.
     */
    public BucketWebsiteResult getBucketWebsite(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketWebsiteResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket website.
     */
    public VoidResult deleteBucketWebsite(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Set bucket lifecycle.
     */
    public VoidResult setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(setBucketLifecycleRequest, "setBucketLifecycleRequest");

        String bucketName = setBucketLifecycleRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketLifecycleRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(setBucketLifecycleRequestMarshaller.marshall(setBucketLifecycleRequest))
                .setOriginalRequest(setBucketLifecycleRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket lifecycle.
     */
    public List<LifecycleRule> getBucketLifecycle(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketLifecycleResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket lifecycle.
     */
    public VoidResult deleteBucketLifecycle(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Set bucket tagging.
     */
    public VoidResult setBucketTagging(SetBucketTaggingRequest setBucketTaggingRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketTaggingRequest, "setBucketTaggingRequest");

        String bucketName = setBucketTaggingRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketTaggingRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(setBucketTaggingRequestMarshaller.marshall(setBucketTaggingRequest))
                .setOriginalRequest(setBucketTaggingRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket tagging.
     */
    public TagSet getBucketTagging(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getTaggingResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket tagging.
     */
    public VoidResult deleteBucketTagging(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket versioning.
     */
    public BucketVersioningConfiguration getBucketVersioning(GenericRequest genericRequest)
        throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_VRESIONING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
            .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
            .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketVersioningResponseParser, bucketName, null, true);
    }

    /**
     * Set bucket versioning.
     */
    public VoidResult setBucketVersioning(SetBucketVersioningRequest setBucketVersioningRequest)
        throws OSSException, ClientException {
        assertParameterNotNull(setBucketVersioningRequest, "setBucketVersioningRequest");
        assertParameterNotNull(setBucketVersioningRequest.getVersioningConfiguration(), "versioningConfiguration");

        String bucketName = setBucketVersioningRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_VRESIONING, null);

        byte[] rawContent = setBucketVersioningRequestMarshaller.marshall(setBucketVersioningRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketVersioningRequest))
            .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
            .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
            .setOriginalRequest(setBucketVersioningRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Add bucket replication.
     */
    public VoidResult addBucketReplication(AddBucketReplicationRequest addBucketReplicationRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(addBucketReplicationRequest, "addBucketReplicationRequest");
        assertParameterNotNull(addBucketReplicationRequest.getTargetBucketName(), "targetBucketName");

        String bucketName = addBucketReplicationRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_ADD);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(addBucketReplicationRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params)
                .setInputStreamWithLength(addBucketReplicationRequestMarshaller.marshall(addBucketReplicationRequest))
                .setOriginalRequest(addBucketReplicationRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket replication.
     */
    public List<ReplicationRule> getBucketReplication(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketReplicationResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket replication.
     */
    public VoidResult deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest)
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

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(deleteBucketReplicationRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(deleteBucketReplicationRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
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

        String bucketName = getBucketReplicationProgressRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION_PROGRESS, null);
        params.put(RequestParameters.RULE_ID, getBucketReplicationProgressRequest.getReplicationRuleID());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(getBucketReplicationProgressRequest))
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

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION_LOCATION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketReplicationLocationResponseParser, bucketName, null, true);
    }

    public AddBucketCnameResult addBucketCname(AddBucketCnameRequest addBucketCnameRequest) throws OSSException, ClientException {

        assertParameterNotNull(addBucketCnameRequest, "addBucketCnameRequest");
        assertParameterNotNull(addBucketCnameRequest.getDomain(), "addBucketCnameRequest.domain");

        String bucketName = addBucketCnameRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_CNAME, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_ADD);

        byte[] rawContent = addBucketCnameRequestMarshaller.marshall(addBucketCnameRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(addBucketCnameRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(addBucketCnameRequest).build();

        return doOperation(request, addBucketCnameResponseParser, bucketName, null);
    }

    public List<CnameConfiguration> getBucketCname(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_CNAME, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketCnameResponseParser, bucketName, null, true);
    }

    public VoidResult deleteBucketCname(DeleteBucketCnameRequest deleteBucketCnameRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(deleteBucketCnameRequest, "deleteBucketCnameRequest");
        assertParameterNotNull(deleteBucketCnameRequest.getDomain(), "deleteBucketCnameRequest.domain");

        String bucketName = deleteBucketCnameRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_CNAME, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_DELETE);

        byte[] rawContent = deleteBucketCnameRequestMarshaller.marshall(deleteBucketCnameRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(deleteBucketCnameRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(deleteBucketCnameRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public CreateBucketCnameTokenResult createBucketCnameToken(CreateBucketCnameTokenRequest createBucketCnameTokenRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(createBucketCnameTokenRequest, "createBucketCnameTokenRequest");
        assertParameterNotNull(createBucketCnameTokenRequest.getDomain(), "createBucketCnameTokenRequest.domain");

        String bucketName = createBucketCnameTokenRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_CNAME, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_TOKEN);

        byte[] rawContent = createBucketCnameTokenRequestMarshaller.marshall(createBucketCnameTokenRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(createBucketCnameTokenRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(createBucketCnameTokenRequest).build();

        return doOperation(request, createBucketCnameTokenResponseParser, bucketName, null, true);
    }

    public GetBucketCnameTokenResult getBucketCnameToken(GetBucketCnameTokenRequest getBucketCnameTokenRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(getBucketCnameTokenRequest, "getBucketCnameTokenRequest");
        assertParameterNotNull(getBucketCnameTokenRequest.getDomain(), "getBucketCnameTokenRequest.domain");

        String bucketName = getBucketCnameTokenRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(getBucketCnameTokenRequest.getDomain(), "bucketName");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_CNAME, getBucketCnameTokenRequest.getDomain());
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_TOKEN);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(getBucketCnameTokenRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(getBucketCnameTokenRequest).build();

        return doOperation(request, getBucketCnameTokenResponseParser, bucketName, null, true);
    }

    public BucketInfo getBucketInfo(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_BUCKET_INFO, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketInfoResponseParser, bucketName, null, true);
    }

    public BucketStat getBucketStat(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_STAT, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketStatResponseParser, bucketName, null, true);
    }

    public VoidResult setBucketStorageCapacity(SetBucketStorageCapacityRequest setBucketStorageCapacityRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(setBucketStorageCapacityRequest, "setBucketStorageCapacityRequest");
        assertParameterNotNull(setBucketStorageCapacityRequest.getUserQos(), "setBucketStorageCapacityRequest.userQos");

        String bucketName = setBucketStorageCapacityRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        UserQos userQos = setBucketStorageCapacityRequest.getUserQos();
        assertParameterNotNull(userQos.getStorageCapacity(), "StorageCapacity");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_QOS, null);

        byte[] rawContent = setBucketQosRequestMarshaller.marshall(userQos);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketStorageCapacityRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(setBucketStorageCapacityRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public UserQos getBucketStorageCapacity(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_QOS, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketQosResponseParser, bucketName, null, true);

    }
    
    /**
     * Set bucket encryption.
     */
    public VoidResult setBucketEncryption(SetBucketEncryptionRequest setBucketEncryptionRequest)
        throws OSSException, ClientException {

        assertParameterNotNull(setBucketEncryptionRequest, "setBucketEncryptionRequest");

        String bucketName = setBucketEncryptionRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ENCRYPTION, null);

        byte[] rawContent = setBucketEncryptionRequestMarshaller.marshall(setBucketEncryptionRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketEncryptionRequest))
            .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
            .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
            .setOriginalRequest(setBucketEncryptionRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * get bucket encryption.
     */
    public ServerSideEncryptionConfiguration getBucketEncryption(GenericRequest genericRequest)
        throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ENCRYPTION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
            .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
            .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketEncryptionResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket encryption.
     */
    public VoidResult deleteBucketEncryption(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ENCRYPTION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
            .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
            .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public VoidResult setBucketPolicy(SetBucketPolicyRequest setBucketPolicyRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketPolicyRequest, "setBucketPolicyRequest");

        String bucketName = setBucketPolicyRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
     	Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_POLICY, null);

        byte[] rawContent = setBucketPolicyRequestMarshaller.marshall(setBucketPolicyRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketPolicyRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(setBucketPolicyRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public GetBucketPolicyResult getBucketPolicy(GenericRequest genericRequest) throws OSSException, ClientException {
    	assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_POLICY, null);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();
        return doOperation(request, getBucketPolicyResponseParser, bucketName, null, true);
    }
    
    public VoidResult deleteBucketPolicy(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_POLICY, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public VoidResult setBucketRequestPayment(SetBucketRequestPaymentRequest setBucketRequestPaymentRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(setBucketRequestPaymentRequest, "setBucketRequestPaymentRequest");
        assertParameterNotNull(setBucketRequestPaymentRequest.getPayer(), "setBucketRequestPaymentRequest.payer");

        String bucketName = setBucketRequestPaymentRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REQUEST_PAYMENT, null);

        Payer payer = setBucketRequestPaymentRequest.getPayer();
        byte[] rawContent = setBucketRequestPaymentRequestMarshaller.marshall(payer.toString());
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketRequestPaymentRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(setBucketRequestPaymentRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public GetBucketRequestPaymentResult getBucketRequestPayment(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REQUEST_PAYMENT, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketRequestPaymentResponseParser, bucketName, null, true);
    }

    public VoidResult setBucketQosInfo(SetBucketQosInfoRequest setBucketQosInfoRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketQosInfoRequest, "setBucketQosInfoRequest");
        assertParameterNotNull(setBucketQosInfoRequest.getBucketQosInfo(), "setBucketQosInfoRequest.getBucketQosInfo");

        String bucketName = setBucketQosInfoRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_QOS_INFO, null);

        byte[] rawContent = setBucketQosInfoRequestMarshaller.marshall(setBucketQosInfoRequest.getBucketQosInfo());
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketQosInfoRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(setBucketQosInfoRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public BucketQosInfo getBucketQosInfo(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_QOS_INFO, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketQosInfoResponseParser, bucketName, null, true);
    }

    public VoidResult deleteBucketQosInfo(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_QOS_INFO, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public UserQosInfo getUserQosInfo() throws OSSException, ClientException {

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_QOS_INFO, null);

        GenericRequest gGenericRequest = new GenericRequest();

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).setOriginalRequest(gGenericRequest).build();

        return doOperation(request, getUSerQosInfoResponseParser, null, null, true);
    }

    public SetAsyncFetchTaskResult setAsyncFetchTask(SetAsyncFetchTaskRequest setAsyncFetchTaskRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(setAsyncFetchTaskRequest, "setAsyncFetchTaskRequest");

        String bucketName = setAsyncFetchTaskRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        AsyncFetchTaskConfiguration taskConfiguration = setAsyncFetchTaskRequest.getAsyncFetchTaskConfiguration();
        assertParameterNotNull(taskConfiguration, "taskConfiguration");

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ASYNC_FETCH, null);

        byte[] rawContent = setAsyncFetchTaskRequestMarshaller.marshall(setAsyncFetchTaskRequest.getAsyncFetchTaskConfiguration());
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setAsyncFetchTaskRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(setAsyncFetchTaskRequest).build();

        return doOperation(request, setAsyncFetchTaskResponseParser, bucketName, null, true);
    }

    public GetAsyncFetchTaskResult getAsyncFetchTask(GetAsyncFetchTaskRequest getAsyncFetchTaskRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(getAsyncFetchTaskRequest, "getAsyncFetchTaskInfoRequest");

        String bucketName = getAsyncFetchTaskRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        String taskId = getAsyncFetchTaskRequest.getTaskId();
        assertParameterNotNull(taskId, "taskId");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_ASYNC_FETCH, null);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.OSS_HEADER_TASK_ID, taskId);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(getAsyncFetchTaskRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setHeaders(headers).setParameters(params)
                .setOriginalRequest(getAsyncFetchTaskRequest).build();

        return doOperation(request, getAsyncFetchTaskResponseParser, bucketName, null, true);
    }

    public CreateVpcipResult createVpcip(CreateVpcipRequest createVpcipRequest) throws OSSException, ClientException{

        assertParameterNotNull(createVpcipRequest, "createVpcipRequest");
        String region = createVpcipRequest.getRegion();
        String vSwitchId = createVpcipRequest.getVSwitchId();

        assertParameterNotNull(region, "region");
        assertParameterNotNull(vSwitchId, "vSwitchId");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.VPCIP, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(createVpcipRequest)).setParameters(params)
                .setMethod(HttpMethod.POST).setInputStreamWithLength(createVpcipRequestMarshaller.marshall(createVpcipRequest))
                .setOriginalRequest(createVpcipRequest).build();

        CreateVpcipResult createVpcipResult = new CreateVpcipResult();
        Vpcip vpcip = doOperation(request, createVpcipResultResponseParser, null, null, true);
        createVpcipResult.setVpcip(vpcip);
        return createVpcipResult;
    }

    public List<Vpcip> listVpcip() {

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.VPCIP, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).build();

        return doOperation(request, listVpcipResultResponseParser, null, null,true);
    }

    public VoidResult deleteVpcip(DeleteVpcipRequest deleteVpcipRequest) {

        assertParameterNotNull(deleteVpcipRequest, "deleteVpcipRequest");
        VpcPolicy vpcPolicy = deleteVpcipRequest.getVpcPolicy();
        String region = vpcPolicy.getRegion();
        String vpcId = vpcPolicy.getVpcId();
        String vip = vpcPolicy.getVip();

        assertParameterNotNull(region, "region");
        assertParameterNotNull(vpcId, "vpcId");
        assertParameterNotNull(vip, "vip");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.VPCIP, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(deleteVpcipRequest))
                .setParameters(params).setMethod(HttpMethod.DELETE)
                .setInputStreamWithLength(deleteVpcipRequestMarshaller.marshall(deleteVpcipRequest))
                .setOriginalRequest(deleteVpcipRequest).build();

        return doOperation(request, requestIdResponseParser, null, null);
    }

    public VoidResult createBucketVpcip(CreateBucketVpcipRequest createBucketVpcipRequest) {

        String bucketName = createBucketVpcipRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(createBucketVpcipRequest, "createBucketVpcipRequest");
        VpcPolicy vpcPolicy = createBucketVpcipRequest.getVpcPolicy();
        String region = vpcPolicy.getRegion();
        String vpcId = vpcPolicy.getVpcId();
        String vip = vpcPolicy.getVip();

        assertParameterNotNull(region, "region");
        assertParameterNotNull(vpcId, "vpcId");
        assertParameterNotNull(vip, "vip");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.VIP, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_ADD);


        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(createBucketVpcipRequest))
                .setMethod(HttpMethod.POST).setParameters(params).setBucket(bucketName)
                .setInputStreamWithLength(createBucketVpcipRequestMarshaller.marshall(createBucketVpcipRequest))
                .setOriginalRequest(createBucketVpcipRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public VoidResult deleteBucketVpcip(DeleteBucketVpcipRequest deleteBucketVpcipRequest) {

        String bucketName = deleteBucketVpcipRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(deleteBucketVpcipRequest, "deleteBucketVpcipRequest");
        VpcPolicy vpcPolicy = deleteBucketVpcipRequest.getVpcPolicy();
        String region = vpcPolicy.getRegion();
        String vpcId = vpcPolicy.getVpcId();
        String vip = vpcPolicy.getVip();

        assertParameterNotNull(region, "region");
        assertParameterNotNull(vpcId, "vpcId");
        assertParameterNotNull(vip, "vip");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.VIP, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_DELETE);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(deleteBucketVpcipRequest))
                .setMethod(HttpMethod.POST).setParameters(params).setBucket(bucketName)
                .setInputStreamWithLength(deleteBucketVpcipRequestMarshaller.marshall(vpcPolicy))
                .setOriginalRequest(vpcPolicy).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public List<VpcPolicy> getBucketVpcip(GenericRequest genericRequest) {

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.VIP, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params).build();

        return doOperation(request, listVpcPolicyResultResponseParser, bucketName, null,true);
    }

    public VoidResult setBucketInventoryConfiguration(SetBucketInventoryConfigurationRequest
            setBucketInventoryConfigurationRequest) throws OSSException, ClientException {
        assertParameterNotNull(setBucketInventoryConfigurationRequest, "SetBucketInventoryConfigurationRequest");
        String bucketName = setBucketInventoryConfigurationRequest.getBucketName();
        String inventoryId = setBucketInventoryConfigurationRequest.getInventoryConfiguration().getInventoryId();
        assertParameterNotNull(inventoryId, "inventory configuration id");
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        byte[] rawContent = setBucketInventoryRequestMarshaller.marshall(
                setBucketInventoryConfigurationRequest.getInventoryConfiguration());
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_INVENTORY, null);
        params.put(SUBRESOURCE_INVENTORY_ID, inventoryId);

        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketInventoryConfigurationRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setOriginalRequest(setBucketInventoryConfigurationRequest)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public GetBucketInventoryConfigurationResult getBucketInventoryConfiguration(GetBucketInventoryConfigurationRequest
            getBucketInventoryConfigurationRequest) throws OSSException, ClientException {
        assertParameterNotNull(getBucketInventoryConfigurationRequest, "getBucketInventoryConfigurationRequest");
        String bucketName = getBucketInventoryConfigurationRequest.getBucketName();
        String inventoryId = getBucketInventoryConfigurationRequest.getInventoryId();
        assertParameterNotNull(inventoryId, "inventory configuration id");
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_INVENTORY, null);
        params.put(SUBRESOURCE_INVENTORY_ID, inventoryId);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(getBucketInventoryConfigurationRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(getBucketInventoryConfigurationRequest)
                .build();

        return doOperation(request, getBucketInventoryConfigurationParser, bucketName, null, true);
    }

    public ListBucketInventoryConfigurationsResult listBucketInventoryConfigurations(ListBucketInventoryConfigurationsRequest
            listBucketInventoryConfigurationsRequest) throws OSSException, ClientException {
        assertParameterNotNull(listBucketInventoryConfigurationsRequest, "listBucketInventoryConfigurationsRequest");
        String bucketName = listBucketInventoryConfigurationsRequest.getBucketName();
        String continuationToken = listBucketInventoryConfigurationsRequest.getContinuationToken();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_INVENTORY, null);
        if (continuationToken != null && !continuationToken.isEmpty()) {
            params.put(SUBRESOURCE_CONTINUATION_TOKEN, continuationToken);
        }

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(listBucketInventoryConfigurationsRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(listBucketInventoryConfigurationsRequest)
                .build();

        return doOperation(request, listBucketInventoryConfigurationsParser, bucketName, null, true);
    }

    public VoidResult deleteBucketInventoryConfiguration(DeleteBucketInventoryConfigurationRequest
            deleteBucketInventoryConfigurationRequest) throws OSSException, ClientException {
        assertParameterNotNull(deleteBucketInventoryConfigurationRequest, "deleteBucketInventoryConfigurationRequest");
        String bucketName = deleteBucketInventoryConfigurationRequest.getBucketName();
        String inventoryId = deleteBucketInventoryConfigurationRequest.getInventoryId();
        assertParameterNotNull(inventoryId, "id");
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_INVENTORY, null);
        params.put(SUBRESOURCE_INVENTORY_ID, inventoryId);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(deleteBucketInventoryConfigurationRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(deleteBucketInventoryConfigurationRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public InitiateBucketWormResult initiateBucketWorm(InitiateBucketWormRequest initiateBucketWormRequest) throws OSSException, ClientException {
        assertParameterNotNull(initiateBucketWormRequest, "initiateBucketWormRequest");
        String bucketName = initiateBucketWormRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WORM, null);

        byte[] rawContent = initiateBucketWormRequestMarshaller.marshall(initiateBucketWormRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(initiateBucketWormRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setOriginalRequest(initiateBucketWormRequest)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .build();


        return doOperation(request, initiateBucketWormResponseParser, bucketName, null);
    }

    public VoidResult abortBucketWorm(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WORM, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public VoidResult completeBucketWorm(CompleteBucketWormRequest completeBucketWormRequest) throws OSSException, ClientException {
        assertParameterNotNull(completeBucketWormRequest, "completeBucketWormRequest");
        String bucketName = completeBucketWormRequest.getBucketName();
        String wormId = completeBucketWormRequest.getWormId();
        assertParameterNotNull(wormId, "wormId");
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WORM_ID, wormId);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(completeBucketWormRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(completeBucketWormRequest)
                .setInputSize(0).setInputStream(new ByteArrayInputStream(new byte[0]))
                .build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public VoidResult extendBucketWorm(ExtendBucketWormRequest extendBucketWormRequest) throws OSSException, ClientException {
        assertParameterNotNull(extendBucketWormRequest, "extendBucketWormRequest");
        String bucketName = extendBucketWormRequest.getBucketName();
        String wormId = extendBucketWormRequest.getWormId();
        assertParameterNotNull(wormId, "wormId");
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WORM_ID, wormId);
        params.put(SUBRESOURCE_WORM_EXTEND, null);

        byte[] rawContent = extendBucketWormRequestMarshaller.marshall(extendBucketWormRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(extendBucketWormRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setOriginalRequest(extendBucketWormRequest)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public GetBucketWormResult getBucketWorm(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WORM, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();

        return doOperation(request, getBucketWormResponseParser, bucketName, null, true);
    }

    public VoidResult setBucketResourceGroup(SetBucketResourceGroupRequest setBucketResourceGroupRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(setBucketResourceGroupRequest, "setBucketResourceGroupRequest");
        assertParameterNotNull(setBucketResourceGroupRequest.getResourceGroupId(), "setBucketResourceGroupRequest.resourceGroupId");

        String bucketName = setBucketResourceGroupRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_RESOURCE_GROUP, null);

        byte[] rawContent = setBucketResourceGroupRequestMarshaller.marshall(setBucketResourceGroupRequest.getResourceGroupId());
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketResourceGroupRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(setBucketResourceGroupRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public GetBucketResourceGroupResult getBucketResourceGroup(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_RESOURCE_GROUP, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketResourceGroupResponseParser, bucketName, null, true);
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

    private static void populateListObjectsV2RequestParameters(ListObjectsV2Request listObjectsV2Request,
            Map<String, String> params) {

        params.put(LIST_TYPE, "2");

        if (listObjectsV2Request.getPrefix() != null) {
            params.put(PREFIX, listObjectsV2Request.getPrefix());
        }

        if (listObjectsV2Request.getDelimiter() != null) {
            params.put(DELIMITER, listObjectsV2Request.getDelimiter());
        }

        if (listObjectsV2Request.getMaxKeys() != null) {
            params.put(MAX_KEYS, Integer.toString(listObjectsV2Request.getMaxKeys()));
        }

        if (listObjectsV2Request.getEncodingType() != null) {
            params.put(ENCODING_TYPE, listObjectsV2Request.getEncodingType());
        }

        if (listObjectsV2Request.getStartAfter() != null) {
            params.put(START_AFTER, listObjectsV2Request.getStartAfter());
        }

        if (listObjectsV2Request.isFetchOwner()) {
            params.put(FETCH_OWNER, Boolean.toString(listObjectsV2Request.isFetchOwner()));
        }

        if (listObjectsV2Request.getContinuationToken() != null) {
            params.put(SUBRESOURCE_CONTINUATION_TOKEN, listObjectsV2Request.getContinuationToken());
        }

    }


    private static void populateListVersionsRequestParameters(ListVersionsRequest listVersionsRequest,
        Map<String, String> params) {

        params.put(SUBRESOURCE_VRESIONS, null);

        if (listVersionsRequest.getPrefix() != null) {
            params.put(PREFIX, listVersionsRequest.getPrefix());
        }

        if (listVersionsRequest.getKeyMarker() != null) {
            params.put(KEY_MARKER, listVersionsRequest.getKeyMarker());
        }

        if (listVersionsRequest.getDelimiter() != null) {
            params.put(DELIMITER, listVersionsRequest.getDelimiter());
        }

        if (listVersionsRequest.getMaxResults() != null) {
            params.put(MAX_KEYS, Integer.toString(listVersionsRequest.getMaxResults()));
        }

        if (listVersionsRequest.getVersionIdMarker() != null) {
            params.put(VERSION_ID_MARKER, listVersionsRequest.getVersionIdMarker());
        }

        if (listVersionsRequest.getEncodingType() != null) {
            params.put(ENCODING_TYPE, listVersionsRequest.getEncodingType());
        }
    }

    private static void addOptionalACLHeader(Map<String, String> headers, CannedAccessControlList cannedAcl) {
        if (cannedAcl != null) {
            headers.put(OSSHeaders.OSS_CANNED_ACL, cannedAcl.toString());
        }
    }

    public VoidResult setBucketTransferAcceleration(SetBucketTransferAccelerationRequest setBucketTransferAccelerationRequest) throws OSSException, ClientException {
        assertParameterNotNull(setBucketTransferAccelerationRequest, "putBucketTransferAccelerationRequest");

        String bucketName = setBucketTransferAccelerationRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TRANSFER_ACCELERATION, null);

        byte[] rawContent = putBucketTransferAccelerationRequestMarshaller.marshall(setBucketTransferAccelerationRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketTransferAccelerationRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(setBucketTransferAccelerationRequest).setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent)).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public TransferAcceleration getBucketTransferAcceleration(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TRANSFER_ACCELERATION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketTransferAccelerationResponseParser, bucketName, null, true);
    }

    public VoidResult deleteBucketTransferAcceleration(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TRANSFER_ACCELERATION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    private static void addOptionalHnsHeader(Map<String, String> headers, String hnsStatus) {
        if (hnsStatus != null ) {
            headers.put(OSSHeaders.OSS_HNS_STATUS, hnsStatus.toLowerCase());
        }
    }
    private static void addOptionalResourceGroupIdHeader(Map<String, String> headers, String resourceGroupId) {
        if (resourceGroupId != null) {
            headers.put(OSSHeaders.OSS_RESOURCE_GROUP_ID, resourceGroupId);
        }
    }

    private static void populateRequestPayerHeader (Map<String, String> headers, Payer payer) {
        if (payer != null && payer.equals(Payer.Requester)) {
            headers.put(OSSHeaders.OSS_REQUEST_PAYER, payer.toString().toLowerCase());
        }
    }

    public VoidResult putBucketAccessMonitor(PutBucketAccessMonitorRequest putBucketAccessMonitorRequest) throws OSSException, ClientException {
        assertParameterNotNull(putBucketAccessMonitorRequest, "putBucketAccessMonitorRequest");

        String bucketName = putBucketAccessMonitorRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(ACCESS_MONITOR, null);

        byte[] rawContent = putBucketAccessMonitorRequestMarshaller.marshall(putBucketAccessMonitorRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(putBucketAccessMonitorRequest).setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent)).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public AccessMonitor getBucketAccessMonitor(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(ACCESS_MONITOR, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketAccessMonitorResponseParser, bucketName, null, true);
    }

    public VoidResult openMetaQuery(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(META_QUERY, null);
        params.put(SUBRESOURCE_COMP, COMP_ADD);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).setInputSize(0).setInputStream(new ByteArrayInputStream(new byte[0])).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public VoidResult openMetaQuery(OpenMetaQueryRequest openMetaQueryRequest) throws OSSException, ClientException {
        assertParameterNotNull(openMetaQueryRequest, "openMetaQueryRequest");

        String bucketName = openMetaQueryRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        MetaQueryMode metaQueryMode = openMetaQueryRequest.getMetaQueryMode();
        assertParameterNotNull(metaQueryMode, "metaQueryMode");

        Map<String, String> params = new HashMap<String, String>();
        params.put(META_QUERY, null);
        params.put(SUBRESOURCE_COMP, COMP_ADD);
        params.put(MODE, metaQueryMode.toString());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(openMetaQueryRequest).setInputSize(0).setInputStream(new ByteArrayInputStream(new byte[0])).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public GetMetaQueryStatusResult getMetaQueryStatus(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(META_QUERY, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getMetaQueryStatusResponseParser, bucketName, null, true);
    }

    public DoMetaQueryResult doMetaQuery(DoMetaQueryRequest doMetaQueryRequest) throws OSSException, ClientException {
        assertParameterNotNull(doMetaQueryRequest, "doMetaQueryRequest");

        String bucketName = doMetaQueryRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(META_QUERY, null);
        params.put(SUBRESOURCE_COMP, COMP_QUERY);

        if (null != doMetaQueryRequest.getEncodingType()) {
            params.put(ENCODING_TYPE, doMetaQueryRequest.getEncodingType());
        }
        if (null != doMetaQueryRequest.getMetaQueryMode()) {
            params.put(MODE, doMetaQueryRequest.getMetaQueryMode().toString());
        }

        byte[] rawContent = doMetaQueryRequestMarshaller.marshall(doMetaQueryRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(doMetaQueryRequest).setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent)).build();

        return doOperation(request, doMetaQueryResponseParser, bucketName, null, true);
    }

    public VoidResult closeMetaQuery(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(META_QUERY, null);
        params.put(SUBRESOURCE_COMP, COMP_DELETE);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).setInputSize(0).setInputStream(new ByteArrayInputStream(new byte[0])).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public DescribeRegionsResult describeRegions(DescribeRegionsRequest describeRegionsRequest) throws OSSException, ClientException {
        assertParameterNotNull(describeRegionsRequest, "describeRegionsRequest");

        String bucketName = describeRegionsRequest.getBucketName();
        String region = describeRegionsRequest.getRegion();

        Map<String, String> params = new HashMap<String, String>();
        params.put(REGIONS, region);


        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(describeRegionsRequest).build();

        return doOperation(request, describeRegionsResponseParser, bucketName, null, true);
    }

    public VoidResult setBucketCallbackPolicy(SetBucketCallbackPolicyRequest setBucketCallbackPolicyRequest) throws OSSException, ClientException {

        assertParameterNotNull(setBucketCallbackPolicyRequest, "setBucketCallbackPolicyRequest");

        String bucketName = setBucketCallbackPolicyRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_POLICY, null);
        params.put(SUBRESOURCE_COMP, SUBRESOURCE_CALLBACK);

        byte[] rawContent = setBucketCallbackPolicyRequestMarshaller.marshall(setBucketCallbackPolicyRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketCallbackPolicyRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(setBucketCallbackPolicyRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public GetBucketCallbackPolicyResult getBucketCallbackPolicy(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_POLICY, null);
        params.put(SUBRESOURCE_COMP, SUBRESOURCE_CALLBACK);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();
        return doOperation(request, getBucketCallbackPolicyResponseParser, bucketName, null, true);
    }

    public VoidResult deleteBucketCallbackPolicy(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_POLICY, null);
        params.put(SUBRESOURCE_COMP, SUBRESOURCE_CALLBACK);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public VoidResult putBucketArchiveDirectRead(PutBucketArchiveDirectReadRequest putBucketArchiveDirectReadRequest) throws OSSException, ClientException {
        assertParameterNotNull(putBucketArchiveDirectReadRequest, "putBucketArchiveDirectReadRequest");
        String bucketName = putBucketArchiveDirectReadRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<String, String>();
        params.put(ARCHIVE_DIRECT_READ, null);
        byte[] rawContent = putBucketArchiveDirectReadRequestMarshaller.marshall(putBucketArchiveDirectReadRequest);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(putBucketArchiveDirectReadRequest).setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent)).build();
        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public GetBucketArchiveDirectReadResult getBucketArchiveDirectRead(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<String, String>();
        params.put(ARCHIVE_DIRECT_READ, null);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();
        return doOperation(request, getBucketArchiveDirectReadResponseParser, bucketName, null, true);
    }

    public VoidResult putBucketHttpsConfig(PutBucketHttpsConfigRequest putBucketHttpsConfigRequest) throws OSSException, ClientException {

        assertParameterNotNull(putBucketHttpsConfigRequest, "putBucketHttpsConfigRequest");

        String bucketName = putBucketHttpsConfigRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<String, String>();
        params.put(HTTPS_CONFIG, null);

        byte[] rawContent = putBucketHttpsConfigRequestMarshaller.marshall(putBucketHttpsConfigRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addRequestRequiredHeaders(headers, rawContent);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(putBucketHttpsConfigRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(putBucketHttpsConfigRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public GetBucketHttpsConfigResult getBucketHttpsConfig(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<String, String>();
        params.put(HTTPS_CONFIG, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();
        return doOperation(request, getBucketHttpsConfigResponseParser, bucketName, null, true);
    }

    public VoidResult putPublicAccessBlock(PutPublicAccessBlockRequest putPublicAccessBlockRequest) throws OSSException, ClientException {
        assertParameterNotNull(putPublicAccessBlockRequest, "putPublicAccessBlockRequest");

        Map<String, String> params = new HashMap<String, String>();
        params.put(PUBLIC_ACCESS_BLOCK, null);

        byte[] rawContent = putPublicAccessBlockRequestMarshaller.marshall(putPublicAccessBlockRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(putPublicAccessBlockRequest))
                .setMethod(HttpMethod.PUT).setParameters(params)
                .setOriginalRequest(putPublicAccessBlockRequest).setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent)).build();

        return doOperation(request, requestIdResponseParser, null, null, true);
    }

    public GetPublicAccessBlockResult getPublicAccessBlock(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        Map<String, String> params = new HashMap<String, String>();
        params.put(PUBLIC_ACCESS_BLOCK, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getPublicAccessBlockResponseParser, null, null, true);
    }

    public VoidResult deletePublicAccessBlock(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        Map<String, String> params = new HashMap<String, String>();
        params.put(PUBLIC_ACCESS_BLOCK, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, null, null);
    }

    public VoidResult putBucketPublicAccessBlock(PutBucketPublicAccessBlockRequest putBucketPublicAccessBlockRequest) throws OSSException, ClientException {
        assertParameterNotNull(putBucketPublicAccessBlockRequest, "putBucketPublicAccessBlockRequest");

        String bucketName = putBucketPublicAccessBlockRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(PUBLIC_ACCESS_BLOCK, null);

        byte[] rawContent = putBucketPublicAccessBlockRequestMarshaller.marshall(putBucketPublicAccessBlockRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(putBucketPublicAccessBlockRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(putBucketPublicAccessBlockRequest).setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent)).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public GetBucketPublicAccessBlockResult getBucketPublicAccessBlock(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(PUBLIC_ACCESS_BLOCK, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketPublicAccessBlockResponseParser, bucketName, null, true);
    }

    public VoidResult deleteBucketPublicAccessBlock(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(PUBLIC_ACCESS_BLOCK, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public GetBucketPolicyStatusResult getBucketPolicyStatus(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(POLICY_STATUS, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketPolicyStatusResponseParser, bucketName, null, true);
    }

    public CreateBucketDataRedundancyTransitionResult createBucketDataRedundancyTransition(CreateBucketDataRedundancyTransitionRequest createBucketDataRedundancyTransitionRequest) throws OSSException, ClientException {
        assertParameterNotNull(createBucketDataRedundancyTransitionRequest, "createBucketDataRedundancyTransitionRequest");
        assertParameterNotNull(createBucketDataRedundancyTransitionRequest.getTargetType(), "targetType");

        String bucketName = createBucketDataRedundancyTransitionRequest.getBucketName();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.REDUNDANCY_TRANSITION, null);
        if (null != createBucketDataRedundancyTransitionRequest.getTargetType()) {
            params.put(X_OSS_TARGET_REDUNDANCY_TYPE, createBucketDataRedundancyTransitionRequest.getTargetType().toString());
        }

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(createBucketDataRedundancyTransitionRequest)
                .setInputSize(0).setInputStream(new ByteArrayInputStream(new byte[0]))
                .build();

        return doOperation(request, createBucketDataRedundancyTransitionResponseParser, bucketName, null, true);
    }

    public GetBucketDataRedundancyTransitionResult getBucketDataRedundancyTransition(GetBucketDataRedundancyTransitionRequest getBucketDataRedundancyTransitionRequest) throws OSSException, ClientException {
        assertParameterNotNull(getBucketDataRedundancyTransitionRequest, "getBucketDataRedundancyTransitionRequest");
        assertParameterNotNull(getBucketDataRedundancyTransitionRequest.getTaskId(), "taskId");

        String bucketName = getBucketDataRedundancyTransitionRequest.getBucketName();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.REDUNDANCY_TRANSITION, null);
        if (null != getBucketDataRedundancyTransitionRequest.getTaskId()) {
            params.put(X_OSS_REDUNDANCY_TRANSITION_TASK_ID, getBucketDataRedundancyTransitionRequest.getTaskId());
        }

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(getBucketDataRedundancyTransitionRequest).build();

        return doOperation(request, getBucketDataRedundancyTransitionResponseParser, bucketName, null, true);
    }

    public VoidResult deleteBucketDataRedundancyTransition(DeleteBucketDataRedundancyTransitionRequest deleteBucketDataRedundancyTransitionRequest) throws OSSException, ClientException {
        assertParameterNotNull(deleteBucketDataRedundancyTransitionRequest, "deleteBucketDataRedundancyTransitionRequest");
        assertParameterNotNull(deleteBucketDataRedundancyTransitionRequest.getTaskId(), "taskId");

        String bucketName = deleteBucketDataRedundancyTransitionRequest.getBucketName();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.REDUNDANCY_TRANSITION, null);
        if (null != deleteBucketDataRedundancyTransitionRequest.getTaskId()) {
            params.put(X_OSS_REDUNDANCY_TRANSITION_TASK_ID, deleteBucketDataRedundancyTransitionRequest.getTaskId());
        }

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(deleteBucketDataRedundancyTransitionRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public ListUserDataRedundancyTransitionResult listUserDataRedundancyTransition(ListUserDataRedundancyTransitionRequest listUserDataRedundancyTransitionRequest) throws OSSException, ClientException {
        assertParameterNotNull(listUserDataRedundancyTransitionRequest, "listUserDataRedundancyTransitionRequest");
        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.REDUNDANCY_TRANSITION, null);
        if (null != listUserDataRedundancyTransitionRequest.getMaxKeys()) {
            params.put(MAX_KEYS, Integer.toString(listUserDataRedundancyTransitionRequest.getMaxKeys()));
        }
        if (null != listUserDataRedundancyTransitionRequest.getContinuationToken()) {
            params.put(SUBRESOURCE_CONTINUATION_TOKEN, listUserDataRedundancyTransitionRequest.getContinuationToken());
        }

        GenericRequest gGenericRequest = new GenericRequest();

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).setOriginalRequest(gGenericRequest).build();

        return doOperation(request, listUserDataRedundancyTransitionResponseParser, null, null, true);
    }

    public List<GetBucketDataRedundancyTransitionResult> listBucketDataRedundancyTransition(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.REDUNDANCY_TRANSITION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, listBucketDataRedundancyTransitionResponseParser, bucketName, null, true);
    }

    public CreateAccessPointResult createAccessPoint(CreateAccessPointRequest createAccessPointRequest) throws OSSException, ClientException {
        assertParameterNotNull(createAccessPointRequest, "createAccessPointRequest");
        assertParameterNotNull(createAccessPointRequest.getNetworkOrigin(), "networkOrigin");
        assertParameterNotNull(createAccessPointRequest.getAccessPointName(), "accessPointName");

        String bucketName = createAccessPointRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_ACCESS_POINT, null);

        byte[] rawContent = createAccessPointRequestParser.marshall(createAccessPointRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(createAccessPointRequest).setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent)).build();

        return doOperation(request, createAccessPointResponseParser, bucketName, null, true);
    }

    public GetAccessPointResult getAccessPoint(GetAccessPointRequest getAccessPointRequest) throws OSSException, ClientException {
        assertParameterNotNull(getAccessPointRequest, "getAccessPointRequest");
        assertParameterNotNull(getAccessPointRequest.getAccessPointName(), "accessPointName");

        String bucketName = getAccessPointRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.OSS_ACCESS_POINT_NAME, getAccessPointRequest.getAccessPointName());

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_ACCESS_POINT, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setOriginalRequest(getAccessPointRequest).build();

        return doOperation(request, getAccessPointResponseParser, bucketName, null, true);
    }

    public VoidResult deleteAccessPoint(DeleteAccessPointRequest deleteAccessPointRequest) throws OSSException, ClientException {
        assertParameterNotNull(deleteAccessPointRequest, "deleteAccessPointRequest");
        assertParameterNotNull(deleteAccessPointRequest.getAccessPointName(), "accessPointName");

        String bucketName = deleteAccessPointRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.OSS_ACCESS_POINT_NAME, deleteAccessPointRequest.getAccessPointName());

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_ACCESS_POINT, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setOriginalRequest(deleteAccessPointRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public VoidResult putAccessPointPolicy(PutAccessPointPolicyRequest putAccessPointPolicyRequest) throws OSSException, ClientException {
        assertParameterNotNull(putAccessPointPolicyRequest, "putAccessPointPolicyRequest");
        assertParameterNotNull(putAccessPointPolicyRequest.getAccessPointName(), "accessPointName");

        String bucketName = putAccessPointPolicyRequest.getBucketName();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.OSS_ACCESS_POINT_NAME, putAccessPointPolicyRequest.getAccessPointName());

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_ACCESS_POINT_POLICY, null);

        byte[] rawContent = putAccessPointPolicyRequestParser.marshall(putAccessPointPolicyRequest);


        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(putAccessPointPolicyRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public GetAccessPointPolicyResult getAccessPointPolicy(GetAccessPointPolicyRequest getAccessPointPolicyRequest) throws OSSException, ClientException {
        assertParameterNotNull(getAccessPointPolicyRequest, "getAccessPointPolicyRequest");
        assertParameterNotNull(getAccessPointPolicyRequest.getAccessPointName(), "accessPointName");

        String bucketName = getAccessPointPolicyRequest.getBucketName();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.OSS_ACCESS_POINT_NAME, getAccessPointPolicyRequest.getAccessPointName());

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_ACCESS_POINT_POLICY, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setOriginalRequest(getAccessPointPolicyRequest).build();

        return doOperation(request, getAccessPointPolicyResponseParser, bucketName, null, true);
    }

    public VoidResult deleteAccessPointPolicy(DeleteAccessPointPolicyRequest deleteAccessPointPolicyRequest) throws OSSException, ClientException {
        assertParameterNotNull(deleteAccessPointPolicyRequest, "deleteAccessPointPolicyRequest");
        assertParameterNotNull(deleteAccessPointPolicyRequest.getAccessPointName(), "accessPointName");

        String bucketName = deleteAccessPointPolicyRequest.getBucketName();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.OSS_ACCESS_POINT_NAME, deleteAccessPointPolicyRequest.getAccessPointName());

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_ACCESS_POINT_POLICY, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setOriginalRequest(deleteAccessPointPolicyRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null, true);
    }

    public ListAccessPointsResult listAccessPoints(ListAccessPointsRequest listAccessPointsRequest) throws OSSException, ClientException {
        assertParameterNotNull(listAccessPointsRequest, "listAccessPointsRequest");
        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_ACCESS_POINT, null);
        if (null != listAccessPointsRequest.getMaxKeys()) {
            params.put(MAX_KEYS, Integer.toString(listAccessPointsRequest.getMaxKeys()));
        }
        if (null != listAccessPointsRequest.getContinuationToken()) {
            params.put(SUBRESOURCE_CONTINUATION_TOKEN, listAccessPointsRequest.getContinuationToken());
        }

        GenericRequest gGenericRequest = new GenericRequest();

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).setOriginalRequest(gGenericRequest).build();

        return doOperation(request, listAccessPointsResponseParser, null, null, true);
    }

    public ListAccessPointsResult listBucketAccessPoints(ListBucketAccessPointsRequest listBucketAccessPointsRequest) throws OSSException, ClientException {
        assertParameterNotNull(listBucketAccessPointsRequest, "listBucketAccessPointsRequest");

        String bucketName = listBucketAccessPointsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_ACCESS_POINT, null);
        if (null != listBucketAccessPointsRequest.getMaxKeys()) {
            params.put(MAX_KEYS, Integer.toString(listBucketAccessPointsRequest.getMaxKeys()));
        }
        if (null != listBucketAccessPointsRequest.getContinuationToken()) {
            params.put(SUBRESOURCE_CONTINUATION_TOKEN, listBucketAccessPointsRequest.getContinuationToken());
        }

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setOriginalRequest(listBucketAccessPointsRequest).build();

        return doOperation(request, listAccessPointsResponseParser, bucketName, null, true);
    }

}
