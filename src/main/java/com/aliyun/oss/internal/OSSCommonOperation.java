package com.aliyun.oss.internal;

import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ServiceClient;
import com.aliyun.oss.common.utils.StringUtils;
import com.aliyun.oss.model.CommonApiRequest;
import com.aliyun.oss.model.CommonApiResult;

import static com.aliyun.oss.internal.OSSUtils.ensureBucketNameValid;

public class OSSCommonOperation extends OSSOperation {

    public OSSCommonOperation(ServiceClient client, CredentialsProvider credentialsProvider) {
        super(client, credentialsProvider);
    }

    /**
     * common api call
     *
     * @param request request
     * @return result
     */
    public <T> CommonApiResult<T> invokeOperation(CommonApiRequest<T> request) {

        if (request.getBucketName() != null) {
            ensureBucketNameValid(request.getBucketName());
        }

        OSSRequestMessageBuilder builder = new OSSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint()).
            setMethod(request.getMethod()).setBucket(request.getBucketName()).setKey(request.getKey()).
            setParameters(request.getParameters()).setHeaders(request.getHeaders()).setInputStream(
            request.getInputStream()).setSignedParameterNames(request.getSignedParameterNames()).setOriginalRequest(request);

        if (null != request.getInputSize()) {
            builder.setInputSize(request.getInputSize());
        }

        if (null != request.getUseChunkEncoding()) {
            builder.setUseChunkEncoding(request.getUseChunkEncoding());
        }

        if (null != request.getFixedLengthInputStream()) {
            builder.setInputStreamWithLength(request.getFixedLengthInputStream());
        }

        RequestMessage requestMessage = builder.build();

        CommonApiResult<T> result = doOperation(requestMessage, request.getResponseParser(), request.getBucketName(),
            request.getKey(), true);

        return result;
    }
}
