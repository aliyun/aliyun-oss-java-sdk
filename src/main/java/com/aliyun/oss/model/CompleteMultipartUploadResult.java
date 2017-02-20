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

import java.io.InputStream;

/**
 * 包含完成一个Multipart上传事件的返回结果。
 *
 */
public class CompleteMultipartUploadResult extends GenericResult implements CallbackResult {

    /** The name of the bucket containing the completed multipart upload. */
    private String bucketName;

    /** The key by which the object is stored. */
    private String key;

    /** The URL identifying the new multipart object. */
    private String location;

    private String eTag;
    
    /** 回调返回的消息体 */
    private InputStream callbackResponseBody;

    /**
     * 返回标识Multipart上传的{@link OSSObject}的URL地址。
     * @return 标识Multipart上传的{@link OSSObject}的URL地址。
     */
    public String getLocation() {
        return location;
    }

    /**
     * 设置标识Multipart上传的{@link OSSObject}的URL地址。
     * @param location
     *          标识Multipart上传的{@link OSSObject}的URL地址。
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 返回包含Multipart上传的{@link OSSObject}的{@link Bucket}名称。
     * @return Bucket名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置包含Multipart上传的{@link OSSObject}的{@link Bucket}名称。
     * @param bucketName
     *          Bucket名称。
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 返回新创建的{@link OSSObject}的Key。
     * @return 新创建的{@link OSSObject}的Key。
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置新创建的{@link OSSObject}的Key。
     * @param key
     *          新创建的{@link OSSObject}的Key。
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 返回ETag值。
     * @return ETag值。
     */
    public String getETag() {
        return eTag;
    }

    /**
     * 设置ETag值。
     * @param etag ETag值。
     */
    public void setETag(String etag) {
        this.eTag = etag;
    }
    
    /**
     * 获取回调返回的消息体，需要close，使用this.getResponse().getContent()代替。。
     * @return 回调返回的消息体
     */
    @Override
    @Deprecated
    public InputStream getCallbackResponseBody() {
        return callbackResponseBody;
    }
    
    /**
     * 设置回调返回的消息体。
     * @param callbackResponseBody 回调返回的消息体。
     */
    @Override
    public void setCallbackResponseBody(InputStream callbackResponseBody) {
        this.callbackResponseBody = callbackResponseBody;
    }

}
