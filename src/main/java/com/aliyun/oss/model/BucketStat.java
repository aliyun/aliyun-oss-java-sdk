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
 * Bucket Stat
 */
public class BucketStat {

	public BucketStat(Long storageSize, Long objectCount, Long multipartUploadCount) {
		this.storageSize = storageSize;
		this.objectCount = objectCount;
		this.multipartUploadCount = multipartUploadCount;
	}

	/**
	 * 返回Bucket存储量，单位byte
	 * @return Bucket存储量
	 */
	public Long getStorageSize() {
		return storageSize;
	}

	/**
	 * 返回Bucket中Object的数量
	 * @return Object的数量
	 */
	public Long getObjectCount() {
		return objectCount;
	}

	/**
	 * 返回Bucket下未完成的分片上传任务数
	 * @return 未完成的分片上传任务数
	 */
	public Long getMultipartUploadCount() {
		return multipartUploadCount;
	}

	private Long storageSize; // bytes
	private Long objectCount;
	private Long multipartUploadCount;
}
