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

import java.io.InputStream;

/**
 * 上传UDF Image请求。
 * 
 * UDF Image有格式要求，详见API说明。
 */
public class UploadUdfImageRequest extends UdfGenericRequest {
	
	/**
	 * 构造函数。
	 * @param udfName UDF名称。
	 * @param udfImage Image有格式要求，详见API说明。
	 */
	public UploadUdfImageRequest(String udfName, InputStream udfImage) {
		super(udfName);
		this.udfImage = udfImage;
	}
	
	/**
	 * 构造函数。
	 * @param udfName UDF名称。
	 * @param udfImageDesc Image描述信息。
	 * @param udfImage Image有格式要求，详见API说明。
	 */
	public UploadUdfImageRequest(String udfName, String udfImageDesc, InputStream udfImage) {
		super(udfName);
		this.udfImageDesc = udfImageDesc;
		this.udfImage = udfImage;
	}

	public String getUdfImageDesc() {
		return udfImageDesc;
	}

	public void setUdfImageDesc(String udfImageDesc) {
		this.udfImageDesc = udfImageDesc;
	}

	public InputStream getUdfImage() {
		return udfImage;
	}

	public void setUdfImage(InputStream udfImage) {
		this.udfImage = udfImage;
	}

	private String udfImageDesc;
    private InputStream udfImage;
}
