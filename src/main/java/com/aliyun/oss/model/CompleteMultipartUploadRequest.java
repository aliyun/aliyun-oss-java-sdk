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
import java.util.List;

/**
 * 包含完成一个Multipart上传事件的请求参数。
 */
public class CompleteMultipartUploadRequest extends GenericRequest {

    /** The ID of the multipart upload to complete */
    private String uploadId;

    /** The list of part numbers and ETags to use when completing the multipart upload */
    private List<PartETag> partETags = new ArrayList<PartETag>();
    
    /** The access control list for multipart uploaded object */
    private CannedAccessControlList cannedACL;
    
    /** callback */
    private Callback callback;
    
    /** process **/
    private String process;

    /**
     * 构造函数。
     * @param bucketName
     *          Bucket名称。
     * @param key
     *          Object key。
     * @param uploadId
     *          Mutlipart上传事件的Upload ID。
     * @param partETags
     *          标识上传Part结果的{@link PartETag}列表。
     */
    public CompleteMultipartUploadRequest(String bucketName, String key, String uploadId, 
            List<PartETag> partETags) {
        super(bucketName, key);
        this.uploadId = uploadId;
        this.partETags = partETags;
        setObjectACL(null);
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
     * 返回标识上传Part结果的{@link PartETag}列表。
     * @return 标识上传Part结果的{@link PartETag}列表。
     */
    public List<PartETag> getPartETags() {
        return partETags;
    }

    /**
     * 设置标识上传Part结果的{@link PartETag}列表。
     * @param partETags
     *          标识上传Part结果的{@link PartETag}列表。
     */
    public void setPartETags(List<PartETag> partETags) {
        this.partETags = partETags;
    }
    
    /**
     * 获取Object ACL。
     * @return Object ACL。
     */
    public CannedAccessControlList getObjectACL() {
        return cannedACL;
    }

    /**
     * 设置Object ACL。
     * @param Object ACL。
     */
    public void setObjectACL(CannedAccessControlList cannedACL) {
        this.cannedACL = cannedACL;
    }
    
    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    
    public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}
}
