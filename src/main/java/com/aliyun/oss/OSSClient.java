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

import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.common.utils.IOUtils.checkFile;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_CHARSET_NAME;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_OSS_ENDPOINT;
import static com.aliyun.oss.internal.OSSUtils.OSS_RESOURCE_MANAGER;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.OSSUtils.populateResponseHeaderParameters;
import static com.aliyun.oss.internal.RequestParameters.OSS_ACCESS_KEY_ID;
import static com.aliyun.oss.internal.RequestParameters.SIGNATURE;
import static com.aliyun.oss.internal.RequestParameters.SECURITY_TOKEN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.ServiceSignature;
import com.aliyun.oss.common.comm.DefaultServiceClient;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyun.oss.internal.CORSOperation;
import com.aliyun.oss.internal.OSSBucketOperation;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.internal.OSSMultipartOperation;
import com.aliyun.oss.internal.OSSObjectOperation;
import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.internal.SignUtils;
import com.aliyun.oss.model.AbortMultipartUploadRequest;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.AppendObjectRequest;
import com.aliyun.oss.model.AppendObjectResult;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.BucketLoggingResult;
import com.aliyun.oss.model.BucketReferer;
import com.aliyun.oss.model.BucketWebsiteResult;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CompleteMultipartUploadRequest;
import com.aliyun.oss.model.CompleteMultipartUploadResult;
import com.aliyun.oss.model.CopyObjectRequest;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.HeadObjectRequest;
import com.aliyun.oss.model.InitiateMultipartUploadRequest;
import com.aliyun.oss.model.InitiateMultipartUploadResult;
import com.aliyun.oss.model.LifecycleRule;
import com.aliyun.oss.model.ListBucketsRequest;
import com.aliyun.oss.model.ListMultipartUploadsRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ListPartsRequest;
import com.aliyun.oss.model.MultipartUploadListing;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.OptionsRequest;
import com.aliyun.oss.model.PartListing;
import com.aliyun.oss.model.PolicyConditions;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyun.oss.model.SetBucketCORSRequest;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;
import com.aliyun.oss.model.SetBucketLifecycleRequest;
import com.aliyun.oss.model.SetBucketLoggingRequest;
import com.aliyun.oss.model.SetBucketWebsiteRequest;
import com.aliyun.oss.model.UploadPartCopyRequest;
import com.aliyun.oss.model.UploadPartCopyResult;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;

/**
 * 访问阿里云开放存储服务（Open Storage Service， OSS）的入口类。
 */
public class OSSClient implements OSS {

	/* The default credentials provider */
	private CredentialsProvider credsProvider;

	/* The valid endpoint for accessing to OSS services */
	private URI endpoint;

	/* The default service client */
	private ServiceClient serviceClient;

	/* The miscellaneous OSS operations */
	private OSSBucketOperation bucketOperation;
	private OSSObjectOperation objectOperation;
	private OSSMultipartOperation multipartOperation;
	private CORSOperation corsOperation;

	/**
	 * 使用默认的OSS Endpoint(http://oss-cn-hangzhou.aliyuncs.com)及
	 * 阿里云颁发的Access Id/Access Key构造一个新的{@link OSSClient}对象。
	 * 
	 * @param accessKeyId
	 *            访问OSS的Access Key ID。
	 * @param secretAccessKey
	 *            访问OSS的Secret Access Key。
	 */
	@Deprecated
	public OSSClient(String accessKeyId, String secretAccessKey) {
		this(DEFAULT_OSS_ENDPOINT, new DefaultCredentialProvider(accessKeyId, secretAccessKey));
	}

	/**
	 * 使用指定的OSS Endpoint、阿里云颁发的Access Id/Access Key构造一个新的{@link OSSClient}对象。
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint。
	 * @param accessKeyId
	 *            访问OSS的Access Key ID。
	 * @param secretAccessKey
	 *            访问OSS的Secret Access Key。
	 */
	public OSSClient(String endpoint, String accessKeyId, String secretAccessKey) {
		this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey), null);
	}
	
	/**
	 * 使用指定的OSS Endpoint、STS提供的临时Token信息(Access Id/Access Key/Security Token)
	 * 构造一个新的{@link OSSClient}对象。
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint。
	 * @param accessKeyId
	 *            STS提供的临时访问ID。
	 * @param secretAccessKey
	 *            STS提供的访问密钥。
	 * @param securityToken
	 * 			  STS提供的安全令牌。
	 */
	public OSSClient(String endpoint, String accessKeyId, String secretAccessKey, String securityToken) {
		this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken), null);
	}
	
	/**
	 * 使用指定的OSS Endpoint、阿里云颁发的Access Id/Access Key、客户端配置
	 * 构造一个新的{@link OSSClient}对象。
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint。
	 * @param accessKeyId
	 *            访问OSS的Access Key ID。
	 * @param secretAccessKey
	 *            访问OSS的Secret Access Key。
	 * @param config
	 *            客户端配置 {@link ClientConfiguration}。 如果为null则会使用默认配置。
	 */
	public OSSClient(String endpoint, String accessKeyId, String secretAccessKey, 
			ClientConfiguration config) {
		this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey), config);
	}
	
	/**
	 * 使用指定的OSS Endpoint、STS提供的临时Token信息(Access Id/Access Key/Security Token)、
	 * 客户端配置构造一个新的{@link OSSClient}对象。
	 * 
	 * @param endpoint
	 *            OSS服务的Endpoint。
	 * @param accessKeyId
	 *            STS提供的临时访问ID。
	 * @param secretAccessKey
	 *            STS提供的访问密钥。
	 * @param securityToken
	 * 			  STS提供的安全令牌。
	 * @param config
	 *            客户端配置 {@link ClientConfiguration}。 如果为null则会使用默认配置。
	 */
	public OSSClient(String endpoint, String accessKeyId, String secretAccessKey, String securityToken, 
			ClientConfiguration config) {
		this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken), config);
	}

	/**
	 * 使用默认配置及指定的{@link CredentialsProvider}与Endpoint构造一个新的{@link OSSClient}对象。
	 * @param endpoint OSS services的Endpoint。
	 * @param credsProvider Credentials提供者。
	 */
	public OSSClient(String endpoint, CredentialsProvider credsProvider) {
		this(endpoint, credsProvider, null);
	}
	
	/**
	 * 使用指定的{@link CredentialsProvider}、配置及Endpoint构造一个新的{@link OSSClient}对象。
	 * @param endpoint OSS services的Endpoint。
	 * @param credsProvider Credentials提供者。
	 * @param config client配置。
	 */
	public OSSClient(String endpoint, CredentialsProvider credsProvider, ClientConfiguration config) {
		this.credsProvider = credsProvider;
		this.serviceClient = new DefaultServiceClient(config);
		initOperations();
		setEndpoint(endpoint);
	}
	
	/**
	 * 获取OSS services的Endpoint。
	 * @return OSS services的Endpoint。
	 */
	public synchronized URI getEndpoint() {
		return URI.create(endpoint.toString());
	}
	
	/**
	 * 设置OSS services的Endpoint。
	 * @param endpoint OSS services的Endpoint。
	 */
	public synchronized void setEndpoint(String endpoint) {
		URI uri = toURI(endpoint);
		this.endpoint = uri;
		
		this.bucketOperation.setEndpoint(uri);
		this.objectOperation.setEndpoint(uri);
		this.multipartOperation.setEndpoint(uri);
		this.corsOperation.setEndpoint(uri);
	}
	
    private URI toURI(String endpoint) throws IllegalArgumentException {    	
        if (endpoint.contains("://") == false) {
        	ClientConfiguration conf = this.serviceClient.getClientConfiguration();
            endpoint = conf.getProtocol().toString() + "://" + endpoint;
        }

        try {
            return new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    private void initOperations() {
    	this.bucketOperation = new OSSBucketOperation(this.serviceClient, this.credsProvider);
    	this.objectOperation = new OSSObjectOperation(this.serviceClient, this.credsProvider);
    	this.multipartOperation = new OSSMultipartOperation(this.serviceClient, this.credsProvider);
    	this.corsOperation = new CORSOperation(this.serviceClient, this.credsProvider);
    }
	
	@Override
	public void switchCredentials(Credentials creds) {
		if (creds == null) {
			throw new IllegalArgumentException("creds should not be null.");
		}
		
		this.credsProvider.setCredentials(creds);
	}
	
	public CredentialsProvider getCredentialsProvider() {
		return this.credsProvider;
	}

	@Override
	public Bucket createBucket(String bucketName) 
			throws OSSException, ClientException {
		return this.createBucket(new CreateBucketRequest(bucketName));
	}

	@Override
	public Bucket createBucket(CreateBucketRequest createBucketRequest)
			throws OSSException, ClientException {
		return bucketOperation.createBucket(createBucketRequest);
	}

	@Override
	public void deleteBucket(String bucketName) throws OSSException, ClientException {
		bucketOperation.deleteBucket(bucketName);
	}

	@Override
    public List<Bucket> listBuckets() throws OSSException, ClientException {
        return bucketOperation.listBuckets();
    }

    @Override
    public BucketList listBuckets(ListBucketsRequest listBucketsRequest) 
    		throws OSSException, ClientException {
        return bucketOperation.listBuckets(listBucketsRequest);
    }

    @Override
    public BucketList listBuckets(String prefix, String marker, Integer maxKeys) 
    		throws OSSException, ClientException {
        return bucketOperation.listBuckets(new ListBucketsRequest(prefix, marker, maxKeys));
    }

	@Override
	public void setBucketAcl(String bucketName, CannedAccessControlList acl) 
			throws OSSException, ClientException {
		bucketOperation.setBucketAcl(bucketName, acl);
	}

	@Override
	public AccessControlList getBucketAcl(String bucketName) 
			throws OSSException, ClientException {
		return bucketOperation.getBucketAcl(bucketName);
	}
 	
 	@Override
 	public void setBucketReferer(String bucketName, BucketReferer referer) 
 			throws OSSException, ClientException {
 		bucketOperation.setBucketReferer(bucketName, referer);
 	}
  
 	@Override
 	public BucketReferer getBucketReferer(String bucketName) 
 			throws OSSException, ClientException {
 		return bucketOperation.getBucketReferer(bucketName);
 	}
 	
	@Override
	public String getBucketLocation(String bucketName) 
			throws OSSException, ClientException {
		return bucketOperation.getBucketLocation(bucketName);
	}

	@Override
	public boolean doesBucketExist(String bucketName) 
			throws OSSException, ClientException {
		return bucketOperation.doesBucketExists(bucketName);
	}

	/**
	 * 已过时。请使用{@link OSSClient#doesBucketExist(String)}。
	 */
	@Deprecated
	public boolean isBucketExist(String bucketName) 
			throws OSSException, ClientException {
		return this.doesBucketExist(bucketName);
	}

	@Override
	public ObjectListing listObjects(String bucketName) 
			throws OSSException, ClientException {
		return listObjects(new ListObjectsRequest(bucketName, null, null, null, null));
	}

	@Override
	public ObjectListing listObjects(String bucketName, String prefix) 
			throws OSSException, ClientException {
		return listObjects(new ListObjectsRequest(bucketName, prefix, null, null, null));
	}

	@Override
	public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) 
			throws OSSException, ClientException {
		return bucketOperation.listObjects(listObjectsRequest);
	}

	@Override
	public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
			throws OSSException, ClientException {
		return putObject(new PutObjectRequest(bucketName, key, input, metadata));
	}
	
	@Override
	public PutObjectResult putObject(String bucketName, String key, File file)
			throws OSSException, ClientException {
		return putObject(new PutObjectRequest(bucketName, key, file)
				.withMetadata(new ObjectMetadata()));
	}

	@Override
	public PutObjectResult putObject(PutObjectRequest putObjectRequest)
			throws OSSException, ClientException {
		return objectOperation.putObject(putObjectRequest);
	}
	
	@Override
	public PutObjectResult putObject(URL signedUrl, String filePath, Map<String, String> requestHeaders)
			throws OSSException, ClientException {
		return putObject(signedUrl, filePath, requestHeaders, false);
	}
	
	@Override
	public PutObjectResult putObject(URL signedUrl, String filePath, Map<String, String> requestHeaders,
			boolean useChunkEncoding) throws OSSException, ClientException {
		
		FileInputStream requestContent = null;
		try {
			File toUpload = new File(filePath);
			if (!checkFile(toUpload)) {
				throw new IllegalArgumentException("Illegal file path: " + filePath);
			}
			long fileSize = toUpload.length();
			requestContent = new FileInputStream(toUpload);
			
			return putObject(signedUrl, requestContent, fileSize, requestHeaders, useChunkEncoding);
		} catch (FileNotFoundException e) {
			throw new ClientException(e);
		} finally {
			if (requestContent != null) {
				try {
					requestContent.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength, 
			Map<String, String> requestHeaders) 
					throws OSSException, ClientException {
		return putObject(signedUrl, requestContent, contentLength, requestHeaders, false);
	}
	
	@Override
	public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
			Map<String, String> requestHeaders, boolean useChunkEncoding) 
					throws OSSException, ClientException {
		return objectOperation.putObject(signedUrl, requestContent, contentLength, requestHeaders, useChunkEncoding);
	}

	@Override
	public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) 
			throws OSSException, ClientException {
		return copyObject(new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey));
	}

	@Override
	public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) 
			throws OSSException, ClientException {
		return objectOperation.copyObject(copyObjectRequest);
	}

	@Override
	public OSSObject getObject(String bucketName, String key) 
			throws OSSException, ClientException {
		return this.getObject(new GetObjectRequest(bucketName, key));
	}

	@Override
	public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file) 
			throws OSSException, ClientException {
		return objectOperation.getObject(getObjectRequest, file);
	}

	@Override
	public OSSObject getObject(GetObjectRequest getObjectRequest) 
			throws OSSException, ClientException {
		return objectOperation.getObject(getObjectRequest);
	}

	@Override
	public OSSObject getObject(URL signedUrl, Map<String, String> requestHeaders) 
			throws OSSException, ClientException {
		GetObjectRequest getObjectRequest = new GetObjectRequest(signedUrl, requestHeaders);
		return objectOperation.getObject(getObjectRequest);
	}

	@Override
	public ObjectMetadata getObjectMetadata(String bucketName, String key)
			throws OSSException, ClientException {
		return objectOperation.getObjectMetadata(bucketName, key);
	}
	
	@Override
	public AppendObjectResult appendObject(AppendObjectRequest appendObjectRequest) 
			throws OSSException, ClientException {
		return objectOperation.appendObject(appendObjectRequest);
	}

	@Override
	public void deleteObject(String bucketName, String key) 
			throws OSSException, ClientException {
		objectOperation.deleteObject(bucketName, key);
	}
	
	@Override
	public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) 
			throws OSSException, ClientException {
		return objectOperation.deleteObjects(deleteObjectsRequest);
	}
	
	private void headObject(HeadObjectRequest headObjectRequest)
			throws OSSException, ClientException {
		objectOperation.headObject(headObjectRequest);
	}
	
	@Override
	public boolean doesObjectExist(String bucketName, String key)
			throws OSSException, ClientException {
		return doesObjectExist(new HeadObjectRequest(bucketName, key));
	}
	
	@Override
	public boolean doesObjectExist(HeadObjectRequest headObjectRequest)
    		throws OSSException, ClientException {
		try {
			headObject(headObjectRequest);
			return true;
		} catch (OSSException e) {
			if (e.getErrorCode() == OSSErrorCode.NO_SUCH_BUCKET 
					|| e.getErrorCode() == OSSErrorCode.NO_SUCH_KEY) {
				return false;
			}
			throw e;
		}
	}

	@Override
	public URL generatePresignedUrl(String bucketName, String key, Date expiration) 
			throws ClientException {
		return generatePresignedUrl(bucketName, key, expiration, HttpMethod.GET);
	}

	@Override
	public URL generatePresignedUrl(String bucketName, String key, Date expiration, HttpMethod method)
			throws ClientException {
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, key);
		request.setExpiration(expiration);
		request.setMethod(method);

		return generatePresignedUrl(request);
	}

	@Override
	public URL generatePresignedUrl(GeneratePresignedUrlRequest request) 
			throws ClientException {

		assertParameterNotNull(request, "request");
		
		String bucketName = request.getBucketName();
		if (request.getBucketName() == null) {
			throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("MustSetBucketName"));
		}
        ensureBucketNameValid(request.getBucketName());
        
		if (request.getExpiration() == null) {
			throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("MustSetExpiration"));
		}

		Credentials currentCreds = credsProvider.getCredentials();
		String accessId = currentCreds.getAccessKeyId();
		String accessKey = currentCreds.getSecretAccessKey();
		boolean useSecurityToken = currentCreds.useSecurityToken();
		HttpMethod method = request.getMethod() != null ? request.getMethod() : HttpMethod.GET;

		String expires = String.valueOf(request.getExpiration().getTime() / 1000L);
		String key = request.getKey();
		String resourcePath = OSSUtils.makeResourcePath(key);

		RequestMessage requestMessage = new RequestMessage();
		ClientConfiguration config = serviceClient.getClientConfiguration();
		requestMessage.setEndpoint(OSSUtils.determineFinalEndpoint(endpoint, bucketName, config));
		requestMessage.setMethod(method);
		requestMessage.setResourcePath(resourcePath);
        
		requestMessage.addHeader(HttpHeaders.DATE, expires);
        if (request.getContentType() != null && request.getContentType().trim() != "") {
            requestMessage.addHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
        }
        if (request.getContentMD5() != null && request.getContentMD5().trim() != "") {
            requestMessage.addHeader(HttpHeaders.CONTENT_MD5, request.getContentMD5());
        }
		for (Map.Entry<String, String> h : request.getUserMetadata().entrySet()) {
			requestMessage.addHeader(OSSHeaders.OSS_USER_METADATA_PREFIX + h.getKey(), h.getValue());
		}
		
		Map<String, String> responseHeaderParams = new HashMap<String, String>();
		populateResponseHeaderParameters(responseHeaderParams, request.getResponseHeaders());
		if (responseHeaderParams.size() > 0) {
			requestMessage.setParameters(responseHeaderParams);
		}

		if (request.getQueryParameter() != null && request.getQueryParameter().size() > 0) {
			for (Map.Entry<String, String> entry : request.getQueryParameter().entrySet()) {
				requestMessage.addParameter(entry.getKey(), entry.getValue());
			}
		}
		
		if (useSecurityToken) {
			requestMessage.addParameter(SECURITY_TOKEN, currentCreds.getSecurityToken());
		}

		String canonicalResource = "/" + ((bucketName != null) ? bucketName : "") 
				+ ((key != null ? "/" + key : ""));
		String canonicalString = SignUtils.buildCanonicalString(method.toString(), canonicalResource, 
				requestMessage, expires);
		String signature = ServiceSignature.create().computeSignature(accessKey, canonicalString);

		Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(HttpHeaders.EXPIRES, expires);
		params.put(OSS_ACCESS_KEY_ID, accessId);
		params.put(SIGNATURE, signature);
		params.putAll(requestMessage.getParameters());

		String queryString = HttpUtil.paramToQueryString(params, DEFAULT_CHARSET_NAME);

		/* Compse HTTP request uri. */
		String url = requestMessage.getEndpoint().toString();
		if (!url.endsWith("/")) {
			url += "/";
		}
		url += resourcePath + "?" + queryString;

		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new ClientException(e);
		}
	}

	@Override
	public void abortMultipartUpload(AbortMultipartUploadRequest request) 
			throws OSSException, ClientException {
		multipartOperation.abortMultipartUpload(request);
	}

	@Override
	public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request)
			throws OSSException, ClientException {
		return multipartOperation.completeMultipartUpload(request);
	}

	@Override
	public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request)
			throws OSSException, ClientException {
		return multipartOperation.initiateMultipartUpload(request);
	}

	@Override
	public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request) 
			throws OSSException, ClientException {
		return multipartOperation.listMultipartUploads(request);
	}

	@Override
	public PartListing listParts(ListPartsRequest request) 
			throws OSSException, ClientException {
		return multipartOperation.listParts(request);
	}

	@Override
	public UploadPartResult uploadPart(UploadPartRequest request) 
			throws OSSException, ClientException {
		return multipartOperation.uploadPart(request);
	}
	
	@Override
	public UploadPartCopyResult uploadPartCopy(UploadPartCopyRequest request) 
			throws OSSException, ClientException {
		return multipartOperation.uploadPartCopy(request);
	}

	@Override
	public void setBucketCORS(SetBucketCORSRequest request) 
			throws OSSException, ClientException {
		corsOperation.setBucketCORS(request);

	}

	@Override
	public List<CORSRule> getBucketCORSRules(String bucketName) 
			throws OSSException, ClientException {
		return corsOperation.getBucketCORSRules(bucketName);
	}

	@Override
	public void deleteBucketCORSRules(String bucketName) 
			throws OSSException, ClientException {
		corsOperation.deleteBucketCORS(bucketName);
	}

	@Override
	@Deprecated
	public ResponseMessage optionsObject(OptionsRequest request) 
			throws OSSException, ClientException {
		return corsOperation.optionsObject(request);
	}
    
	@Override
	public void setBucketLogging(SetBucketLoggingRequest request) 
			throws OSSException, ClientException {
		 bucketOperation.setBucketLogging(request);
	}
	
	@Override
	public BucketLoggingResult getBucketLogging(String bucketName)
			throws OSSException, ClientException {
		return bucketOperation.getBucketLogging(bucketName);
	}
	
	@Override
	public void deleteBucketLogging(String bucketName) 
			throws OSSException, ClientException {
		bucketOperation.deleteBucketLogging(bucketName);
	}

	@Override
	public void setBucketWebsite(SetBucketWebsiteRequest setBucketWebSiteRequest)
			throws OSSException, ClientException {
		bucketOperation.setBucketWebsite(setBucketWebSiteRequest);
	}

	@Override
	public BucketWebsiteResult getBucketWebsite(String bucketName)
			throws OSSException, ClientException {
		return	bucketOperation.getBucketWebsite(bucketName);
	}

	@Override
	public void deleteBucketWebsite(String bucketName) 
			throws OSSException, ClientException {
		bucketOperation.deleteBucketWebsite(bucketName);
	}
	
	@Override
	public String generatePostPolicy(Date expiration, PolicyConditions conds) {
		String formatedExpiration = DateUtil.formatIso8601Date(expiration);
		String jsonizedExpiration = String.format("\"expiration\":\"%s\"", formatedExpiration);
		String jsonizedConds = conds.jsonize();
        
        StringBuilder postPolicy = new StringBuilder();
        postPolicy.append("{");
        postPolicy.append(String.format("%s,%s", jsonizedExpiration, jsonizedConds));
        postPolicy.append("}");

        return postPolicy.toString();
	}
	
	@Override
	public String calculatePostSignature(String postPolicy) throws ClientException {
		try {
			byte[] binaryData = postPolicy.getBytes(DEFAULT_CHARSET_NAME);
			String encPolicy = BinaryUtil.toBase64String(binaryData);
			return ServiceSignature.create().computeSignature(
					credsProvider.getCredentials().getSecretAccessKey(), encPolicy);
		} catch (UnsupportedEncodingException ex) {
			throw new ClientException("Unsupported charset: " + ex.getMessage());
		}
	}
	
	@Override
	public void setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest)
			throws OSSException, ClientException {
		bucketOperation.setBucketLifecycle(setBucketLifecycleRequest);
	}
	
	@Override
	public List<LifecycleRule> getBucketLifecycle(String bucketName)
			throws OSSException, ClientException {
		return bucketOperation.getBucketLifecycle(bucketName);
	}

	@Override
	public void deleteBucketLifecycle(String bucketName)
			throws OSSException, ClientException {
		bucketOperation.deleteBucketLifecycle(bucketName);
	}

	@Override
	public void shutdown() {
		serviceClient.shutdown();
	}
}
