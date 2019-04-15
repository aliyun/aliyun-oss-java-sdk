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

import static com.aliyun.oss.common.utils.CodingUtils.assertTrue;
import static com.aliyun.oss.internal.RequestParameters.*;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_CACHE_CONTROL;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_DISPOSITION;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_ENCODING;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_LANGUAGE;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_TYPE;
import static com.aliyun.oss.model.ResponseHeaderOverrides.RESPONSE_HEADER_EXPIRES;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.utils.HttpHeaders;

public class SignUtils {

    private static final String NEW_LINE = "\n";

    private static final List<String> SIGNED_PARAMTERS = Arrays.asList(new String[] { SUBRESOURCE_ACL,
            SUBRESOURCE_UPLOADS, SUBRESOURCE_LOCATION, SUBRESOURCE_CORS, SUBRESOURCE_LOGGING, SUBRESOURCE_WEBSITE,
            SUBRESOURCE_REFERER, SUBRESOURCE_LIFECYCLE, SUBRESOURCE_DELETE, SUBRESOURCE_APPEND, SUBRESOURCE_TAGGING,
            SUBRESOURCE_OBJECTMETA, UPLOAD_ID, PART_NUMBER, SECURITY_TOKEN, POSITION, RESPONSE_HEADER_CACHE_CONTROL,
            RESPONSE_HEADER_CONTENT_DISPOSITION, RESPONSE_HEADER_CONTENT_ENCODING, RESPONSE_HEADER_CONTENT_LANGUAGE,
            RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_EXPIRES, SUBRESOURCE_IMG, SUBRESOURCE_STYLE, STYLE_NAME,
            SUBRESOURCE_REPLICATION, SUBRESOURCE_REPLICATION_PROGRESS, SUBRESOURCE_REPLICATION_LOCATION,
            SUBRESOURCE_CNAME, SUBRESOURCE_BUCKET_INFO, SUBRESOURCE_COMP, SUBRESOURCE_QOS, SUBRESOURCE_LIVE,
            SUBRESOURCE_STATUS, SUBRESOURCE_VOD, SUBRESOURCE_START_TIME, SUBRESOURCE_END_TIME, SUBRESOURCE_PROCESS,
            SUBRESOURCE_PROCESS_CONF, SUBRESOURCE_SYMLINK, SUBRESOURCE_STAT, SUBRESOURCE_UDF, SUBRESOURCE_UDF_NAME,
            SUBRESOURCE_UDF_IMAGE, SUBRESOURCE_UDF_IMAGE_DESC, SUBRESOURCE_UDF_APPLICATION, SUBRESOURCE_UDF_LOG,
            SUBRESOURCE_RESTORE, SUBRESOURCE_REQUEST_PAYMENT,SUBRESOURCE_CSV_SELECT, SUBRESOURCE_CSV_META, SUBRESOURCE_SQL,
            SUBRESOURCE_WORM, SUBRESOURCE_WORM_ID, SUBRESOURCE_WORM_EXTEND, SUBRESOURCE_USER_REGION, SUBRESOURCE_REGION_LIST,
            SUBRESOURCE_NOTIFICATION, SUBRESOURCE_ENCRYPTION, SUBRESOURCE_VPC_ID, 
            SUBRESOURCE_VERSIONING, SUBRESOURCE_VERSIONS, VERSION_ID});

    public static String buildCanonicalString(String method, String resourcePath, RequestMessage request,
            String expires) {

        StringBuilder canonicalString = new StringBuilder();
        canonicalString.append(method + NEW_LINE);

        Map<String, String> headers = request.getHeaders();
        TreeMap<String, String> headersToSign = new TreeMap<String, String>();

        if (headers != null) {
            for (Entry<String, String> header : headers.entrySet()) {
                if (header.getKey() == null) {
                    continue;
                }

                String lowerKey = header.getKey().toLowerCase();
                if (lowerKey.equals(HttpHeaders.CONTENT_TYPE.toLowerCase())
                        || lowerKey.equals(HttpHeaders.CONTENT_MD5.toLowerCase())
                        || lowerKey.equals(HttpHeaders.DATE.toLowerCase())
                        || lowerKey.startsWith(OSSHeaders.OSS_PREFIX)) {
                    if (header.getValue() != null) {
                        headersToSign.put(lowerKey, header.getValue().trim());
                    }
                }
            }
        }

        if (!headersToSign.containsKey(HttpHeaders.CONTENT_TYPE.toLowerCase())) {
            headersToSign.put(HttpHeaders.CONTENT_TYPE.toLowerCase(), "");
        }
        if (!headersToSign.containsKey(HttpHeaders.CONTENT_MD5.toLowerCase())) {
            headersToSign.put(HttpHeaders.CONTENT_MD5.toLowerCase(), "");
        }

        // Append all headers to sign to canonical string
        for (Map.Entry<String, String> entry : headersToSign.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.startsWith(OSSHeaders.OSS_PREFIX)) {
                canonicalString.append(key).append(':').append(value);
            } else {
                canonicalString.append(value);
            }

            canonicalString.append(NEW_LINE);
        }

        // Append canonical resource to canonical string
        canonicalString.append(buildCanonicalizedResource(resourcePath, request.getParameters()));

        return canonicalString.toString();
    }

    public static String buildRtmpCanonicalString(String canonicalizedResource, RequestMessage request,
            String expires) {

        StringBuilder canonicalString = new StringBuilder();

        // Append expires
        canonicalString.append(expires + NEW_LINE);

        // Append canonicalized parameters
        for (Map.Entry<String, String> entry : request.getParameters().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            canonicalString.append(key).append(':').append(value);
            canonicalString.append(NEW_LINE);
        }

        // Append canonicalized resource
        canonicalString.append(canonicalizedResource);

        return canonicalString.toString();
    }

    private static String buildCanonicalizedResource(String resourcePath, Map<String, String> parameters) {

        assertTrue(resourcePath.startsWith("/"), "Resource path should start with slash character");

        StringBuilder builder = new StringBuilder();
        builder.append(resourcePath);

        if (parameters != null) {
            String[] parameterNames = parameters.keySet().toArray(new String[parameters.size()]);
            Arrays.sort(parameterNames);

            char separater = '?';
            for (String paramName : parameterNames) {
                if (!SIGNED_PARAMTERS.contains(paramName)) {
                    continue;
                }

                builder.append(separater);
                builder.append(paramName);
                String paramValue = parameters.get(paramName);
                if (paramValue != null) {
                    builder.append("=").append(paramValue);
                }

                separater = '&';
            }
        }

        return builder.toString();
    }
}
