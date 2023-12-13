package com.aliyun.oss.internal.signer;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.ServiceSignature;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.internal.SignUtils;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import java.net.URI;

import static com.aliyun.oss.internal.RequestParameters.*;

public class OSSV1Signer extends OSSSignerBase {

    public OSSV1Signer(OSSSignerParams signerParams) {
        super(signerParams);
    }

    @Override
    protected void addAuthorizationHeader(RequestMessage request) {
        Credentials cred = signerParams.getCredentials();
        String accessKeyId = cred.getAccessKeyId();
        String secretAccessKey = cred.getSecretAccessKey();
        String signature;
        signature = SignUtils.buildSignature(secretAccessKey, request.getMethod().toString(), signerParams.getResourcePath(), request);
        request.addHeader(OSSHeaders.AUTHORIZATION, SignUtils.composeRequestAuthorization(accessKeyId, signature));
    }

    @Override
    public void presign(RequestMessage request) throws ClientException {
        Credentials cred = signerParams.getCredentials();
        String accessKeyId = cred.getAccessKeyId();
        String secretAccessKey = cred.getSecretAccessKey();
        String canonicalResource = signerParams.getResourcePath();


        String expires = String.valueOf(signerParams.getExpiration().getTime() / 1000L);

        if (cred.useSecurityToken()) {
            request.addParameter(SECURITY_TOKEN, cred.getSecurityToken());
        }
        request.addHeader(HttpHeaders.DATE, expires);

        String canonicalString = SignUtils.buildCanonicalString(request.getMethod().toString(), canonicalResource, request, expires);
        String signature = ServiceSignature.create().computeSignature(secretAccessKey, canonicalString);

        request.addParameter(HttpHeaders.EXPIRES, expires);
        request.addParameter(OSS_ACCESS_KEY_ID, accessKeyId);
        request.addParameter(SIGNATURE, signature);
    }
}
