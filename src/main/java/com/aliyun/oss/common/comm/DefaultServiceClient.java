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

package com.aliyun.oss.common.comm;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.utils.ExceptionFactory;
import com.aliyun.oss.common.utils.HttpHeaders;
import com.aliyun.oss.common.utils.HttpUtil;

/**
 * Default implementation of {@link ServiceClient}.
 */
public class DefaultServiceClient extends ServiceClient {
	
	private static HttpClientFactory httpClientFactory = new HttpClientFactory();
	private static HttpRequestFactory httpRequestFactory = new HttpRequestFactory();

    private HttpClient httpClient;

    public DefaultServiceClient(ClientConfiguration config) {
        super(config);
        httpClient = httpClientFactory.createHttpClient(this.config);
    }

    @Override
    public ResponseMessage sendRequestCore(ServiceClient.Request request, ExecutionContext context)
            throws IOException {        
        HttpRequestBase httpRequest = httpRequestFactory.createHttpRequest(request, context);

        HttpResponse apacheHttpResponse = null;
        try {
        	apacheHttpResponse = httpClient.execute(httpRequest);
        } catch (IOException ex) {
        	httpRequest.abort();
            throw ExceptionFactory.createNetworkException(ex);
        } 

        return buildResponse(request, apacheHttpResponse);
    }
    
    private static ResponseMessage buildResponse(ServiceClient.Request request, 
    		HttpResponse apacheHttpResponse) throws IOException {
    	
    	ResponseMessage response = new ResponseMessage(request);

    	response.setUrl(request.getUri());
        if (apacheHttpResponse.getStatusLine() != null) {
            response.setStatusCode(apacheHttpResponse.getStatusLine().getStatusCode());
        }
        if (apacheHttpResponse.getEntity() != null) {
            response.setContent(apacheHttpResponse.getEntity().getContent());
        }

        for (Header header : apacheHttpResponse.getAllHeaders()) {
        	if (HttpHeaders.CONTENT_LENGTH.equals(header.getName())) {
        		response.setContentLength(Long.parseLong(header.getValue()));
        	}
            response.addHeader(header.getName(), header.getValue());
        }
        HttpUtil.convertHeaderCharsetFromIso88591(response.getHeaders());

        return response;
    }
    
    private static class DefaultRetryStrategy extends RetryStrategy {
        
        @Override
        public boolean shouldRetry(Exception ex, RequestMessage request, ResponseMessage response, int retries) {
            if (ex instanceof ClientException) {
                String errorCode = ((ClientException) ex).getErrorCode();
                if (errorCode.equals(ClientErrorCode.CONNECTION_TIMEOUT)
                        || errorCode.equals(ClientErrorCode.SOCKET_TIMEOUT)
                        || errorCode.equals(ClientErrorCode.CONNECTION_REFUSED)
                        || errorCode.equals(ClientErrorCode.UNKNOWN_HOST)
                        || errorCode.equals(ClientErrorCode.SOCKET_EXCEPTION)) {
                    return true;
                }
                // Don't retry when request input stream is non-repeatable
                if (errorCode.equals(ClientErrorCode.NONREPEATABLE_REQUEST)) {
                	return false;
                }
            }
            
            if (response != null) {
                int statusCode = response.getStatusCode();
                if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR ||
                    statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    return true;
                }
            }
            
            return false;
        }
    }
    
    @Override
    protected RetryStrategy getDefaultRetryStrategy() {
        return new DefaultRetryStrategy();
    }

	@SuppressWarnings("deprecation")
	@Override
	public void shutdown() {
		IdleConnectionReaper.removeConnectionManager(httpClient.getConnectionManager());
		httpClient.getConnectionManager().shutdown();
	}
}
