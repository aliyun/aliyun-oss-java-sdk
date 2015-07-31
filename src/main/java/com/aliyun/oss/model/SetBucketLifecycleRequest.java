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

public class SetBucketLifecycleRequest extends WebServiceRequest {
	
	public static final int MAX_LIFECYCLE_RULE_LIMIT = 1000;
	public static final int MAX_RULE_ID_LENGTH = 255;
    
	private String bucketName;
	private List<LifecycleRule> lifecycleRules = new ArrayList<LifecycleRule>();

	public SetBucketLifecycleRequest(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getBucketName() {
		return bucketName;
	}
	
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public List<LifecycleRule> getLifecycleRules() {
		return lifecycleRules;
	}

	public void setLifecycleRules(List<LifecycleRule> lifecycleRules) {
		if (lifecycleRules == null || lifecycleRules.isEmpty()) {
			throw new IllegalArgumentException("lifecycleRules should not be null or empty.");
		}
		
		if (lifecycleRules.size() > MAX_LIFECYCLE_RULE_LIMIT) {
			throw new IllegalArgumentException("One bucket not allow exceed one thousand items of LifecycleRules.");
		}
		
		this.lifecycleRules.clear();
		this.lifecycleRules.addAll(lifecycleRules);
	}
	
	public void clearLifecycles() {
		this.lifecycleRules.clear();
	}
	
	public void AddLifecycleRule(LifecycleRule lifecycleRule) {
		if (lifecycleRule == null) {
			throw new IllegalArgumentException("lifecycleRule should not be null or empty.");
		}
		
		if (this.lifecycleRules.size() >= MAX_LIFECYCLE_RULE_LIMIT) {
			throw new IllegalArgumentException("One bucket not allow exceed one thousand items of LifecycleRules.");
		}
		
		if (lifecycleRule.getId() != null && lifecycleRule.getId().length() > MAX_RULE_ID_LENGTH) {
			throw new IllegalArgumentException("Length of lifecycle rule id exceeds max limit " 
					+ MAX_RULE_ID_LENGTH);
		}
		
		boolean hasSetExpirationTime = (lifecycleRule.getExpirationTime() != null);
		boolean hasSetExpirationDays =(lifecycleRule.getExpriationDays() != 0);
		if ((!hasSetExpirationTime && !hasSetExpirationDays) 
				|| (hasSetExpirationTime && hasSetExpirationDays)) {
			throw new IllegalArgumentException("Only one expiration property should be specified.");
		}
		
		if (lifecycleRule.getStatus() == LifecycleRule.RuleStatus.Unknown) {
			throw new IllegalArgumentException("RuleStatus property should be specified with 'Enabled' or 'Disabled'.");
		}
		
		this.lifecycleRules.add(lifecycleRule);
	}
}





