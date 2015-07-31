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
 * 表示一组常用的用户访问权限。
 * <p>
 * 这一组常用权限相当于给所有用户指定权限的快捷方法。
 * </p>
 *
 */
public enum CannedAccessControlList {

    /**
     * 指定只有所有者具有完全控制权限 {@link Permission#FullControl}，
     * 其他用户{@link GroupGrantee#AllUsers}无权访问。
     */
    Private("private"),

    /**
     * 指定所有者具有完全控制权限 {@link Permission#FullControl}，
     * 其他用户{@link GroupGrantee#AllUsers}只有只读权限 {@link Permission#Read}。
     */
    PublicRead("public-read"),

    /**
     * 指定所有者和其他用户{@link GroupGrantee#AllUsers}均有完全控制权限{@link Permission#FullControl}。
     * 不推荐使用。
     */
    PublicReadWrite("public-read-write");

    private String cannedAclString;

    private CannedAccessControlList(String cannedAclString){
        this.cannedAclString = cannedAclString;
    }

    @Override
    public String toString() {
        return this.cannedAclString;
    }
    
    public static CannedAccessControlList parse(String acl) {
        for(CannedAccessControlList cacl : CannedAccessControlList.values()) {
            if (cacl.toString().equals(acl)) {
                return cacl;
            }
        }
        
        throw new IllegalArgumentException("Unable to parse the provided acl " + acl);
    }
}
