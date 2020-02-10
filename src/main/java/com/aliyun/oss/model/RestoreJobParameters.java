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
 * The job parameters of restoring the {@link StorageClass#LongTermArchive} object. If the restore job parameters
 * has not be specified, the default restore priority is {@link RestoreTier#RESTORE_TIER_STANDARD}.
 */
public class RestoreJobParameters {
    /**
     * The priority of restore the {@link StorageClass#LongTermArchive} object job.
     */
    RestoreTier restoreTier;

    public RestoreJobParameters(RestoreTier restoreTier) {
        this.restoreTier = restoreTier;
    }

    /**
     * Gets the priority of restore the {@link StorageClass#LongTermArchive} object job.
     */
    public RestoreTier getRestoreTier() {
        return restoreTier;
    }

    /**
     * Sets the priority of restore the {@link StorageClass#LongTermArchive} object job.
     */
    public void setRestoreTier(RestoreTier restoreTier) {
        this.restoreTier = restoreTier;
    }
}
