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
 * 包含初始化一个Multipart上传事件的请求参数。
 *
 */
public class InitiateMultipartUploadRequest  {

    private String bucketName;

    private String key;

    private ObjectMetadata objectMetadata;

    /**
     * 构造函数。
     * @param bucketName
     *          用来创建Multipart上传的Bucket的名称。
     * @param key
     *          用来创建的Multipart的Object（也就是Multipart上传完成后新生成的Object）的key。
     */
    public InitiateMultipartUploadRequest(String bucketName, String key) {
        this(bucketName, key, null);
    }

    /**
     * 构造函数。
     * @param bucketName
     *          用来创建Multipart上传的Bucket的名称。
     * @param key
     *          用来创建的Multipart的Object（也就是Multipart上传完成后新生成的Object）的key。
     * @param objectMetadata
     *          将创建的Object的附加信息。
     */
    public InitiateMultipartUploadRequest(String bucketName, String key, ObjectMetadata objectMetadata) {
        this.bucketName = bucketName;
        this.key = key;
        this.objectMetadata = objectMetadata;
    }
    
    /**
     * 返回用来创建Multipart上传的Bucket的名称。
     * @return 用来创建Multipart上传的Bucket的名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置用来创建Multipart上传的Bucket的名称。
     * @param bucketName
     *          用来创建Multipart上传的Bucket的名称。
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 返回用来创建的Multipart的Object（也就是Multipart上传完成后新生成的Object）的key。
     * @return
     *      用来创建的Multipart的Object（也就是Multipart上传完成后新生成的Object）的key。
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置用来创建的Multipart的Object（也就是Multipart上传完成后新生成的Object）的key。
     * @param key
     *          用来创建的Multipart的Object（也就是Multipart上传完成后新生成的Object）的key。
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 返回将创建的Object的附加信息。
     * @return 将创建的Object的附加信息。
     */
    public ObjectMetadata getObjectMetadata() {
        return objectMetadata;
    }

    /**
     * 设置将创建的Object的附加信息。
     * @param objectMetadata
     *          将创建的Object的附加信息。
     */
    public void setObjectMetadata(ObjectMetadata objectMetadata) {
        this.objectMetadata = objectMetadata;
    }
}
