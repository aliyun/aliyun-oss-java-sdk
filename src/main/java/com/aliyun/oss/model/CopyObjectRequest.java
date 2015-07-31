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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 拷贝一个在OSS上已经存在的Object成另外一个Object的请求参数。
 */
public class CopyObjectRequest extends WebServiceRequest {

    // 源Object所在的Bucket的名称。
    private String sourceBucketName;

    // 源Object的Key。
    private String sourceKey;

    // 目标Object所在的Bucket的名称。
    private String destinationBucketName;
    
    // 目标Object的Key。
    private String destinationKey;

    // 目标Object在服务端的加密算法
    private String serverSideEncryption;

    // 目标Object的Metadata信息。
    private ObjectMetadata newObjectMetadata;

    // 如果源Object的ETAG值和用户提供的ETAG相等，则执行拷贝操作；否则返回412 HTTP错误码（预处理失败）。
    private List<String> matchingETagConstraints = new ArrayList<String>();

    // 如果源Object的ETAG值和用户提供的ETAG不相等，则执行拷贝操作；否则返回412 HTTP错误码（预处理失败）。
    private List<String> nonmatchingEtagConstraints = new ArrayList<String>();

    // 如果传入参数中的时间等于或者晚于文件实际修改时间，则正常传输文件，并返回200 OK；
    // 否则返回412 precondition failed错误
    private Date unmodifiedSinceConstraint;

    // 如果源Object自从用户指定的时间以后被修改过，则执行拷贝操作；
    // 否则返回412 HTTP错误码（预处理失败）。
    private Date modifiedSinceConstraint;

    /**
     * 初始化一个新的{@link CopyObjectRequest}实例。
     * @param sourceBucketName
     *          源Object所在的Bucket的名称。
     * @param sourceKey
     *          源Object的Key。
     * @param destinationBucketName
     *          目标Object所在的Bucket的名称。
     * @param destinationKey
     *          目标Object的Key。
     */
    public CopyObjectRequest(String sourceBucketName, String sourceKey,
            String destinationBucketName, String destinationKey){
        setSourceBucketName(sourceBucketName);
        setSourceKey(sourceKey);
        setDestinationBucketName(destinationBucketName);
        setDestinationKey(destinationKey);
    }

    /**
     * 返回源Object所在的Bucket的名称。
     * @return 源Object所在的Bucket的名称。
     */
    public String getSourceBucketName() {
        return sourceBucketName;
    }

    /**
     * 设置源Object所在的Bucket的名称。
     * @param sourceBucketName
     *          源Object所在的Bucket的名称。
     */
    public void setSourceBucketName(String sourceBucketName) {
        this.sourceBucketName = sourceBucketName;
    }

    /**
     * 返回源Object的Key。
     * @return 源Object的Key。
     */
    public String getSourceKey() {
        return sourceKey;
    }

    /**
     * 设置源Object的Key。
     * @param sourceKey
     *          源Object的Key。
     */
    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    /**
     * 返回目标Object所在的Bucket的名称。
     * @return 目标Object所在的Bucket的名称。
     */
    public String getDestinationBucketName() {
        return destinationBucketName;
    }

    /**
     * 设置目标Object所在的Bucket的名称。
     * @param destinationBucketName
     *          目标Object所在的Bucket的名称。
     */
    public void setDestinationBucketName(String destinationBucketName) {
        this.destinationBucketName = destinationBucketName;
    }

    /**
     * 返回目标Object的Key。
     * @return 目标Object的Key。
     */
    public String getDestinationKey() {
        return destinationKey;
    }

    /**
     * 设置目标Object的Key。
     * @param destinationKey
     *          目标Object的Key。
     */
    public void setDestinationKey(String destinationKey) {
        this.destinationKey = destinationKey;
    }

    /**
     * 返回目标Object的{@link ObjectMetadata}信息。
     * @return 目标Object的{@link ObjectMetadata}信息。
     */
    public ObjectMetadata getNewObjectMetadata() {
        return newObjectMetadata;
    }

    /**
     * 设置目标Object的{@link ObjectMetadata}信息。可选。
     * @param newObjectMetadata
     *          目标Object的{@link ObjectMetadata}信息。
     */
    public void setNewObjectMetadata(ObjectMetadata newObjectMetadata) {
        this.newObjectMetadata = newObjectMetadata;
    }

    /**
     * 返回限定Object的ETag限定必须匹配给定值的列表。
     * 如果源Object的ETAG值和用户提供的ETAG相等，则执行拷贝操作；
     * 否则抛出异常。
     * @return ETag限定值的列表。
     */
    public List<String> getMatchingETagConstraints() {
        return matchingETagConstraints;
    }

    /**
     * 设置ETag限定值的列表。可选。
     * 如果源Object的ETAG值和用户提供的ETAG相等，则执行拷贝操作；
     * 否则抛出异常。
     * @param matchingETagConstraints
     *          ETag限定值的列表。
     */
    public void setMatchingETagConstraints(List<String> matchingETagConstraints) {
        this.matchingETagConstraints.clear();
        if (matchingETagConstraints != null && !matchingETagConstraints.isEmpty()) {
        	this.matchingETagConstraints.addAll(matchingETagConstraints);
        }
    }
    
    public void clearMatchingETagConstraints() {
    	this.matchingETagConstraints.clear();
    }

    /**
     * 返回限定Object的ETag限定必须不匹配给定值的列表。
     * 如果源Object的ETAG值和用户提供的ETAG不相等，则执行拷贝操作；
     * 否则抛出异常。
     * @return ETag限定值的列表。
     */
    public List<String> getNonmatchingEtagConstraints() {
        return nonmatchingEtagConstraints;
    }

    /**
     * 设置限定Object的ETag限定必须不匹配给定值的列表。可选。
     * 如果源Object的ETAG值和用户提供的ETAG不相等，则执行拷贝操作；
     * 否则抛出异常。
     * @param nonmatchingEtagConstraints
     *          ETag限定值的列表。
     */
    public void setNonmatchingETagConstraints(List<String> nonmatchingEtagConstraints) {
    	this.nonmatchingEtagConstraints.clear();
        if (nonmatchingEtagConstraints != null && !nonmatchingEtagConstraints.isEmpty()) {
        	this.nonmatchingEtagConstraints.addAll(nonmatchingEtagConstraints);
        }
    }
    
    public void clearNonmatchingETagConstraints() {
    	this.nonmatchingEtagConstraints.clear();
    }

    /**
     * 返回一个时间，如果该时间等于或者晚于文件实际修改时间，则正常传输文件；
     * 否则抛出异常。
     * @return 返回一个时间，如果该时间等于或者晚于文件实际修改时间，则正常传输文件。
     */
    public Date getUnmodifiedSinceConstraint() {
        return unmodifiedSinceConstraint;
    }

    /**
     * 设置一个时间，如果该时间等于或者晚于文件实际修改时间，则正常传输文件；
     * 否则抛出异常。可选。
     * @param unmodifiedSinceConstraint
     *          设置一个时间，如果该时间等于或者晚于文件实际修改时间，则正常传输文件。
     */
    public void setUnmodifiedSinceConstraint(Date unmodifiedSinceConstraint) {
        this.unmodifiedSinceConstraint = unmodifiedSinceConstraint;
    }

    /**
     * 返回一个时间，如果源Object自从该时间以后被修改过，则执行拷贝操作；
     * 否则抛出异常。
     * @return 返回一个时间，如果源Object自从该时间以后被修改过，则执行拷贝操作。
     */
    public Date getModifiedSinceConstraint() {
        return modifiedSinceConstraint;
    }

    /**
     * 设置返回一个时间，如果源Object自从该时间以后被修改过，则执行拷贝操作；
     * 否则抛出异常。可选。
     * @param modifiedSinceConstraint
     *          设置一个时间，如果源Object自从该时间以后被修改过，则执行拷贝操作。
     */
    public void setModifiedSinceConstraint(Date modifiedSinceConstraint) {
        this.modifiedSinceConstraint = modifiedSinceConstraint;
    }

    /**
     * 获取Object在服务器端加密的熵编码
     * @return 服务器端加密的熵编码，null表示没有进行加密
     */
    public String getServerSideEncryption() {
        return this.serverSideEncryption;
    }

    /**
     * 设置Object在服务器端熵编码的类型
     * @param 服务器端加密的熵编码类型
     */
    public void setServerSideEncryption(String serverSideEncryption) {
       this.serverSideEncryption = serverSideEncryption;
    }
}
