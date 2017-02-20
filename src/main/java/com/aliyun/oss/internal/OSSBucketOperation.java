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
import static com.aliyun.oss.common.parser.RequestMarshallers.putBucketImageRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.putImageStyleRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketLifecycleRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketLoggingRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketTaggingRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketWebsiteRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.addBucketReplicationRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.deleteBucketReplicationRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.addBucketCnameRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.deleteBucketCnameRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.setBucketQosRequestMarshaller;
import static com.aliyun.oss.common.parser.RequestMarshallers.bucketImageProcessConfMarshaller;
import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.RequestParameters.DELIMITER;
import static com.aliyun.oss.internal.RequestParameters.ENCODING_TYPE;
import static com.aliyun.oss.internal.RequestParameters.MARKER;
import static com.aliyun.oss.internal.RequestParameters.MAX_KEYS;
import static com.aliyun.oss.internal.RequestParameters.PREFIX;
import static com.aliyun.oss.internal.RequestParameters.BID;
import static com.aliyun.oss.internal.RequestParameters.STYLE_NAME;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_ACL;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_IMG;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LIFECYCLE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LOCATION;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LOGGING;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_REFERER;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_STYLE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_TAGGING;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_WEBSITE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_PROCESS_CONF;
import static com.aliyun.oss.internal.ResponseParsers.getBucketAclResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketLifecycleResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketLocationResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketLoggingResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketRefererResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketTaggingResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketWebsiteResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketReplicationResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketReplicationProgressResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketReplicationLocationResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketCnameResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketInfoResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketStatResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketQosResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.listBucketResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.listObjectsReponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketImageResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getImageStyleResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.listImageStyleResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getBucketImageProcessConfResponseParser;

import java.io.ByteArrayInputStream;
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
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.BucketLoggingResult;
import com.aliyun.oss.model.BucketProcess;
import com.aliyun.oss.model.BucketReferer;
import com.aliyun.oss.model.BucketReplicationProgress;
import com.aliyun.oss.model.BucketStat;
import com.aliyun.oss.model.BucketWebsiteResult;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CnameConfiguration;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.DeleteBucketCnameRequest;
import com.aliyun.oss.model.DeleteBucketReplicationRequest;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.GetBucketImageResult;
import com.aliyun.oss.model.GetBucketReplicationProgressRequest;
import com.aliyun.oss.model.ImageProcess;
import com.aliyun.oss.model.ReplicationRule;
import com.aliyun.oss.model.GetImageStyleResult;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.ListBucketsRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutBucketImageRequest;
import com.aliyun.oss.model.PutImageStyleRequest;
import com.aliyun.oss.model.SetBucketAclRequest;
import com.aliyun.oss.model.AddBucketCnameRequest;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.aliyun.oss.model.SetBucketLoggingRequest;
import com.aliyun.oss.model.SetBucketProcessRequest;
import com.aliyun.oss.model.SetBucketRefererRequest;
import com.aliyun.oss.model.AddBucketReplicationRequest;
import com.aliyun.oss.model.SetBucketStorageCapacityRequest;
import com.aliyun.oss.model.SetBucketTaggingRequest;
import com.aliyun.oss.model.SetBucketWebsiteRequest;
import com.aliyun.oss.model.TagSet;
import com.aliyun.oss.model.Style;
import com.aliyun.oss.model.UserQos;

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
        if (listBucketRequest.getBid() != null) {
            params.put(BID, listBucketRequest.getBid());
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
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setHeaders(headers)
                .setParameters(params)
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
     * Put bucket image
     */
    public void putBucketImage(PutBucketImageRequest putBucketImageRequest){
    	assertParameterNotNull(putBucketImageRequest, "putBucketImageRequest");
    	String bucketName = putBucketImageRequest.GetBucketName();
    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_IMG, null);  
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(putBucketImageRequest)
                .setInputStreamWithLength(putBucketImageRequestMarshaller.marshall(putBucketImageRequest))
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    /**
     *  Get bucket image
     */
    public GetBucketImageResult getBucketImage(String bucketName, GenericRequest genericRequest){
    		assertParameterNotNull(bucketName, "bucketName");
    		ensureBucketNameValid(bucketName);
    		
    		Map<String, String> params = new HashMap<String, String>();
    		params.put(SUBRESOURCE_IMG, null);
    		 RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
    			        .setEndpoint(getEndpoint())
    			        .setMethod(HttpMethod.GET)
    			        .setBucket(bucketName)
    			        .setParameters(params)
    			        .setOriginalRequest(genericRequest)
    			        .build();
    		return doOperation(request, getBucketImageResponseParser, bucketName, null, true);
    }
    
   /**
    * Delete bucket image
    */
    public void deleteBucketImage(String bucketName, GenericRequest genericRequest)
   		 throws OSSException, ClientException {
    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_IMG, null);
        
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
     * put image style
     */
    public void putImageStyle(PutImageStyleRequest putImageStyleRequest)
    		throws OSSException, ClientException {
    	assertParameterNotNull(putImageStyleRequest, "putImageStyleRequest");
    	String bucketName = putImageStyleRequest.GetBucketName();
    	String styleName = putImageStyleRequest.GetStyleName();
    	assertParameterNotNull(styleName, "styleName");
    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_STYLE, null);  
        params.put(STYLE_NAME, styleName);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(putImageStyleRequest)
                .setInputStreamWithLength(putImageStyleRequestMarshaller.marshall(putImageStyleRequest))
                .build();
        
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
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
      
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
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		        .setEndpoint(getEndpoint())
		        .setMethod(HttpMethod.GET)
		        .setBucket(bucketName)
		        .setParameters(params)
		        .setOriginalRequest(genericRequest)
		        .build();
        
        return doOperation(request, getImageStyleResponseParser, bucketName, null, true);
    }
    
    
    /**
     * List image style. 
     */
    public List<Style> listImageStyle(String bucketName, GenericRequest genericRequest) 
    		throws OSSException, ClientException {

    	
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();;
        params.put(SUBRESOURCE_STYLE, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		        .setEndpoint(getEndpoint())
		        .setMethod(HttpMethod.GET)
		        .setBucket(bucketName)
		        .setParameters(params)
		        .setOriginalRequest(genericRequest)
		        .build();
        
        return doOperation(request, listImageStyleResponseParser, bucketName, null, true);
    }
    
    public void setBucketProcess(SetBucketProcessRequest setBucketProcessRequest)
            throws OSSException, ClientException {
        
        assertParameterNotNull(setBucketProcessRequest, "setBucketProcessRequest");
        
        ImageProcess imageProcessConf = setBucketProcessRequest.getImageProcess();
        assertParameterNotNull(imageProcessConf, "imageProcessConf");
        
        String bucketName = setBucketProcessRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_PROCESS_CONF, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setInputStreamWithLength(bucketImageProcessConfMarshaller.marshall(imageProcessConf))
                .setOriginalRequest(setBucketProcessRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    public BucketProcess getBucketProcess(GenericRequest genericRequest) 
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_PROCESS_CONF, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketImageProcessConfResponseParser , bucketName, null, true);
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
        
        if (setBucketWebSiteRequest.getIndexDocument() == null && 
                setBucketWebSiteRequest.getErrorDocument() == null && 
                setBucketWebSiteRequest.getRoutingRules().size() == 0) {
            throw new IllegalArgumentException(
                    String.format("IndexDocument/ErrorDocument/RoutingRules must have one"));
        } 
        
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
    
    /**
     * Set bucket tagging.
     */
    public void setBucketTagging(SetBucketTaggingRequest setBucketTaggingRequest)
            throws OSSException, ClientException {
        
        assertParameterNotNull(setBucketTaggingRequest, "setBucketTaggingRequest");
        
        String bucketName = setBucketTaggingRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setInputStreamWithLength(setBucketTaggingRequestMarshaller.marshall(setBucketTaggingRequest))
                .setOriginalRequest(setBucketTaggingRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    /**
     * Get bucket tagging.
     */
    public TagSet getBucketTagging(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketTaggingResponseParser, bucketName, null, true);
    }
    
    /**
     * Delete bucket tagging.
     */
    public void deleteBucketTagging(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);
        
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
     * Add bucket replication.
     */
    public void addBucketReplication(AddBucketReplicationRequest addBucketReplicationRequest)
            throws OSSException, ClientException {
        
        assertParameterNotNull(addBucketReplicationRequest, "addBucketReplicationRequest");
        assertParameterNotNull(addBucketReplicationRequest.getTargetBucketName(), "targetBucketName");
        assertParameterNotNull(addBucketReplicationRequest.getTargetBucketLocation(), "targetBucketLocation");
 
        String bucketName = addBucketReplicationRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION, null);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_ADD);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST)
                .setBucket(bucketName)
                .setParameters(params)
                .setInputStreamWithLength(addBucketReplicationRequestMarshaller.marshall(addBucketReplicationRequest))
                .setOriginalRequest(addBucketReplicationRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
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
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
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
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST)
                .setBucket(bucketName)
                .setParameters(params)
                .setHeaders(headers)
                .setInputSize(rawContent.length)
                .setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(deleteBucketReplicationRequest)
                .build();
        
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
    public BucketReplicationProgress getBucketReplicationProgress(GetBucketReplicationProgressRequest getBucketReplicationProgressRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(getBucketReplicationProgressRequest, "getBucketReplicationProgressRequest");
        assertParameterNotNull(getBucketReplicationProgressRequest.getReplicationRuleID(), "replicationRuleID");
        
        String bucketName = getBucketReplicationProgressRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_REPLICATION_PROGRESS, null);
        params.put(RequestParameters.RULE_ID, getBucketReplicationProgressRequest.getReplicationRuleID());
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(getBucketReplicationProgressRequest)
                .build();
        
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
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketReplicationLocationResponseParser, bucketName, null, true);
    }
    
    public void addBucketCname(AddBucketCnameRequest addBucketCnameRequest)
            throws OSSException, ClientException {
        
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
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST)
                .setBucket(bucketName)
                .setParameters(params)
                .setHeaders(headers)
                .setInputSize(rawContent.length)
                .setInputStream(new ByteArrayInputStream(rawContent))                
                .setOriginalRequest(addBucketCnameRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    public List<CnameConfiguration> getBucketCname(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_CNAME, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketCnameResponseParser, bucketName, null, true);
    }
    
    public void deleteBucketCname(DeleteBucketCnameRequest deleteBucketCnameRequest)
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
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST)
                .setBucket(bucketName)
                .setParameters(params)
                .setHeaders(headers)
                .setInputSize(rawContent.length)
                .setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(deleteBucketCnameRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
    }
    
    public BucketInfo getBucketInfo(GenericRequest genericRequest)
            throws OSSException, ClientException {
        
        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_BUCKET_INFO, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketInfoResponseParser, bucketName, null, true);
    }
    
    public BucketStat getBucketStat(GenericRequest genericRequest)
            throws OSSException, ClientException {
        
        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_STAT, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
        return doOperation(request, getBucketStatResponseParser, bucketName, null, true);
    }
    
    public void setBucketStorageCapacity(SetBucketStorageCapacityRequest setBucketStorageCapacityRequest)
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
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setHeaders(headers)
                .setInputSize(rawContent.length)
                .setInputStream(new ByteArrayInputStream(rawContent))                
                .setOriginalRequest(setBucketStorageCapacityRequest)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, null);
        
    }
    
    public UserQos getBucketStorageCapacity(GenericRequest genericRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");
        
        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_QOS, null);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setOriginalRequest(genericRequest)
                .build();
        
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
    
    private static void addOptionalACLHeader(Map<String, String> headers, CannedAccessControlList cannedAcl) {
        if (cannedAcl != null) {
            headers.put(OSSHeaders.OSS_CANNED_ACL, cannedAcl.toString());
        }
    }
} 
