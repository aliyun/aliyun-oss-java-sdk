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

import static com.aliyun.oss.common.parser.RequestMarshallers.bucketRefererMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.createBucketRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketLifecycleRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketLoggingRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketWebsiteRequestMarshaller;
import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.RequestParameters.DELIMITER;
import static com.aliyun.oss.internal.RequestParameters.ENCODING_TYPE;
import static com.aliyun.oss.internal.RequestParameters.MARKER;
import static com.aliyun.oss.internal.RequestParameters.MAX_KEYS;
import static com.aliyun.oss.internal.RequestParameters.PREFIX;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_ACL;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LIFECYCLE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LOCATION;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LOGGING;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_REFERER;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_WEBSITE;
import static com.aliyun.oss.internal.ResponseParsers.getBucketAclResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketLifecycleResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketLocationResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketLoggingResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketRefererResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketWebsiteResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.listBucketResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.listObjectsReponseParser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.BucketLoggingResult;
import com.aliyun.oss.model.BucketReferer;
import com.aliyun.oss.model.BucketWebsiteResult;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.ListBucketsRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.SetBucketAclRequest;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.aliyun.oss.model.SetBucketLoggingRequest;
import com.aliyun.oss.model.SetBucketRefererRequest;
import com.aliyun.oss.model.SetBucketWebsiteRequest;

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
    public Bucket createBucket(CreateBucketRequest createBucketRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(createBucketRequest, "createBucketRequest");
        
        String bucketName = createBucketRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> headers = new HashMap<String, String>();
        addOptionalACLHeader(headers, createBucketRequest.getCannedACL());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setHeaders(headers)
                .setInputStreamWithLength(createBucketRequestMarshaller.marshall(createBucketRequest))
                .setOriginalRequest(createBucketRequest)
                .build();

        doOperation(request, emptyResponseParser, bucketName, null);
        return new Bucket(bucketName);
    }

    /**
     * Delete a bucket.
     */
    public void deleteBucket(GenericRequest genericRequest) 
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setOriginalRequest(genericRequest)
                .build();
        
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
    public BucketList listBuckets(ListBucketsRequest listBucketRequest) 
            throws OSSException, ClientException {

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

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setParameters(params)
                .setOriginalRequest(listBucketRequest)
                .build();
        
        return doOperation(request, listBucketResponseParser, null, null, true);
    }

    /**
     * Set bucket's canned ACL.
     */
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest)
            throws OSSException, ClientException {
        
        assertParameterNotNull(setBucketAclRequest, "setBucketAclRequest");
        
        String bucketName = setBucketAclRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> headers = new HashMap<String, String>();
        addOptionalACLHeader(headers, setBucketAclRequest.getCannedACL());
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setHeaders(headers)
                .setOriginalRequest(setBucketAclRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket's ACL.
     */
    public AccessControlList getBucketAcl(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketAclResponseParser, bucketName, null, true);
    }
    
    /**
     * Set bucket referer.
     */
    public void setBucketReferer(SetBucketRefererRequest setBucketRefererRequest)
            throws OSSException, ClientException {
        
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
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setInputStreamWithLength(bucketRefererMarshaller.marshall(referer))
                .setOriginalRequest(setBucketRefererRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket referer.
     */
    public BucketReferer getBucketReferer(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_REFERER, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketRefererResponseParser , bucketName, null, true);
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
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketLocationResponseParser, bucketName, null, true);
    }

    /**
     * Determine whether a bucket exists or not.
     */
    public boolean doesBucketExists(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        try {
             getBucketAcl(new GenericRequest(bucketName));
        } catch (OSSException oe) {
           if(oe.getErrorCode().equals(OSSErrorCode.NO_SUCH_BUCKET)) {
               return false;
           }
        } catch (Exception e) {
            System.err.println("doesBucketExists " + e.getMessage());
        }
        return true;
    }

    /**
     * List objects under the specified bucket.
     */
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(listObjectsRequest, "listObjectsRequest");
        
        String bucketName = listObjectsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new LinkedHashMap<String, String>();
        populateListObjectsRequestParameters(listObjectsRequest, params);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(listObjectsRequest)
                .build();
        
        return doOperation(request, listObjectsReponseParser, bucketName, null, true);
    }
    
    /**
     * Set bucket logging.
     */
    public void setBucketLogging(SetBucketLoggingRequest setBucketLoggingRequest)
            throws OSSException, ClientException {
        
        assertParameterNotNull(setBucketLoggingRequest, "setBucketLoggingRequest");

        String bucketName = setBucketLoggingRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);  

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setInputStreamWithLength(setBucketLoggingRequestMarshaller.marshall(setBucketLoggingRequest))
                .setOriginalRequest(setBucketLoggingRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    /**
     * Get bucket logging.
     */
    public BucketLoggingResult getBucketLogging(GenericRequest genericRequest)
            throws OSSException, ClientException {
        
        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketLoggingResponseParser, bucketName, null, true);
    }
    
    /**
     * Delete bucket logging.
     */
    public void deleteBucketLogging(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    /**
     * Set bucket website.
     */
    public void setBucketWebsite(SetBucketWebsiteRequest setBucketWebSiteRequest)
            throws OSSException, ClientException {
        
        assertParameterNotNull(setBucketWebSiteRequest, "setBucketWebSiteRequest");

        String bucketName = setBucketWebSiteRequest.getBucketName();         
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        String indexDocument = setBucketWebSiteRequest.getIndexDocument();
        assertParameterNotNull(indexDocument, "indexDocument");
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setInputStreamWithLength(setBucketWebsiteRequestMarshaller.marshall(setBucketWebSiteRequest))
                .setOriginalRequest(setBucketWebSiteRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    /**
     * Get bucket website.
     */
    public BucketWebsiteResult getBucketWebsite(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketWebsiteResponseParser, bucketName, null, true);
    }
    
    /**
     * Delete bucket website.
     */
    public void deleteBucketWebsite(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    /**
     * Set bucket lifecycle.
     */
    public void setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest)
            throws OSSException, ClientException {
        
        assertParameterNotNull(setBucketLifecycleRequest, "setBucketLifecycleRequest");
        
        String bucketName = setBucketLifecycleRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setInputStreamWithLength(setBucketLifecycleRequestMarshaller.marshall(setBucketLifecycleRequest))
                .setOriginalRequest(setBucketLifecycleRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    /**
     * Get bucket lifecycle.
     */
    public List<LifecycleRule> getBucketLifecycle(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketLifecycleResponseParser, bucketName, null, true);
    }
    
    /**
     * Delete bucket lifecycle.
     */
    public void deleteBucketLifecycle(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
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
    
    private static void addOptionalACLHeader(Map<String, String> headers, CannedAccessControlList cannedAcl) {
        if (cannedAcl != null) {
            headers.put(OSSHeaders.OSS_CANNED_ACL, cannedAcl.toString());
        }
    }
} 
