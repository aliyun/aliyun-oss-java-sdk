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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.aliyun.oss.HttpMethod;

/**
 * 生成带有签名信息的URL的请求。
 */
public class GeneratePresignedUrlRequest {

    /** The HTTP method (GET, PUT, DELETE, HEAD) to be used in this request and when the pre-signed URL is used */
    private HttpMethod method;

    /** The name of the bucket involved in this request */
    private String bucketName;

    /** The key of the object involved in this request */
    private String key;
 
    /** Content-Type to url sign */
    private String contentType;

    /** Content-MD5 */
    private String contentMD5;

    /**
     * An optional expiration date at which point the generated pre-signed URL
     * will no longer be accepted by OSS. If not specified, a default
     * value will be supplied.
     */
    private Date expiration;

    // 要重载的返回请求头。
    private ResponseHeaderOverrides responseHeaders = new ResponseHeaderOverrides();

    // 用户自定义的元数据，表示以x-oss-meta-为前缀的请求头。
    private Map<String, String> userMetadata = new HashMap<String, String>();
    
    private Map<String, String> queryParam = new HashMap<String, String>(); 
    /**
     * 构造函数。默认的HTTP Method为{@link HttpMethod#GET}。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object key。
     */
    public GeneratePresignedUrlRequest(String bucketName, String key) {
        this(bucketName, key, HttpMethod.GET);
    }

    /**
     * 构造函数。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object key。
     * @param method
     *          {@link HttpMethod#GET}。
     */
    public GeneratePresignedUrlRequest(String bucketName, String key, HttpMethod method) {
        this.bucketName = bucketName;
        this.key = key;
        this.method = method;
    }

    /**
     * 返回HTTP访问方法。
     * @return HTTP访问方法。
     */
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * 设置HTTP访问方法。
     * @param method
     *          HTTP访问方法。
     */
    public void setMethod(HttpMethod method) {
        if (method != HttpMethod.GET && method != HttpMethod.PUT)
            throw new IllegalArgumentException("仅支持GET和PUT方法。");

        this.method = method;
    }

    /**
     * 返回{@link Bucket}名称。
     * @return Bucket名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置{@link Bucket}名称。
     * @param bucketName
     *          {@link Bucket}名称。
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 返回{@link OSSObject} key。
     * @return  Object key。
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置{@link OSSObject} key。
     * @param key
     *          {@link OSSObject} key。
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 返回生成的URL的超时时间。
     * @return 生成的URL的超时时间。
     */
    public Date getExpiration() {
        return expiration;
    }

    /**
     * 设置生成的URL的超时时间。。
     * @param expiration
     *          生成的URL的超时时间。
     */
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    /**
     * 设置签名URL对应的文件类型ContentType
     * @param contentType
     *     上传文件对应的Content-Type
     */
    public void setContentType(String contentType){
       this.contentType = contentType;
    }

    /**
     * 返回文件类型
     * @return Content-Type Header
     */
    public String getContentType(){
       return this.contentType;
    }

    /**
     * 设置签名URL对应文件的MD5
     * @param contentMD5
     *     文件对应的Content-MD5
     */
    public void setContentMD5(String contentMD5) {
       this.contentMD5 = contentMD5;
    }

    /**
     * 返回文件内容的MD5
     * @return Content-MD5 
     */
    public String getContentMD5() {
       return this.contentMD5;
    }

    /**
     * 设置要重载的返回请求头（可选）。
     * @param responseHeaders
     *          要重载的返回请求头。
     */
    public void setResponseHeaders(ResponseHeaderOverrides responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    /**
     * 返回要重载的返回请求头。
     * @return 要重载的返回请求头。
     */
    public ResponseHeaderOverrides getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * <p>
     * 获取用户自定义的元数据。
     * </p>
     * <p>
     * OSS内部保存用户自定义的元数据时，会以x-oss-meta-为请求头的前缀。
     * 但用户通过该接口处理用户自定义元数据里，不需要加上前缀“x-oss-meta-”。
     * 同时，元数据字典的键名是不区分大小写的，并且在从服务器端返回时会全部以小写形式返回，
     * 即使在设置时给定了大写字母。比如键名为：MyUserMeta，通过getObjectMetadata接口
     * 返回时键名会变为：myusermeta。
     * </p>
     * @return 用户自定义的元数据。
     */
    public Map<String, String> getUserMetadata() {
        return userMetadata;
    }

    /**
     * 设置用户自定义的元数据，表示以x-oss-meta-为前缀的请求头。
     * @param userMetadata
     *          用户自定义的元数据。
     */
    public void setUserMetadata(Map<String, String> userMetadata) {
        if (userMetadata == null) {
            throw new NullPointerException("参数'userMeta'为空指针。");
        }
        this.userMetadata = userMetadata;
    }
    
    /**
     * 添加一个用户自定义的元数据。
     * @param key
     *          请求头的Key。
     *          这个Key不需要包含OSS要求的前缀，即不需要加入“x-oss-meta-”。
     * @param value
     *          请求头的Value。
     */
    public void addUserMetadata(String key, String value) {
        this.userMetadata.put(key, value);
    }

    public Map<String,String> getQueryParameter(){
    	return this.queryParam;
    }
    /**
     * 用户请求参数，Query String。
     * @param queryParam
     *          QueryString。
     */
    public void setQueryParameter(Map<String, String> queryParam) {
        if (queryParam == null){
            throw new NullPointerException("参数'queryParameter'为空指针。");
        }
        this.queryParam = queryParam;
    }
    
    /**
     * @param paramter key
     * @param value
     */
    public void addQueryParameter(String key, String value) {
        this.queryParam.put(key, value);
    }

}
