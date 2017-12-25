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
 * The request class that is to download file with multiple parts download.
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

    public DownloadFileRequest(String bucketName, String key, String downloadFile, long partSize, int taskNum,
            boolean enableCheckpoint) {
        this(bucketName, key, downloadFile, partSize, taskNum, enableCheckpoint, null);
    }

    public DownloadFileRequest(String bucketName, String key, String downloadFile, long partSize, int taskNum,
            boolean enableCheckpoint, String checkpointFile) {
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
     * Gets the ETag matching constraints. The download only happens if the
     * specified ETag matches the source file's ETag. If ETag does not match,
     * returns the precondition failure (412)
     * 
     * @return The expected ETag list.
     */
    public List<String> getMatchingETagConstraints() {
        return matchingETagConstraints;
    }

    /**
     * Sets the ETag matching constraints (optional). The download only happens
     * if the specified ETag matches the source file's ETag. If ETag does not
     * match, returns the precondition failure (412)
     * 
     * @param eTagList
     *            The expected ETag list.
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
     * Gets the ETag non-matching constraints. The download only happens if the
     * specified ETag does not match the source file's ETag. If ETag matches,
     * returns the precondition failure (412)
     * 
     * @return The expected ETag list.
     */
    public List<String> getNonmatchingETagConstraints() {
        return nonmatchingEtagConstraints;
    }

    /**
     * Sets the ETag non-matching constraints. The download only happens if the
     * specified ETag does not match the source file's ETag. If ETag matches,
     * returns the precondition failure (412)
     * 
     * @param eTagList
     *            The expected ETag list. For now only the first ETag is used,
     *            though the parameter is the list.
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
     * Gets the unmodified since constraint.
     * 
     * @return The time threshold. If it's same or later than the actual
     *         modified time, download the file.
     */
    public Date getUnmodifiedSinceConstraint() {
        return unmodifiedSinceConstraint;
    }

    /**
     * Sets the unmodified since constraint.
     * 
     * @param date
     *            The time threshold. If it's same or later than the actual
     *            modified time, download the file.
     */
    public void setUnmodifiedSinceConstraint(Date date) {
        this.unmodifiedSinceConstraint = date;
    }

    /**
     * Gets the modified since constraint.
     * 
     * @return The time threshold. If it's earlier than the actual modified
     *         time, download the file.
     */
    public Date getModifiedSinceConstraint() {
        return modifiedSinceConstraint;
    }

    /**
     * Sets the modified since constraint.
     * 
     * @param date
     *            The time threshold. If it's earlier than the actual modified
     *            time, download the file.
     */
    public void setModifiedSinceConstraint(Date date) {
        this.modifiedSinceConstraint = date;
    }

    /**
     * Gets response headers to override.
     * 
     * @return The headers to override with.
     */
    public ResponseHeaderOverrides getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Sets response headers to override.
     * 
     * @param responseHeaders
     *            The headers to override with.
     */
    public void setResponseHeaders(ResponseHeaderOverrides responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    // Part size in byte, by default it's 100KB.
    private long partSize = 1024 * 100;
    // Thread count for downloading parts, by default it's 1.
    private int taskNum = 1;
    // The local file path for the download.
    private String downloadFile;
    // Flag of enabling checkpoint.
    private boolean enableCheckpoint;
    // The local file path of the checkpoint file
    private String checkpointFile;

    // The matching ETag constraints
    private List<String> matchingETagConstraints = new ArrayList<String>();
    // The non-matching ETag constraints.
    private List<String> nonmatchingEtagConstraints = new ArrayList<String>();
    // The unmodified since constraint.
    private Date unmodifiedSinceConstraint;
    // The modified since constraints.
    private Date modifiedSinceConstraint;
    // The response headers to override.
    private ResponseHeaderOverrides responseHeaders;

}
