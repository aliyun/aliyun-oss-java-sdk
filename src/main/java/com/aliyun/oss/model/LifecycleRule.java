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
 * Life cycle rule class.
 */
public class LifecycleRule {

    public static enum RuleStatus {
        Unknown, Enabled, // The Rule is enabled.
        Disabled // The rule is disabled.
    };

    public static class AbortMultipartUpload {
        private int expirationDays;
        private Date createdBeforeDate;

        public AbortMultipartUpload() {
        }

        public AbortMultipartUpload(int expirationDays) {
            this.expirationDays = expirationDays;
        }

        public AbortMultipartUpload(Date createdBeforeDate) {
            this.createdBeforeDate = createdBeforeDate;
        }

        public int getExpirationDays() {
            return expirationDays;
        }

        public void setExpirationDays(int expirationDays) {
            this.expirationDays = expirationDays;
        }

        public AbortMultipartUpload withExpirationDays(int expirationDays) {
            setExpirationDays(expirationDays);
            return this;
        }

        public boolean hasExpirationDays() {
            return this.expirationDays != 0;
        }

        public Date getCreatedBeforeDate() {
            return createdBeforeDate;
        }

        public void setCreatedBeforeDate(Date createdBeforeDate) {
            this.createdBeforeDate = createdBeforeDate;
        }

        public AbortMultipartUpload withCreatedBeforeDate(Date createdBeforeDate) {
            setCreatedBeforeDate(createdBeforeDate);
            return this;
        }

        public boolean hasCreatedBeforeDate() {
            return this.createdBeforeDate != null;
        }
    }

    public static class StorageTransition {
        private Integer expirationDays;
        private Date createdBeforeDate;
        private StorageClass storageClass;

        public StorageTransition() {
        }

        public StorageTransition(Integer expirationDays, StorageClass storageClass) {
            this.expirationDays = expirationDays;
            this.storageClass = storageClass;
        }

        public StorageTransition(Date createdBeforeDate, StorageClass storageClass) {
            this.createdBeforeDate = createdBeforeDate;
            this.storageClass = storageClass;
        }

        public Integer getExpirationDays() {
            return expirationDays;
        }

        public void setExpirationDays(Integer expirationDays) {
            this.expirationDays = expirationDays;
        }

        public StorageTransition withExpirationDays(Integer expirationDays) {
            setExpirationDays(expirationDays);
            return this;
        }

        public boolean hasExpirationDays() {
            return this.expirationDays != null;
        }

        public Date getCreatedBeforeDate() {
            return createdBeforeDate;
        }

        public void setCreatedBeforeDate(Date createdBeforeDate) {
            this.createdBeforeDate = createdBeforeDate;
        }

        public StorageTransition withCreatedBeforeDate(Date createdBeforeDate) {
            setCreatedBeforeDate(createdBeforeDate);
            return this;
        }

        public boolean hasCreatedBeforeDate() {
            return this.createdBeforeDate != null;
        }

        public StorageClass getStorageClass() {
            return storageClass;
        }

        public void setStorageClass(StorageClass storageClass) {
            this.storageClass = storageClass;
        }

        public StorageTransition withStrorageClass(StorageClass storageClass) {
            setStorageClass(storageClass);
            return this;
        }
    }

    private String id;
    private String prefix;
    private RuleStatus status;
    private int expirationDays;
    private Date expirationTime;
    private Date createdBeforeDate;

    private AbortMultipartUpload abortMultipartUpload;
    private List<StorageTransition> storageTransitions = new ArrayList<StorageTransition>();

    public LifecycleRule() {
        status = RuleStatus.Unknown;
    }

    public LifecycleRule(String id, String prefix, RuleStatus status) {
        this(id, prefix, status, null, null, null);
    }

    public LifecycleRule(String id, String prefix, RuleStatus status, int expirationDays) {
        this(id, prefix, status, expirationDays, null, null);
    }

    public LifecycleRule(String id, String prefix, RuleStatus status, Date expirationTime) {
        this(id, prefix, status, expirationTime, null, null);
    }

    public LifecycleRule(String id, String prefix, RuleStatus status, int expirationDays,
            AbortMultipartUpload abortMultipartUpload) {
        this.id = id;
        this.prefix = prefix;
        this.status = status;
        this.expirationDays = expirationDays;
        this.abortMultipartUpload = abortMultipartUpload;
    }

    public LifecycleRule(String id, String prefix, RuleStatus status, Date expirationTime,
            AbortMultipartUpload abortMultipartUpload) {
        this.id = id;
        this.prefix = prefix;
        this.status = status;
        this.expirationTime = expirationTime;
        this.abortMultipartUpload = abortMultipartUpload;
    }

    public LifecycleRule(String id, String prefix, RuleStatus status, int expirationDays,
            List<StorageTransition> storageTransitions) {
        this.id = id;
        this.prefix = prefix;
        this.status = status;
        this.expirationDays = expirationDays;
        if (storageTransitions != null && !storageTransitions.isEmpty()) {
            this.storageTransitions.addAll(storageTransitions);
        }
    }

    public LifecycleRule(String id, String prefix, RuleStatus status, Date expirationTime,
            List<StorageTransition> storageTransitions) {
        this.id = id;
        this.prefix = prefix;
        this.status = status;
        this.expirationTime = expirationTime;
        if (storageTransitions != null && !storageTransitions.isEmpty()) {
            this.storageTransitions.addAll(storageTransitions);
        }
    }

    public LifecycleRule(String id, String prefix, RuleStatus status, int expirationDays,
            AbortMultipartUpload abortMultipartUpload, List<StorageTransition> storageTransitions) {
        this.id = id;
        this.prefix = prefix;
        this.status = status;
        this.expirationDays = expirationDays;
        this.abortMultipartUpload = abortMultipartUpload;
        if (storageTransitions != null && !storageTransitions.isEmpty()) {
            this.storageTransitions.addAll(storageTransitions);
        }
    }

    public LifecycleRule(String id, String prefix, RuleStatus status, Date expirationTime,
            AbortMultipartUpload abortMultipartUpload, List<StorageTransition> storageTransitions) {
        this.id = id;
        this.prefix = prefix;
        this.status = status;
        this.expirationTime = expirationTime;
        this.abortMultipartUpload = abortMultipartUpload;
        if (storageTransitions != null && !storageTransitions.isEmpty()) {
            this.storageTransitions.addAll(storageTransitions);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public RuleStatus getStatus() {
        return status;
    }

    public void setStatus(RuleStatus status) {
        this.status = status;
    }

    @Deprecated
    public int getExpriationDays() {
        return expirationDays;
    }

    @Deprecated
    public void setExpriationDays(int expriationDays) {
        this.expirationDays = expriationDays;
    }

    public int getExpirationDays() {
        return expirationDays;
    }

    public void setExpirationDays(int expirationDays) {
        this.expirationDays = expirationDays;
    }

    public boolean hasExpirationDays() {
        return this.expirationDays != 0;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public boolean hasExpirationTime() {
        return this.expirationTime != null;
    }

    public Date getCreatedBeforeDate() {
        return createdBeforeDate;
    }

    public void setCreatedBeforeDate(Date date) {
        this.createdBeforeDate = date;
    }

    public boolean hasCreatedBeforeDate() {
        return this.createdBeforeDate != null;
    }

    public AbortMultipartUpload getAbortMultipartUpload() {
        return abortMultipartUpload;
    }

    public void setAbortMultipartUpload(AbortMultipartUpload abortMultipartUpload) {
        this.abortMultipartUpload = abortMultipartUpload;
    }

    public boolean hasAbortMultipartUpload() {
        return this.abortMultipartUpload != null;
    }

    public List<StorageTransition> getStorageTransition() {
        return this.storageTransitions;
    }

    public void setStorageTransition(List<StorageTransition> storageTransitions) {
        this.storageTransitions = storageTransitions;
    }

    public boolean hasStorageTransition() {
        return this.storageTransitions != null && !storageTransitions.isEmpty();
    }
}
