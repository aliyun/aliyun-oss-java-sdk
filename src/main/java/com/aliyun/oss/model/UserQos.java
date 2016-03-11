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

public class UserQos {
    
    public UserQos() {
    }
    
    public UserQos(int storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    /**
     * 返回bucket的容量，单位是GB。
     * @return bucket容量。
     */
    public int getStorageCapacity() {
        return storageCapacity;
    }

    /**
     * 设置bucket的容量，只允许是-1和非负整数；设置为-1时，表示不限制容量，单位是GB。
     * @param storageCapacity
     */
    public void setStorageCapacity(int storageCapacity) {
        this.storageCapacity = storageCapacity;
    }
    
    /**
     * StorageCapacity是否设置过
     * @return StorageCapacity是否设置过
     */
    public boolean hasStorageCapacity() {
        return storageCapacity != null;
    }

    private Integer storageCapacity;
    
}
