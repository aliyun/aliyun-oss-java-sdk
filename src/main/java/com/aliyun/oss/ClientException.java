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
 * 表示尝试访问阿里云服务时遇到的异常。
 * </p>
 * 
 * <p>
 * {@link ClientException}表示的则是在向阿里云服务发送请求时出现的错误，以及客户端无法处理返回结果。
 * 例如，在发送请求时网络连接不可用，则会抛出{@link ClientException}的异常。
 * </p>
 * 
 * <p>
 * {@link ServiceException}用于处理阿里云服务返回的错误消息。比如，用于身份验证的Access ID不存在，
 * 则会抛出{@link ServiceException}（严格上讲，会是该类的一个继承类。比如，OSSClient会抛出OSSException）。
 * 异常中包含了错误代码，用于让调用者进行特定的处理。
 * </p>
 * 
 * <p>
 * 通常来讲，调用者只需要处理{@link ServiceException}。因为该异常表明请求被服务处理，但处理的结果表明
 * 存在错误。异常中包含了细节的信息，特别是错误代码，可以帮助调用者进行处理。
 * </p>
 * 
 */
public class ClientException extends RuntimeException {
    
    private static final long serialVersionUID = 1870835486798448798L;
    
    private String errorMessage;
    private String requestId;
    private String errorCode;
    
    /**
     * 构造新实例。
     */
    public ClientException(){
        super();
    }

    /**
     * 用给定的异常信息构造新实例。
     * @param errorMessage 异常信息。
     */
    public ClientException(String errorMessage) {
    	this(errorMessage, null);
    }

    /**
     * 用表示异常原因的对象构造新实例。
     * @param cause 异常原因。
     */
    public ClientException(Throwable cause) {
    	this(null, cause);
    }
    
    /**
     * 用异常消息和表示异常原因的对象构造新实例。
     * @param errorMessage 异常信息。
     * @param cause 异常原因。
     */
    public ClientException(String errorMessage, Throwable cause) {
        super(null, cause);
        this.errorMessage = errorMessage;
        this.errorCode = ClientErrorCode.UNKNOWN;
        this.requestId = "Unknown";
    }
    
    /**
     * 用异常消息构造新实例。
     * @param errorMessage 异常信息。
     * @param errorCode 错误码。
     * @param requestId 请求编号。
     */
    public ClientException(String errorMessage, String errorCode, String requestId) {
        this(errorMessage, errorCode, requestId, null);
    }

    /**
     * 用异常消息和表示异常原因的对象构造新实例。
     * @param errorMessage 异常信息。
     * @param errorCode 错误码。
     * @param requestId 请求编号。
     * @param cause 异常原因。
     */
    public ClientException(String errorMessage, String errorCode, String requestId, Throwable cause) {
        this(errorMessage, cause);
        this.errorCode = errorCode;
        this.requestId = requestId;
    }

    /**
     * 获取异常信息。
     * @return 异常信息。
     */
	public String getErrorMessage() {
		return errorMessage;
	}
	
    /**
     * 获取异常的错误码
     * @return 异常错误码
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 获取本次异常的 RequestId
     * @return 本次异常的 RequestId
     */
    public String getRequestId() {
       return requestId;
    }

	@Override
    public String getMessage() {
    	return getErrorMessage() 
    			+ "\n[ErrorCode]: " + getErrorCode()
    			+ "\n[RequestId]: " + getRequestId();
    }
}
