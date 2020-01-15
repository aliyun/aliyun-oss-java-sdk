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
import java.util.List;

public class InventoryConfiguration implements Serializable {
    private static final long serialVersionUID = 5232902629016021641L;

    /** The ID used to identify the inventory configuration. */
    private String inventoryId;

    /** Contains information about where to publish the inventory results. */
    private InventoryDestination destination;

    /** Specifies whether the inventory is enabled or disabled. */
    private Boolean isEnabled;

    /** Specifies an inventory filter. */
    private InventoryFilter inventoryFilter;

    /** Specifies which object version(s) to included in the inventory results. */
    private String includedObjectVersions;

    /** List to store the optional fields that are included in the inventory results. */
    private List<String> optionalFields;

    /** Specifies the schedule for generating inventory results. */
    private InventorySchedule schedule;

    /**
     * Returns the ID used to identify the inventory configuration.
     */
    public String getInventoryId() {
        return inventoryId;
    }

    /**
     * Sets the ID used to identify the inventory configuration.
     */
    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    /**
     * Sets the ID used to identify the inventory configuration.
     * And returns the {@link InventoryConfiguration} object itself.
     */
    public InventoryConfiguration withInventoryId(String inventoryId) {
        setInventoryId(inventoryId);
        return this;
    }

    /**
     * Returns the {@link InventoryDestination} that contains information
     * about where to publish the inventory results.
     */
    public InventoryDestination getDestination() {
        return destination;
    }

    /**
     * Sets the {@link InventoryDestination} that contains information
     * about where to publish the inventory results.
     */
    public void setDestination(InventoryDestination destination) {
        this.destination = destination;
    }

    /**
     * Sets the {@link InventoryDestination} that contains information
     * about where to publish the inventory results.
     * And returns the {@link InventoryConfiguration} object itself.
     */
    public InventoryConfiguration withDestination(InventoryDestination destination) {
        setDestination(destination);
        return this;
    }

    /**
     * Returns true if the inventory configuration is enabled or
     * false if it is disabled.
     */
    public Boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Sets the value whether the inventory configuration is enabled or disabled.
     */
    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * Sets the value whether the inventory configuration is enabled or disabled.
     * And returns the {@link InventoryConfiguration} object itself.
     */
    public InventoryConfiguration withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    /**
     * Returns the inventoryFilter used to describe a set of objects
     * to include in inventory results.
     */
    public InventoryFilter getInventoryFilter() {
        return inventoryFilter;
    }

    /**
     * Sets the inventoryFilter used to describe a set of objects
     * to include in inventory results.
     */
    public void setInventoryFilter(InventoryFilter inventoryFilter) {
        this.inventoryFilter = inventoryFilter;
    }

    /**
     * Sets the inventoryFilter used to describe a set of objects
     * to include in inventory results.
     * And returns the {@link InventoryConfiguration} object itself.
     */
    public InventoryConfiguration withFilter(InventoryFilter inventoryFilter) {
        setInventoryFilter(inventoryFilter);
        return this;
    }

    /**
     * Returns which object version(s) to included in the inventory results.
     */
    public String getIncludedObjectVersions() {
        return includedObjectVersions;
    }

    /**
     * Sets which object version(s) to included in the inventory results.
     */
    public void setIncludedObjectVersions(String includedObjectVersions) {
        this.includedObjectVersions = includedObjectVersions;
    }

    /**
     * Sets which object version(s) to included in the inventory results
     * And returns the {@link InventoryConfiguration} object itself.
     */
    public InventoryConfiguration withIncludedObjectVersions(String includedObjectVersions) {
        setIncludedObjectVersions(includedObjectVersions);
        return this;
    }

    /**
     * Sets which object version(s) to included in the inventory results.
     */
    public void setIncludedObjectVersions(InventoryIncludedObjectVersions includedObjectVersions) {
        setIncludedObjectVersions(includedObjectVersions == null ? (String) null : includedObjectVersions.toString());
    }

    /**
     * Sets which object version(s) to included in the inventory results
     * And returns the {@link InventoryConfiguration} object itself.
     */
    public InventoryConfiguration withIncludedObjectVersions(InventoryIncludedObjectVersions includedObjectVersions) {
        setIncludedObjectVersions(includedObjectVersions);
        return this;
    }

    /**
     * Returns the optional fields that are included in the inventory results.
     */
    public List<String> getOptionalFields() {
        return optionalFields;
    }

    /**
     * Sets the optional fields that are included in the inventory results.
     */
    public void setOptionalFields(List<String> optionalFields) {
        this.optionalFields = optionalFields;
    }

    /**
     * Sets the optional fields that are included in the inventory results.
     * And returns the {@link InventoryConfiguration} object itself.
     */
    public InventoryConfiguration withOptionalFields(List<String> optionalFields) {
        setOptionalFields(optionalFields);
        return this;
    }

    /**
     * Add a field to the list of optional fields that are included in the inventory results.
     */
    public void addOptionalField(String optionalField) {
        if (optionalField == null) {
            return;
        } else if (this.optionalFields == null) {
            this.optionalFields = new ArrayList<String >();
        }
        this.optionalFields.add(optionalField);
    }

    /**
     * Returns the schedule for generating inventory results.
     */
    public InventorySchedule getSchedule() {
        return schedule;
    }

    /**
     * Sets the schedule for generating inventory results.
     */
    public void setSchedule(InventorySchedule schedule) {
        this.schedule = schedule;
    }

    /**
     * Returns the schedule for generating inventory results.
     * And returns the {@link InventoryConfiguration} object itself.
     */
    public InventoryConfiguration withSchedule(InventorySchedule schedule) {
        setSchedule(schedule);
        return this;
    }
}