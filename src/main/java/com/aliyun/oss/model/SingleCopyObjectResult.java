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
 * Single copy object result.
 * @see {@link CopyObjectsResult}.
 */
public class SingleCopyObjectResult {
    private String sourceKey;
    private String targetKey;
    private String etag;
    private String errorStatus;

    public static SingleCopyObjectResult success(String sourceKey, String targetKey, String etag) {
        SingleCopyObjectResult result = new SingleCopyObjectResult();
        result.setSourceKey(sourceKey);
        result.setTargetKey(targetKey);
        result.setEtag(etag);
        return result;
    }

    public static SingleCopyObjectResult failure(String sourceKey, String targetKey, String errorStatus) {
        SingleCopyObjectResult result = new SingleCopyObjectResult();
        result.setSourceKey(sourceKey);
        result.setTargetKey(targetKey);
        result.setErrorStatus(errorStatus);
        return result;
    }

    public String getSourceKey() {
        return sourceKey;
    }

    public void setSourceKey(String sourceKey) {
        this.sourceKey = sourceKey;
    }

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(String errorStatus) {
        this.errorStatus = errorStatus;
    }

    @Override
    public String toString() {
        if (etag != null) {
            return "SourceKey: " + sourceKey + ", TargetKey: " + targetKey + ", ETag: " + etag;
        } else {
            return "SourceKey: " + sourceKey + ", TargetKey: " + targetKey + ", ErrorStatus: " + errorStatus;
        }
    }
}
