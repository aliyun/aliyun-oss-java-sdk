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

/**
 * 表示OSS Object ACL。
 */
public class ObjectAcl implements Serializable {
    
    private static final long serialVersionUID = 211267925081748283L;

    private Owner owner;
    private ObjectPermission permission;

    /**
     * 返回所有者{@link Owner}。
     * @return 
     *             所有者{@link Owner}。
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * 设置所有者{@link Owner}。
     * @param owner    
     *             所有者{@link Owner}。
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * 返回Object访问控制权限{@link ObjectPermission}。
     * @return 
     *             Object访问控制权限{@link ObjectPermission}。
     */
    public ObjectPermission getPermission() {
        return permission;
    }

    /**
     * 设置Object访问控制权限{@link ObjectPermission}。
     * @param permission 
     *             Object访问控制权限{@link ObjectPermission}。
     */
    public void setPermission(ObjectPermission permission) {
        this.permission = permission;
    }

    /**
     * 返回该对象的字符串表示。
     */
    public String toString() {
        return "AccessControlList [owner=" + owner + ", permission=" + permission + "]";
    }
}
