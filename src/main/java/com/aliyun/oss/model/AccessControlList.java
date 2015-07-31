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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 表示OSS的访问控制列表（Access Control List， ACL），
 * 包含了一组为指定被授权者（{@link Grantee}}）
 * 分配特定权限（{@link Permission}）的集合。
 * */
public class AccessControlList implements Serializable {
    
	private static final long serialVersionUID = 211267925081748283L;

    private HashSet<Grant> grants = new HashSet<Grant>();
    private Owner owner;

    /**
     * 返回所有者{@link Owner}。
     * @return 所有者{@link Owner}。
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * 设置所有者{@link Owner}。
     * @param owner
     *          所有者{@link Owner}。
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * 为指定{@link Grantee}授权特定权限（{@link Permission}）。
     * 目前只支持被授权者为{@link GroupGrantee#AllUsers}。
     * @param grantee
     *          被授权者。目前只支持被授权者为{@link GroupGrantee#AllUsers}。
     * @param permission
     *          {@link Permission}中定义的权限。
     */
    public void grantPermission(Grantee grantee, Permission permission) {
        if (grantee == null || permission == null) {
            throw new NullPointerException();
        }
        
        grants.add(new Grant(grantee, permission));
    }
    
    /**
     * 取消指定{@link Grantee}已分配的所有权限。
     * @param grantee
     *           被授权者。目前只支持被授权者为{@link GroupGrantee#AllUsers}。
     */
    public void revokeAllPermissions(Grantee grantee) {
        if (grantee == null) {
            throw new NullPointerException();
        }
        
        ArrayList<Grant> grantsToRemove = new ArrayList<Grant>();
        for(Grant g : grants) {
            if (g.getGrantee().equals(g)) {
                grantsToRemove.add(g);
            }
        }
        grants.removeAll(grantsToRemove);
    }
    
    /**
     * 返回该{@link AccessControlList}中包含的所有授权信息{@link Grant}。
     * @return 该{@link AccessControlList}中包含的所有授权信息。
     */
    public Set<Grant> getGrants() {
        return this.grants;
    }

    /**
     * 返回该对象的字符串表示。
     */
    public String toString() {
        return "AccessControlList [owner=" + owner + ", grants=" + getGrants() + "]";
    }
}
