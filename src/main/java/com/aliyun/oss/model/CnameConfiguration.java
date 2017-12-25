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

public class CnameConfiguration {
    public static enum CnameStatus {
        Unknown, // initial state.
        Enabled, // Enable the CName
        Disabled // Disable the CName
    };

    public CnameConfiguration() {
        status = CnameStatus.Unknown;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public CnameStatus getStatus() {
        return status;
    }

    public void setStatus(CnameStatus status) {
        this.status = status;
    }

    public Date getLastMofiedTime() {
        return lastMofiedTime;
    }

    public void setLastMofiedTime(Date lastMofiedTime) {
        this.lastMofiedTime = lastMofiedTime;
    }

    public Boolean getPurgeCdnCache() {
        return purgeCdnCache;
    }

    public void setPurgeCdnCache(Boolean purgeCdnCache) {
        this.purgeCdnCache = purgeCdnCache;
    }

    @Override
    public String toString() {
        return "CnameConfiguration [domain=" + domain + ", status=" + status + ", lastMofiedTime=" + lastMofiedTime
                + "]";
    }

    private String domain;
    private CnameStatus status;
    private Date lastMofiedTime;
    private Boolean purgeCdnCache;
}
