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

public class InventoryFilter implements Serializable {
    private static final long serialVersionUID = 6611200273488675580L;

    private String prefix;
    private Integer lastModifyBeginTimeStamp;
    private Integer lastModifyEndTimeStamp;
    private Integer lowerSizeBound;
    private Integer upperSizeBound;
    private String storageClass;

    /**
     * Returns the prefix to use when evaluating an inventory filter.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sepecfies the prefix to use when evaluating an inventory filter.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Sepecfies the prefix to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     */
    public InventoryFilter withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }

    /**
     * Returns the start timestamp for data to use when evaluating an inventory filter.
     */
    public Integer getLastModifyBeginTimeStamp() {
        return lastModifyBeginTimeStamp;
    }

    /**
     * Specifies the start timestamp for data to use when evaluating an inventory filter.
     */
    public void setLastModifyBeginTimeStamp(Integer lastModifyBeginTimeStamp) {
        this.lastModifyBeginTimeStamp = lastModifyBeginTimeStamp;
    }

    /**
     * Specifies the the start timestamp for data to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     */
    public InventoryFilter withLastModifyBeginTimeStamp(Integer lastModifyBeginTimeStamp) {
        setLastModifyBeginTimeStamp(lastModifyBeginTimeStamp);
        return this;
    }

    /**
     * Returns the end timestamp of data to use when evaluating an inventory filter.
     */
    public Integer getLastModifyEndTimeStamp() {
        return lastModifyEndTimeStamp;
    }

    /**
     * Specifies the end timestamp of data to use when evaluating an inventory filter.
     */
    public void setLastModifyEndTimeStamp(Integer lastModifyEndTimeStamp) {
        this.lastModifyEndTimeStamp = lastModifyEndTimeStamp;
    }

    /**
     * Specifies the end timestamp of data to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     */
    public InventoryFilter withLastModifyEndTimeStamp(Integer lastModifyEndTimeStamp) {
        setLastModifyEndTimeStamp(lastModifyEndTimeStamp);
        return this;
    }

    /**
     * Returns the minimum size of filter file size to use when evaluating an inventory filter.
     */
    public Integer getLowerSizeBound() {
        return lowerSizeBound;
    }

    /**
     * Specifies the minimum size of filter file size to use when evaluating an inventory filter.
     */
    public void setLowerSizeBound(Integer lowerSizeBound) {
        this.lowerSizeBound = lowerSizeBound;
    }

    /**
     * Specifies the minimum size of filter file size to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     */
    public InventoryFilter withLowerSizeBound(Integer lowerSizeBound) {
        setLowerSizeBound(lowerSizeBound);
        return this;
    }

    /**
     * Returns the maximum size of filter file size to use when evaluating an inventory filter.
     */
    public Integer getUpperSizeBound() {
        return upperSizeBound;
    }

    /**
     * Specifies the maximum size of filter file size to use when evaluating an inventory filter.
     */
    public void setUpperSizeBound(Integer upperSizeBound) {
        this.upperSizeBound = upperSizeBound;
    }

    /**
     * Specifies the maximum size of filter file size to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     */
    public InventoryFilter withUpperSizeBound(Integer upperSizeBound) {
        setUpperSizeBound(upperSizeBound);
        return this;
    }

    /**
     * Returns the storage types to use when evaluating an inventory filter.
     */
    public String getStorageClass() {
        return storageClass;
    }

    /**
     * Specifies the storage types to use when evaluating an inventory filter.
     */
    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    /**
     * Specifies the storage types to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     */
    public InventoryFilter withStorageClass(String storageClass) {
        setStorageClass(storageClass);
        return this;
    }
}