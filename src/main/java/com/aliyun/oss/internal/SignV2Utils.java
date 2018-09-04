package com.aliyun.oss.internal;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.HmacSignature;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.common.utils.HttpUtil;
import com.aliyun.oss.common.utils.StringUtils;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;

import java.net.URI;
import java.util.*;

import static com.aliyun.oss.common.utils.CodingUtils.assertTrue;
import static com.aliyun.oss.internal.OSSConstants.DEFAULT_CHARSET_NAME;
import static com.aliyun.oss.internal.OSSUtils.populateResponseHeaderParameters;
import static com.aliyun.oss.internal.RequestParameters.*;
import static com.aliyun.oss.internal.SignParameters.AUTHORIZATION_ACCESS_KEY_ID;
import static com.aliyun.oss.internal.SignParameters.AUTHORIZATION_ADDITIONAL_HEADERS;
import static com.aliyun.oss.internal.SignParameters.AUTHORIZATION_PREFIX_V2;
import static com.aliyun.oss.internal.SignParameters.AUTHORIZATION_SIGNATURE;

public class SignV2Utils {

    public static String composeRequestAuthorization(String accessKeyId, String signature, RequestMessage request) {
        StringBuilder sb = new StringBuilder();
        Set<String> ts = buildSortedAdditionalHeaderNames(request.getOriginalRequest().getHeaders().keySet(),
                request.getOriginalRequest().getAdditionalHeaderNames());

        sb.append(AUTHORIZATION_PREFIX_V2 + AUTHORIZATION_ACCESS_KEY_ID + ":" + accessKeyId + ", ");

        if (ts != null && !ts.isEmpty()) {
            sb.append(AUTHORIZATION_ADDITIONAL_HEADERS + ":");

            String separator = "";

            for (String header : ts) {
                sb.append(separator);
                sb.append(header.toLowerCase());
                separator = ";";
            }
            sb.append(", ");
        }
        sb.append(AUTHORIZATION_SIGNATURE).append(":").append(signature);

        return sb.toString();
    }

    private static TreeSet<String> buildSortedAdditionalHeaderNames(Set<String> headerNames, Set<String> additionalHeaderNames) {
        TreeSet<String> ts = new TreeSet<String>();

        if (headerNames != null && additionalHeaderNames != null) {
            for (String additionalHeaderName : additionalHeaderNames) {
                if (headerNames.contains(additionalHeaderName)) {
                    ts.add(additionalHeaderName.toLowerCase());
                }
            }
        }
        return ts;
    }

    private static Set<String> buildRawAdditionalHeaderNames(Set<String> headerNames, Set<String> additionalHeaderNames) {
        Set<String> hs = new HashSet<String>();

        if (headerNames != null && additionalHeaderNames != null) {
            for (String additionalHeaderName : additionalHeaderNames) {
                if (headerNames.contains(additionalHeaderName)) {
                    hs.add(additionalHeaderName);
                }
            }
        }
        return hs;
    }

    public static String buildCanonicalString(String method, String resourcePath, RequestMessage request, Set<String> additionalHeaderNames) {
        StringBuilder canonicalString = new StringBuilder();
        canonicalString.append(method + SignParameters.NEW_LINE);
        Map<String, String> headers = request.getHeaders();
        TreeMap<String, String> fixedHeadersToSign = new TreeMap<String, String>();
        TreeMap<String, String> canonicalizedOssHeadersToSign = new TreeMap<String, String>();

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                if (header.getKey() != null) {
                    String lowerKey = header.getKey().toLowerCase();
                    if (lowerKey.equals(HttpHeaders.CONTENT_TYPE.toLowerCase())
                            || lowerKey.equals(HttpHeaders.CONTENT_MD5.toLowerCase())
                            || lowerKey.equals(HttpHeaders.DATE.toLowerCase())) {
                        fixedHeadersToSign.put(lowerKey, header.getValue().trim());
                    } else if (lowerKey.startsWith(OSSHeaders.OSS_PREFIX)){
                        canonicalizedOssHeadersToSign.put(lowerKey, header.getValue().trim());
                    }
                }
            }
        }

        if (!fixedHeadersToSign.containsKey(HttpHeaders.CONTENT_TYPE.toLowerCase())) {
            fixedHeadersToSign.put(HttpHeaders.CONTENT_TYPE.toLowerCase(), "");
        }
        if (!fixedHeadersToSign.containsKey(HttpHeaders.CONTENT_MD5.toLowerCase())) {
            fixedHeadersToSign.put(HttpHeaders.CONTENT_MD5.toLowerCase(), "");
        }

        for (String additionalHeaderName : additionalHeaderNames) {
            if (additionalHeaderName != null && headers.get(additionalHeaderName) != null) {
                canonicalizedOssHeadersToSign.put(additionalHeaderName.toLowerCase(), headers.get(additionalHeaderName).trim());
            }
        }

        // Append fixed headers to sign to canonical string
        for (Map.Entry<String, String> entry : fixedHeadersToSign.entrySet()) {
            Object value = entry.getValue();

            canonicalString.append(value);
            canonicalString.append(SignParameters.NEW_LINE);
        }

        // Append canonicalized oss headers to sign to canonical string
        for (Map.Entry<String, String> entry : canonicalizedOssHeadersToSign.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            canonicalString.append(key).append(':').append(value).append(SignParameters.NEW_LINE);
        }


        // Append additional header names
        TreeSet<String> ts = new TreeSet<String>();
        for (String additionalHeaderName : additionalHeaderNames) {
            ts.add(additionalHeaderName.toLowerCase());
        }
        String separator = "";

        for (String additionalHeaderName : ts) {
            canonicalString.append(separator).append(additionalHeaderName);
            separator = ";";
        }
        canonicalString.append(SignParameters.NEW_LINE);

        // Append canonical resource to canonical string
        canonicalString.append(buildCanonicalizedResource(resourcePath, request.getParameters()));

        return canonicalString.toString();
    }

    public static String buildSignedURL(GeneratePresignedUrlRequest request, Credentials currentCreds, ClientConfiguration config, URI endpoint) {
        String bucketName = request.getBucketName();
        String accessId = currentCreds.getAccessKeyId();
        String accessKey = currentCreds.getSecretAccessKey();
        boolean useSecurityToken = currentCreds.useSecurityToken();
        HttpMethod method = request.getMethod() != null ? request.getMethod() : HttpMethod.GET;

        String expires = String.valueOf(request.getExpiration().getTime() / 1000L);
        String key = request.getKey();
        String resourcePath = OSSUtils.determineResourcePath(bucketName, key, config.isSLDEnabled());

        RequestMessage requestMessage = new RequestMessage(bucketName, key);
        requestMessage.setEndpoint(OSSUtils.determineFinalEndpoint(endpoint, bucketName, config));
        requestMessage.setMethod(method);
        requestMessage.setResourcePath(resourcePath);
        requestMessage.setHeaders(request.getHeaders());

        requestMessage.addHeader(HttpHeaders.DATE, expires);
        if (request.getContentType() != null && !request.getContentType().trim().equals("")) {
            requestMessage.addHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
        }
        if (request.getContentMD5() != null && request.getContentMD5().trim().equals("")) {
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

        if (request.getProcess() != null && !request.getProcess().trim().equals("")) {
            requestMessage.addParameter(SUBRESOURCE_PROCESS, request.getProcess());
        }

        if (useSecurityToken) {
            requestMessage.addParameter(SECURITY_TOKEN, currentCreds.getSecurityToken());
        }

        String canonicalResource = "/" + ((bucketName != null) ? bucketName : "") + ((key != null ? "/" + key : ""));
        Set<String> rawAdditionalHeaderNames = buildRawAdditionalHeaderNames(request.getHeaders().keySet(), request.getAdditionalHeaderNames());
        Set<String> sortedAdditionalHeaderNames = buildSortedAdditionalHeaderNames(request.getHeaders().keySet(), request.getAdditionalHeaderNames());
        String canonicalString = buildCanonicalString(method.toString(), canonicalResource, requestMessage, rawAdditionalHeaderNames);
        String signature = new HmacSignature(HmacSignature.SHA256).computeSignature(accessKey, canonicalString);

        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(OSS_SIGNATURE_VERSION, "OSS2");
        params.put(OSS_EXPIRES, expires);
        params.put(OSS_ACCESS_KEY_ID_PARAM, accessId);
        params.put(OSS_ADDITIONAL_HEADERS, StringUtils.join(";", sortedAdditionalHeaderNames));
        params.put(OSS_SIGNATURE, signature);
        params.putAll(requestMessage.getParameters());

        String queryString = HttpUtil.paramToQueryString(params, DEFAULT_CHARSET_NAME);

        /* Compse HTTP request uri. */
        String url = requestMessage.getEndpoint().toString();
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += resourcePath + "?" + queryString;
        return url;
    }

    private static String buildCanonicalizedResource(String resourcePath, Map<String, String> parameters) {
        assertTrue(resourcePath.startsWith("/"), "Resource path should start with slash character");

        StringBuilder builder = new StringBuilder();
        builder.append(uriEncoding(resourcePath));

        if (parameters != null) {
            String[] parameterNames = parameters.keySet().toArray(new String[parameters.size()]);
            Arrays.sort(parameterNames);

            char separator = '?';
            for (String paramName : parameterNames) {
                builder.append(separator);
                builder.append(uriEncoding(paramName));
                String paramValue = parameters.get(paramName);
                if (paramValue != null) {
                    builder.append("=").append(uriEncoding(paramValue));
                }

                separator = '&';
            }
        }

        return builder.toString();
    }

    public static String uriEncoding(String uri) {
        String result = "";

        for (char c : uri.toCharArray()) {
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                || (c >= '0' && c <= '9') || c == '_' || c == '-'
                || c == '~' || c == '.') {
                result += c;
            } else if (c == '/') {
                result += "%2F";
            } else {
                String temp = Integer.toHexString((int)c);

                if (temp.length() < 2) {
                    temp = "0" + temp;
                }
                result += "%" + temp.toUpperCase();
            }
        }

        return result;
    }

    public static String buildSignature(String secretAccessKey, String httpMethod, String resourcePath, RequestMessage request) {
        String canonicalString = buildCanonicalString(httpMethod, resourcePath, request,
                buildRawAdditionalHeaderNames(request.getOriginalRequest().getHeaders().keySet(), request.getOriginalRequest().getAdditionalHeaderNames()));
        return new HmacSignature(HmacSignature.SHA256).computeSignature(secretAccessKey, canonicalString);
    }

}
