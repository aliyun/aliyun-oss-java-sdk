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

/**
 * {@link OSSObject}摘要信息。
 */
public class OSSObjectSummary {

    /** The name of the bucket in which this object is stored */
    private String bucketName;

    /** The key under which this object is stored */
    private String key;

    private String eTag;

    private long size;

    private Date lastModified;

    private String storageClass;
    
    private Owner owner;

    /**
     * 初始化一个新的{@link OSSObjectSummary}实例。
     */
    public OSSObjectSummary() {
    }

    /**
     * 返回所在{@link Bucket}的名称。
     * @return
     *      所在{@link Bucket}的名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置所在{@link Bucket}的名称。
     * @param bucketName
     *          所在{@link Bucket}的名称。
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 返回Object的Key。
     * @return
     *      Object的Key。
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置Object的Key。
     * @param key
     *          Object的Key。
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 返回一个值表示与Object相关的hex编码的128位MD5摘要。
     * @return
     *      与Object相关的hex编码的128位MD5摘要。
     */
    public String getETag() {
        return eTag;
    }

    /**
     * 设置一个值表示与Object相关的hex编码的128位MD5摘要。
     * @param eTag
     *          一个值表示与Object相关的hex编码的128位MD5摘要。
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    /**
     * 获取Object的文件字节数。
     * @return
     *      文件字节数。
     */
    public long getSize() {
        return size;
    }

    /**
     * 设置Object的文件字节数。
     * @param size
     *          文件字节数。
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * 返回最后修改时间。
     * @return 最后修改时间。
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * 设置最后修改时间。
     * @param lastModified
     *          最后修改时间。
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * 返回Object的所有者。
     * @return Object的所有者。
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * 设置Object的所有者。
     * @param owner
     *          Object的所有者。
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * 返回Object的存储类别。
     * @return Object的存储类别。
     */
    public String getStorageClass() {
        return storageClass;
    }

    /**
     * 设置Object的存储类别。
     * @param storageClass
     *          Object的存储类别。
     */
    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

}
