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
 * 访问控制的授权信息。
 */
public class Grant {
    
	private Grantee grantee;
    private Permission permission;
    
    /**
     * 构造函数。
     * @param grantee
     *          被授权者。
     *          目前只支持 {@link GroupGrantee#AllUsers}。
     * @param permission
     *          权限。
     */
    public Grant(Grantee grantee, Permission permission) {
        if (grantee == null || permission == null) {
            throw new NullPointerException();
        }
        
        this.grantee = grantee;
        this.permission = permission;
    }

    /**
     * 返回被授权者信息{@link Grantee}。
     * @return 被授权者信息{@link Grantee}。
     */
    public Grantee getGrantee() {
        return grantee;
    }

    /**
     * 返回权限{@link Permission}。
     * @return 权限{@link Permission}。
     */
    public Permission getPermission() {
        return permission;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Grant)) {
            return false;
        }
        Grant g = (Grant)o;
        return this.getGrantee().getIdentifier().equals(g.getGrantee().getIdentifier())
                && this.getPermission().equals(g.getPermission());
    }
    
    @Override
    public int hashCode() {
        return (grantee.getIdentifier() + ":" + this.getPermission().toString()).hashCode();
    }

    @Override
    public String toString() {
        return "Grant [grantee=" + getGrantee() + ",permission=" + getPermission() + "]";
    }
}
