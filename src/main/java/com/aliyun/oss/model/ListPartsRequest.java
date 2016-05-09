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
 * 包含列出Part的请求参数。
 *
 */
public class ListPartsRequest extends GenericRequest {

    private String uploadId;

    private Integer maxParts;

    private Integer partNumberMarker;
    
    private String encodingType;

    /**
     * 构造函数。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object key。
     * @param uploadId
     *          Mutlipart上传事件的Upload ID。
     */
    public ListPartsRequest(String bucketName, String key, String uploadId) {
        super(bucketName, key);
        this.uploadId = uploadId;
    }

    /**
     * 返回标识Multipart上传事件的Upload ID。
     * @return 标识Multipart上传事件的Upload ID。
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * 设置标识Multipart上传事件的Upload ID。
     * @param uploadId
     *          标识Multipart上传事件的Upload ID。
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * 返回一个值表示最大返回多少条记录。（默认值1000）
     * @return 最大返回多少条记录。
     */
    public Integer getMaxParts() {
        return maxParts;
    }

    /**
     * 设置一个值最大返回多少条记录。（可选）
     * 最大值和默认值均为1000。
     * @param maxParts
     *          最大返回多少条记录。
     */
    public void setMaxParts(int maxParts) {
        this.maxParts = maxParts;
    }

    /**
     * 返回一个值表示从哪个Part号码开始获取列表。
     * @return 表示从哪个Part号码开始获取列表。
     */
    public Integer getPartNumberMarker() {
        return partNumberMarker;
    }

    /**
     * 设置一个值表示从哪个Part号码开始获取列表。
     * @param partNumberMarker
     *          表示从哪个Part号码开始获取列表。
     */
    public void setPartNumberMarker(Integer partNumberMarker) {
        this.partNumberMarker = partNumberMarker;
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
