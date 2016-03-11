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
 * 跨区域复制的状态。
 * <p>
 * 目前有starting，doing，closing三种状态。PutBucketReplication后，OSS会为Bucket准备复制任务，
 * 这时候复制状态会处于starting。当跨区域复制真正开始时，复制状态会显示doing。如果用户DeleteBucketReplication后，
 * OSS会完成跨区域复制的清理工作，同步状态显示closing。
 * </p>
 *
 */
public enum ReplicationStatus {

    /**
     * 正在准备复制任务
     */
    Starting("starting"),

    /**
     * 跨区域复制进行中
     */
    Doing("doing"),

    /**
     * 正在清理跨区域复制
     */
    Closing("closing");

    private String statusString;

    private ReplicationStatus(String replicationStatusString) {
        this.statusString = replicationStatusString;
    }

    @Override
    public String toString() {
        return this.statusString;
    }

    public static ReplicationStatus parse(String replicationStatus) {
        for (ReplicationStatus status : ReplicationStatus.values()) {
            if (status.toString().equals(replicationStatus)) {
                return status;
            }
        }

        throw new IllegalArgumentException(
                "Unable to parse the provided replication status "
                        + replicationStatus);
    }
}
