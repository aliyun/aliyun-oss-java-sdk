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

package com.aliyun.oss.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.aliyun.oss.event.ProgressListener;

public abstract class WebServiceRequest {
    
    public static final WebServiceRequest NOOP = new WebServiceRequest() { };
    
    private ProgressListener progressListener = ProgressListener.NOOP;
    
    private Map<String, String> parameters = new LinkedHashMap<String, String>();
    private Map<String, String> headers = new LinkedHashMap<String, String>();
    private Set<String> signatureFields = new TreeSet<String>();
    
	public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = (progressListener == null) ? 
                ProgressListener.NOOP : progressListener;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }

    public <T extends WebServiceRequest> T withProgressListener(ProgressListener progressListener) {
        setProgressListener(progressListener);
        @SuppressWarnings("unchecked") 
        T t = (T)this;
        return t;
    }
    
    public Map<String, String> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }
    
    public Set<String> getSignatureFields() {
		return signatureFields;
	}

	public void addSignatureField(String signatureField) {
		this.signatureFields.add(signatureField);
	}
	
	public void setSignatureFields(Set<String> signatureFields) {
		this.signatureFields = signatureFields;
	}
}
