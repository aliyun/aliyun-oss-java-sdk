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

package com.aliyun.oss;

/**
 * <p>
 * 表示OSS端的数据与SDK端的数据不一致。
 * </p>
 * 
 * 
 * <p>
 * 通常来讲，调用者需要处理{@link InconsistentException}。因为该异常表明请求被服务处理，
 * 上传操作已经成功，但是OSS端的数据与SDK端不一致。调用者需要上传上传的文件然后重新删除。
 * </p>
 * 
 * <p>
 * 抛出该异常的操作包括putObject、appendObject、uploadPart、uploadFile等上传操作，
 * getObject下载操作的数据一致性，调用者需要在数据流读取结束后校验，即比较OSS端与SDK端的数据校验和。
 * </p>
 * 
 */
public class InconsistentException extends RuntimeException {

	private static final long serialVersionUID = 2140587868503948665L;

	private Long clientChecksum;
    private Long serverChecksum;
    private String requestId;

	public InconsistentException(Long clientChecksum, Long serverChecksum, String requestId) {
		super();
		this.clientChecksum = clientChecksum;
		this.serverChecksum = serverChecksum;
		this.requestId = requestId;
	}
    
	public Long getClientChecksum() {
		return clientChecksum;
	}

	public void setClientChecksum(Long clientChecksum) {
		this.clientChecksum = clientChecksum;
	}

	public Long getServerChecksum() {
		return serverChecksum;
	}

	public void setServerChecksum(Long serverChecksum) {
		this.serverChecksum = serverChecksum;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
   
	@Override
    public String getMessage() {
		return "InconsistentException " + "\n[RequestId]: " + getRequestId()
        + "\n[ClientChecksum]: " + getClientChecksum()
        + "\n[ServerChecksum]: " + getServerChecksum();
    }
	
}
