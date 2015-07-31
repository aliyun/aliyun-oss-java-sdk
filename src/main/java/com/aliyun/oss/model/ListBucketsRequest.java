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

public class ListBucketsRequest extends WebServiceRequest {
	
	public static final int MAX_RETURNED_KEYS = 1000;
	
    // prefix限定返回的object key必须以prefix作为前缀。
    private String prefix;
    
    // maker用户设定结果从marker之后按字母排序的第一个开始返回。
    private String marker;

    // 用于限定此次返回bucket的最大数，如果不设定，默认为100。
    private Integer maxKeys;

    /**
     * 构造函数。
     */
    public ListBucketsRequest() { }

    /**
     * 构造函数。
     * @param prefix
     *          prefix限定返回的bucket name必须以prefix作为前缀。
     * @param marker
     *          maker用户设定结果从marker之后按字母排序的第一个开始返回。
     * @param maxKeys
     *          用于限定此次返回object的最大数，如果不设定，默认为100。
     */
    public ListBucketsRequest(String prefix, String marker, Integer maxKeys) {
        setPrefix(prefix);
        setMarker(marker);
        if (maxKeys != null) {
            setMaxKeys(maxKeys);
        }
    }

    /**
     * 返回prefix，限定返回的bucket name必须以prefix作为前缀。
     * @return
     *      prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * 设置prefix，限定返回的bucket name必须以prefix作为前缀。
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
     * 返回用于限定此次返回bucket的最大数，如果不设定，默认为100。
     * @return
     *      用于限定此次返回bucket的最大数。
     */
    public Integer getMaxKeys() {
        return maxKeys;
    }

    /**
     * 设置用于限定此次返回bucket的最大数，如果不设定，默认为100。最大值为1000。
     * @param maxKeys
     *      用于限定此次返回bucket的最大数。最大值为1000。
     */
    public void setMaxKeys(Integer maxKeys) {
    	int tmp = maxKeys.intValue();
        if (tmp < 0 || tmp > MAX_RETURNED_KEYS) {
            throw new IllegalArgumentException(
                OSSUtils.OSS_RESOURCE_MANAGER.getString("MaxKeysOutOfRange"));
        }
        this.maxKeys = maxKeys;
    }
}
