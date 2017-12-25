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

import static com.aliyun.oss.common.utils.CodingUtils.assertParameterNotNull;
import static com.aliyun.oss.common.utils.IOUtils.newRepeatableInputStream;
import static com.aliyun.oss.common.utils.LogUtils.logException;
import static com.aliyun.oss.event.ProgressPublisher.publishProgress;
import static com.aliyun.oss.internal.OSSUtils.determineInputStreamLength;
import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CheckedInputStream;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.common.parser.RequestMarshallers;
import com.aliyun.oss.common.utils.CRC64;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressInputStream;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.internal.ResponseParsers.GetUdfApplicationLogResponseParser;
import com.aliyun.oss.model.CreateUdfApplicationRequest;
import com.aliyun.oss.model.CreateUdfRequest;
import com.aliyun.oss.model.GetUdfApplicationLogRequest;
import com.aliyun.oss.model.ResizeUdfApplicationRequest;
import com.aliyun.oss.model.UdfApplicationInfo;
import com.aliyun.oss.model.UdfApplicationLog;
import com.aliyun.oss.model.UdfGenericRequest;
import com.aliyun.oss.model.UdfImageInfo;
import com.aliyun.oss.model.UdfInfo;
import com.aliyun.oss.model.UpgradeUdfApplicationRequest;
import com.aliyun.oss.model.UploadUdfImageRequest;

/**
 * OSSUdfOperation
 *
 */
public class OSSUdfOperation extends OSSOperation {

    public OSSUdfOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }

    /**
     * UDF
     */

    public void createUdf(CreateUdfRequest createUdfRequest) throws OSSException, ClientException {
        assertParameterNotNull(createUdfRequest, "createUdfRequest");
        assertParameterNotNull(createUdfRequest.getName(), "createUdfRequest.name");

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF, null);

        byte[] rawContent = RequestMarshallers.createUdfRequestMarshaller.marshall(createUdfRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setParameters(params).setInputSize(rawContent.length)
                .setInputStream(new ByteArrayInputStream(rawContent)).setOriginalRequest(createUdfRequest).build();

        doOperation(request, emptyResponseParser, null, null);
    }

    public UdfInfo getUdfInfo(UdfGenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String udfName = genericRequest.getName();
        assertParameterNotNull(udfName, "udfName");
        ensureBucketNameValid(udfName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, udfName);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).setOriginalRequest(genericRequest).build();

        return doOperation(request, ResponseParsers.getUdfInfoResponseParser, null, null, true);
    }

    public List<UdfInfo> listUdfs() throws OSSException, ClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).build();

        return doOperation(request, ResponseParsers.listUdfResponseParser, null, null, true);
    }

    public void deleteUdf(UdfGenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String udfName = genericRequest.getName();
        assertParameterNotNull(udfName, "udfName");
        ensureBucketNameValid(udfName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, udfName);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setParameters(params).setOriginalRequest(genericRequest).build();

        doOperation(request, emptyResponseParser, null, null);
    }

    /**
     * UDF Image
     */
    public void uploadUdfImage(UploadUdfImageRequest uploadUdfImageRequest) throws OSSException, ClientException {
        assertParameterNotNull(uploadUdfImageRequest, "uploadUdfImageRequest");
        assertParameterNotNull(uploadUdfImageRequest.getUdfImage(), "udfImage");
        assertParameterNotNull(uploadUdfImageRequest.getName(), "udfImage");
        ensureBucketNameValid(uploadUdfImageRequest.getName());

        InputStream repeatableInputStream = null;
        try {
            repeatableInputStream = newRepeatableInputStream(uploadUdfImageRequest.getUdfImage());
        } catch (IOException ex) {
            logException("Cannot wrap to repeatable input stream: ", ex);
            throw new ClientException("Cannot wrap to repeatable input stream: ", ex);
        }

        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF_IMAGE, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, uploadUdfImageRequest.getName());
        if (uploadUdfImageRequest.getUdfImageDesc() != null) {
            params.put(RequestParameters.SUBRESOURCE_UDF_IMAGE_DESC, uploadUdfImageRequest.getUdfImageDesc());
        }

        RequestMessage httpRequest = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setHeaders(headers).setParameters(params)
                .setInputStream(repeatableInputStream)
                .setInputSize(determineInputStreamLength(repeatableInputStream, 0))
                .setOriginalRequest(uploadUdfImageRequest).build();

        ProgressListener listener = uploadUdfImageRequest.getProgressListener();
        try {
            publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            doOperation(httpRequest, emptyResponseParser, null, null, true);
            publishProgress(listener, ProgressEventType.TRANSFER_COMPLETED_EVENT);
        } catch (RuntimeException e) {
            publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw e;
        }
    }

    public List<UdfImageInfo> getUdfImageInfo(UdfGenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String udfName = genericRequest.getName();
        assertParameterNotNull(udfName, "udfName");
        ensureBucketNameValid(udfName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF_IMAGE, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, udfName);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).setOriginalRequest(genericRequest).build();

        return doOperation(request, ResponseParsers.getUdfImageInfoResponseParser, null, null, true);
    }

    public void deleteUdfImage(UdfGenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String udfName = genericRequest.getName();
        assertParameterNotNull(udfName, "udfName");
        ensureBucketNameValid(udfName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF_IMAGE, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, udfName);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setParameters(params).setOriginalRequest(genericRequest).build();

        doOperation(request, emptyResponseParser, null, null, true);
    }

    /**
     * UDF Application
     */

    public void createUdfApplication(CreateUdfApplicationRequest createUdfApplicationRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(createUdfApplicationRequest, "createUdfApplicationRequest");

        String udfName = createUdfApplicationRequest.getName();
        assertParameterNotNull(udfName, "udfName");
        ensureBucketNameValid(udfName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF_APPLICATION, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, udfName);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_CREATE);

        byte[] rawContent = RequestMarshallers.createUdfApplicationRequestMarshaller
                .marshall(createUdfApplicationRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setParameters(params).setInputSize(rawContent.length)
                .setInputStream(new ByteArrayInputStream(rawContent)).setOriginalRequest(createUdfApplicationRequest)
                .build();

        doOperation(request, emptyResponseParser, null, null);
    }

    public UdfApplicationInfo getUdfApplicationInfo(UdfGenericRequest genericRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String udfName = genericRequest.getName();
        assertParameterNotNull(udfName, "udfName");
        ensureBucketNameValid(udfName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF_APPLICATION, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, udfName);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).setOriginalRequest(genericRequest).build();

        return doOperation(request, ResponseParsers.getUdfApplicationInfoResponseParser, null, null, true);
    }

    public List<UdfApplicationInfo> listUdfApplication() throws OSSException, ClientException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF_APPLICATION, null);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).build();

        return doOperation(request, ResponseParsers.listUdfApplicationInfoResponseParser, null, null, true);
    }

    public void deleteUdfApplication(UdfGenericRequest genericRequest) throws OSSException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String udfName = genericRequest.getName();
        assertParameterNotNull(udfName, "udfName");
        ensureBucketNameValid(udfName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF_APPLICATION, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, udfName);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.DELETE).setParameters(params).setOriginalRequest(genericRequest).build();

        doOperation(request, emptyResponseParser, null, null, true);
    }

    /**
     * UDF Application CLT
     */
    public void upgradeUdfApplication(UpgradeUdfApplicationRequest upgradeUdfApplicationRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(upgradeUdfApplicationRequest, "upgradeUdfApplicationRequest");

        String udfName = upgradeUdfApplicationRequest.getName();
        assertParameterNotNull(udfName, "udfName");
        ensureBucketNameValid(udfName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF_APPLICATION, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, udfName);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_UPGRADE);

        byte[] rawContent = RequestMarshallers.upgradeUdfApplicationRequestMarshaller
                .marshall(upgradeUdfApplicationRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setParameters(params).setInputSize(rawContent.length)
                .setInputStream(new ByteArrayInputStream(rawContent)).setOriginalRequest(upgradeUdfApplicationRequest)
                .build();

        doOperation(request, emptyResponseParser, null, null);
    }

    public void resizeUdfApplication(ResizeUdfApplicationRequest resizeUdfApplicationRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(resizeUdfApplicationRequest, "resizeUdfApplicationRequest");

        String udfName = resizeUdfApplicationRequest.getName();
        assertParameterNotNull(udfName, "udfName");
        ensureBucketNameValid(udfName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(RequestParameters.SUBRESOURCE_UDF_APPLICATION, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, udfName);
        params.put(RequestParameters.SUBRESOURCE_COMP, RequestParameters.COMP_RESIZE);

        byte[] rawContent = RequestMarshallers.resizeUdfApplicationRequestMarshaller
                .marshall(resizeUdfApplicationRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setParameters(params).setInputSize(rawContent.length)
                .setInputStream(new ByteArrayInputStream(rawContent)).setOriginalRequest(resizeUdfApplicationRequest)
                .build();

        doOperation(request, emptyResponseParser, null, null);
    }

    public UdfApplicationLog getUdfApplicationLog(GetUdfApplicationLogRequest getUdfApplicationLogRequest)
            throws OSSException, ClientException {
        assertParameterNotNull(getUdfApplicationLogRequest, "resizeUdfApplicationRequest");

        String udfName = getUdfApplicationLogRequest.getName();
        assertParameterNotNull(udfName, "udfName");
        ensureBucketNameValid(udfName);

        Map<String, String> params = new HashMap<String, String>();
        populateGetUdfApplicationLogParameters(params, getUdfApplicationLogRequest);

        RequestMessage request = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.GET).setParameters(params).setOriginalRequest(getUdfApplicationLogRequest)
                .build();

        ProgressListener listener = getUdfApplicationLogRequest.getProgressListener();
        UdfApplicationLog udfApplicationLog = null;
        try {
            publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            udfApplicationLog = doOperation(request, new GetUdfApplicationLogResponseParser(udfName), null, null, true);
            InputStream instream = udfApplicationLog.getLogContent();
            ProgressInputStream progressInputStream = new ProgressInputStream(instream, listener) {
                @Override
                protected void onEOF() {
                    publishProgress(getListener(), ProgressEventType.TRANSFER_COMPLETED_EVENT);
                };
            };
            CRC64 crc = new CRC64();
            CheckedInputStream checkedInputstream = new CheckedInputStream(progressInputStream, crc);
            udfApplicationLog.setLogContent(checkedInputstream);
        } catch (RuntimeException e) {
            publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw e;
        }

        return udfApplicationLog;
    }

    /**
     * Private
     */
    private static void populateGetUdfApplicationLogParameters(Map<String, String> params,
            GetUdfApplicationLogRequest getUdfApplicationLogRequest) {
        params.put(RequestParameters.SUBRESOURCE_UDF_LOG, null);
        params.put(RequestParameters.SUBRESOURCE_UDF_NAME, getUdfApplicationLogRequest.getName());
        Date startTime = getUdfApplicationLogRequest.getStartTime();
        if (startTime != null) {
            params.put(RequestParameters.SINCE, Long.toString(startTime.getTime() / 1000));
        }
        Long endLines = getUdfApplicationLogRequest.getEndLines();
        if (endLines != null) {
            params.put(RequestParameters.TAIL, endLines.toString());
        }
    }

}
