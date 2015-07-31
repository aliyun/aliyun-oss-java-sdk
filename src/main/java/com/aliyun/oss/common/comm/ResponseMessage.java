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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.common.utils.ResourceManager;
import com.aliyun.oss.internal.OSSConstants;
import com.aliyun.oss.internal.OSSHeaders;

public class ResponseMessage extends HttpMesssage {
	
	private static final int HTTP_SUCCESS_STATUS_CODE = 200;
	private static ResourceManager rm = ResourceManager.getInstance(OSSConstants.RESOURCE_NAME_COMMON);
    
	private String uri;
    private int statusCode;

    private ServiceClient.Request request;
    
    public ResponseMessage(ServiceClient.Request request) {
    	this.request = request;
    }

    public String getUri() {
        return uri;
    }

    public void setUrl(String uri) {
        this.uri = uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
 
    public String getRequestId() {
       return getHeaders().get(OSSHeaders.OSS_HEADER_REQUEST_ID);
    }

    public ServiceClient.Request getRequest() {
		return request;
	}
    
	public boolean isSuccessful(){
        return statusCode / 100 == HTTP_SUCCESS_STATUS_CODE / 100;
    }

    public String getDebugInfo() throws ClientException {
        String debugInfo = "Response Header:\n" + getHeaders().toString() +
        	"\nResponse Content:\n";
        InputStream inStream = getContent();
        if (inStream == null) {
        	return debugInfo;
        }
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            outStream.flush();
            debugInfo += outStream.toString("utf-8");
            setContent(new ByteArrayInputStream(outStream.toByteArray()));
            //outStream.close(); //close has no effect
            return debugInfo;
        } catch (IOException e) {
            throw new ClientException(getRequestId(), ClientErrorCode.INVALID_RESPONSE,
                rm.getFormattedString("FailedToParseResponse", e.getMessage()), e);
        }
    }
}
