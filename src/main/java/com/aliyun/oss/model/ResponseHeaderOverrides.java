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

/**
 * 包含了在发送OSS GET请求时可以重载的返回请求头。
 */
public class ResponseHeaderOverrides {

    private String contentType;
    private String contentLangauge;
    private String expires;
    private String cacheControl;
    private String contentDisposition;
    private String contentEncoding;
    
    public static final String RESPONSE_HEADER_CONTENT_TYPE = "response-content-type";
    public static final String RESPONSE_HEADER_CONTENT_LANGUAGE = "response-content-language";
    public static final String RESPONSE_HEADER_EXPIRES = "response-expires";
    public static final String RESPONSE_HEADER_CACHE_CONTROL = "response-cache-control";
    public static final String RESPONSE_HEADER_CONTENT_DISPOSITION = "response-content-disposition";
    public static final String RESPONSE_HEADER_CONTENT_ENCODING = "response-content-encoding";
    
    /**
     * 返回重载的content type返回请求头。如果未指定，则返回null。
     * @return 重载的content type请求头。如果未指定，则返回null。
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * 设置重载的content type返回请求头。
     * @param contentType
     *          重载的content type返回请求头。
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**返回重载的content language返回请求头。如果未指定，则返回null。
     * @return 重载的content language返回请求头。
     */
    public String getContentLangauge() {
        return contentLangauge;
    }

    /**
     * 设置重载的content language返回请求头。
     * @param contentLangauge
     *          重载的content language返回请求头。
     */
    public void setContentLangauge(String contentLangauge) {
        this.contentLangauge = contentLangauge;
    }

    /**
     * 返回重载的expires返回请求头。如果未指定，则返回null。
     * @return 重载的expires返回请求头。
     */
    public String getExpires() {
        return expires;
    }

    /**
     * 设置重载的expires返回请求头。
     * @param expires
     *          重载的expires返回请求头。
     */
    public void setExpires(String expires) {
        this.expires = expires;
    }

    /**
     * 返回重载的cacheControl返回请求头。如果未指定，则返回null。
     * @return 重载的cacheControl返回请求头。
     */
    public String getCacheControl() {
        return cacheControl;
    }

    /**
     * 设置重载的cacheControl返回请求头。
     * @param cacheControl
     *          重载的cacheControl返回请求头。
     */
    public void setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
    }

    /**
     * 返回重载的contentDisposition返回请求头。如果未指定，则返回null。
     * @return 重载的contentDisposition返回请求头。
     */
    public String getContentDisposition() {
        return contentDisposition;
    }

    /**
     * 设置重载的contentDisposition返回请求头。
     * @param contentDisposition
     *          重载的contentDisposition返回请求头。
     */
    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    /**
     * 返回重载的contentEncoding返回请求头。如果未指定，则返回null。
     * @return 重载的contentEncoding返回请求头。
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * 设置重载的contentEncoding返回请求头。
     * @param contentEncoding
     *          重载的contentEncoding返回请求头。
     */
    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }
}
