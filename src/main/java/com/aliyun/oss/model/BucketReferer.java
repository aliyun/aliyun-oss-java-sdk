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
 * 表示{@link Bucket}的http referer信息。
 * <p>
 * 该配置会指明是否允许空的http referer，以及能够访问{@link Bucket}的白名单，常用于防盗链。
 * </p>
 *
 */
public class BucketReferer {
	private boolean allowEmptyReferer = true;
	private List<String> refererList = new ArrayList<String>();

	public BucketReferer() {
		
	}
	
	public BucketReferer(boolean allowEmptyReferer, List<String> refererList) {
		setAllowEmptyReferer(allowEmptyReferer);
		setRefererList(refererList);
	}
	
	@Deprecated
	public boolean allowEmpty() {
		return this.allowEmptyReferer;
	}
	
	public boolean isAllowEmptyReferer() {
		return allowEmptyReferer;
	}

	public void setAllowEmptyReferer(boolean allowEmptyReferer) {
		this.allowEmptyReferer = allowEmptyReferer;
	}

	public List<String> getRefererList() {
		return refererList;
	}
	
	public void setRefererList(List<String> refererList) {
		this.refererList.clear();
		if (refererList != null && !refererList.isEmpty()) {
			this.refererList.addAll(refererList);
		}
	}
	
	public void clearRefererList() {
		this.refererList.clear();
	}
}
