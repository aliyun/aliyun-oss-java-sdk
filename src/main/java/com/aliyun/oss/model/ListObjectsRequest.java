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

import com.aliyun.oss.internal.OSSUtils;

/**
 * 包含获取object列表的请求信息。
 */
public class ListObjectsRequest extends WebServiceRequest {

	private static final int MAX_RETURNED_KEYS_LIMIT = 1000;
	
    // bucket 名称。
    private String bucketName;
    
    // prefix限定返回的object key必须以prefix作为前缀。
    private String prefix;
    
    // maker用户设定结果从marker之后按字母排序的第一个开始返回。
    private String marker;

    // 用于限定此次返回object的最大数，如果不设定，默认为100。
    private Integer maxKeys;
    
    // delimiter是一个用于对Object名字进行分组的字符。
    private String delimiter;
    
    /**
     * 该可选参数表示请求响应体中Object名称采用的编码方式，目前Object名称允许包含任意Unicode字符，
     * 然而XML 1.0不能解析某些Unicode字符，例如ASCII字符0~10。对于XML 1.0不支持的字符集，可通过
     * 添加该参数指示OSS对响应体中的Object名称进行编码。
     */
    private String encodingType;
    
    public ListObjectsRequest() { }
    
    public ListObjectsRequest(String bucketName) {
        this(bucketName, null, null, null, null);
    }
    
    /**
     * 构造函数。
     * @param bucketName
     *          bucket 名称。
     * @param prefix
     *          prefix限定返回的object key必须以prefix作为前缀。
     * @param marker
     *          maker用户设定结果从marker之后按字母排序的第一个开始返回。
     * @param maxKeys
     *          用于限定此次返回object的最大数，如果不设定，默认为100。
     * @param delimiter
     *          delimiter是一个用于对Object名字进行分组的字符。
     */
    public ListObjectsRequest(String bucketName, String prefix, String marker, String delimiter, Integer maxKeys) {
        setBucketName(bucketName);
        setPrefix(prefix);
        setMarker(marker);
        setDelimiter(delimiter);
        if (maxKeys != null) {
            setMaxKeys(maxKeys);
        }
    }

    /**
     * 返回bucket名称。
     * @return bucket名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置bucket名称。
     * @param bucketName
     *          bucket名称。
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 返回prefix，限定返回的object key必须以prefix作为前缀。
     * @return
     *      prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置prefix，限定返回的object key必须以prefix作为前缀。
     * @param prefix
     *          前缀prefix。
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * 返回marker，用户设定结果从marker之后按字母排序的第一个开始返回。
     * @return
     *          marker
     */
    public String getMarker() {
        return marker;
    }

    /**
     * 设置marker, 用户设定结果从marker之后按字母排序的第一个开始返回。
     * @param marker
     *          marker
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * 返回用于限定此次返回object的最大数，如果不设定，默认为100。
     * @return
     *      用于限定此次返回object的最大数。
     */
    public Integer getMaxKeys() {
        return maxKeys;
    }

    /**
     * 设置用于限定此次返回object的最大数，如果不设定，默认为100。最大值为1000。
     * @param maxKeys
     *      用于限定此次返回object的最大数。最大值为1000。
     */
    public void setMaxKeys(Integer maxKeys) {
        if (maxKeys < 0 || maxKeys > MAX_RETURNED_KEYS_LIMIT) {
            throw new IllegalArgumentException(
                    OSSUtils.OSS_RESOURCE_MANAGER.getString("MaxKeysOutOfRange"));
        }

        this.maxKeys = maxKeys;
    }

    /**
     * 获取一个用于对Object名字进行分组的字符。
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * 设置一个用于对Object名字进行分组的字符。
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
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
