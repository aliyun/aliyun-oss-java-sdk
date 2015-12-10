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

package com.aliyun.oss.common.auth;

/**
 * 表示用于计算访问签名的接口。
 */
public abstract class ServiceSignature {

    /**
     * 获取签名的算法。
     * @return 签名算法。
     */
    public abstract String getAlgorithm();
    
    /**
     * 获取签名算法的版本信息。
     * @return 签名算法的版本。
     */
    public abstract String getVersion();
    
    /**
     * 计算签名。
     * @param key 签名所需的密钥，对应于访问的Access Key。
     * @param data 用于计算签名的字符串信息。
     * @return 签名字符串。
     */
    public abstract String computeSignature(String key, String data);

    /**
     * 创建默认的<code>ServiceSignature</code>实例。
     * @return 默认的<code>ServiceSignature</code>实现。
     */
    public static ServiceSignature create() {
        return new HmacSHA1Signature();
    }
}