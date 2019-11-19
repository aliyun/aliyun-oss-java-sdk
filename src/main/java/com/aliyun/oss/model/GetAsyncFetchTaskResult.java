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

public class GetAsyncFetchTaskResult extends GenericResult {
    private String taskId;
    private AsyncFetchTaskState asyncFetchTaskState;
    private String errorMsg;
    private AsyncFetchTaskConfiguration asyncFetchTaskConfiguration;

    /**
     * Gets the async fetch task id.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * Sets the async fetch task id.
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    /**
     * Gets the async fetch task state.
     */
    public AsyncFetchTaskState getAsyncFetchTaskState() {
        return asyncFetchTaskState;
    }

    /**
     * Sets the async fetch task state.
     */
    public void setAsyncFetchTaskState(AsyncFetchTaskState asyncFetchTaskState) {
        this.asyncFetchTaskState = asyncFetchTaskState;
    }

    /**
     * Gets the errorMsg that received from server.
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * Sets the errorMsg received from server.
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /***
     * Gets the async task configuration.
     */
    public AsyncFetchTaskConfiguration getAsyncFetchTaskConfiguration() {
        return asyncFetchTaskConfiguration;
    }

    /***
     * Sets the async task configuration.
     */
    public void setAsyncFetchTaskConfiguration(AsyncFetchTaskConfiguration asyncFetchTaskConfiguration) {
        this.asyncFetchTaskConfiguration = asyncFetchTaskConfiguration;
    }
}
