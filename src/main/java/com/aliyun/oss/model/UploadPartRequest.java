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

import com.aliyun.oss.common.comm.io.PartialStream;

/**
 * 包含上传Multipart分块（Part）参数。
 */
public class UploadPartRequest extends WebServiceRequest {

    private String bucketName;

    private String key;

    private String uploadId;

    private int partNumber;

    private long partSize;

    private String md5Digest;

    private InputStream inputStream;
    
    private boolean useChunkEncoding = false;

    /**
     * 默认构造函数。
     */
    public UploadPartRequest() {}

	/**
     * 设置包含上传分块内容的数据流。
     * @param inputStream
     *          包含上传分块内容的数据流。
     */
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * 返回包含上传分块内容的数据流。
     * @return  包含上传分块内容的数据流。
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * 返回{@link Bucket}名称。
     * @return Bucket名称。
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * 设置{@link Bucket}名称。
     * @param bucketName
     *          Bucket名称。
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * 返回{@link OSSObject} key。
     * @return Object key。
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置{@link OSSObject} key。
     * @param key
     *          Object key。
     */
    public void setKey(String key) {
        this.key = key;
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
     * 返回上传分块（Part）的标识号码（Part Number）。
     * 每一个上传分块（Part）都有一个标识它的号码（范围1~10000）。
     * 对于同一个Upload ID，该号码不但唯一标识这一块数据，也标识了这块数据在整个文件中的
     * 相对位置。如果你用同一个Part号码上传了新的数据，那么OSS上已有的这个号码的Part数据
     * 将被覆盖。
     * @return 上传分块（Part）的标识号码（Part Number）。
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * 设置上传分块（Part）的标识号码（Part Number）。
     * 每一个上传分块（Part）都有一个标识它的号码（范围1~10000）。
     * 对于同一个Upload ID，该号码不但唯一标识这一块数据，也标识了这块数据在整个文件中的
     * 相对位置。如果你用同一个Part号码上传了新的数据，那么OSS上已有的这个号码的Part数据
     * 将被覆盖。
     * @param partNumber
     *          上传分块（Part）的标识号码（Part Number）。
     */
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * 返回分块（Part）数据的字节数。
     * 除最后一个Part外，其他Part最小为5MB。
     * @return 分块（Part）数据的字节数。
     */
    public long getPartSize() {
        return this.partSize;
    }

    /**
     * 设置返回分块（Part）数据的字节数。
     * 除最后一个Part外，其他Part最小为5MB。
     * @param partSize
     *          分块（Part）数据的字节数。
     */
    public void setPartSize(long partSize) {
        this.partSize = partSize;
    }

    /**
     * 返回分块（Part）数据的MD5校验值。
     * @return 分块（Part）数据的MD5校验值。
     */
    public String getMd5Digest() {
        return md5Digest;
    }

    /**
     * 设置分块（Part）数据的MD5校验值。
     * @param md5Digest
     *          分块（Part）数据的MD5校验值。
     */
    public void setMd5Digest(String md5Digest) {
        this.md5Digest = md5Digest;
    }

    /**
     * 获取是否采用Chunked编码方式传输请求数据。
     * @return 是否采用Chunked编码方式
     */
	public boolean isUseChunkEncoding() {
		return useChunkEncoding;
	}
	
	/**
	 * 设置是否采用Chunked编码方式传输请求数据。
	 * @param useChunkEncoding 是否采用Chunked编码
	 */
	public void setUseChunkEncoding(boolean useChunkEncoding) {
		this.useChunkEncoding = useChunkEncoding;
	}
	
	public PartialStream buildPartialStream() {
		return new PartialStream(inputStream, (int)partSize);
	}
}
