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

import java.util.Date;

/**
 * 跨区域复制进度。
 * <p>
 * 历史数据用已复制文件数目的百分比表示，如0.85表示已经完成85%，仅对开启了历史数据复制的Bucket有效；
 * 新写入复制进度用数据的时间点表示，表示早于这个时间点写入的数据都已复制到目标Bucket。
 * </p>
 */
public class BucketReplicationProgress {
    public String getReplicationRuleID() {
        return replicationRuleID;
    }

    public void setReplicationRuleID(String replicationRuleID) {
        this.replicationRuleID = replicationRuleID;
    }

    public ReplicationStatus getReplicationStatus() {
        return replicationStatus;
    }

    public void setReplicationStatus(ReplicationStatus replicationStatus) {
        this.replicationStatus = replicationStatus;
    }

    public String getTargetBucketName() {
        return targetBucketName;
    }

    public void setTargetBucketName(String targetBucketName) {
        this.targetBucketName = targetBucketName;
    }

    public String getTargetBucketLocation() {
        return targetBucketLocation;
    }

    public void setTargetBucketLocation(String targetBucketLocation) {
        this.targetBucketLocation = targetBucketLocation;
    }
    
    public boolean isEnableHistoricalObjectReplication() {
        return enableHistoricalObjectReplication;
    }

    public void setEnableHistoricalObjectReplication(
            boolean enableHistoricalObjectReplication) {
        this.enableHistoricalObjectReplication = enableHistoricalObjectReplication;
    }

    public float getHistoricalObjectProgress() {
        return historicalObjectProgress;
    }

    public void setHistoricalObjectProgress(float historicalObjectProgress) {
        this.historicalObjectProgress = historicalObjectProgress;
    }

    public Date getNewObjectProgress() {
        return newObjectProgress;
    }

    public void setNewObjectProgress(Date newObjectProgress) {
        this.newObjectProgress = newObjectProgress;
    }

    private String replicationRuleID;
    private ReplicationStatus replicationStatus;
    private String targetBucketName;
    private String targetBucketLocation;
    private boolean enableHistoricalObjectReplication;

    private float historicalObjectProgress;
    private Date newObjectProgress;
}
