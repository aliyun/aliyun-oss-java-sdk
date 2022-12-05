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
    private Long lastModifyBeginTimeStamp;
    private Long lastModifyEndTimeStamp;
    private Long lowerSizeBound;
    private Long upperSizeBound;
    private String storageClass;

    /**
     * @return Returns the prefix to use when evaluating an inventory filter.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sepecfies the prefix to use when evaluating an inventory filter.
     *
     * @param prefix
     *            the prefix to use when evaluating an inventory filter.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Sepecfies the prefix to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     *
     * @param prefix
     *            the prefix to use when evaluating an inventory filter.
     *
     * @return  The {@link InventoryFilter} instance.
     */
    public InventoryFilter withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }

    /**
     * @return Returns the start timestamp for data to use when evaluating an inventory filter.
     */
    public Long getLastModifyBeginTimeStamp() {
        return lastModifyBeginTimeStamp;
    }

    /**
     * Specifies the start timestamp for data to use when evaluating an inventory filter.
     *
     * @param lastModifyBeginTimeStamp
     *            start timestamp for data to use when evaluating an inventory filter.
     */
    public void setLastModifyBeginTimeStamp(Long lastModifyBeginTimeStamp) {
        this.lastModifyBeginTimeStamp = lastModifyBeginTimeStamp;
    }

    /**
     * Specifies the the start timestamp for data to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     *
     * @param lastModifyBeginTimeStamp
     *            start timestamp for data to use when evaluating an inventory filter.
     *
     * @return  The {@link InventoryFilter} instance.
     */
    public InventoryFilter withLastModifyBeginTimeStamp(Long lastModifyBeginTimeStamp) {
        setLastModifyBeginTimeStamp(lastModifyBeginTimeStamp);
        return this;
    }

    /**
     * @return Returns the end timestamp of data to use when evaluating an inventory filter.
     */
    public Long getLastModifyEndTimeStamp() {
        return lastModifyEndTimeStamp;
    }

    /**
     * Specifies the end timestamp of data to use when evaluating an inventory filter.
     *
     * @param lastModifyEndTimeStamp
     *            end timestamp of data to use when evaluating an inventory filter.
     */
    public void setLastModifyEndTimeStamp(Long lastModifyEndTimeStamp) {
        this.lastModifyEndTimeStamp = lastModifyEndTimeStamp;
    }

    /**
     * Specifies the end timestamp of data to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     *
     * @param lastModifyEndTimeStamp
     *            end timestamp of data to use when evaluating an inventory filter.
     *
     * @return  The {@link InventoryFilter} instance.
     */
    public InventoryFilter withLastModifyEndTimeStamp(Long lastModifyEndTimeStamp) {
        setLastModifyEndTimeStamp(lastModifyEndTimeStamp);
        return this;
    }

    /**
     * @return Returns the minimum size of filter file size to use when evaluating an inventory filter.
     */
    public Long getLowerSizeBound() {
        return lowerSizeBound;
    }

    /**
     * Specifies the minimum size of filter file size to use when evaluating an inventory filter.
     *
     * @param lowerSizeBound
     *            minimum size of filter file size to use when evaluating an inventory filter.
     */
    public void setLowerSizeBound(Long lowerSizeBound) {
        this.lowerSizeBound = lowerSizeBound;
    }

    /**
     * Specifies the minimum size of filter file size to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     *
     * @param lowerSizeBound
     *            minimum size of filter file size to use when evaluating an inventory filter.
     *
     * @return  The {@link InventoryFilter} instance.
     */
    public InventoryFilter withLowerSizeBound(Long lowerSizeBound) {
        setLowerSizeBound(lowerSizeBound);
        return this;
    }

    /**
     * @return Returns the maximum size of filter file size to use when evaluating an inventory filter.
     */
    public Long getUpperSizeBound() {
        return upperSizeBound;
    }

    /**
     * Specifies the maximum size of filter file size to use when evaluating an inventory filter.
     *
     * @param upperSizeBound
     *            maximum size of filter file size to use when evaluating an inventory filter.
     */
    public void setUpperSizeBound(Long upperSizeBound) {
        this.upperSizeBound = upperSizeBound;
    }

    /**
     * Specifies the maximum size of filter file size to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     *
     * @param upperSizeBound
     *            maximum size of filter file size to use when evaluating an inventory filter.
     *
     * @return  The {@link InventoryFilter} instance.
     */
    public InventoryFilter withUpperSizeBound(Long upperSizeBound) {
        setUpperSizeBound(upperSizeBound);
        return this;
    }

    /**
     * @return Returns the storage types to use when evaluating an inventory filter.
     */
    public String getStorageClass() {
        return storageClass;
    }

    /**
     * Specifies the storage types to use when evaluating an inventory filter.
     *
     * @param storageClass
     *            storage types to use when evaluating an inventory filter.
     */
    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    /**
     * Specifies the storage types to use when evaluating an inventory filter.
     * And returns the {@link InventoryFilter} object itself.
     *
     * @param storageClass
     *            storage types to use when evaluating an inventory filter.
     *
     * @return  The {@link InventoryFilter} instance.
     */
    public InventoryFilter withStorageClass(String storageClass) {
        setStorageClass(storageClass);
        return this;
    }
}