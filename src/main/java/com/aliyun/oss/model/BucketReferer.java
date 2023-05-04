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
 * The {@link Bucket}'s http referer information.
 * <p>
 * It defines the whitelist of websites that could access a bucket. Empty http
 * referer could also be included. Http referer is typically used to prevent
 * unauthorized access from other website.
 * </p>
 *
 */
public class BucketReferer extends GenericResult {
    private boolean allowEmptyReferer = true;
    private Boolean allowTruncateQueryString = null;
    private List<String> refererList = new ArrayList<String>();
    private List<String> blackRefererList = new ArrayList<String>();

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

    public BucketReferer withAllowEmptyReferer(boolean allowEmptyReferer) {
        setAllowEmptyReferer(allowEmptyReferer);
        return this;
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

    public BucketReferer withRefererList(List<String> refererList) {
        setRefererList(refererList);
        return this;
    }

    public void clearRefererList() {
        this.refererList.clear();
    }

    public Boolean isAllowTruncateQueryString() {
        return allowTruncateQueryString;
    }

    public void setAllowTruncateQueryString(Boolean allowTruncateQueryString) {
        this.allowTruncateQueryString = allowTruncateQueryString;
    }

    public BucketReferer withAllowTruncateQueryString(Boolean allowTruncateQueryString) {
        setAllowTruncateQueryString(allowTruncateQueryString);
        return this;
    }

    public List<String> getBlackRefererList() {
        return blackRefererList;
    }

    public void setBlackRefererList(List<String> blackRefererList) {
        this.blackRefererList = blackRefererList;
    }

    public BucketReferer withBlackRefererList(List<String> blackRefererList) {
        setBlackRefererList(blackRefererList);
        return this;
    }

    public void clearBlackRefererList() {
        this.blackRefererList.clear();
    }
}
