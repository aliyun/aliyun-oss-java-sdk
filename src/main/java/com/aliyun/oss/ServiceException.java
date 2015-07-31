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
 * 表示阿里云服务返回的错误消息。
 * </p>
 * 
 * <p>
 * {@link ServiceException}用于处理阿里云服务返回的错误消息。比如，用于身份验证的Access ID不存在，
 * 则会抛出{@link ServiceException}（严格上讲，会是该类的一个继承类。比如，OSSClient会抛出OSSException）。
 * 异常中包含了错误代码，用于让调用者进行特定的处理。
 * </p>
 * 
 * <p>
 * {@link ClientException}表示的则是在向阿里云服务发送请求时出现的错误，以及客户端无法处理返回结果。
 * 例如，在发送请求时网络连接不可用，则会抛出{@link ClientException}的异常。
 * </p>
 * 
 * <p>
 * 通常来讲，调用者只需要处理{@link ServiceException}。因为该异常表明请求被服务处理，但处理的结果表明
 * 存在错误。异常中包含了细节的信息，特别是错误代码，可以帮助调用者进行处理。
 * </p>
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 430933593095358673L;
    
    private String errorMessage;
    private String errorCode;
    private String requestId;
    private String hostId;

    /**
     * 构造新实例。
     */
    public ServiceException() {
        super();
    }

    /**
     * 用给定的异常信息构造新实例。
     * @param errorMessage 异常信息。
     */
    public ServiceException(String errorMessage) {
        super((String)null);
        this.errorMessage = errorMessage;
    }

    /**
     * 用表示异常原因的对象构造新实例。
     * @param cause 异常原因。
     */
    public ServiceException(Throwable cause) {
        super(null, cause);
    }

    /**
     * 用异常消息和表示异常原因的对象构造新实例。
     * @param errorMessage 异常信息。
     * @param cause 异常原因。
     */
    public ServiceException(String errorMessage, Throwable cause) {
        super(null, cause);
        this.errorMessage = errorMessage;
    }

    /**
     * 用异常消息和表示异常原因及其他信息的对象构造新实例。
     * @param errorMessage 异常信息。
     * @param errorCode 错误代码。
     * @param requestId Request ID。
     * @param hostId Host ID。
     */
    public ServiceException(String errorMessage, String errorCode, String requestId, String hostId) {
        this(errorMessage, errorCode, requestId, hostId, null);
    }
    
    /**
     * 用异常消息和表示异常原因及其他信息的对象构造新实例。
     * @param errorMessage 异常信息。
     * @param errorCode 错误代码。
     * @param requestId Request ID。
     * @param hostId Host ID。
     * @param cause 异常原因。
     */
    public ServiceException(String errorMessage, String errorCode, String requestId, String hostId, 
    		Throwable cause) {
        this(errorMessage, cause);
        this.errorCode = errorCode;
        this.requestId = requestId;
        this.hostId = hostId;
    }

    /**
     * 返回异常信息。
     * @return 异常信息。
     */
    public String getErrorMessage() {
		return errorMessage;
	}

	/**
     * 返回错误代码的字符串表示。
     * @return 错误代码的字符串表示。
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * 返回Request标识。
     * @return Request标识。
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * 返回Host标识。
     * @return Host标识。
     */
    public String getHostId() {
        return hostId;
    }
    
    @Override
    public String getMessage() {
    	return getErrorMessage() 
    			+ "\n[ErrorCode]: " + getErrorCode()
    			+ "\n[RequestId]: " + getRequestId()
    			+ "\n[HostId]: " + getHostId();
    }
}
