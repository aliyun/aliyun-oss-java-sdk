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
 * 文件分片下载请求
 *
 */
public class DownloadFileRequest extends GenericRequest {

    public DownloadFileRequest(String bucketName, String key) {
        super(bucketName, key);
    }
    
    public DownloadFileRequest(String bucketName, String key, String downloadFile, long partSize) {
        super(bucketName, key);
        this.partSize = partSize;
        this.downloadFile = downloadFile;
    }
    
    public DownloadFileRequest(String bucketName, String key, String downloadFile, long partSize, 
            int taskNum, boolean enableCheckpoint) {
        this(bucketName, key, downloadFile, partSize, taskNum, enableCheckpoint, null);
    }
    
    public DownloadFileRequest(String bucketName, String key, String downloadFile, long partSize, 
            int taskNum, boolean enableCheckpoint, String checkpointFile) {
        super(bucketName, key);
        this.partSize = partSize;
        this.taskNum = taskNum;
        this.downloadFile = downloadFile;
        this.enableCheckpoint = enableCheckpoint;
        this.checkpointFile = checkpointFile;
    }

    public long getPartSize() {
        return partSize;
    }

    public void setPartSize(long partSize) {
        this.partSize = partSize;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }
    
    public String getDownloadFile() {
        return downloadFile;
    }
    
    public String getTempDownloadFile() {
        return downloadFile + ".tmp";
    }

    public void setDownloadFile(String downloadFile) {
        this.downloadFile = downloadFile;
    }

    public boolean isEnableCheckpoint() {
        return enableCheckpoint;
    }

    public void setEnableCheckpoint(boolean enableCheckpoint) {
        this.enableCheckpoint = enableCheckpoint;
    }

    public String getCheckpointFile() {
        return checkpointFile;
    }

    public void setCheckpointFile(String checkpointFile) {
        this.checkpointFile = checkpointFile;
    }
    
    /**
     * 返回“If-Match”参数，表示：如果传入期望的 ETag 和 object 的 ETag 匹配，正常的发送文件。
     * 如果不符合，返回错误。
     * @return 表示期望object的ETag与之匹配的ETag列表。
     */
    public List<String> getMatchingETagConstraints() {
        return matchingETagConstraints;
    }

    /**
     * 返回“If-Match”参数（可选）。
     * 表示如果传入期望的 ETag 和 Object 的 ETag 匹配，则正常的发送文件。
     * 如果不符合，则返回错误。
     * @param eTagList
     *          表示期望object的ETag与之匹配的ETag列表。
     *          目前OSS支持传入一个ETag，如果传入多于一个ETag，将只有列表中的第一个有效。
     */
    public void setMatchingETagConstraints(List<String> eTagList) {
        this.matchingETagConstraints.clear();
        if (eTagList != null && !eTagList.isEmpty()) {
            this.matchingETagConstraints.addAll(eTagList);
        }
    }
    
    public void clearMatchingETagConstraints() {
        this.matchingETagConstraints.clear();
    }

    /**
     * 返回“If-None-Match”参数，可以用来检查文件是否有更新。
     * 如果传入的 ETag值和Object的ETag 相同，返回错误；否则正常传输文件。 
     * @return 表示期望Object的ETag与之不匹配的ETag列表。
     */
    public List<String> getNonmatchingETagConstraints() {
        return nonmatchingEtagConstraints;
    }

    /**
     * 返回“If-None-Match”参数，可以用来检查文件是否有更新（可选）。
     * 如果传入的 ETag值和Object的ETag 相同，返回错误；否则正常传输文件。 
     * @param eTagList
     *          表示期望Object的ETag与之不匹配的ETag列表。
     *          目前OSS支持传入一个ETag，如果传入多于一个ETag，将只有列表中的第一个有效。
     */
    public void setNonmatchingETagConstraints(List<String> eTagList) {
        this.nonmatchingEtagConstraints.clear();
        if (eTagList != null && !eTagList.isEmpty()) {
            this.nonmatchingEtagConstraints.addAll(eTagList);
        }
    }
    
    public void clearNonmatchingETagConstraints() {
        this.nonmatchingEtagConstraints.clear();
    }

    /**
     * 返回“If-Unmodified-Since”参数。
     * 表示：如果传入参数中的时间等于或者晚于文件实际修改时间，则传送文件；
     * 如果早于实际修改时间，则返回错误。 
     * @return “If-Unmodified-Since”参数。
     */
    public Date getUnmodifiedSinceConstraint() {
        return unmodifiedSinceConstraint;
    }

    /**
     * 设置“If-Unmodified-Since”参数（可选）。
     * 表示：如果传入参数中的时间等于或者晚于文件实际修改时间，则传送文件；
     * 如果早于实际修改时间，则返回错误。 
     * @param date
     *          “If-Unmodified-Since”参数。
     */
    public void setUnmodifiedSinceConstraint(Date date) {
        this.unmodifiedSinceConstraint = date;
    }

    /**
     * 返回“If-Modified-Since”参数。
     * 表示：如果指定的时间早于实际修改时间，则正常传送文件，并返回 200 OK；
     * 如果参数中的时间和实际修改时间一样或者更晚，会返回错误。
     * @return “If-Modified-Since”参数。
     */
    public Date getModifiedSinceConstraint() {
        return modifiedSinceConstraint;
    }

    /**
     * 设置“If-Modified-Since”参数（可选）。
     * 表示：如果指定的时间早于实际修改时间，则正常传送文件，并返回 200 OK；
     * 如果参数中的时间和实际修改时间一样或者更晚，会返回错误。
     * @param date
     *          “If-Modified-Since”参数。
     */
    public void setModifiedSinceConstraint(Date date) {
        this.modifiedSinceConstraint = date;
    }

    /**
     * 返回要重载的返回请求头。
     * @return 要重载的返回请求头。
     */
    public ResponseHeaderOverrides getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * 设置要重载的返回请求头（可选）。
     * @param responseHeaders
     *          要重载的返回请求头。
     */
    public void setResponseHeaders(ResponseHeaderOverrides responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    // 分片大小，单位字节，默认100KB
    private long partSize = 1024 * 100;
    // 分片上传线程数，默认1
    private int taskNum = 1;
    // 本地文件
    private String downloadFile;
    // 是否开启断点续传
    private boolean enableCheckpoint;
    // 断点续传时保存分片上传的信息的本地文件
    private String checkpointFile;
    
    // If-Match参数，如果传入期望的 ETag和 object的 ETag匹配，正常发送文件
    private List<String> matchingETagConstraints = new ArrayList<String>();
    // If-None-Match”参数，如果传入的 ETag值和Object的ETag不相同，正常传输文件。 
    private List<String> nonmatchingEtagConstraints = new ArrayList<String>();
    // If-Unmodified-Since参数，如果传入参数中的时间等于或者晚于文件实际修改时间，则传送文件
    private Date unmodifiedSinceConstraint;
    // If-Modified-Since参数，如果指定的时间早于实际修改时间，正常传送文件
    private Date modifiedSinceConstraint;
    // 包含了在发送GET请求时可以重载的返回请求头
    private ResponseHeaderOverrides responseHeaders;

}
