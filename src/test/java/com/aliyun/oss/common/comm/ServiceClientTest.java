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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.common.comm.DefaultServiceClient;
import com.aliyun.oss.common.comm.ExecutionContext;
import com.aliyun.oss.common.comm.RequestMessage;
import com.aliyun.oss.common.comm.ResponseHandler;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.internal.OSSConstants;
import com.aliyun.oss.utils.ResourceUtils;
import com.aliyun.oss.utils.StreamUtils;

public class ServiceClientTest {
    
    static class ServiceClientImpl extends DefaultServiceClient {

        private int requestAttemps = 0;
        private int maxFailureCount;
        private int statusCode;
        private ClientException exceptionToThrow;
        private String expectedContent;
                
        public int getRequestAttempts(){
            return requestAttemps;
        }

        protected ServiceClientImpl(ClientConfiguration config, int maxFailureCount,
                ClientException exceptionToThrow, int statusCode, String expectedContent) {
            super(config);
            this.maxFailureCount = maxFailureCount;
            this.exceptionToThrow = exceptionToThrow;
            this.expectedContent = expectedContent;
            this.statusCode = statusCode;
        }

        @Override
        public ResponseMessage sendRequestCore(Request request,
                ExecutionContext context) throws IOException {            
            // verify content stream
            if (this.expectedContent != null){
                assertTrue(request.getContent() != null);
                assertEquals(expectedContent, StreamUtils.readContent(request.getContent(), OSSConstants.DEFAULT_CHARSET_NAME));
            }

            if (++requestAttemps <= maxFailureCount){
                // make the request fail.
                throw exceptionToThrow;
            }
            
            ResponseMessage response = new ResponseMessage(null);
            response.setStatusCode(statusCode);

            return response;
        }
    }
    
    
    static ClientException createRetryableException() {
        return new ClientException(null, ClientErrorCode.CONNECTION_TIMEOUT, "requestid", null);
    }

    @Test
    public void testRetryWillSucceed() throws Exception{
        // Fix the max error retry count to 3
        final int MAX_RETRIES = 3;
        ClientConfiguration config = new ClientConfiguration();
        config.setMaxErrorRetry(MAX_RETRIES);
        int maxFailures = 3;

        final long skipBeforeSend = 3;
        String content = "Let's retry!";
        byte[] contentBytes = content.getBytes(OSSConstants.DEFAULT_CHARSET_NAME);
        ByteArrayInputStream contentStream = new ByteArrayInputStream(contentBytes);
        contentStream.skip(skipBeforeSend);
        
        RequestMessage request = new RequestMessage(null, null);
        request.setEndpoint(new URI("http://localhost"));
        request.setMethod(HttpMethod.GET);
        request.setContent(contentStream);
        request.setContentLength(contentBytes.length - skipBeforeSend);

        ExecutionContext context = new ExecutionContext();

        ClientException exceptionToThrow = createRetryableException();

        // This request will succeed after 2 retries
        ServiceClientImpl client =
                new ServiceClientImpl(config, maxFailures, exceptionToThrow, 200, content.substring((int)skipBeforeSend));
        client.sendRequest(request, context);
        assertEquals(MAX_RETRIES + 1, client.getRequestAttempts());
    }
    
    @Test
    public void testRetryWillFail() throws Exception{

        retryButFail(3);
    }

    @Test
    public void testNoRetry() throws Exception{
        retryButFail(0);
    }

    private void retryButFail(final int maxRetries)
            throws UnsupportedEncodingException, URISyntaxException,
            ServiceException {
        // This request will fail after 3 retries
        ClientConfiguration config = new ClientConfiguration();
        config.setMaxErrorRetry(maxRetries);
        int maxFailures = 4;

        String content = "Let's retry!";
        byte[] contentBytes = content.getBytes(OSSConstants.DEFAULT_CHARSET_NAME);
        ByteArrayInputStream contentStream = new ByteArrayInputStream(contentBytes);

        RequestMessage request = new RequestMessage(null ,null);
        request.setEndpoint(new URI("http://localhost"));
        request.setMethod(HttpMethod.GET);
        request.setContent(contentStream);
        request.setContentLength(contentBytes.length);

        ExecutionContext context = new ExecutionContext();

        ClientException exceptionToThrown = createRetryableException();

        ServiceClientImpl client = new ServiceClientImpl(config, maxFailures, exceptionToThrown, 200, content);

        try{
            client.sendRequest(request, context);
            fail("ClientException has not been thrown.");
        } catch (ClientException e){
            assertEquals(exceptionToThrown, e);
            assertEquals(maxRetries + 1, client.getRequestAttempts());
        }
    }

    @Test
    public void testRetryWithServiceException() throws Exception{
        // This request will fail after 1 retries
        ClientConfiguration config = new ClientConfiguration();
        config.setMaxErrorRetry(1);
        int maxFailures = 0; // It should be always successful.

        String content = "Let's retry!";
        byte[] contentBytes = content.getBytes(OSSConstants.DEFAULT_CHARSET_NAME);
        ByteArrayInputStream contentStream = new ByteArrayInputStream(contentBytes);

        RequestMessage request = new RequestMessage(null, null);
        request.setEndpoint(new URI("http://localhost"));
        request.setMethod(HttpMethod.GET);
        request.setContent(contentStream);
        request.setContentLength(contentBytes.length);

        ExecutionContext context = new ExecutionContext();
        context.getResponseHandlers().add(new ResponseHandler() {
            
            @Override
            public void handle(ResponseMessage responseData) throws ServiceException,
                    ClientException {
                throw new ServiceException();
            }
        });

        // This request will succeed after 2 retries
        ServiceClientImpl client = new ServiceClientImpl(config, maxFailures, null, 500, content);
        // Fix the max error retry count to 3
        try{
            client.sendRequest(request, context);
            fail("ServiceException has not been thrown.");
        } catch (ServiceException e){
            assertEquals(2, client.getRequestAttempts());
        }
    }

    @Test
    public void testRetryWithNoneMarkSupportedStream() throws Exception {
        String filename = ResourceUtils.getTestFilename("oss/listBucket.xml");
        File file = new File(filename);
        InputStream contentStream = new FileInputStream(file);
        RequestMessage request = new RequestMessage(null, null);
        request.setEndpoint(new URI("http://localhost"));
        request.setMethod(HttpMethod.GET);
        request.setContent(contentStream);
        request.setContentLength(file.length());

        ExecutionContext context = new ExecutionContext();

        ClientException exceptionToThrown = createRetryableException();
        String content = "";
        InputStream contentStream2 = new FileInputStream(file);
        try{
            content = StreamUtils.readContent(contentStream2, "utf-8");
        } finally {
            contentStream2.close();
        }

        // This request will succeed after 2 retries
        ServiceClientImpl client = new ServiceClientImpl(
                new ClientConfiguration(), 3,
                exceptionToThrown, 400, content);
        // Fix the max error retry count to 3
        try{
            client.sendRequest(request, context);
            fail("ClientException has not been thrown.");
        } catch (ClientException e){
            assertEquals(exceptionToThrown, e);
            assertEquals(1, client.getRequestAttempts());
        }
    }

    @Test
    public void testResponseMessageAbort()  {
        try {
            ResponseMessage responseMessage = new ResponseMessage(new ServiceClient.Request());
            Assert.assertNull(responseMessage.getHttpResponse());
            responseMessage.abort();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
