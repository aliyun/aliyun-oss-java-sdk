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
 * 包含列出所有执行中Multipart上传事件的请求参数。
 *
 */
public class ListMultipartUploadsRequest extends GenericRequest {

    private String delimiter;

    private String prefix;

    private Integer maxUploads;

    private String keyMarker;

    private String uploadIdMarker;
    
    private String encodingType;

    /**
     * 构造函数。
     * @param bucketName
     *          Bucket名称。
     */
    public ListMultipartUploadsRequest(String bucketName) {
        super(bucketName);
    }

    /**
     * 返回限制的最大返回记录数。
     * @return 限制的最大返回记录数。
     */
    public Integer getMaxUploads() {
        return maxUploads;
    }

    /**
     * 设置限制的最大返回记录数。
     * 最大值和默认值均为1000。
     * @param maxUploads
     *          限制的最大返回记录数。
     */
    public void setMaxUploads(Integer maxUploads) {
        this.maxUploads = maxUploads;
    }

    /**
     * 返回一个标识表示从哪里返回列表。
     * @return 标识表示从哪里返回列表。
     */
    public String getKeyMarker() {
        return keyMarker;
    }

    /**
     * 设置一个标识表示从哪里返回列表。（可选）
     * @param keyMarker
     *          标识表示从哪里返回列表。
     */
    public void setKeyMarker(String keyMarker) {
        this.keyMarker = keyMarker;
    }
    
    /**
     * 返回一个标识表示从哪里返回列表。
     * @return 标识表示从哪里返回列表。
     */
    public String getUploadIdMarker() {
        return uploadIdMarker;
    }

    /**
     * 设置一个标识表示从哪里返回列表。（可选）
     * @param uploadIdMarker
     *          标识表示从哪里返回列表。
     */
    public void setUploadIdMarker(String uploadIdMarker) {
        this.uploadIdMarker = uploadIdMarker;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    /**
     * 获取应用于请求响应体中Object名称的编码方式。

     * @return 请求响应体中Object名称的编码方式。
     */
    public String getEncodingType() {
        return encodingType;
    }

    /**
     * 设置应用于请求响应体中Object名称的编码方式。
     * 
     * @param encodingType
     *            请求响应体中Object名称的编码方式。
     *            有效值: null (不进行编码处理) 或 "url".
     */
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

}
