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
import static com.aliyun.oss.common.parser.RequestMarshallers.deleteObjectsRequestMarshaller;
import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.common.utils.CodingUtils.assertTrue;
import static com.aliyun.oss.common.utils.IOUtils.checkFile;
import static com.aliyun.oss.common.utils.IOUtils.newRepeatableInputStream;
import static com.aliyun.oss.common.utils.IOUtils.safeClose;
import static com.aliyun.oss.internal.OSSUtils.OSS_RESOURCE_MANAGER;
import static com.aliyun.oss.internal.OSSUtils.addDateHeader;
import static com.aliyun.oss.internal.OSSUtils.addHeader;
import static com.aliyun.oss.internal.OSSUtils.addStringListHeader;
import static com.aliyun.oss.internal.OSSUtils.removeHeader;
import static com.aliyun.oss.internal.OSSUtils.determineInputStreamLength;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.OSSUtils.ensureObjectKeyValid;
import static com.aliyun.oss.internal.OSSUtils.populateResponseHeaderParameters;
import static com.aliyun.oss.internal.OSSUtils.joinETags;
import static com.aliyun.oss.internal.OSSUtils.populateRequestMetadata;
import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;
import static com.aliyun.oss.internal.ResponseParsers.copyObjectResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.deleteObjectsResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getObjectMetadataResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.putObjectReponseParser;
import static com.aliyun.oss.internal.ResponseParsers.appendObjectResponseParser;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_BUFFER_SIZE;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_CHARSET_NAME;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;

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
import com.aliyun.oss.common.comm.io.RepeatableFileInputStream;
import com.aliyun.oss.common.parser.ResponseParser;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.common.utils.ExceptionFactory;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyun.oss.common.utils.RangeSpec;
import com.aliyun.oss.internal.ResponseParsers.GetObjectResponseParser;
import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.HeadObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;

/**
 * Object operation.
 */
public class OSSObjectOperation extends OSSOperation {
	
    public OSSObjectOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }
    
    /**
     * Upload input stream or file to oss.
     */
	public PutObjectResult putObject(PutObjectRequest putObjectRequest) 
    		throws OSSException, ClientException {
    	assertParameterNotNull(putObjectRequest, "putObjectRequest");
    	return writeObjectInternal(WriteMode.OVERWRITE, putObjectRequest, putObjectReponseParser);
	}
    
    /**
     * Upload input stream to oss by using url signature.
     */
    public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
    		Map<String, String> requestHeaders, boolean useChunkEncoding) throws OSSException, ClientException {

        assertParameterNotNull(signedUrl, "signedUrl");
        assertParameterNotNull(requestContent, "requestContent");
        
        if (requestHeaders == null) {
        	requestHeaders = new HashMap<String, String>();
        }
        
        RequestMessage request = new RequestMessage();
        request.setMethod(HttpMethod.PUT);
    	request.setAbsoluteUrl(signedUrl);
    	request.setUseUrlSignature(true);
    	request.setContent(requestContent);
    	request.setContentLength(determineInputStreamLength(requestContent, contentLength, useChunkEncoding));
    	request.setHeaders(requestHeaders);
    	request.setUseChunkEncoding(useChunkEncoding);
    	
    	return doOperation(request, putObjectReponseParser, null, null, true);
    }
    
    /**
     * Upload input stream or file to oss by append mode.
     */
	public AppendObjectResult appendObject(AppendObjectRequest appendObjectRequest) 
    		throws OSSException, ClientException {
    	assertParameterNotNull(appendObjectRequest, "appendObjectRequest");
    	return writeObjectInternal(WriteMode.APPEND, appendObjectRequest, appendObjectResponseParser);
	}

    /**
     * Pull an object from oss.
     */
    public OSSObject getObject(String bucketName, String key)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);

        return getObject(new GetObjectRequest(bucketName, key));
    }

    /**
     * Pull an object from oss.
     */
    public OSSObject getObject(GetObjectRequest getObjectRequest)
            throws OSSException, ClientException {
    	
    	assertParameterNotNull(getObjectRequest, "getObjectRequest");
    	
    	String bucketName = null;
    	String key = null;
    	RequestMessage request = null;
    	
    	if (!getObjectRequest.isUseUrlSignature()) {
        	assertParameterNotNull(getObjectRequest, "getObjectRequest");

            bucketName = getObjectRequest.getBucketName();
            key = getObjectRequest.getKey();
            
            assertParameterNotNull(bucketName, "bucketName");
            assertParameterNotNull(key, "key");
            ensureBucketNameValid(bucketName);
            ensureObjectKeyValid(key);

            Map<String, String> headers = new HashMap<String, String>();
            populateGetObjectRequestHeaders(getObjectRequest, headers);

            Map<String, String> params = new HashMap<String, String>();
            populateResponseHeaderParameters(params, getObjectRequest.getResponseHeaders());
            
            request = new OSSRequestMessageBuilder(getInnerClient())
	                .setEndpoint(getEndpoint())
	                .setMethod(HttpMethod.GET)
	                .setBucket(bucketName)
	                .setKey(key)
	                .setHeaders(headers)
	                .setParameters(params)
	                .build();
        } else {
        	request = new RequestMessage();
        	request.setMethod(HttpMethod.GET);
        	request.setAbsoluteUrl(getObjectRequest.getAbsoluteUri());
        	request.setUseUrlSignature(true);
        	request.setHeaders(getObjectRequest.getHeaders());
        }

        return doOperation(request, new GetObjectResponseParser(bucketName, key), 
        		bucketName, key, true);
    }

    /**
     * Populate a local file with the specified object.
     */
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file)
            throws OSSException, ClientException {

        assertParameterNotNull(file, "file");

        OSSObject ossObject = getObject(getObjectRequest);

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = ossObject.getObjectContent().read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            
            return ossObject.getObjectMetadata();
        } catch (IOException e) {
            throw new ClientException(OSS_RESOURCE_MANAGER.getString("CannotReadContentStream"), e);
        } finally {
            safeClose(outputStream);
            safeClose(ossObject.getObjectContent());
        }
    }

    /**
     * Get object matadata.
     */
    public ObjectMetadata getObjectMetadata(String bucketName, String key)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
	        	.setEndpoint(getEndpoint())
	            .setMethod(HttpMethod.HEAD)
	            .setBucket(bucketName)
	            .setKey(key)
	            .build();
        
        List<ResponseHandler> reponseHandlers = new ArrayList<ResponseHandler>();
        reponseHandlers.add(new ResponseHandler() {
        	
            @Override
            public void handle(ResponseMessage response) 
            		throws ServiceException, ClientException {
                if (response.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                    safeCloseResponse(response);
                    throw ExceptionFactory.createOSSException(
                    		response.getHeaders().get(OSSHeaders.OSS_HEADER_REQUEST_ID), 
                    		OSSErrorCode.NO_SUCH_KEY, 
                            OSS_RESOURCE_MANAGER.getString("NoSuchKey"));
                }
            }
            
        });
        
        return doOperation(request, getObjectMetadataResponseParser, 
        		bucketName, key, true, null, reponseHandlers);
    }

    /**
     * Copy an existing object to another one.
     */
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(copyObjectRequest, "copyObjectRequest");

        Map<String, String> headers = new HashMap<String, String>();
        populateCopyObjectHeaders(copyObjectRequest, headers);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
        		.setEndpoint(getEndpoint())
	            .setMethod(HttpMethod.PUT)
	            .setBucket(copyObjectRequest.getDestinationBucketName())
	            .setKey(copyObjectRequest.getDestinationKey())
	            .setHeaders(headers)
	            .build();
        
        return doOperation(request, copyObjectResponseParser, 
        		copyObjectRequest.getDestinationBucketName(), 
        		copyObjectRequest.getDestinationKey(), true);
    }


    /**
     * Delete an object.
     */
    public void deleteObject(String bucketName, String key)
            throws OSSException, ClientException {

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setKey(key)
                .build();
        
        doOperation(request, emptyResponseParser, bucketName, key);
    }
    
    /**
     * Delete multiple objects.
     */
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {
    	
    	assertParameterNotNull(deleteObjectsRequest, "deleteObjectsRequest");
    	
    	String bucketName = deleteObjectsRequest.getBucketName();
    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
    	
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_DELETE, null);
        
        byte[] rawContent = deleteObjectsRequestMarshaller.marshall(deleteObjectsRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addDeleteObjectsRequiredHeaders(headers, rawContent);
        addDeleteObjectsOptionalHeaders(headers, deleteObjectsRequest);
        
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
	        	.setEndpoint(getEndpoint())
	        	.setMethod(HttpMethod.POST)
	        	.setBucket(bucketName)
	        	.setParameters(params)
	        	.setHeaders(headers)
	        	.setInputSize(rawContent.length)
	        	.setInputStream(new ByteArrayInputStream(rawContent))
	        	.build();
        
        return doOperation(request, deleteObjectsResponseParser, bucketName, null, true);
    }
    
    /**
     * Check if the object key exists under the specified bucket.
     */
    public void headObject(HeadObjectRequest headObjectRequest)
			throws OSSException, ClientException {
    	
    	assertParameterNotNull(headObjectRequest, "headObjectRequest");
    	
    	String bucketName = headObjectRequest.getBucketName();
    	String key = headObjectRequest.getKey();
    	
    	assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        
        Map<String, String> headers = new HashMap<String, String>();
        addDateHeader(headers,
    			OSSHeaders.HEAD_OBJECT_IF_MODIFIED_SINCE,
    			headObjectRequest.getModifiedSinceConstraint());
    	addDateHeader(headers,
    			OSSHeaders.HEAD_OBJECT_IF_UNMODIFIED_SINCE,
    			headObjectRequest.getUnmodifiedSinceConstraint());
    	addStringListHeader(headers,
    			OSSHeaders.HEAD_OBJECT_IF_MATCH,
    			headObjectRequest.getMatchingETagConstraints());
    	addStringListHeader(headers,
    			OSSHeaders.HEAD_OBJECT_IF_NONE_MATCH,
    			headObjectRequest.getNonmatchingETagConstraints());
    	
    	RequestMessage request = new OSSRequestMessageBuilder(getInnerClient())
		    	.setEndpoint(getEndpoint())
		    	.setMethod(HttpMethod.HEAD)
		    	.setBucket(bucketName)
		    	.setKey(key)
		    	.setHeaders(headers)
		    	.build();
    	
    	doOperation(request, emptyResponseParser, bucketName, key);
	}
    
    private static enum MetadataDirective {
    	
    	/* Copy metadata from source object */
    	COPY("COPY"),

    	/* Replace metadata with newly metadata */
    	REPLACE("REPLACE");
    	
    	private final String directiveAsString;
    	
    	private MetadataDirective(String directiveAsString) {
    		this.directiveAsString = directiveAsString;
    	}
    	
    	@Override
    	public String toString() {
    		return this.directiveAsString;
    	}
    }
    
    /**
     * An enum to represent different modes the client may specify to upload specified file or inputstream.
     */
    private static enum WriteMode {
    	
    	/* If object already not exists, create it. otherwise, append it with the new input */
    	APPEND("APPEND"),
    	
    	/* No matter object exists or not, just overwrite it with the new input */
    	OVERWRITE("OVERWRITE");
    	
    	private final String modeAsString;
    	
    	private WriteMode(String modeAsString) {
    		this.modeAsString = modeAsString;
    	}
    	
    	@Override
    	public String toString() {
    		return this.modeAsString;
    	}
    	
    	public static HttpMethod getMappingMethod(WriteMode mode) {
    		switch (mode) {
			case APPEND:
				return HttpMethod.POST;
			
			case OVERWRITE:
				return HttpMethod.PUT;
				
			default:
				throw new IllegalArgumentException("Unsuported write mode" + mode.toString());
			}
    	}
    }
    
    private <RequestType extends PutObjectRequest, ResponseType> 
    	ResponseType writeObjectInternal(WriteMode mode, RequestType originalRequest, 
    			ResponseParser<ResponseType> responseParser) {
    	final String bucketName = originalRequest.getBucketName();
		final String key = originalRequest.getKey();
		InputStream originalInputStream = originalRequest.getInputStream();
		ObjectMetadata metadata = originalRequest.getMetadata();
		if (metadata == null) {
			metadata = new ObjectMetadata();
		}
		
		assertParameterNotNull(bucketName, "bucketName");
		assertParameterNotNull(key, "key");
		ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);
		
        InputStream repeatableInputStream = null;
    	if (originalRequest.getFile() != null) {
        	File toUpload = originalRequest.getFile();
        	
        	if (!checkFile(toUpload)) {
				throw new ClientException("Illegal file path: " + toUpload.getPath());
			}
        	
        	metadata.setContentLength(toUpload.length());
        	if (metadata.getContentType() == null) {
        		metadata.setContentType(Mimetypes.getInstance().getMimetype(toUpload));
        	}
        	
        	try {
        		repeatableInputStream = new RepeatableFileInputStream(toUpload);
			} catch (IOException e) {
				throw new ClientException("Cannot locate file to upload", e);
			}
        } else {
        	assertTrue(originalInputStream != null, "Please specify input stream or file to upload");
        	
        	if (metadata.getContentType() == null) {
        		metadata.setContentType(Mimetypes.DEFAULT_MIMETYPE);
        	}
        	
        	try {
				repeatableInputStream = newRepeatableInputStream(originalInputStream);
			} catch (IOException e) {
				throw new ClientException("Cannot wrap to repeatable input stream", e);
			}
        }
        
        Map<String, String> headers = new HashMap<String, String>();
        populateRequestMetadata(headers, metadata);
        Map<String, String> params = new LinkedHashMap<String, String>();
        populateWriteObjectHeaders(mode, originalRequest, params);
        
        RequestMessage httpRequest = new OSSRequestMessageBuilder(getInnerClient())
		        .setEndpoint(getEndpoint())
		        .setMethod(WriteMode.getMappingMethod(mode))
		        .setBucket(bucketName)
		        .setKey(key)
		        .setHeaders(headers)
		        .setParameters(params)
		        .setInputStream(repeatableInputStream)
		        .setInputSize(determineInputStreamLength(repeatableInputStream, metadata.getContentLength()))
		        .build();
        
        return doOperation(httpRequest, responseParser, bucketName, key, true);
    }

    private static void populateCopyObjectHeaders(CopyObjectRequest copyObjectRequest,
    		Map<String, String> headers) {
    	String copySourceHeader = "/" + copyObjectRequest.getSourceBucketName() + "/"
    			+ HttpUtil.urlEncode(copyObjectRequest.getSourceKey(), DEFAULT_CHARSET_NAME);
    	headers.put(OSSHeaders.COPY_OBJECT_SOURCE, copySourceHeader);
    	
    	addDateHeader(headers,
    			OSSHeaders.COPY_OBJECT_SOURCE_IF_MODIFIED_SINCE,
    			copyObjectRequest.getModifiedSinceConstraint());
    	addDateHeader(headers,
    			OSSHeaders.COPY_OBJECT_SOURCE_IF_UNMODIFIED_SINCE,
    			copyObjectRequest.getUnmodifiedSinceConstraint());
    	
    	addStringListHeader(headers,
    			OSSHeaders.COPY_OBJECT_SOURCE_IF_MATCH,
    			copyObjectRequest.getMatchingETagConstraints());
    	addStringListHeader(headers,
    			OSSHeaders.COPY_OBJECT_SOURCE_IF_NONE_MATCH,
    			copyObjectRequest.getNonmatchingEtagConstraints());
    	
    	addHeader(headers, 
    			OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION, 
    			copyObjectRequest.getServerSideEncryption());
    	
    	ObjectMetadata newObjectMetadata = copyObjectRequest.getNewObjectMetadata();
    	if (newObjectMetadata != null) {
    		headers.put(OSSHeaders.COPY_OBJECT_METADATA_DIRECTIVE, MetadataDirective.REPLACE.toString());
    		populateRequestMetadata(headers, newObjectMetadata);
    	}
    	
    	// The header of Content-Length should not be specified on copying an object.
    	removeHeader(headers, HttpHeaders.CONTENT_LENGTH);
    }
    
    private static void populateGetObjectRequestHeaders(GetObjectRequest getObjectRequest,
    		Map<String, String> headers) {
    	
    	if (getObjectRequest.getRange() != null) {
            addGetObjectRangeHeader(getObjectRequest.getRange(), headers);
        }

        if (getObjectRequest.getModifiedSinceConstraint() != null) {
            headers.put(OSSHeaders.GET_OBJECT_IF_MODIFIED_SINCE, DateUtil
                    .formatRfc822Date(getObjectRequest
                            .getModifiedSinceConstraint()));
        }
        
        if (getObjectRequest.getUnmodifiedSinceConstraint() != null) {
            headers.put(OSSHeaders.GET_OBJECT_IF_UNMODIFIED_SINCE, DateUtil
                    .formatRfc822Date(getObjectRequest
                            .getUnmodifiedSinceConstraint()));
        }
        
        if (getObjectRequest.getMatchingETagConstraints().size() > 0){
            headers.put(OSSHeaders.GET_OBJECT_IF_MATCH,
                    joinETags(getObjectRequest.getMatchingETagConstraints()));
        }
        
        if (getObjectRequest.getNonmatchingETagConstraints().size() > 0){
            headers.put(OSSHeaders.GET_OBJECT_IF_NONE_MATCH,
                    joinETags(getObjectRequest.getNonmatchingETagConstraints()));
        }
        
        // Populate all standard HTTP headers provided by SDK users
        headers.putAll(getObjectRequest.getHeaders());
    }
     
    private static void addDeleteObjectsRequiredHeaders(Map<String, String> headers, byte[] rawContent) {
    	headers.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(rawContent.length));
    	
    	byte[] md5 = BinaryUtil.calculateMd5(rawContent);
    	String md5Base64 = BinaryUtil.toBase64String(md5);
    	headers.put(HttpHeaders.CONTENT_MD5, md5Base64);
    }
    
    private static void addDeleteObjectsOptionalHeaders(Map<String, String> headers, DeleteObjectsRequest request) {
		if (request.getEncodingType() != null) {
			headers.put(ENCODING_TYPE, request.getEncodingType());
		}
	}
    
    private static void addGetObjectRangeHeader(long[] range, Map<String, String> headers) {    	
    	RangeSpec rangeSpec = RangeSpec.parse(range);
        headers.put(OSSHeaders.RANGE, rangeSpec.toString());
    }
    
    private static void populateWriteObjectHeaders(WriteMode mode, PutObjectRequest originalRequest, Map<String, String> params) {
    	if (mode == WriteMode.OVERWRITE) {
    		return;
    	}
    	
    	assert (originalRequest instanceof AppendObjectRequest);
    	params.put(RequestParameters.SUBRESOURCE_APPEND, null);
    	AppendObjectRequest appendObjectRequest = (AppendObjectRequest)originalRequest;
    	if (appendObjectRequest.getPosition() != null) {
    		params.put(RequestParameters.POSITION, String.valueOf(appendObjectRequest.getPosition()));    		
    	}
    }
}
