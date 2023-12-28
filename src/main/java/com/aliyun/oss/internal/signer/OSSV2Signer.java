package com.aliyun.oss.internal.signer;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.HmacSHA256Signature;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.internal.SignParameters;
import com.aliyun.oss.internal.SignV2Utils;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import java.net.URI;
import java.util.Set;

import static com.aliyun.oss.internal.RequestParameters.*;
import static com.aliyun.oss.internal.RequestParameters.OSS_SIGNATURE;

public class OSSV2Signer extends OSSSignerBase {

    protected OSSV2Signer(OSSSignerParams signerParams) {
        super(signerParams);
    }

    @Override
    protected void addAuthorizationHeader(RequestMessage request) {
        Credentials cred = signerParams.getCredentials();
        String accessKeyId = cred.getAccessKeyId();
        String secretAccessKey = cred.getSecretAccessKey();
        String signature;
        signature = SignV2Utils.buildSignature(secretAccessKey, request.getMethod().toString(), signerParams.getResourcePath(), request);
        request.addHeader(OSSHeaders.AUTHORIZATION, SignV2Utils.composeRequestAuthorization(accessKeyId,signature, request));
    }

    @Override
    public void presign(RequestMessage request) throws ClientException {
        Credentials cred = signerParams.getCredentials();
        String accessKeyId = cred.getAccessKeyId();
        String secretAccessKey = cred.getSecretAccessKey();
        String expires = String.valueOf(signerParams.getExpiration().getTime() / 1000L);
        String canonicalResource = signerParams.getResourcePath();

        if (cred.useSecurityToken()) {
            request.addParameter(SECURITY_TOKEN, cred.getSecurityToken());
        }
        request.addHeader(HttpHeaders.DATE, expires);

        request.addParameter(OSS_SIGNATURE_VERSION, SignParameters.AUTHORIZATION_V2);
        request.addParameter(OSS_EXPIRES, expires);
        request.addParameter(OSS_ACCESS_KEY_ID_PARAM, accessKeyId);
        String additionalHeaderNameStr = SignV2Utils.buildSortedAdditionalHeaderNameStr(request.getHeaders().keySet(),
                signerParams.getAdditionalHeaderNames());

        if (!additionalHeaderNameStr.isEmpty()) {
            request.addParameter(OSS_ADDITIONAL_HEADERS, additionalHeaderNameStr);
        }
        Set<String> rawAdditionalHeaderNames = SignV2Utils.buildRawAdditionalHeaderNames(request.getHeaders().keySet(),
                signerParams.getAdditionalHeaderNames());
        String canonicalString = SignV2Utils.buildCanonicalString(request.getMethod().toString(), canonicalResource, request, rawAdditionalHeaderNames);
        String signature = new HmacSHA256Signature().computeSignature(secretAccessKey, canonicalString);

        request.addParameter(OSS_SIGNATURE, signature);
    }
}
