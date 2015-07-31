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

package com.aliyun.oss.common.utils;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.model.OSSErrorResult;

/**
 * A simple factory used for creating instances of <code>ClientException</code> 
 * and <code>OSSException</code>.
 */
public class ExceptionFactory {
	
    public static ClientException createNetworkException(IOException ex) {
        String requestId = "Unknown";
        String errorCode = ClientErrorCode.UNKNOWN;
        
        if (ex instanceof SocketTimeoutException) {
            errorCode = ClientErrorCode.SOCKET_TIMEOUT;
        } else if (ex instanceof SocketException) {
        	errorCode = ClientErrorCode.SOCKET_EXCEPTION;
        } else if (ex instanceof ConnectTimeoutException) {
            errorCode = ClientErrorCode.CONNECTION_TIMEOUT;
        } else if (ex instanceof UnknownHostException) {
           errorCode = ClientErrorCode.UNKNOWN_HOST;
        } else if (ex instanceof HttpHostConnectException) {
        	errorCode = ClientErrorCode.CONNECTION_REFUSED;
        } else if (ex instanceof ClientProtocolException) {
        	Throwable cause = ex.getCause();
        	if (cause instanceof NonRepeatableRequestException) {
        		errorCode = ClientErrorCode.NONREPEATABLE_REQUEST;
        		return new ClientException(requestId, errorCode, cause.getMessage(), cause);
        	}
        }
        
        return new ClientException(ex.getMessage(), errorCode, requestId, ex);
    }
    
    public static ClientException createInvalidResponseException(String requestId, String message, Throwable cause) {
    	return new ClientException(message, ClientErrorCode.INVALID_RESPONSE, requestId, cause);
    }
    
    public static OSSException createOSSException(OSSErrorResult errorResult) {
        return new OSSException(
                errorResult.Message != null ? errorResult.Message : null,
                errorResult.Code != null ? errorResult.Code : null,
                errorResult.RequestId != null ? errorResult.RequestId : null,
                errorResult.HostId != null ? errorResult.HostId : null,
                errorResult.Header != null ? errorResult.Header : null,
                errorResult.ResourceType != null ? errorResult.ResourceType : null,
                errorResult.Method != null ? errorResult.Method : null);
    }

    
    public static OSSException createOSSException(String requestId, String errorCode, String message) {
        return new OSSException(message, errorCode, requestId, null, null, null, null);
    }
}
