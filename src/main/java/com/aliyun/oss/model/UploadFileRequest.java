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
 * The file upload request to start a multipart upload.
 *
 */
public class UploadFileRequest extends GenericRequest {

    public UploadFileRequest(String bucketName, String key) {
        super(bucketName, key);
    }

    public UploadFileRequest(String bucketName, String key, String uploadFile, long partSize, int taskNum) {
        super(bucketName, key);
        this.partSize = partSize;
        this.taskNum = taskNum;
        this.uploadFile = uploadFile;
    }

    public UploadFileRequest(String bucketName, String key, String uploadFile, long partSize, int taskNum,
            boolean enableCheckpoint) {
        super(bucketName, key);
        this.partSize = partSize;
        this.taskNum = taskNum;
        this.uploadFile = uploadFile;
        this.enableCheckpoint = enableCheckpoint;
    }

    public UploadFileRequest(String bucketName, String key, String uploadFile, long partSize, int taskNum,
            boolean enableCheckpoint, String checkpointFile) {
        super(bucketName, key);
        this.partSize = partSize;
        this.taskNum = taskNum;
        this.uploadFile = uploadFile;
        this.enableCheckpoint = enableCheckpoint;
        this.checkpointFile = checkpointFile;
    }

    public long getPartSize() {
        return partSize;
    }

    public void setPartSize(long partSize) {
        if (partSize < 1024 * 100) {
            this.partSize = 1024 * 100;
        } else {
            this.partSize = partSize;
        }
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        if (taskNum < 1) {
            this.taskNum = 1;
        } else if (taskNum > 1000) {
            this.taskNum = 1000;
        } else {
            this.taskNum = taskNum;
        }
    }

    public String getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(String uploadFile) {
        this.uploadFile = uploadFile;
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

    public ObjectMetadata getObjectMetadata() {
        return objectMetadata;
    }

    public void setObjectMetadata(ObjectMetadata objectMetadata) {
        this.objectMetadata = objectMetadata;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    // Part size, by default it's 100KB.
    private long partSize = 1024 * 100;
    // Concurrent parts upload thread count. By default it's 1.
    private int taskNum = 1;
    // The local file path to upload.
    private String uploadFile;
    // Enable the checkpoint
    private boolean enableCheckpoint = false;
    // The checkpoint file's local path.
    private String checkpointFile;
    // The metadata of the target file.
    private ObjectMetadata objectMetadata;
    // callback entry.
    private Callback callback;
}
