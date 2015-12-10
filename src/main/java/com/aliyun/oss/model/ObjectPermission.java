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
 * 表示{@link OSSObject}访问控制权限。
 */
public enum ObjectPermission {
    
    /**
     * 表明某个Object是私有资源，即只有该Object的Owner拥有该Object的读写权限，
     * 其他的用户没有权限操作该Object。
     */
    Private("private"),
    
    /**
     * 表明某个Object是公共读资源，即非Object Owner只有该Object的读权限，
     * 而Object Owner拥有该Object的读写权限
     */
    PublicRead("public-read"),
    
    /**
     * 表明某个Object是公共读写资源，即所有用户拥有对该Object的读写权限。
     */
    PublicReadWrite("public-read-write"),
    
    /**
     * 表明该Object ACL遵循Bucket ACL。即：如果Bucket是private的，则该object也是private的；
     * 如果该object是public-read-write的，则该object也是public-read-write的。
     */
    Default("default"),
    
    /**
     * 表明该Object ACL为未知类型，当出现该类型时，请联系OSS管理员获取更多信息。
     */
    Unknown("");
    
    private String permissionString;
    
    private ObjectPermission(String permissionString) {
        this.permissionString = permissionString;
    }
    
    @Override
    public String toString() {
        return permissionString;
    }
    
    public static ObjectPermission parsePermission(String str) {
        final ObjectPermission[] knownPermissions = { Private, PublicRead, PublicReadWrite, Default };
        for (ObjectPermission permission : knownPermissions) {
            if (permission.permissionString.equals(str)) {
                return permission;
            }
        }
        
        return Unknown;
    }
}
