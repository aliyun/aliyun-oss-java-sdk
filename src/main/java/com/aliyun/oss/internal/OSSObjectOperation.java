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
import static com.aliyun.oss.common.utils.CodingUtils.assertStringNotNullOrEmpty;
import static com.aliyun.oss.common.utils.CodingUtils.assertTrue;
import static com.aliyun.oss.common.utils.IOUtils.checkFile;
import static com.aliyun.oss.common.utils.IOUtils.newRepeatableInputStream;
import static com.aliyun.oss.common.utils.IOUtils.safeClose;
import static com.aliyun.oss.common.utils.LogUtils.getLog;
import static com.aliyun.oss.common.utils.LogUtils.logException;
import static com.aliyun.oss.event.ProgressPublisher.publishProgress;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_BUFFER_SIZE;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_CHARSET_NAME;
import static com.aliyun.oss.internal.OSSHeaders.OSS_SELECT_OUTPUT_RAW;
import static com.aliyun.oss.internal.OSSUtils.OSS_RESOURCE_MANAGER;
import static com.aliyun.oss.internal.OSSUtils.addDateHeader;
import static com.aliyun.oss.internal.OSSUtils.addHeader;
import static com.aliyun.oss.internal.OSSUtils.addStringListHeader;
import static com.aliyun.oss.internal.OSSUtils.determineInputStreamLength;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;
import static com.aliyun.oss.internal.OSSUtils.ensureObjectKeyValid;
import static com.aliyun.oss.internal.OSSUtils.ensureCallbackValid;
import static com.aliyun.oss.internal.OSSUtils.joinETags;
import static com.aliyun.oss.internal.OSSUtils.populateRequestMetadata;
import static com.aliyun.oss.internal.OSSUtils.populateResponseHeaderParameters;
import static com.aliyun.oss.internal.OSSUtils.populateRequestCallback;
import static com.aliyun.oss.internal.OSSUtils.removeHeader;
import static com.aliyun.oss.internal.OSSUtils.safeCloseResponse;
import static com.aliyun.oss.internal.RequestParameters.ENCODING_TYPE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_ACL;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_DELETE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_OBJECTMETA;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_SYMLINK;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_TAGGING;
import static com.aliyun.oss.internal.ResponseParsers.appendObjectResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.copyObjectResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.deleteObjectsResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getTaggingResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getObjectAclResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getObjectMetadataResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.putObjectReponseParser;
import static com.aliyun.oss.internal.ResponseParsers.putObjectProcessReponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getSimplifiedObjectMetaResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.getSymbolicLinkResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.headObjectResponseParser;
import static com.aliyun.oss.internal.ResponseParsers.deleteVersionsResponseParser;

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
import java.util.zip.CheckedInputStream;

import com.aliyun.oss.model.*;
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
import com.aliyun.oss.common.utils.CRC64;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.common.utils.ExceptionFactory;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.common.utils.RangeSpec;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressInputStream;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.internal.ResponseParsers.GetObjectResponseParser;

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
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws OSSException, ClientException {

        assertParameterNotNull(putObjectRequest, "putObjectRequest");

        PutObjectResult result = null;

        if (!isNeedReturnResponse(putObjectRequest)) {
            result = writeObjectInternal(WriteMode.OVERWRITE, putObjectRequest, putObjectReponseParser);
        } else {
            result = writeObjectInternal(WriteMode.OVERWRITE, putObjectRequest, putObjectProcessReponseParser);
        }

        if (isCrcCheckEnabled()) {
            OSSUtils.checkChecksum(result.getClientCRC(), result.getServerCRC(), result.getRequestId());
        }

        return result;
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

        RequestMessage request = new RequestMessage(null, null);
        request.setMethod(HttpMethod.PUT);
        request.setAbsoluteUrl(signedUrl);
        request.setUseUrlSignature(true);
        request.setContent(requestContent);
        request.setContentLength(determineInputStreamLength(requestContent, contentLength, useChunkEncoding));
        request.setHeaders(requestHeaders);
        request.setUseChunkEncoding(useChunkEncoding);

        PutObjectResult result = null;
        if (requestHeaders.get(OSSHeaders.OSS_HEADER_CALLBACK) == null) {
            result = doOperation(request, putObjectReponseParser, null, null, true);
        } else {
            result = doOperation(request, putObjectProcessReponseParser, null, null, true);
        }

        if (isCrcCheckEnabled()) {
            OSSUtils.checkChecksum(result.getClientCRC(), result.getServerCRC(), result.getRequestId());
        }

        return result;
    }

    /**
     * Upload input stream or file to oss by append mode.
     */
    public AppendObjectResult appendObject(AppendObjectRequest appendObjectRequest)
            throws OSSException, ClientException {

        assertParameterNotNull(appendObjectRequest, "appendObjectRequest");

        AppendObjectResult result = writeObjectInternal(WriteMode.APPEND, appendObjectRequest,
                appendObjectResponseParser);

        if (appendObjectRequest.getInitCRC() != null && result.getClientCRC() != null) {
            result.setClientCRC(CRC64.combine(appendObjectRequest.getInitCRC(), result.getClientCRC(),
                    (result.getNextPosition() - appendObjectRequest.getPosition())));
        }

        if (isCrcCheckEnabled() && appendObjectRequest.getInitCRC() != null) {
            OSSUtils.checkChecksum(result.getClientCRC(), result.getServerCRC(), result.getRequestId());
        }

        return result;
    }

    public SelectObjectMetadata createSelectObjectMetadata(CreateSelectObjectMetadataRequest createSelectObjectMetadataRequest) throws OSSException, ClientException {
        String process = createSelectObjectMetadataRequest.getProcess();
        assertParameterNotNull(process, "process");

        GenericRequest genericRequest = new GenericRequest(
                createSelectObjectMetadataRequest.getBucketName(), createSelectObjectMetadataRequest.getKey());
        genericRequest.getParameters().put(RequestParameters.SUBRESOURCE_PROCESS, process);

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, createSelectObjectMetadataRequest.getRequestPayer());

        byte[] content = createSelectObjectMetadataRequestMarshaller.marshall(createSelectObjectMetadataRequest);
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setInputSize(content.length).setInputStream(new ByteArrayInputStream(content))
                .setBucket(bucketName).setKey(key).setHeaders(headers).setOriginalRequest(genericRequest)
                .build();

        //create meta progress listener(scanned bytes)
        final ProgressListener selectProgressListener = createSelectObjectMetadataRequest.getSelectProgressListener();
        try {
            OSSObject ossObject = doOperation(request, new GetObjectResponseParser(bucketName, key), bucketName, key, true);
            publishProgress(selectProgressListener, ProgressEventType.SELECT_STARTED_EVENT);
            SelectObjectMetadata selectObjectMetadata = new SelectObjectMetadata(ossObject.getObjectMetadata());
            SelectObjectMetadata.SelectContentMetadataBase selectContentMetadataBase;
            if (createSelectObjectMetadataRequest.getInputSerialization().getSelectContentFormat() == SelectContentFormat.CSV) {
                selectObjectMetadata.setCsvObjectMetadata(new SelectObjectMetadata.CSVObjectMetadata());
                selectContentMetadataBase = selectObjectMetadata.getCsvObjectMetadata();
            } else {
                selectObjectMetadata.setJsonObjectMetadata(new SelectObjectMetadata.JsonObjectMetadata());
                selectContentMetadataBase = selectObjectMetadata.getJsonObjectMetadata();
            }
            InputStream in = ossObject.getObjectContent();
            CreateSelectMetaInputStream warppedStream = new CreateSelectMetaInputStream(in, selectContentMetadataBase, selectProgressListener);
            warppedStream.setRequestId(ossObject.getRequestId());
            while (warppedStream.read() != -1) {
                //read until eof
            }
            return selectObjectMetadata;
        } catch (IOException e) {
            publishProgress(selectProgressListener, ProgressEventType.SELECT_FAILED_EVENT);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            publishProgress(selectProgressListener, ProgressEventType.SELECT_FAILED_EVENT);
            throw e;
        }
    }

    /**
     * Select an object from oss.
     */
    public OSSObject selectObject(SelectObjectRequest selectObjectRequest) throws OSSException, ClientException {
        assertParameterNotNull(selectObjectRequest, "selectObjectRequest");
        String bucketName = selectObjectRequest.getBucketName();
        String key = selectObjectRequest.getKey();
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);
        Map<String, String> headers = new HashMap<String, String>();
        populateGetObjectRequestHeaders(selectObjectRequest, headers);

        populateRequestPayerHeader(headers, selectObjectRequest.getRequestPayer());

        Map<String, String> params = new HashMap<String, String>();
        populateResponseHeaderParameters(params, selectObjectRequest.getResponseHeaders());
        String process = selectObjectRequest.getProcess();
        assertParameterNotNull(process, "process");

        params.put(RequestParameters.SUBRESOURCE_PROCESS, process);

        SelectObjectRequest.ExpressionType expressionType = selectObjectRequest.getExpressionType();
        if (expressionType != SelectObjectRequest.ExpressionType.SQL) {
            throw new IllegalArgumentException("Select object only support sql expression");
        }
        if (selectObjectRequest.getExpression() == null) {
            throw new IllegalArgumentException("Select expression is null");
        }
        if (selectObjectRequest.getLineRange() != null && selectObjectRequest.getSplitRange() != null) {
            throw new IllegalArgumentException("Line range and split range of select request should not both set");
        }

        byte[] content = selectObjectRequestMarshaller.marshall(selectObjectRequest);

        headers.put(HttpHeaders.CONTENT_MD5, BinaryUtil.toBase64String(BinaryUtil.calculateMd5(content)));
        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setKey(key).setHeaders(headers)
                .setInputSize(content.length).setInputStream(new ByteArrayInputStream(content))
                .setParameters(params).setOriginalRequest(selectObjectRequest).build();
        //select progress listener(scanned bytes)
        final ProgressListener selectProgressListener = selectObjectRequest.getSelectProgressListener();
        try {
            OSSObject ossObject = doOperation(request, new GetObjectResponseParser(bucketName, key), bucketName, key, true);
            publishProgress(selectProgressListener, ProgressEventType.SELECT_STARTED_EVENT);
            InputStream inputStream = ossObject.getObjectContent();
            if (!Boolean.parseBoolean(ossObject.getObjectMetadata().getRawMetadata().get(OSS_SELECT_OUTPUT_RAW).toString())) {
                SelectInputStream selectInputStream = new SelectInputStream(inputStream, selectProgressListener,
                        selectObjectRequest.getOutputSerialization().isPayloadCrcEnabled());
                selectInputStream.setRequestId(ossObject.getRequestId());
                ossObject.setObjectContent(selectInputStream);
            }
            return ossObject;
        } catch (RuntimeException e) {
            publishProgress(selectProgressListener, ProgressEventType.SELECT_FAILED_EVENT);
            throw e;
        }
    }

    /**
     * Pull an object from oss.
     */
    public OSSObject getObject(GetObjectRequest getObjectRequest) throws OSSException, ClientException {

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

            String versionId = getObjectRequest.getVersionId();
            if (versionId != null) {
                params.put(RequestParameters.SUBRESOURCE_VRESION_ID, versionId);
            }
            
            String process = getObjectRequest.getProcess();
            if (process != null) {
                params.put(RequestParameters.SUBRESOURCE_PROCESS, process);
            }

            request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                    .setMethod(HttpMethod.GET).setBucket(bucketName).setKey(key).setHeaders(headers)
                    .setParameters(params).setOriginalRequest(getObjectRequest).build();
        } else {
            request = new RequestMessage(getObjectRequest, bucketName, key);
            request.setMethod(HttpMethod.GET);
            request.setAbsoluteUrl(getObjectRequest.getAbsoluteUri());
            request.setUseUrlSignature(true);
            request.setHeaders(getObjectRequest.getHeaders());
        }

        final ProgressListener listener = getObjectRequest.getProgressListener();
        OSSObject ossObject = null;
        try {
            publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            ossObject = doOperation(request, new GetObjectResponseParser(bucketName, key), bucketName, key, true);
            InputStream instream = ossObject.getObjectContent();
            ProgressInputStream progressInputStream = new ProgressInputStream(instream, listener) {
                @Override
                protected void onEOF() {
                    publishProgress(getListener(), ProgressEventType.TRANSFER_COMPLETED_EVENT);
                };
            };
            CRC64 crc = new CRC64();
            CheckedInputStream checkedInputstream = new CheckedInputStream(progressInputStream, crc);
            ossObject.setObjectContent(checkedInputstream);
        } catch (RuntimeException e) {
            publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw e;
        }

        return ossObject;
    }

    /**
     * Populate a local file with the specified object.
     */
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file) throws OSSException, ClientException {

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

            if (isCrcCheckEnabled() && !hasRangeInRequest(getObjectRequest)) {
                Long clientCRC = IOUtils.getCRCValue(ossObject.getObjectContent());
                OSSUtils.checkChecksum(clientCRC, ossObject.getServerCRC(), ossObject.getRequestId());
            }

            return ossObject.getObjectMetadata();
        } catch (IOException ex) {
            logException("Cannot read object content stream: ", ex);
            throw new ClientException(OSS_RESOURCE_MANAGER.getString("CannotReadContentStream"), ex);
        } finally {
            safeClose(outputStream);
            safeClose(ossObject.getObjectContent());
        }
    }
    
    /**
     * Get simplified object meta.
     */
    public SimplifiedObjectMeta getSimplifiedObjectMeta(GenericRequest genericRequest) {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_OBJECTMETA, null);
        if (genericRequest.getVersionId() != null) {
            params.put(RequestParameters.SUBRESOURCE_VRESION_ID,
                genericRequest.getVersionId());
        }

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, genericRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getSimplifiedObjectMetaResponseParser, bucketName, key, true);
    }

    /**
     * Get object matadata.
     */
    public ObjectMetadata getObjectMetadata(GenericRequest genericRequest) 
        throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);
        
        Map<String, String> params = new HashMap<String, String>();
        if (genericRequest.getVersionId() != null) {
            params.put(RequestParameters.SUBRESOURCE_VRESION_ID, genericRequest.getVersionId());
        }

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, genericRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.HEAD).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
            .setOriginalRequest(genericRequest).build();

        List<ResponseHandler> reponseHandlers = new ArrayList<ResponseHandler>();
        reponseHandlers.add(new ResponseHandler() {

            @Override
            public void handle(ResponseMessage response) throws ServiceException, ClientException {
                if (response.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                    safeCloseResponse(response);
                    throw ExceptionFactory.createOSSException(
                            response.getHeaders().get(OSSHeaders.OSS_HEADER_REQUEST_ID), OSSErrorCode.NO_SUCH_KEY,
                            OSS_RESOURCE_MANAGER.getString("NoSuchKey"));
                }
            }

        });

        return doOperation(request, getObjectMetadataResponseParser, bucketName, key, true, null, reponseHandlers);
    }

    /**
     * Copy an existing object to another one.
     */
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws OSSException, ClientException {

        assertParameterNotNull(copyObjectRequest, "copyObjectRequest");

        Map<String, String> headers = new HashMap<String, String>();
        populateCopyObjectHeaders(copyObjectRequest, headers);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(copyObjectRequest.getDestinationBucketName())
                .setKey(copyObjectRequest.getDestinationKey()).setHeaders(headers).setOriginalRequest(copyObjectRequest)
                .build();

        return doOperation(request, copyObjectResponseParser, copyObjectRequest.getDestinationBucketName(),
                copyObjectRequest.getDestinationKey(), true);
    }

    /**
     * Delete an object.
     */
    public void deleteObject(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, genericRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setKey(key).setHeaders(headers).setOriginalRequest(genericRequest)
                .build();

        doOperation(request, emptyResponseParser, bucketName, key);
    }

    /**
     * Delete an object version.
     */
    public void deleteVersion(DeleteVersionRequest deleteVersionRequest) throws OSSException, ClientException {

        assertParameterNotNull(deleteVersionRequest, "deleteVersionRequest");

        String bucketName = deleteVersionRequest.getBucketName();
        String key = deleteVersionRequest.getKey();
        String versionId = deleteVersionRequest.getVersionId();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        assertParameterNotNull(versionId, "versionId");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_VRESION_ID, versionId);

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, deleteVersionRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.DELETE).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
            .setOriginalRequest(deleteVersionRequest).build();

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

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(deleteObjectsRequest).build();

        return doOperation(request, deleteObjectsResponseParser, bucketName, null, true);
    }
    
    /**
     * Delete multiple versions.
     */
    public DeleteVersionsResult deleteVersions(DeleteVersionsRequest deleteVersionsRequest)
        throws OSSException, ClientException {

        assertParameterNotNull(deleteVersionsRequest, "deleteObjectsRequest");

        String bucketName = deleteVersionsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_DELETE, null);

        byte[] rawContent = deleteVersionsRequestMarshaller.marshall(deleteVersionsRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addDeleteVersionsRequiredHeaders(headers, rawContent);

        populateRequestPayerHeader(headers, deleteVersionsRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
            .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
            .setOriginalRequest(deleteVersionsRequest).build();

        return doOperation(request, deleteVersionsResponseParser, bucketName, null, true);
    }

    /**
     * Get head information.
     */
    public ObjectMetadata headObject(HeadObjectRequest headObjectRequest) throws OSSException, ClientException {

        assertParameterNotNull(headObjectRequest, "headObjectRequest");

        String bucketName = headObjectRequest.getBucketName();
        String key = headObjectRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> headers = new HashMap<String, String>();
        addDateHeader(headers, OSSHeaders.HEAD_OBJECT_IF_MODIFIED_SINCE,
                headObjectRequest.getModifiedSinceConstraint());
        addDateHeader(headers, OSSHeaders.HEAD_OBJECT_IF_UNMODIFIED_SINCE,
                headObjectRequest.getUnmodifiedSinceConstraint());

        addStringListHeader(headers, OSSHeaders.HEAD_OBJECT_IF_MATCH, headObjectRequest.getMatchingETagConstraints());
        addStringListHeader(headers, OSSHeaders.HEAD_OBJECT_IF_NONE_MATCH,
                headObjectRequest.getNonmatchingETagConstraints());

        populateRequestPayerHeader(headers, headObjectRequest.getRequestPayer());

        Map<String, String> params = new HashMap<String, String>();
        if (headObjectRequest.getVersionId() != null) {
            params.put(RequestParameters.SUBRESOURCE_VRESION_ID, headObjectRequest.getVersionId());
        }

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.HEAD).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(headObjectRequest).build();

        return doOperation(request, headObjectResponseParser, bucketName, key);
    }

    public void setObjectAcl(SetObjectAclRequest setObjectAclRequest) throws OSSException, ClientException {

        assertParameterNotNull(setObjectAclRequest, "setObjectAclRequest");

        String bucketName = setObjectAclRequest.getBucketName();
        String key = setObjectAclRequest.getKey();
        CannedAccessControlList cannedAcl = setObjectAclRequest.getCannedACL();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        assertParameterNotNull(cannedAcl, "cannedAcl");

        Map<String, String> headers = new HashMap<String, String>();
        headers.put(OSSHeaders.OSS_OBJECT_ACL, cannedAcl.toString());

        populateRequestPayerHeader(headers, setObjectAclRequest.getRequestPayer());

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);
        if (setObjectAclRequest.getVersionId() != null) {
            params.put(RequestParameters.SUBRESOURCE_VRESION_ID, setObjectAclRequest.getVersionId());
        }

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setKey(key).setParameters(params).setHeaders(headers)
                .setOriginalRequest(setObjectAclRequest).build();

        doOperation(request, emptyResponseParser, bucketName, key);
    }

    public ObjectAcl getObjectAcl(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);
        if (genericRequest.getVersionId() != null) {
            params.put(RequestParameters.SUBRESOURCE_VRESION_ID, genericRequest.getVersionId());
        }

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, genericRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getObjectAclResponseParser, bucketName, key, true);
    }

    public RestoreObjectResult restoreObject(GenericRequest genericRequest) throws OSSException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();
        String versionId = genericRequest.getVersionId();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_RESTORE, null);
        if (versionId != null) {
            params.put(RequestParameters.SUBRESOURCE_VRESION_ID, versionId);
        }

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, genericRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setInputStream(new ByteArrayInputStream(new byte[0])).setInputSize(0)
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, ResponseParsers.restoreObjectResponseParser, bucketName, key);
    }
    
    public void setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) throws OSSException, ClientException {
        assertParameterNotNull(setObjectTaggingRequest, "setBucketTaggingRequest");

        String bucketName = setObjectTaggingRequest.getBucketName();
        String key = setObjectTaggingRequest.getKey();
        String versionId = setObjectTaggingRequest.getVersionId();
        
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);
        if (versionId != null) {
            params.put(RequestParameters.SUBRESOURCE_VRESION_ID, versionId);
        }

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, setObjectTaggingRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setInputStreamWithLength(setBucketTaggingRequestMarshaller.marshall(setObjectTaggingRequest))
                .setOriginalRequest(setObjectTaggingRequest).build();

        doOperation(request, emptyResponseParser, bucketName, key);
    }

    public TagSet getObjectTagging(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();
        String versionId = genericRequest.getVersionId();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);
        if (versionId != null) {
            params.put(RequestParameters.SUBRESOURCE_VRESION_ID, versionId);
        }

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, genericRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(genericRequest).build();
        
        return doOperation(request, getTaggingResponseParser, bucketName, key, true);
    }

    public void deleteObjectTagging(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();
        String versionId = genericRequest.getVersionId();
        
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);
        if (versionId != null) {
            params.put(RequestParameters.SUBRESOURCE_VRESION_ID, versionId);
        }

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, genericRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
            .setMethod(HttpMethod.DELETE).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
            .setOriginalRequest(genericRequest).build();

        doOperation(request, emptyResponseParser, bucketName, key);
    }

    public OSSSymlink getSymlink(GenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String symlink = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(symlink, "symlink");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(symlink);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_SYMLINK, null);

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, genericRequest.getRequestPayer());

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setBucket(bucketName).setKey(symlink).setHeaders(headers).setParameters(params)
                .setOriginalRequest(genericRequest).build();

        OSSSymlink symbolicLink = doOperation(request, getSymbolicLinkResponseParser, bucketName, symlink, true);

        if (symbolicLink != null) {
            symbolicLink.setSymlink(new String(symlink));
        }

        return symbolicLink;
    }

    public void createSymlink(CreateSymlinkRequest createSymlinkRequest) throws OSSException, ClientException {

        assertParameterNotNull(createSymlinkRequest, "createSymlinkRequest");

        String bucketName = createSymlinkRequest.getBucketName();
        String symlink = createSymlinkRequest.getSymlink();
        String target = createSymlinkRequest.getTarget();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(symlink, "symlink");
        assertParameterNotNull(target, "target");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(symlink);
        ensureObjectKeyValid(target);

        ObjectMetadata metadata = createSymlinkRequest.getMetadata();
        if (metadata == null) {
            metadata = new ObjectMetadata();
        }

        // 设置链接的目标文件
        String encodeTargetObject = HttpUtil.urlEncode(target, DEFAULT_CHARSET_NAME);
        metadata.setHeader(OSSHeaders.OSS_HEADER_SYMLINK_TARGET, encodeTargetObject);
        // 设置链接文件的ContentType，目标文件优先，然后是链接文件
        if (metadata.getContentType() == null) {
            metadata.setContentType(Mimetypes.getInstance().getMimetype(target, symlink));
        }

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestMetadata(headers, metadata);

        populateRequestPayerHeader(headers, createSymlinkRequest.getRequestPayer());

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_SYMLINK, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setKey(symlink).setHeaders(headers)
                .setParameters(params).setOriginalRequest(createSymlinkRequest).build();

        doOperation(request, emptyResponseParser, bucketName, symlink);
    }

    public GenericResult processObject(ProcessObjectRequest processObjectRequest) throws OSSException, ClientException {

        assertParameterNotNull(processObjectRequest, "genericRequest");

        String bucketName = processObjectRequest.getBucketName();
        String key = processObjectRequest.getKey();
        String process = processObjectRequest.getProcess();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        assertStringNotNullOrEmpty(process, "process");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_PROCESS, null);

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestPayerHeader(headers, processObjectRequest.getRequestPayer());

        byte[] rawContent = processObjectRequestMarshaller.marshall(processObjectRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(processObjectRequest).build();

        return doOperation(request, ResponseParsers.processObjectResponseParser, bucketName, key, true);
    }

    public boolean doesObjectExist(GenericRequest genericRequest) throws OSSException, ClientException {
        try {
            this.getSimplifiedObjectMeta(genericRequest);
            return true;
        } catch (OSSException e) {
            if (e.getErrorCode().equals(OSSErrorCode.NO_SUCH_BUCKET)
                    || e.getErrorCode().equals(OSSErrorCode.NO_SUCH_KEY)) {
                return false;
            }
            throw e;
        }
    }

    public boolean doesObjectExistWithRedirect(GenericRequest genericRequest) throws OSSException, ClientException {
        OSSObject ossObject = null;
        try {
            String bucketName = genericRequest.getBucketName();
            String key = genericRequest.getKey();
            Payer payer = genericRequest.getRequestPayer();

            GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
            if (payer != null) {
                getObjectRequest.setRequestPayer(payer);
            }
            ossObject = this.getObject(getObjectRequest);
            return true;
        } catch (OSSException e) {
            if (e.getErrorCode() == OSSErrorCode.NO_SUCH_BUCKET || e.getErrorCode() == OSSErrorCode.NO_SUCH_KEY) {
                return false;
            }
            throw e;
        } finally {
            if (ossObject != null) {
                try {
                    ossObject.forcedClose();
                } catch (IOException e) {
                    logException("Forced close failed: ", e);
                }
            }
        }
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
     * An enum to represent different modes the client may specify to upload
     * specified file or inputstream.
     */
    private static enum WriteMode {

        /*
         * If object already not exists, create it. otherwise, append it with
         * the new input
         */
        APPEND("APPEND"),

        /*
         * No matter object exists or not, just overwrite it with the new input
         */
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

    private <RequestType extends PutObjectRequest, ResponseType> ResponseType writeObjectInternal(WriteMode mode,
            RequestType originalRequest, ResponseParser<ResponseType> responseParser) {

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
        ensureCallbackValid(originalRequest.getCallback());

        InputStream repeatableInputStream = null;
        if (originalRequest.getFile() != null) {
            File toUpload = originalRequest.getFile();

            if (!checkFile(toUpload)) {
                getLog().info("Illegal file path: " + toUpload.getPath());
                throw new ClientException("Illegal file path: " + toUpload.getPath());
            }

            metadata.setContentLength(toUpload.length());
            if (metadata.getContentType() == null) {
                metadata.setContentType(Mimetypes.getInstance().getMimetype(toUpload, key));
            }

            try {
                repeatableInputStream = new RepeatableFileInputStream(toUpload);
            } catch (IOException ex) {
                logException("Cannot locate file to upload: ", ex);
                throw new ClientException("Cannot locate file to upload: ", ex);
            }
        } else {
            assertTrue(originalInputStream != null, "Please specify input stream or file to upload");

            if (metadata.getContentType() == null) {
                metadata.setContentType(Mimetypes.getInstance().getMimetype(key));
            }

            try {
                repeatableInputStream = newRepeatableInputStream(originalInputStream);
            } catch (IOException ex) {
                logException("Cannot wrap to repeatable input stream: ", ex);
                throw new ClientException("Cannot wrap to repeatable input stream: ", ex);
            }
        }

        Map<String, String> headers = new HashMap<String, String>();
        populateRequestMetadata(headers, metadata);
        populateRequestCallback(headers, originalRequest.getCallback());
        populateRequestPayerHeader(headers, originalRequest.getRequestPayer());
        populateTrafficLimitHeader(headers, originalRequest.getTrafficLimit());

        Map<String, String> params = new LinkedHashMap<String, String>();
        populateWriteObjectParams(mode, originalRequest, params);

        RequestMessage httpRequest = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(WriteMode.getMappingMethod(mode)).setBucket(bucketName).setKey(key).setHeaders(headers)
                .setParameters(params).setInputStream(repeatableInputStream)
                .setInputSize(determineInputStreamLength(repeatableInputStream, metadata.getContentLength()))
                .setOriginalRequest(originalRequest).build();

        List<ResponseHandler> reponseHandlers = new ArrayList<ResponseHandler>();
        reponseHandlers.add(new OSSCallbackErrorResponseHandler());

        final ProgressListener listener = originalRequest.getProgressListener();
        ResponseType result = null;
        try {
            publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            if (originalRequest.getCallback() == null) {
                result = doOperation(httpRequest, responseParser, bucketName, key, true);
            } else {
                result = doOperation(httpRequest, responseParser, bucketName, key, true, null, reponseHandlers);
            }
            publishProgress(listener, ProgressEventType.TRANSFER_COMPLETED_EVENT);
        } catch (RuntimeException e) {
            publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw e;
        }
        return result;
    }

    private boolean isCrcCheckEnabled() {
        return getInnerClient().getClientConfiguration().isCrcCheckEnabled();
    }

    private boolean hasRangeInRequest(GetObjectRequest getObjectRequest) {
        return getObjectRequest.getHeaders().get(OSSHeaders.RANGE) != null;
    }

    private static void populateCopyObjectHeaders(CopyObjectRequest copyObjectRequest, Map<String, String> headers) {

        String copySourceHeader = "/" + copyObjectRequest.getSourceBucketName() + "/"
                + HttpUtil.urlEncode(copyObjectRequest.getSourceKey(), DEFAULT_CHARSET_NAME);
        if (copyObjectRequest.getSourceVersionId() != null) {
            copySourceHeader += "?versionId=" + copyObjectRequest.getSourceVersionId();
        }

        headers.put(OSSHeaders.COPY_OBJECT_SOURCE, copySourceHeader);

        addDateHeader(headers, OSSHeaders.COPY_OBJECT_SOURCE_IF_MODIFIED_SINCE,
                copyObjectRequest.getModifiedSinceConstraint());
        addDateHeader(headers, OSSHeaders.COPY_OBJECT_SOURCE_IF_UNMODIFIED_SINCE,
                copyObjectRequest.getUnmodifiedSinceConstraint());

        addStringListHeader(headers, OSSHeaders.COPY_OBJECT_SOURCE_IF_MATCH,
                copyObjectRequest.getMatchingETagConstraints());
        addStringListHeader(headers, OSSHeaders.COPY_OBJECT_SOURCE_IF_NONE_MATCH,
                copyObjectRequest.getNonmatchingEtagConstraints());

        addHeader(headers, OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION, copyObjectRequest.getServerSideEncryption());
        addHeader(headers, OSSHeaders.OSS_SERVER_SIDE_ENCRYPTION_KEY_ID, copyObjectRequest.getServerSideEncryptionKeyId());

        ObjectMetadata newObjectMetadata = copyObjectRequest.getNewObjectMetadata();
        if (newObjectMetadata != null) {
            headers.put(OSSHeaders.COPY_OBJECT_METADATA_DIRECTIVE, MetadataDirective.REPLACE.toString());
            if (newObjectMetadata.getRawMetadata().get(OSSHeaders.OSS_TAGGING) != null) {
            	headers.put(OSSHeaders.COPY_OBJECT_TAGGING_DIRECTIVE, MetadataDirective.REPLACE.toString());
            }
            populateRequestMetadata(headers, newObjectMetadata);
        }

        populateRequestPayerHeader(headers, copyObjectRequest.getRequestPayer());

        // The header of Content-Length should not be specified on copying an
        // object.
        removeHeader(headers, HttpHeaders.CONTENT_LENGTH);
    }

    private static void populateGetObjectRequestHeaders(GetObjectRequest getObjectRequest,
            Map<String, String> headers) {

        if (getObjectRequest.getRange() != null) {
            addGetObjectRangeHeader(getObjectRequest.getRange(), headers);
        }

        if (getObjectRequest.getModifiedSinceConstraint() != null) {
            headers.put(OSSHeaders.GET_OBJECT_IF_MODIFIED_SINCE,
                    DateUtil.formatRfc822Date(getObjectRequest.getModifiedSinceConstraint()));
        }

        if (getObjectRequest.getUnmodifiedSinceConstraint() != null) {
            headers.put(OSSHeaders.GET_OBJECT_IF_UNMODIFIED_SINCE,
                    DateUtil.formatRfc822Date(getObjectRequest.getUnmodifiedSinceConstraint()));
        }

        if (getObjectRequest.getMatchingETagConstraints().size() > 0) {
            headers.put(OSSHeaders.GET_OBJECT_IF_MATCH, joinETags(getObjectRequest.getMatchingETagConstraints()));
        }

        if (getObjectRequest.getNonmatchingETagConstraints().size() > 0) {
            headers.put(OSSHeaders.GET_OBJECT_IF_NONE_MATCH,
                    joinETags(getObjectRequest.getNonmatchingETagConstraints()));
        }

        populateRequestPayerHeader(headers, getObjectRequest.getRequestPayer());
        populateTrafficLimitHeader(headers, getObjectRequest.getTrafficLimit());
    }

    private static void addDeleteObjectsRequiredHeaders(Map<String, String> headers, byte[] rawContent) {
        headers.put(HttpHeaders.CONTENT_LENGTH, String.valueOf(rawContent.length));

        byte[] md5 = BinaryUtil.calculateMd5(rawContent);
        String md5Base64 = BinaryUtil.toBase64String(md5);
        headers.put(HttpHeaders.CONTENT_MD5, md5Base64);
    }
    
    private static void addDeleteVersionsRequiredHeaders(Map<String, String> headers, byte[] rawContent) {
        addDeleteObjectsRequiredHeaders(headers, rawContent);
        headers.put(ENCODING_TYPE, OSSConstants.URL_ENCODING);
    }

    private static void addDeleteObjectsOptionalHeaders(Map<String, String> headers, DeleteObjectsRequest request) {
        if (request.getEncodingType() != null) {
            headers.put(ENCODING_TYPE, request.getEncodingType());
        }

        populateRequestPayerHeader(headers, request.getRequestPayer());
    }

    private static void addGetObjectRangeHeader(long[] range, Map<String, String> headers) {
        RangeSpec rangeSpec = RangeSpec.parse(range);
        headers.put(OSSHeaders.RANGE, rangeSpec.toString());
    }

    private static void populateRequestPayerHeader(Map<String, String> headers, Payer payer) {
        if (payer != null && payer.equals(Payer.Requester)) {
            headers.put(OSSHeaders.OSS_REQUEST_PAYER, payer.toString().toLowerCase());
        }
    }

    private static void populateTrafficLimitHeader(Map<String, String> headers, int limit) {
        if (limit > 0) {
            headers.put(OSSHeaders.OSS_HEADER_TRAFFIC_LIMIT, String.valueOf(limit));
        }
    }

    private static void populateWriteObjectParams(WriteMode mode, PutObjectRequest originalRequest,
            Map<String, String> params) {

        if (mode == WriteMode.OVERWRITE) {
            return;
        }

        assert (originalRequest instanceof AppendObjectRequest);
        params.put(RequestParameters.SUBRESOURCE_APPEND, null);
        AppendObjectRequest appendObjectRequest = (AppendObjectRequest) originalRequest;
        if (appendObjectRequest.getPosition() != null) {
            params.put(RequestParameters.POSITION, String.valueOf(appendObjectRequest.getPosition()));
        }
    }

    private static boolean isNeedReturnResponse(PutObjectRequest putObjectRequest) {
        if (putObjectRequest.getCallback() != null || putObjectRequest.getProcess() != null) {
            return true;
        }
        return false;
    }

}
