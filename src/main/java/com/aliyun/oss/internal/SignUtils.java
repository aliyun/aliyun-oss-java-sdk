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
import static com.aliyun.oss.internal.RequestParameters.PART_NUMBER;
import static com.aliyun.oss.internal.RequestParameters.POSITION;
import static com.aliyun.oss.internal.RequestParameters.SECURITY_TOKEN;
import static com.aliyun.oss.internal.RequestParameters.STYLE_NAME;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_ACL;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_APPEND;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_CORS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_DELETE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_IMG;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LIFECYCLE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LOCATION;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LOGGING;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_REFERER;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_STYLE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_UPLOADS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_WEBSITE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_TAGGING;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_REPLICATION;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_REPLICATION_PROGRESS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_REPLICATION_LOCATION;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_CNAME;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_BUCKET_INFO;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_COMP;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_OBJECTMETA;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_LIVE;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_STATUS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_VOD;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_START_TIME;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_END_TIME;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_PROCESS_CONF;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_PROCESS;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_SYMLINK;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_STAT;
import static com.aliyun.oss.internal.RequestParameters.UPLOAD_ID;
import static com.aliyun.oss.internal.RequestParameters.SUBRESOURCE_QOS;
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

    private static final List<String> SIGNED_PARAMTERS = Arrays.asList(new String[] {
            SUBRESOURCE_ACL, SUBRESOURCE_UPLOADS, SUBRESOURCE_LOCATION, 
            SUBRESOURCE_CORS, SUBRESOURCE_LOGGING, SUBRESOURCE_WEBSITE, 
            SUBRESOURCE_REFERER, SUBRESOURCE_LIFECYCLE, SUBRESOURCE_DELETE, 
            SUBRESOURCE_APPEND, SUBRESOURCE_TAGGING, SUBRESOURCE_OBJECTMETA,
            UPLOAD_ID, PART_NUMBER, SECURITY_TOKEN, POSITION, RESPONSE_HEADER_CACHE_CONTROL, 
            RESPONSE_HEADER_CONTENT_DISPOSITION, RESPONSE_HEADER_CONTENT_ENCODING, 
            RESPONSE_HEADER_CONTENT_LANGUAGE, RESPONSE_HEADER_CONTENT_TYPE, 
            RESPONSE_HEADER_EXPIRES, SUBRESOURCE_IMG,SUBRESOURCE_STYLE,STYLE_NAME,
            SUBRESOURCE_REPLICATION, SUBRESOURCE_REPLICATION_PROGRESS,
            SUBRESOURCE_REPLICATION_LOCATION, SUBRESOURCE_CNAME, 
            SUBRESOURCE_BUCKET_INFO, SUBRESOURCE_COMP, SUBRESOURCE_QOS,
            SUBRESOURCE_LIVE, SUBRESOURCE_STATUS, SUBRESOURCE_VOD, 
            SUBRESOURCE_START_TIME, SUBRESOURCE_END_TIME, SUBRESOURCE_PROCESS,
            SUBRESOURCE_PROCESS_CONF, SUBRESOURCE_SYMLINK, SUBRESOURCE_STAT,  
    });
    
    public static String buildCanonicalString(String method, String resourcePath,
            RequestMessage request, String expires) {
        
        StringBuilder canonicalString = new StringBuilder();
        canonicalString.append(method + NEW_LINE);
        
        Map<String, String> headers = request.getHeaders();
        TreeMap<String, String> headersToSign = new TreeMap<String, String>();
        
        if (headers != null) {
            for(Entry<String, String> header : headers.entrySet()) {
                if (header.getKey() == null) {
                    continue;
                }
                
                String lowerKey = header.getKey().toLowerCase();
                if (lowerKey.equals(HttpHeaders.CONTENT_TYPE.toLowerCase()) || 
                        lowerKey.equals(HttpHeaders.CONTENT_MD5.toLowerCase()) || 
                        lowerKey.equals(HttpHeaders.DATE.toLowerCase()) || 
                        lowerKey.startsWith(OSSHeaders.OSS_PREFIX)) {
                    headersToSign.put(lowerKey, header.getValue().trim());
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
        for(Map.Entry<String, String> entry : headersToSign.entrySet()) {
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
        for(Map.Entry<String, String> entry : request.getParameters().entrySet()) {
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
            String[] parameterNames = parameters.keySet().toArray(
                    new String[parameters.size()]);
            Arrays.sort(parameterNames);
            
            char separater = '?';
            for(String paramName : parameterNames) {
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
