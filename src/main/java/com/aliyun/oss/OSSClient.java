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
import static com.aliyun.oss.common.utils.LogUtils.logException;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_CHARSET_NAME;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_OSS_ENDPOINT;
import static com.aliyun.oss.internal.OSSUtils.OSS_RESOURCE_MANAGER;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.auth.ServiceSignature;
import com.aliyun.oss.common.comm.*;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.internal.*;
import com.aliyun.oss.model.*;
import com.aliyun.oss.model.SetBucketCORSRequest.CORSRule;

/**
 * The entry point class of OSS that implements the OSS interface.
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
    private OSSUploadOperation uploadOperation;
    private OSSDownloadOperation downloadOperation;
    private LiveChannelOperation liveChannelOperation;

    /**
     * Uses the default OSS Endpoint(http://oss-cn-hangzhou.aliyuncs.com) and
     * Access Id/Access Key to create a new {@link OSSClient} instance.
     * 
     * @param accessKeyId
     *            Access Key ID.
     * @param secretAccessKey
     *            Secret Access Key.
     */
    @Deprecated
    public OSSClient(String accessKeyId, String secretAccessKey) {
        this(DEFAULT_OSS_ENDPOINT, new DefaultCredentialProvider(accessKeyId, secretAccessKey));
    }

    /**
     * Uses the specified OSS Endpoint and Access Id/Access Key to create a new
     * {@link OSSClient} instance.
     * 
     * @param endpoint
     *            OSS endpoint.
     * @param accessKeyId
     *            Access Key ID.
     * @param secretAccessKey
     *            Secret Access Key.
     */
    @Deprecated
    public OSSClient(String endpoint, String accessKeyId, String secretAccessKey) {
        this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey), null);
    }

    /**
     * Uses the specified OSS Endpoint、a security token from AliCloud STS and
     * Access Id/Access Key to create a new {@link OSSClient} instance.
     * 
     * @param endpoint
     *            OSS Endpoint.
     * @param accessKeyId
     *            Access Id from STS.
     * @param secretAccessKey
     *            Access Key from STS
     * @param securityToken
     *            Security Token from STS.
     */
    @Deprecated
    public OSSClient(String endpoint, String accessKeyId, String secretAccessKey, String securityToken) {
        this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken), null);
    }

    /**
     * Uses a specified OSS Endpoint、Access Id, Access Key、Client side
     * configuration to create a {@link OSSClient} instance.
     * 
     * @param endpoint
     *            OSS Endpoint。
     * @param accessKeyId
     *            Access Key ID。
     * @param secretAccessKey
     *            Secret Access Key。
     * @param config
     *            A {@link ClientConfiguration} instance. The method would use
     *            default configuration if it's null.
     */
    @Deprecated
    public OSSClient(String endpoint, String accessKeyId, String secretAccessKey, ClientConfiguration config) {
        this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey), config);
    }

    /**
     * Uses specified OSS Endpoint, the temporary (Access Id/Access Key/Security
     * Token) from STS and the client configuration to create a new
     * {@link OSSClient} instance.
     * 
     * @param endpoint
     *            OSS Endpoint。
     * @param accessKeyId
     *            Access Key Id provided by STS.
     * @param secretAccessKey
     *            Secret Access Key provided by STS.
     * @param securityToken
     *            Security token provided by STS.
     * @param config
     *            A {@link ClientConfiguration} instance. The method would use
     *            default configuration if it's null.
     */
    @Deprecated
    public OSSClient(String endpoint, String accessKeyId, String secretAccessKey, String securityToken,
            ClientConfiguration config) {
        this(endpoint, new DefaultCredentialProvider(accessKeyId, secretAccessKey, securityToken), config);
    }

    /**
     * Uses the specified {@link CredentialsProvider} and OSS Endpoint to create
     * a new {@link OSSClient} instance.
     * 
     * @param endpoint
     *            OSS services Endpoint。
     * @param credsProvider
     *            Credentials provider which has access key Id and access Key
     *            secret.
     */
    @Deprecated
    public OSSClient(String endpoint, CredentialsProvider credsProvider) {
        this(endpoint, credsProvider, null);
    }

    /**
     * Uses the specified {@link CredentialsProvider}, client configuration and
     * OSS endpoint to create a new {@link OSSClient} instance.
     * 
     * @param endpoint
     *            OSS services Endpoint.
     * @param credsProvider
     *            Credentials provider.
     * @param config
     *            client configuration.
     */
    public OSSClient(String endpoint, CredentialsProvider credsProvider, ClientConfiguration config) {
        this.credsProvider = credsProvider;
        config = config == null ? new ClientConfiguration() : config;
        if (config.isRequestTimeoutEnabled()) {
            this.serviceClient = new TimeoutServiceClient(config);
        } else {
            this.serviceClient = new DefaultServiceClient(config);
        }
        initOperations();
        setEndpoint(endpoint);
    }

    /**
     * Gets OSS services Endpoint.
     * 
     * @return OSS services Endpoint.
     */
    public synchronized URI getEndpoint() {
        return URI.create(endpoint.toString());
    }

    /**
     * Sets OSS services endpoint.
     * 
     * @param endpoint
     *            OSS services endpoint.
     */
    public synchronized void setEndpoint(String endpoint) {
        URI uri = toURI(endpoint);
        this.endpoint = uri;

        if (isIpOrLocalhost(uri)) {
            serviceClient.getClientConfiguration().setSLDEnabled(true);
        }

        this.bucketOperation.setEndpoint(uri);
        this.objectOperation.setEndpoint(uri);
        this.multipartOperation.setEndpoint(uri);
        this.corsOperation.setEndpoint(uri);
        this.liveChannelOperation.setEndpoint(uri);
    }

    /**
     * Checks if the uri is an IP or domain. If it's IP or local host, then it
     * will use secondary domain of Alibaba cloud. Otherwise, it will use domain
     * directly to access the OSS.
     * 
     * @param uri
     *            URI。
     */
    private boolean isIpOrLocalhost(URI uri) {
        if (uri.getHost().equals("localhost")) {
            return true;
        }

        InetAddress ia;
        try {
            ia = InetAddress.getByName(uri.getHost());
        } catch (UnknownHostException e) {
            return false;
        }

        if (uri.getHost().equals(ia.getHostAddress())) {
            return true;
        }

        return false;
    }

    private URI toURI(String endpoint) throws IllegalArgumentException {
        if (!endpoint.contains("://")) {
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
        this.uploadOperation = new OSSUploadOperation(this.multipartOperation);
        this.downloadOperation = new OSSDownloadOperation(objectOperation);
        this.liveChannelOperation = new LiveChannelOperation(this.serviceClient, this.credsProvider);
    }

    @Override
    public void switchCredentials(Credentials creds) {
        if (creds == null) {
            throw new IllegalArgumentException("creds should not be null.");
        }

        this.credsProvider.setCredentials(creds);
    }

    @Override
    public void switchSignatureVersion(SignVersion signatureVersion) {
        if (signatureVersion == null) {
            throw new IllegalArgumentException("signatureVersion should not be null.");
        }

        this.getClientConfiguration().setSignatureVersion(signatureVersion);
    }

    public CredentialsProvider getCredentialsProvider() {
        return this.credsProvider;
    }

    public ClientConfiguration getClientConfiguration() {
        return serviceClient.getClientConfiguration();
    }

    @Override
    public Bucket createBucket(String bucketName) throws OSSException, ClientException {
        return this.createBucket(new CreateBucketRequest(bucketName));
    }

    @Override
    public Bucket createBucket(CreateBucketRequest createBucketRequest) throws OSSException, ClientException {
        return bucketOperation.createBucket(createBucketRequest);
    }

    @Override
    public void deleteBucket(String bucketName) throws OSSException, ClientException {
        this.deleteBucket(new GenericRequest(bucketName));
    }

    @Override
    public void deleteBucket(GenericRequest genericRequest) throws OSSException, ClientException {
        bucketOperation.deleteBucket(genericRequest);
    }

    @Override
    public List<Bucket> listBuckets() throws OSSException, ClientException {
        return bucketOperation.listBuckets();
    }

    @Override
    public BucketList listBuckets(ListBucketsRequest listBucketsRequest) throws OSSException, ClientException {
        return bucketOperation.listBuckets(listBucketsRequest);
    }

    @Override
    public BucketList listBuckets(String prefix, String marker, Integer maxKeys) throws OSSException, ClientException {
        return bucketOperation.listBuckets(new ListBucketsRequest(prefix, marker, maxKeys));
    }

    @Override
    public void setBucketAcl(String bucketName, CannedAccessControlList cannedACL)
            throws OSSException, ClientException {
        this.setBucketAcl(new SetBucketAclRequest(bucketName, cannedACL));
    }

    @Override
    public void setBucketAcl(SetBucketAclRequest setBucketAclRequest) throws OSSException, ClientException {
        bucketOperation.setBucketAcl(setBucketAclRequest);
    }

    @Override
    public AccessControlList getBucketAcl(String bucketName) throws OSSException, ClientException {
        return this.getBucketAcl(new GenericRequest(bucketName));
    }

    @Override
    public AccessControlList getBucketAcl(GenericRequest genericRequest) throws OSSException, ClientException {
        return bucketOperation.getBucketAcl(genericRequest);
    }

    @Override
    public BucketMetadata getBucketMetadata(String bucketName) throws OSSException, ClientException {
        return this.getBucketMetadata(new GenericRequest(bucketName));
    }

    @Override
    public BucketMetadata getBucketMetadata(GenericRequest genericRequest) throws OSSException, ClientException {
        return bucketOperation.getBucketMetadata(genericRequest);
    }

    @Override
    public void setBucketReferer(String bucketName, BucketReferer referer) throws OSSException, ClientException {
        this.setBucketReferer(new SetBucketRefererRequest(bucketName, referer));
    }

    @Override
    public void setBucketReferer(SetBucketRefererRequest setBucketRefererRequest) throws OSSException, ClientException {
        bucketOperation.setBucketReferer(setBucketRefererRequest);
    }

    @Override
    public BucketReferer getBucketReferer(String bucketName) throws OSSException, ClientException {
        return this.getBucketReferer(new GenericRequest(bucketName));
    }

    @Override
    public BucketReferer getBucketReferer(GenericRequest genericRequest) throws OSSException, ClientException {
        return bucketOperation.getBucketReferer(genericRequest);
    }

    @Override
    public String getBucketLocation(String bucketName) throws OSSException, ClientException {
        return this.getBucketLocation(new GenericRequest(bucketName));
    }

    @Override
    public String getBucketLocation(GenericRequest genericRequest) throws OSSException, ClientException {
        return bucketOperation.getBucketLocation(genericRequest);
    }

    @Override
    public boolean doesBucketExist(String bucketName) throws OSSException, ClientException {
        return this.doesBucketExist(new GenericRequest(bucketName));
    }

    @Override
    public boolean doesBucketExist(GenericRequest genericRequest) throws OSSException, ClientException {
        return bucketOperation.doesBucketExists(genericRequest);
    }

    /**
     * Deprecated. Please use {@link OSSClient#doesBucketExist(String)} instead.
     */
    @Deprecated
    public boolean isBucketExist(String bucketName) throws OSSException, ClientException {
        return this.doesBucketExist(bucketName);
    }

    @Override
    public ObjectListing listObjects(String bucketName) throws OSSException, ClientException {
        return listObjects(new ListObjectsRequest(bucketName, null, null, null, null));
    }

    @Override
    public ObjectListing listObjects(String bucketName, String prefix) throws OSSException, ClientException {
        return listObjects(new ListObjectsRequest(bucketName, prefix, null, null, null));
    }

    @Override
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws OSSException, ClientException {
        return bucketOperation.listObjects(listObjectsRequest);
    }
    
	@Override
	public VersionListing listVersions(String bucketName, String prefix) throws OSSException, ClientException {
        return listVersions(new ListVersionsRequest(bucketName, prefix, null, null, null, null));
	}

    @Override
    public VersionListing listVersions(String bucketName, String prefix, String keyMarker, String versionIdMarker,
        String delimiter, Integer maxResults) throws OSSException, ClientException {
        ListVersionsRequest request = new ListVersionsRequest()
            .withBucketName(bucketName)
            .withPrefix(prefix)
            .withDelimiter(delimiter)
            .withKeyMarker(keyMarker)
            .withVersionIdMarker(versionIdMarker)
            .withMaxResults(maxResults);
        return listVersions(request);
    }

    @Override
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest) throws OSSException, ClientException {
        return bucketOperation.listVersions(listVersionsRequest);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input)
            throws OSSException, ClientException {
        return putObject(bucketName, key, input, null);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws OSSException, ClientException {
        return putObject(new PutObjectRequest(bucketName, key, input, metadata));
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, File file, ObjectMetadata metadata)
            throws OSSException, ClientException {
        return putObject(new PutObjectRequest(bucketName, key, file, metadata));
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, File file) throws OSSException, ClientException {
        return putObject(bucketName, key, file, null);
    }

    @Override
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws OSSException, ClientException {
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
            Map<String, String> requestHeaders) throws OSSException, ClientException {
        return putObject(signedUrl, requestContent, contentLength, requestHeaders, false);
    }

    @Override
    public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
            Map<String, String> requestHeaders, boolean useChunkEncoding) throws OSSException, ClientException {
        return objectOperation.putObject(signedUrl, requestContent, contentLength, requestHeaders, useChunkEncoding);
    }

    @Override
    public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
            String destinationKey) throws OSSException, ClientException {
        return copyObject(new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey));
    }

    @Override
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws OSSException, ClientException {
        return objectOperation.copyObject(copyObjectRequest);
    }

    @Override
    public OSSObject getObject(String bucketName, String key) throws OSSException, ClientException {
        return this.getObject(new GetObjectRequest(bucketName, key));
    }

    @Override
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file) throws OSSException, ClientException {
        return objectOperation.getObject(getObjectRequest, file);
    }

    @Override
    public OSSObject getObject(GetObjectRequest getObjectRequest) throws OSSException, ClientException {
        return objectOperation.getObject(getObjectRequest);
    }

    @Override
    public OSSObject getObject(URL signedUrl, Map<String, String> requestHeaders) throws OSSException, ClientException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(signedUrl, requestHeaders);
        return objectOperation.getObject(getObjectRequest);
    }

    @Override
    public OSSObject selectObject(SelectObjectRequest selectObjectRequest) throws OSSException, ClientException {
        return objectOperation.selectObject(selectObjectRequest);
    }

    @Override
    public SimplifiedObjectMeta getSimplifiedObjectMeta(String bucketName, String key)
            throws OSSException, ClientException {
        return this.getSimplifiedObjectMeta(new GenericRequest(bucketName, key));
    }

    @Override
    public SimplifiedObjectMeta getSimplifiedObjectMeta(GenericRequest genericRequest)
            throws OSSException, ClientException {
        return this.objectOperation.getSimplifiedObjectMeta(genericRequest);
    }

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String key) throws OSSException, ClientException {
        return this.getObjectMetadata(new GenericRequest(bucketName, key));
    }

    @Override
    public SelectObjectMetadata createSelectObjectMetadata(CreateSelectObjectMetadataRequest createSelectObjectMetadataRequest) throws OSSException, ClientException {
        return objectOperation.createSelectObjectMetadata(createSelectObjectMetadataRequest);
    }
    
    @Override
    public ObjectMetadata getObjectMetadata(GenericRequest genericRequest) throws OSSException, ClientException {
        return objectOperation.getObjectMetadata(genericRequest);
    }

    @Override
    public ObjectMetadata headObject(String bucketName, String key) throws OSSException, ClientException {
        return this.headObject(new HeadObjectRequest(bucketName, key));
    }

    @Override
    public ObjectMetadata headObject(HeadObjectRequest headObjectRequest) throws OSSException, ClientException {
        return objectOperation.headObject(headObjectRequest);
    }

    @Override
    public AppendObjectResult appendObject(AppendObjectRequest appendObjectRequest)
            throws OSSException, ClientException {
        return objectOperation.appendObject(appendObjectRequest);
    }

    @Override
    public void deleteObject(String bucketName, String key) throws OSSException, ClientException {
        this.deleteObject(new GenericRequest(bucketName, key));
    }

    @Override
    public void deleteObject(GenericRequest genericRequest) throws OSSException, ClientException {
        objectOperation.deleteObject(genericRequest);
    }
    
    @Override
    public void deleteVersion(String bucketName, String key, String versionId) throws OSSException, ClientException {
        deleteVersion(new DeleteVersionRequest(bucketName, key, versionId));
    }

    @Override
    public void deleteVersion(DeleteVersionRequest deleteVersionRequest) throws OSSException, ClientException {
        objectOperation.deleteVersion(deleteVersionRequest);
    }

    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest)
            throws OSSException, ClientException {
        return objectOperation.deleteObjects(deleteObjectsRequest);
    }
    
    @Override
    public DeleteVersionsResult deleteVersions(DeleteVersionsRequest deleteVersionsRequest)
        throws OSSException, ClientException {
        return objectOperation.deleteVersions(deleteVersionsRequest);
    }

    @Override
    public boolean doesObjectExist(String bucketName, String key) throws OSSException, ClientException {
        return doesObjectExist(new GenericRequest(bucketName, key));
    }

    @Override
    public boolean doesObjectExist(String bucketName, String key, boolean isOnlyInOSS) {
        if (isOnlyInOSS) {
            return doesObjectExist(bucketName, key);
        } else {
            return objectOperation.doesObjectExistWithRedirect(new GenericRequest(bucketName, key));
        }
    }

    @Deprecated
    @Override
    public boolean doesObjectExist(HeadObjectRequest headObjectRequest) throws OSSException, ClientException {
        return doesObjectExist(new GenericRequest(headObjectRequest.getBucketName(), headObjectRequest.getKey()));
    }

    @Override
    public boolean doesObjectExist(GenericRequest genericRequest) throws OSSException, ClientException {
        return objectOperation.doesObjectExist(genericRequest);
    }

    @Override
    public boolean doesObjectExist(GenericRequest genericRequest, boolean isOnlyInOSS) throws OSSException, ClientException {
    	if (isOnlyInOSS) {
    	    return objectOperation.doesObjectExist(genericRequest);	
    	} else {
    	    return objectOperation.doesObjectExistWithRedirect(genericRequest);
    	}
    }

    @Override
    public void setObjectAcl(String bucketName, String key, CannedAccessControlList cannedACL)
            throws OSSException, ClientException {
        this.setObjectAcl(new SetObjectAclRequest(bucketName, key, cannedACL));
    }

    @Override
    public void setObjectAcl(SetObjectAclRequest setObjectAclRequest) throws OSSException, ClientException {
        objectOperation.setObjectAcl(setObjectAclRequest);
    }

    @Override
    public ObjectAcl getObjectAcl(String bucketName, String key) throws OSSException, ClientException {
        return this.getObjectAcl(new GenericRequest(bucketName, key));
    }

    @Override
    public ObjectAcl getObjectAcl(GenericRequest genericRequest) throws OSSException, ClientException {
        return objectOperation.getObjectAcl(genericRequest);
    }

    @Override
    public RestoreObjectResult restoreObject(String bucketName, String key) throws OSSException, ClientException {
        return this.restoreObject(new GenericRequest(bucketName, key));
    }

    @Override
    public RestoreObjectResult restoreObject(GenericRequest genericRequest) throws OSSException, ClientException {
        return objectOperation.restoreObject(genericRequest);
    }
    
    @Override
    public void setObjectTagging(String bucketName, String key, Map<String, String> tags)
        throws OSSException, ClientException {
        this.setObjectTagging(new SetObjectTaggingRequest(bucketName, key, tags));
    }

    @Override
    public void setObjectTagging(String bucketName, String key, TagSet tagSet) throws OSSException, ClientException {
        this.setObjectTagging(new SetObjectTaggingRequest(bucketName, key, tagSet));
    }

    @Override
    public void setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) throws OSSException, ClientException {
        objectOperation.setObjectTagging(setObjectTaggingRequest);
    }

    @Override
    public TagSet getObjectTagging(String bucketName, String key) throws OSSException, ClientException {
        return this.getObjectTagging(new GenericRequest(bucketName, key));
    }

    @Override
    public TagSet getObjectTagging(GenericRequest genericRequest) throws OSSException, ClientException {
        return objectOperation.getObjectTagging(genericRequest);
    }

    @Override
    public void deleteObjectTagging(String bucketName, String key) throws OSSException, ClientException {
        this.deleteObjectTagging(new GenericRequest(bucketName, key));
    }

    @Override
    public void deleteObjectTagging(GenericRequest genericRequest) throws OSSException, ClientException {
        objectOperation.deleteObjectTagging(genericRequest);
    }

    @Override
    public URL generatePresignedUrl(String bucketName, String key, Date expiration) throws ClientException {
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
    public URL generatePresignedUrl(GeneratePresignedUrlRequest request) throws ClientException {

        assertParameterNotNull(request, "request");

        if (request.getBucketName() == null) {
            throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("MustSetBucketName"));
        }
        ensureBucketNameValid(request.getBucketName());

        if (request.getExpiration() == null) {
            throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getString("MustSetExpiration"));
        }
        String url;

        if (serviceClient.getClientConfiguration().getSignatureVersion() != null && serviceClient.getClientConfiguration().getSignatureVersion() == SignVersion.V2) {
            url = SignV2Utils.buildSignedURL(request, credsProvider.getCredentials(), serviceClient.getClientConfiguration(), endpoint);
        } else {
            url = SignUtils.buildSignedURL(request, credsProvider.getCredentials(), serviceClient.getClientConfiguration(), endpoint);
        }

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new ClientException(e);
        }
    }

    @Override
    public void abortMultipartUpload(AbortMultipartUploadRequest request) throws OSSException, ClientException {
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
    public PartListing listParts(ListPartsRequest request) throws OSSException, ClientException {
        return multipartOperation.listParts(request);
    }

    @Override
    public UploadPartResult uploadPart(UploadPartRequest request) throws OSSException, ClientException {
        return multipartOperation.uploadPart(request);
    }

    @Override
    public UploadPartCopyResult uploadPartCopy(UploadPartCopyRequest request) throws OSSException, ClientException {
        return multipartOperation.uploadPartCopy(request);
    }

    @Override
    public void setBucketCORS(SetBucketCORSRequest request) throws OSSException, ClientException {
        corsOperation.setBucketCORS(request);
    }

    @Override
    public List<CORSRule> getBucketCORSRules(String bucketName) throws OSSException, ClientException {
        return this.getBucketCORSRules(new GenericRequest(bucketName));
    }

    @Override
    public List<CORSRule> getBucketCORSRules(GenericRequest genericRequest) throws OSSException, ClientException {
        return corsOperation.getBucketCORSRules(genericRequest);
    }

    @Override
    public void deleteBucketCORSRules(String bucketName) throws OSSException, ClientException {
        this.deleteBucketCORSRules(new GenericRequest(bucketName));
    }

    @Override
    public void deleteBucketCORSRules(GenericRequest genericRequest) throws OSSException, ClientException {
        corsOperation.deleteBucketCORS(genericRequest);
    }

    @Override
    public ResponseMessage optionsObject(OptionsRequest request) throws OSSException, ClientException {
        return corsOperation.optionsObject(request);
    }

    @Override
    public void setBucketLogging(SetBucketLoggingRequest request) throws OSSException, ClientException {
        bucketOperation.setBucketLogging(request);
    }

    @Override
    public BucketLoggingResult getBucketLogging(String bucketName) throws OSSException, ClientException {
        return this.getBucketLogging(new GenericRequest(bucketName));
    }

    @Override
    public BucketLoggingResult getBucketLogging(GenericRequest genericRequest) throws OSSException, ClientException {
        return bucketOperation.getBucketLogging(genericRequest);
    }

    @Override
    public void deleteBucketLogging(String bucketName) throws OSSException, ClientException {
        this.deleteBucketLogging(new GenericRequest(bucketName));
    }

    @Override
    public void deleteBucketLogging(GenericRequest genericRequest) throws OSSException, ClientException {
        bucketOperation.deleteBucketLogging(genericRequest);
    }

    @Override
    public void putBucketImage(PutBucketImageRequest request) throws OSSException, ClientException {
        bucketOperation.putBucketImage(request);
    }

    @Override
    public GetBucketImageResult getBucketImage(String bucketName) throws OSSException, ClientException {
        return bucketOperation.getBucketImage(bucketName, new GenericRequest());
    }

    @Override
    public GetBucketImageResult getBucketImage(String bucketName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        return bucketOperation.getBucketImage(bucketName, genericRequest);
    }

    @Override
    public void deleteBucketImage(String bucketName) throws OSSException, ClientException {
        bucketOperation.deleteBucketImage(bucketName, new GenericRequest());
    }

    @Override
    public void deleteBucketImage(String bucketName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        bucketOperation.deleteBucketImage(bucketName, genericRequest);
    }

    @Override
    public void putImageStyle(PutImageStyleRequest putImageStyleRequest) throws OSSException, ClientException {
        bucketOperation.putImageStyle(putImageStyleRequest);
    }

    @Override
    public void deleteImageStyle(String bucketName, String styleName) throws OSSException, ClientException {
        bucketOperation.deleteImageStyle(bucketName, styleName, new GenericRequest());
    }

    @Override
    public void deleteImageStyle(String bucketName, String styleName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        bucketOperation.deleteImageStyle(bucketName, styleName, genericRequest);
    }

    @Override
    public GetImageStyleResult getImageStyle(String bucketName, String styleName) throws OSSException, ClientException {
        return bucketOperation.getImageStyle(bucketName, styleName, new GenericRequest());
    }

    @Override
    public GetImageStyleResult getImageStyle(String bucketName, String styleName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        return bucketOperation.getImageStyle(bucketName, styleName, genericRequest);
    }

    @Override
    public List<Style> listImageStyle(String bucketName) throws OSSException, ClientException {
        return bucketOperation.listImageStyle(bucketName, new GenericRequest());
    }

    @Override
    public List<Style> listImageStyle(String bucketName, GenericRequest genericRequest)
            throws OSSException, ClientException {
        return bucketOperation.listImageStyle(bucketName, genericRequest);
    }

    @Override
    public void setBucketProcess(SetBucketProcessRequest setBucketProcessRequest) throws OSSException, ClientException {
        bucketOperation.setBucketProcess(setBucketProcessRequest);
    }

    @Override
    public BucketProcess getBucketProcess(String bucketName) throws OSSException, ClientException {
        return this.getBucketProcess(new GenericRequest(bucketName));
    }

    @Override
    public BucketProcess getBucketProcess(GenericRequest genericRequest) throws OSSException, ClientException {
        return bucketOperation.getBucketProcess(genericRequest);
    }

    @Override
    public void setBucketWebsite(SetBucketWebsiteRequest setBucketWebSiteRequest) throws OSSException, ClientException {
        bucketOperation.setBucketWebsite(setBucketWebSiteRequest);
    }

    @Override
    public BucketWebsiteResult getBucketWebsite(String bucketName) throws OSSException, ClientException {
        return this.getBucketWebsite(new GenericRequest(bucketName));
    }

    @Override
    public BucketWebsiteResult getBucketWebsite(GenericRequest genericRequest) throws OSSException, ClientException {
        return bucketOperation.getBucketWebsite(genericRequest);
    }

    @Override
    public void deleteBucketWebsite(String bucketName) throws OSSException, ClientException {
        this.deleteBucketWebsite(new GenericRequest(bucketName));
    }

    @Override
    public void deleteBucketWebsite(GenericRequest genericRequest) throws OSSException, ClientException {
        bucketOperation.deleteBucketWebsite(genericRequest);
    }
    
    @Override
    public BucketVersioningConfiguration getBucketVersioning(String bucketName) throws OSSException, ClientException {
        return getBucketVersioning(new GenericRequest(bucketName));
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioning(GenericRequest genericRequest)
        throws OSSException, ClientException {
        return bucketOperation.getBucketVersioning(genericRequest);
    }

    @Override
    public void setBucketVersioning(SetBucketVersioningRequest setBucketVersioningRequest)
        throws OSSException, ClientException {
        bucketOperation.setBucketVersioning(setBucketVersioningRequest);
    }

    @Override
    public String generatePostPolicy(Date expiration, PolicyConditions conds) {
        String formatedExpiration = DateUtil.formatIso8601Date(expiration);
        String jsonizedExpiration = String.format("\"expiration\":\"%s\"", formatedExpiration);
        String jsonizedConds = conds.jsonize();

        StringBuilder postPolicy = new StringBuilder();
        postPolicy.append(String.format("{%s,%s}", jsonizedExpiration, jsonizedConds));

        return postPolicy.toString();
    }

    @Override
    public String calculatePostSignature(String postPolicy) throws ClientException {
        try {
            byte[] binaryData = postPolicy.getBytes(DEFAULT_CHARSET_NAME);
            String encPolicy = BinaryUtil.toBase64String(binaryData);
            return ServiceSignature.create().computeSignature(credsProvider.getCredentials().getSecretAccessKey(),
                    encPolicy);
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
    public List<LifecycleRule> getBucketLifecycle(String bucketName) throws OSSException, ClientException {
        return this.getBucketLifecycle(new GenericRequest(bucketName));
    }

    @Override
    public List<LifecycleRule> getBucketLifecycle(GenericRequest genericRequest) throws OSSException, ClientException {
        return bucketOperation.getBucketLifecycle(genericRequest);
    }

    @Override
    public void deleteBucketLifecycle(String bucketName) throws OSSException, ClientException {
        this.deleteBucketLifecycle(new GenericRequest(bucketName));
    }

    @Override
    public void deleteBucketLifecycle(GenericRequest genericRequest) throws OSSException, ClientException {
        bucketOperation.deleteBucketLifecycle(genericRequest);
    }

    @Override
    public void setBucketTagging(String bucketName, Map<String, String> tags) throws OSSException, ClientException {
        this.setBucketTagging(new SetBucketTaggingRequest(bucketName, tags));
    }

    @Override
    public void setBucketTagging(String bucketName, TagSet tagSet) throws OSSException, ClientException {
        this.setBucketTagging(new SetBucketTaggingRequest(bucketName, tagSet));
    }

    @Override
    public void setBucketTagging(SetBucketTaggingRequest setBucketTaggingRequest) throws OSSException, ClientException {
        this.bucketOperation.setBucketTagging(setBucketTaggingRequest);
    }

    @Override
    public TagSet getBucketTagging(String bucketName) throws OSSException, ClientException {
        return this.getBucketTagging(new GenericRequest(bucketName));
    }

    @Override
    public TagSet getBucketTagging(GenericRequest genericRequest) throws OSSException, ClientException {
        return this.bucketOperation.getBucketTagging(genericRequest);
    }

    @Override
    public void deleteBucketTagging(String bucketName) throws OSSException, ClientException {
        this.deleteBucketTagging(new GenericRequest(bucketName));
    }

    @Override
    public void deleteBucketTagging(GenericRequest genericRequest) throws OSSException, ClientException {
        this.bucketOperation.deleteBucketTagging(genericRequest);
    }

    @Override
    public void addBucketReplication(AddBucketReplicationRequest addBucketReplicationRequest)
            throws OSSException, ClientException {
        this.bucketOperation.addBucketReplication(addBucketReplicationRequest);
    }

    @Override
    public List<ReplicationRule> getBucketReplication(String bucketName) throws OSSException, ClientException {
        return this.getBucketReplication(new GenericRequest(bucketName));
    }

    @Override
    public List<ReplicationRule> getBucketReplication(GenericRequest genericRequest)
            throws OSSException, ClientException {
        return this.bucketOperation.getBucketReplication(genericRequest);
    }

    @Override
    public void deleteBucketReplication(String bucketName, String replicationRuleID)
            throws OSSException, ClientException {
        this.deleteBucketReplication(new DeleteBucketReplicationRequest(bucketName, replicationRuleID));
    }

    @Override
    public void deleteBucketReplication(DeleteBucketReplicationRequest deleteBucketReplicationRequest)
            throws OSSException, ClientException {
        this.bucketOperation.deleteBucketReplication(deleteBucketReplicationRequest);
    }

    @Override
    public BucketReplicationProgress getBucketReplicationProgress(String bucketName, String replicationRuleID)
            throws OSSException, ClientException {
        return this
                .getBucketReplicationProgress(new GetBucketReplicationProgressRequest(bucketName, replicationRuleID));
    }

    @Override
    public BucketReplicationProgress getBucketReplicationProgress(
            GetBucketReplicationProgressRequest getBucketReplicationProgressRequest)
            throws OSSException, ClientException {
        return this.bucketOperation.getBucketReplicationProgress(getBucketReplicationProgressRequest);
    }

    @Override
    public List<String> getBucketReplicationLocation(String bucketName) throws OSSException, ClientException {
        return this.getBucketReplicationLocation(new GenericRequest(bucketName));
    }

    @Override
    public List<String> getBucketReplicationLocation(GenericRequest genericRequest)
            throws OSSException, ClientException {
        return this.bucketOperation.getBucketReplicationLocation(genericRequest);
    }

    @Override
    public void addBucketCname(AddBucketCnameRequest addBucketCnameRequest) throws OSSException, ClientException {
        this.bucketOperation.addBucketCname(addBucketCnameRequest);
    }

    @Override
    public List<CnameConfiguration> getBucketCname(String bucketName) throws OSSException, ClientException {
        return this.getBucketCname(new GenericRequest(bucketName));
    }

    @Override
    public List<CnameConfiguration> getBucketCname(GenericRequest genericRequest) throws OSSException, ClientException {
        return this.bucketOperation.getBucketCname(genericRequest);
    }

    @Override
    public void deleteBucketCname(String bucketName, String domain) throws OSSException, ClientException {
        this.deleteBucketCname(new DeleteBucketCnameRequest(bucketName, domain));
    }

    @Override
    public void deleteBucketCname(DeleteBucketCnameRequest deleteBucketCnameRequest)
            throws OSSException, ClientException {
        this.bucketOperation.deleteBucketCname(deleteBucketCnameRequest);
    }

    @Override
    public BucketInfo getBucketInfo(String bucketName) throws OSSException, ClientException {
        return this.getBucketInfo(new GenericRequest(bucketName));
    }

    @Override
    public BucketInfo getBucketInfo(GenericRequest genericRequest) throws OSSException, ClientException {
        return this.bucketOperation.getBucketInfo(genericRequest);
    }

    @Override
    public BucketStat getBucketStat(String bucketName) throws OSSException, ClientException {
        return this.getBucketStat(new GenericRequest(bucketName));
    }

    @Override
    public BucketStat getBucketStat(GenericRequest genericRequest) throws OSSException, ClientException {
        return this.bucketOperation.getBucketStat(genericRequest);
    }

    @Override
    public void setBucketStorageCapacity(String bucketName, UserQos userQos) throws OSSException, ClientException {
        this.setBucketStorageCapacity(new SetBucketStorageCapacityRequest(bucketName).withUserQos(userQos));
    }

    @Override
    public void setBucketStorageCapacity(SetBucketStorageCapacityRequest setBucketStorageCapacityRequest)
            throws OSSException, ClientException {
        this.bucketOperation.setBucketStorageCapacity(setBucketStorageCapacityRequest);
    }

    @Override
    public UserQos getBucketStorageCapacity(String bucketName) throws OSSException, ClientException {
        return this.getBucketStorageCapacity(new GenericRequest(bucketName));
    }

    @Override
    public UserQos getBucketStorageCapacity(GenericRequest genericRequest) throws OSSException, ClientException {
        return this.bucketOperation.getBucketStorageCapacity(genericRequest);
    }

    @Override
    public void setBucketEncryption(SetBucketEncryptionRequest setBucketEncryptionRequest)
        throws OSSException, ClientException {
        this.bucketOperation.setBucketEncryption(setBucketEncryptionRequest);
    }

    @Override
    public ServerSideEncryptionConfiguration getBucketEncryption(String bucketName)
        throws OSSException, ClientException {
        return this.getBucketEncryption(new GenericRequest(bucketName));
    }

    @Override
    public ServerSideEncryptionConfiguration getBucketEncryption(GenericRequest genericRequest)
        throws OSSException, ClientException {
        return this.bucketOperation.getBucketEncryption(genericRequest);
    }

    @Override
    public void deleteBucketEncryption(String bucketName) throws OSSException, ClientException {
        this.deleteBucketEncryption(new GenericRequest(bucketName));
    }

    @Override
    public void deleteBucketEncryption(GenericRequest genericRequest) throws OSSException, ClientException {
        this.bucketOperation.deleteBucketEncryption(genericRequest);
    }

    @Override
    public void setBucketPolicy(String bucketName,  String policyText) throws OSSException, ClientException {
        this.setBucketPolicy(new SetBucketPolicyRequest(bucketName, policyText));
    }

    @Override
    public void setBucketPolicy(SetBucketPolicyRequest setBucketPolicyRequest) throws OSSException, ClientException {
        this.bucketOperation.setBucketPolicy(setBucketPolicyRequest);
    }

    @Override
    public GetBucketPolicyResult getBucketPolicy(GenericRequest genericRequest) throws OSSException, ClientException {
        return this.bucketOperation.getBucketPolicy(genericRequest);
    }

    @Override
    public GetBucketPolicyResult getBucketPolicy(String bucketName) throws OSSException, ClientException {
        return this.getBucketPolicy(new GenericRequest(bucketName));
    }

    @Override
    public void deleteBucketPolicy(GenericRequest genericRequest) throws OSSException, ClientException {
        this.bucketOperation.deleteBucketPolicy(genericRequest);
    }

    @Override
    public void deleteBucketPolicy(String bucketName) throws OSSException, ClientException {
        this.deleteBucketPolicy(new GenericRequest(bucketName));
    }

    @Override
    public UploadFileResult uploadFile(UploadFileRequest uploadFileRequest) throws Throwable {
        return this.uploadOperation.uploadFile(uploadFileRequest);
    }

    @Override
    public DownloadFileResult downloadFile(DownloadFileRequest downloadFileRequest) throws Throwable {
        return downloadOperation.downloadFile(downloadFileRequest);
    }

    @Override
    public CreateLiveChannelResult createLiveChannel(CreateLiveChannelRequest createLiveChannelRequest)
            throws OSSException, ClientException {
        return liveChannelOperation.createLiveChannel(createLiveChannelRequest);
    }

    @Override
    public void setLiveChannelStatus(String bucketName, String liveChannel, LiveChannelStatus status)
            throws OSSException, ClientException {
        this.setLiveChannelStatus(new SetLiveChannelRequest(bucketName, liveChannel, status));
    }

    @Override
    public void setLiveChannelStatus(SetLiveChannelRequest setLiveChannelRequest) throws OSSException, ClientException {
        liveChannelOperation.setLiveChannelStatus(setLiveChannelRequest);
    }

    @Override
    public LiveChannelInfo getLiveChannelInfo(String bucketName, String liveChannel)
            throws OSSException, ClientException {
        return this.getLiveChannelInfo(new LiveChannelGenericRequest(bucketName, liveChannel));
    }

    @Override
    public LiveChannelInfo getLiveChannelInfo(LiveChannelGenericRequest liveChannelGenericRequest)
            throws OSSException, ClientException {
        return liveChannelOperation.getLiveChannelInfo(liveChannelGenericRequest);
    }

    @Override
    public LiveChannelStat getLiveChannelStat(String bucketName, String liveChannel)
            throws OSSException, ClientException {
        return this.getLiveChannelStat(new LiveChannelGenericRequest(bucketName, liveChannel));
    }

    @Override
    public LiveChannelStat getLiveChannelStat(LiveChannelGenericRequest liveChannelGenericRequest)
            throws OSSException, ClientException {
        return liveChannelOperation.getLiveChannelStat(liveChannelGenericRequest);
    }

    @Override
    public void deleteLiveChannel(String bucketName, String liveChannel) throws OSSException, ClientException {
        this.deleteLiveChannel(new LiveChannelGenericRequest(bucketName, liveChannel));
    }

    @Override
    public void deleteLiveChannel(LiveChannelGenericRequest liveChannelGenericRequest)
            throws OSSException, ClientException {
        liveChannelOperation.deleteLiveChannel(liveChannelGenericRequest);
    }

    @Override
    public List<LiveChannel> listLiveChannels(String bucketName) throws OSSException, ClientException {
        return liveChannelOperation.listLiveChannels(bucketName);
    }

    @Override
    public LiveChannelListing listLiveChannels(ListLiveChannelsRequest listLiveChannelRequest)
            throws OSSException, ClientException {
        return liveChannelOperation.listLiveChannels(listLiveChannelRequest);
    }

    @Override
    public List<LiveRecord> getLiveChannelHistory(String bucketName, String liveChannel)
            throws OSSException, ClientException {
        return this.getLiveChannelHistory(new LiveChannelGenericRequest(bucketName, liveChannel));
    }

    @Override
    public List<LiveRecord> getLiveChannelHistory(LiveChannelGenericRequest liveChannelGenericRequest)
            throws OSSException, ClientException {
        return liveChannelOperation.getLiveChannelHistory(liveChannelGenericRequest);
    }

    @Override
    public void generateVodPlaylist(String bucketName, String liveChannelName, String PlaylistName, long startTime,
            long endTime) throws OSSException, ClientException {
        this.generateVodPlaylist(
                new GenerateVodPlaylistRequest(bucketName, liveChannelName, PlaylistName, startTime, endTime));
    }

    @Override
    public void generateVodPlaylist(GenerateVodPlaylistRequest generateVodPlaylistRequest)
            throws OSSException, ClientException {
        liveChannelOperation.generateVodPlaylist(generateVodPlaylistRequest);
    }

    @Override
    public OSSObject getVodPlaylist(String bucketName, String liveChannelName, long startTime,
                                    long endTime) throws OSSException, ClientException {
        return this.getVodPlaylist(
                new GetVodPlaylistRequest(bucketName, liveChannelName, startTime, endTime));
    }

    @Override
    public OSSObject getVodPlaylist(GetVodPlaylistRequest getVodPlaylistRequest)
            throws OSSException, ClientException {
        return liveChannelOperation.getVodPlaylist(getVodPlaylistRequest);
    }

    @Override
    public String generateRtmpUri(String bucketName, String liveChannelName, String PlaylistName, long expires)
            throws OSSException, ClientException {
        return this.generateRtmpUri(new GenerateRtmpUriRequest(bucketName, liveChannelName, PlaylistName, expires));
    }

    @Override
    public String generateRtmpUri(GenerateRtmpUriRequest generateRtmpUriRequest) throws OSSException, ClientException {
        return liveChannelOperation.generateRtmpUri(generateRtmpUriRequest);
    }

    @Override
    public void createSymlink(String bucketName, String symLink, String targetObject)
            throws OSSException, ClientException {
        this.createSymlink(new CreateSymlinkRequest(bucketName, symLink, targetObject));
    }

    @Override
    public void createSymlink(CreateSymlinkRequest createSymlinkRequest) throws OSSException, ClientException {
        objectOperation.createSymlink(createSymlinkRequest);
    }

    @Override
    public OSSSymlink getSymlink(String bucketName, String symLink) throws OSSException, ClientException {
        return this.getSymlink(new GenericRequest(bucketName, symLink));
    }

    @Override
    public OSSSymlink getSymlink(GenericRequest genericRequest) throws OSSException, ClientException {
        return objectOperation.getSymlink(genericRequest);
    }

    @Override
    public GenericResult processObject(ProcessObjectRequest processObjectRequest) throws OSSException, ClientException {
        return this.objectOperation.processObject(processObjectRequest);
    }

    @Override
    public void setBucketRequestPayment(String bucketName, Payer payer) throws OSSException, ClientException {
        this.setBucketRequestPayment(new SetBucketRequestPaymentRequest(bucketName, payer));
    }

    @Override
    public void setBucketRequestPayment(SetBucketRequestPaymentRequest setBucketRequestPaymentRequest) throws OSSException, ClientException {
        this.bucketOperation.setBucketRequestPayment(setBucketRequestPaymentRequest);
    }

    @Override
    public GetBucketRequestPaymentResult getBucketRequestPayment(String bucketName) throws OSSException, ClientException {
        return this.getBucketRequestPayment(new GenericRequest(bucketName));
    }

    @Override
    public GetBucketRequestPaymentResult getBucketRequestPayment(GenericRequest genericRequest) throws OSSException, ClientException {
        return this.bucketOperation.getBucketRequestPayment(genericRequest);
    }

    @Override
    public void setBucketQosInfo(String bucketName, BucketQosInfo bucketQosInfo) throws OSSException, ClientException {
        this.setBucketQosInfo(new SetBucketQosInfoRequest(bucketName, bucketQosInfo));
    }

    @Override
    public void setBucketQosInfo(SetBucketQosInfoRequest setBucketQosInfoRequest) throws OSSException, ClientException {
        this.bucketOperation.setBucketQosInfo(setBucketQosInfoRequest);
    }

    @Override
    public BucketQosInfo getBucketQosInfo(String bucketName) throws OSSException, ClientException {
        return this.getBucketQosInfo(new GenericRequest(bucketName));
    }

    @Override
    public BucketQosInfo getBucketQosInfo(GenericRequest genericRequest) throws OSSException, ClientException {
        return this.bucketOperation.getBucketQosInfo(genericRequest);
    }

    @Override
    public void deleteBucketQosInfo(String bucketName) throws OSSException, ClientException {
        this.deleteBucketQosInfo(new GenericRequest(bucketName));
    }
 
    @Override
    public void deleteBucketQosInfo(GenericRequest genericRequest) throws OSSException, ClientException {
        this.bucketOperation.deleteBucketQosInfo(genericRequest);
    }

    @Override
    public UserQosInfo getUserQosInfo() throws OSSException, ClientException {
        return this.bucketOperation.getUserQosInfo();
    }

    @Override
    public SetAsyncFetchTaskResult setAsyncFetchTask(String bucketName,
        AsyncFetchTaskConfiguration asyncFetchTaskConfiguration) throws OSSException, ClientException {
        return this.setAsyncFetchTask(new SetAsyncFetchTaskRequest(bucketName,asyncFetchTaskConfiguration));
    }

    @Override
    public SetAsyncFetchTaskResult setAsyncFetchTask(SetAsyncFetchTaskRequest setAsyncFetchTaskRequest)
            throws OSSException, ClientException {
        return this.bucketOperation.setAsyncFetchTask(setAsyncFetchTaskRequest);
    }

    @Override
    public GetAsyncFetchTaskResult getAsyncFetchTask(String bucketName, String taskId)
            throws OSSException, ClientException {
        return this.getAsyncFetchTask(new GetAsyncFetchTaskRequest(bucketName, taskId));
    }

    @Override
    public GetAsyncFetchTaskResult getAsyncFetchTask(GetAsyncFetchTaskRequest getAsyncFetchTaskRequest)
            throws OSSException, ClientException {
        return this.bucketOperation.getAsyncFetchTask(getAsyncFetchTaskRequest);
    }

    @Override
    public void createUdf(CreateUdfRequest createUdfRequest) throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public UdfInfo getUdfInfo(UdfGenericRequest genericRequest) throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public List<UdfInfo> listUdfs() throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public void deleteUdf(UdfGenericRequest genericRequest) throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public void uploadUdfImage(UploadUdfImageRequest uploadUdfImageRequest) throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public List<UdfImageInfo> getUdfImageInfo(UdfGenericRequest genericRequest) throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public void deleteUdfImage(UdfGenericRequest genericRequest) throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public void createUdfApplication(CreateUdfApplicationRequest createUdfApplicationRequest)
            throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public UdfApplicationInfo getUdfApplicationInfo(UdfGenericRequest genericRequest)
            throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public List<UdfApplicationInfo> listUdfApplications() throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public void deleteUdfApplication(UdfGenericRequest genericRequest) throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public void upgradeUdfApplication(UpgradeUdfApplicationRequest upgradeUdfApplicationRequest)
            throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public void resizeUdfApplication(ResizeUdfApplicationRequest resizeUdfApplicationRequest)
            throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public UdfApplicationLog getUdfApplicationLog(GetUdfApplicationLogRequest getUdfApplicationLogRequest)
            throws OSSException, ClientException {
        throw new ClientException("Not supported.");
    }

    @Override
    public void shutdown() {
        try {
            serviceClient.shutdown();
        } catch (Exception e) {
            logException("shutdown throw exception: ", e);
        }
    }

}
