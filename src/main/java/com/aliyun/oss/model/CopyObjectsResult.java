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
 * The result of copying existing OSS objects.
 */
public class CopyObjectsResult extends GenericResult {
    private List<SingleCopyObjectResult> successResults;
    private List<SingleCopyObjectResult> failureResults;

    public CopyObjectsResult() {
        successResults = new ArrayList<SingleCopyObjectResult>();
        failureResults = new ArrayList<SingleCopyObjectResult>();
    }

    public void addSuccessResult(SingleCopyObjectResult successResult) {
        successResults.add(successResult);
    }

    public void addFailureResult(SingleCopyObjectResult failureResult) {
        failureResults.add(failureResult);
    }

    public List<SingleCopyObjectResult> getSuccessResults() {
        return successResults;
    }

    public List<SingleCopyObjectResult> getFailureResults() {
        return failureResults;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CopyObjectsResult:\n");
        sb.append("\tSuccessObjects:\n");
        for (SingleCopyObjectResult result : successResults) {
            sb.append("\t\t").append(result.toString()).append("\n");
        }
        sb.append("\tFailureObjects:\n");
        for (SingleCopyObjectResult result : failureResults) {
            sb.append("\t\t").append(result.toString()).append("\n");
        }
        return sb.toString();
    }
}
