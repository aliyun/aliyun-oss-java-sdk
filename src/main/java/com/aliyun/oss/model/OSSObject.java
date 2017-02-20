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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * 表示OSS中的Object。
 * <p>
 * 在 OSS 中，用户的每个文件都是一个 Object，每个文件需小于 5G。
 * Object包含key、data和user meta。其中，key是Object 的名字；
 * data是Object 的数据；user meta是用户对该object的描述。
 * </p>
 * <p>
 * Object 命名规范
 * <ul>
 *  <li>使用UTF-8编码</li>
 *  <li>长度必须在 1-1023字节之间</li>
 *  <li>不能以斜线（/）或反斜线（\）开关</li>
 * </ul>
 * </p>
 *
 */
public class OSSObject extends GenericResult implements Closeable {

    // Object key (name)
    private String key;

    // Object所在的Bucket的名称。
    private String bucketName;

    // Object的元数据。
    private ObjectMetadata metadata = new ObjectMetadata();

    // Object所包含的内容。
    private InputStream objectContent;

    /**
     * 返回Object的元数据。
     * @return Object的元数据（{@link ObjectMetadata}）。
     */
    public ObjectMetadata getObjectMetadata() {
        return metadata;
    }

    /**
     * 设置Object的元数据。
     * @param metadata
     *          Object的元数据（{@link ObjectMetadata}）。
     */
    public void setObjectMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * 返回包含Object内容的{@link InputStream}。
     * @return 包含Object内容的{@link InputStream}。
     */
    public InputStream getObjectContent() {
        return objectContent;
    }

    /**
     * 设置包含Object内容的{@link InputStream}。
     * @param objectContent
     *          包含Object内容的{@link InputStream}。
     */
    public void setObjectContent(InputStream objectContent) {
        this.objectContent = objectContent;
    }

    /**
     * 获取Object所在的Bucket的名称。
     * @return Object所在的Bucket的名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置Object所在的Bucket的名称。
     * @param bucketName
     *          Object所在的Bucket的名称。
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 获取Object的Key。
     * @return Object Key。
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置Object的Key。
     * @param key
     *          Object Key。
     */
    public void setKey(String key) {
        this.key = key;
    }
    
    @Override
    public void close() throws IOException {
        if (objectContent != null) {
            objectContent.close();
        }
    }
    
    /**
     * 强制关闭，放弃读取剩余数据。
     * @throws IOException
     */
    public void forcedClose() throws IOException {
    	this.response.abort();
    }
    
    @Override
    public String toString() {
        return "OSSObject [key=" + getKey()
            + ",bucket=" + (bucketName == null ? "<Unknown>" : bucketName)
            + "]";
    }
}
