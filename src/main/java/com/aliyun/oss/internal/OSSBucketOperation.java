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

import static com.aliyun.oss.internal.RequestParameters.*;
import static com.aliyun.oss.common.parser.RequestMarshallers.bucketRefererMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.createBucketRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketLifecycleRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketLoggingRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketWebsiteRequestMarshaller;
import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
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
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.ListBucketsRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.aliyun.oss.model.SetBucketLoggingRequest;
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
        
        CannedAccessControlList cannedACL = createBucketRequest.getCannedACL();
        assertParameterNotNull(cannedACL, "cannedACL");
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.OSS_CANNED_ACL, cannedACL.toString());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setHeaders(headers)
                .setInputStreamWithLength(createBucketRequestMarshaller.marshall(createBucketRequest))
                .build();

        doOperation(request, emptyResponseParser, bucketName, null);
        return new Bucket(bucketName);
    }

    /**
     * Delete a bucket.
     */
    public void deleteBucket(String bucketName)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
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
		        .build();
        
        return doOperation(request, listBucketResponseParser, null, null, true);
    }

    /**
     * Set bucket's ACL.
     */
    public void setBucketAcl(String bucketName, CannedAccessControlList acl)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        if (acl == null) {
            acl = CannedAccessControlList.Private;
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.OSS_CANNED_ACL, acl.toString());
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setHeaders(headers)
                .build();
        
       doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket's ACL.
     */
    public AccessControlList getBucketAcl(String bucketName)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		        .setEndpoint(getEndpoint())
		        .setMethod(HttpMethod.GET)
		        .setBucket(bucketName)
		        .setParameters(params)
		        .build();
        
        return doOperation(request, getBucketAclResponseParser, bucketName, null, true);
    }
    
    /**
     * Set bucket referer.
     */
    public void setBucketReferer(String bucketName, BucketReferer referer)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

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
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }

    /**
     * Get bucket referer.
     */
    public BucketReferer getBucketReferer(String bucketName)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_REFERER, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		        .setEndpoint(getEndpoint())
		        .setMethod(HttpMethod.GET)
		        .setBucket(bucketName)
		        .setParameters(params)
		        .build();
        
        return doOperation(request, getBucketRefererResponseParser , bucketName, null, true);
    }
    
    /**
     * Get bucket location.
     */
    public String getBucketLocation(String bucketName) {
        
    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOCATION, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		        .setEndpoint(getEndpoint())
		        .setMethod(HttpMethod.GET)
		        .setBucket(bucketName)
		        .setParameters(params)
		        .build();
        
        return doOperation(request, getBucketLocationResponseParser, bucketName, null, true);
    }

    /**
     * Determine whether a bucket exists or not.
     */
    public boolean doesBucketExists(String bucketName)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        try {
        	 getBucketAcl(bucketName);
		} catch (OSSException e) {
		   if(e.getErrorCode().equals(OSSErrorCode.NO_SUCH_BUCKET)) {
			   return false;
		   }
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
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    /**
     * Get bucket logging.
     */
    public BucketLoggingResult getBucketLogging(String bucketName)
            throws OSSException, ClientException {
        
    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		        .setEndpoint(getEndpoint())
		        .setMethod(HttpMethod.GET)
		        .setBucket(bucketName)
		        .setParameters(params)
		        .build();
        
        return doOperation(request, getBucketLoggingResponseParser, bucketName, null, true);
    }
    
    /**
     * Delete bucket logging.
     */
    public void deleteBucketLogging(String bucketName)
            throws OSSException, ClientException {

    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LOGGING, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setParameters(params)
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
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    /**
     * Get bucket website.
     */
    public BucketWebsiteResult getBucketWebsite(String bucketName)
            throws OSSException, ClientException {
        
    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		        .setEndpoint(getEndpoint())
		        .setMethod(HttpMethod.GET)
		        .setBucket(bucketName)
		        .setParameters(params)
		        .build();
        
        return doOperation(request, getBucketWebsiteResponseParser, bucketName, null, true);
    }
    
    /**
     * Delete bucket website.
     */
    public void deleteBucketWebsite(String bucketName)
            throws OSSException, ClientException {

    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_WEBSITE, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setParameters(params)
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
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    /**
     * Get bucket lifecycle.
     */
    public List<LifecycleRule> getBucketLifecycle(String bucketName)
            throws OSSException, ClientException {
        
    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		        .setEndpoint(getEndpoint())
		        .setMethod(HttpMethod.GET)
		        .setBucket(bucketName)
		        .setParameters(params)
		        .build();
        
        return doOperation(request, getBucketLifecycleResponseParser, bucketName, null, true);
    }
    
    /**
     * Delete bucket lifecycle.
     */
    public void deleteBucketLifecycle(String bucketName)
            throws OSSException, ClientException {

    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setParameters(params)
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
} 
