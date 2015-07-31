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

import static com.aliyun.oss.internal.OSSUtils.OSS_RESOURCE_MANAGER;

/**
 * 定义了可以被授权的一组OSS用户。
 */
public enum GroupGrantee implements Grantee {
    /**
     * 表示为OSS的{@link Bucket}或{@link OSSObject}指定匿名访问的权限。
     * 任何用户都可以根据被授予的权限进行访问。
     */
    AllUsers("http://oss.service.aliyun.com/acl/group/ALL_USERS");

    private String groupUri;

    private GroupGrantee(String groupUri){
        this.groupUri = groupUri;
    }

    /**
     * 获取被授权者的ID。
     */
    public String getIdentifier(){
        return this.groupUri;
    }

    /**
     * 不支持该操作。
     */
    public void setIdentifier(String id){
        throw new UnsupportedOperationException(OSS_RESOURCE_MANAGER.getString("GroupGranteeNotSupportId"));
    }
}
