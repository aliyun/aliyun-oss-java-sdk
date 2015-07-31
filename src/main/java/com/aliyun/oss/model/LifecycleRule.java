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
 * 表示一条Lifecycle规则。
 */
public class LifecycleRule {
	
	public static enum RuleStatus {
		Unknown,
        Enabled,    // 启用规则
        Disabled    // 禁用规则
    };
	
	private String id;
	private String prefix;
	private RuleStatus status;
	private int expriationDays;
	private Date expirationTime;
	
	public LifecycleRule() {
		status = RuleStatus.Unknown;
	}
	
	public LifecycleRule(String id, String prefix, RuleStatus status,
			int expriationDays) {
		this.id = id;
		this.prefix = prefix;
		this.status = status;
		this.expriationDays = expriationDays;
	}

	public LifecycleRule(String id, String prefix, RuleStatus status,
			Date expirationTime) {
		this.id = id;
		this.prefix = prefix;
		this.status = status;
		this.expirationTime = expirationTime;
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
	
	public int getExpriationDays() {
		return expriationDays;
	}
	
	public void setExpriationDays(int expriationDays) {
		this.expriationDays = expriationDays;
	}
	
	public Date getExpirationTime() {
		return expirationTime;
	}
	
	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}
}
