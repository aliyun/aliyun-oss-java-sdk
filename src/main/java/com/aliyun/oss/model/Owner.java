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
 * 表示OSS {@link Bucket}的所有者。
 *
 */
public class Owner implements Serializable {

    private static final long serialVersionUID = -1942759024112448066L;
    private String displayName;
    private String id;

    /**
     * 构造函数。
     */
    public Owner() {
    }

    /**
     * 构造函数。
     * @param id
     *          所有者ID。
     * @param displayName
     *          显示名称。
     */
    public Owner(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    /**
     * 返回该对象的字符串表示。
     */
    @Override
    public String toString() {
        return "Owner [name=" + getDisplayName() + ",id=" + getId() + "]";
    }

    /**
     * 返回所有者的ID。
     * @return 所有者的ID。
     */
    public String getId() {
        return id;
    }

    /**
     * 设置所有者的ID。
     * @param id
     *          所有者的ID。
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 返回所有者的显示名称。
     * @return 所有者的显示名称。
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 设置所有者的显示名称。
     * @param name
     *          所有者的显示名称。
     */
    public void setDisplayName(String name) {
        this.displayName = name;
    }

    /**
     * 判断该对象与指定对象是否相等。
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Owner)) {
            return false;
        }

        Owner otherOwner = (Owner)obj;

        String otherOwnerId = otherOwner.getId();
        String otherOwnerName = otherOwner.getDisplayName();
        String thisOwnerId = this.getId();
        String thisOwnerName = this.getDisplayName();

        if (otherOwnerId == null) otherOwnerId = "";
        if (otherOwnerName == null) otherOwnerName = "";
        if (thisOwnerId == null) thisOwnerId = "";
        if (thisOwnerName == null) thisOwnerName = "";

        return (otherOwnerId.equals(thisOwnerId) &&
                otherOwnerName.equals(thisOwnerName));
    }

    /**
     * 返回该实例的哈布值。
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else {
            return 0;
        }
    }

}
