package com.aliyun.oss.model;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.common.comm.io.FixedLengthInputStream;
import com.aliyun.oss.common.parser.ResponseParser;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;


public class CommonApiRequest<T> extends GenericRequest {

    private HttpMethod method;

    private InputStream inputStream;

    private FixedLengthInputStream fixedLengthInputStream;

    private Long inputSize;

    private Boolean useChunkEncoding;

    private ResponseParser<CommonApiResult<T>> responseParser;

    private Set<String> signedParameterNames = new HashSet<String>();

    public Set<String> getSignedParameterNames() {
        return signedParameterNames;
    }

    public void setSignedParameterNames(Set<String> signedParameterNames) {
        this.signedParameterNames = signedParameterNames;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public FixedLengthInputStream getFixedLengthInputStream() {
        return fixedLengthInputStream;
    }

    public void setFixedLengthInputStream(FixedLengthInputStream fixedLengthInputStream) {
        this.fixedLengthInputStream = fixedLengthInputStream;
    }

    public Long getInputSize() {
        return inputSize;
    }

    public void setInputSize(Long inputSize) {
        this.inputSize = inputSize;
    }

    public Boolean getUseChunkEncoding() {
        return useChunkEncoding;
    }

    public void setUseChunkEncoding(Boolean useChunkEncoding) {
        this.useChunkEncoding = useChunkEncoding;
    }

    public ResponseParser<CommonApiResult<T>> getResponseParser() {
        return responseParser;
    }

    public void setResponseParser(
        ResponseParser<CommonApiResult<T>> responseParser) {
        this.responseParser = responseParser;
    }
}
