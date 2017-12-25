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
 * OSS Object ACL。
 */
public class ObjectAcl extends GenericResult implements Serializable {

    private static final long serialVersionUID = 211267925081748283L;

    private Owner owner;
    private ObjectPermission permission;

    /**
     * Gets the {@link Owner}.
     * 
     * @return The {@link Owner}。
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Sets the {@link Owner}.
     * 
     * @param owner
     *            The {@link Owner}.
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * Gets the object's {@link ObjectPermission}。
     * 
     * @return Object's {@link ObjectPermission}。
     */
    public ObjectPermission getPermission() {
        return permission;
    }

    /**
     * Sets the object's {@link ObjectPermission}.
     * 
     * @param permission
     *            Object's {@link ObjectPermission}.
     */
    public void setPermission(ObjectPermission permission) {
        this.permission = permission;
    }

    /**
     * The ACL's serialization string, which includes owner and permission.
     */
    public String toString() {
        return "AccessControlList [owner=" + owner + ", permission=" + permission + "]";
    }
}
