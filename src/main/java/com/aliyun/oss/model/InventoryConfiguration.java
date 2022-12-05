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
     * @return Returns the ID used to identify the inventory configuration.
     */
    public String getInventoryId() {
        return inventoryId;
    }

    /**
     * Sets the ID used to identify the inventory configuration.
     *
     * @param inventoryId
     *            inventory id
     */
    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    /**
     * Sets the ID used to identify the inventory configuration.
     * And returns the {@link InventoryConfiguration} object itself.
     * @param inventoryId
     *            inventory id.
     *
     * @return The {@link InventoryConfiguration} instance.
     */
    public InventoryConfiguration withInventoryId(String inventoryId) {
        setInventoryId(inventoryId);
        return this;
    }

    /**
     * Returns the {@link InventoryDestination} that contains information
     * about where to publish the inventory results.
     *
     * @return The {@link InventoryDestination} instance.
     */
    public InventoryDestination getDestination() {
        return destination;
    }

    /**
     * Sets the {@link InventoryDestination} that contains information
     * about where to publish the inventory results.
     *
     * @param destination
     *            The {@link InventoryDestination} instance.
     */
    public void setDestination(InventoryDestination destination) {
        this.destination = destination;
    }

    /**
     * Sets the {@link InventoryDestination} that contains information
     * about where to publish the inventory results.
     * And returns the {@link InventoryConfiguration} object itself.
     *
     * @param destination
     *            The {@link InventoryDestination} instance.
     *
     * @return The {@link InventoryConfiguration} instance.
     */
    public InventoryConfiguration withDestination(InventoryDestination destination) {
        setDestination(destination);
        return this;
    }

    /**
     * @return Returns true if the inventory configuration is enabled or
     * false if it is disabled.
     */
    public Boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Sets the value whether the inventory configuration is enabled or disabled.
     *
     * @param enabled
     *            inventory configuration is enabled or disabled
     */
    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * Sets the value whether the inventory configuration is enabled or disabled.
     * And returns the {@link InventoryConfiguration} object itself.
     *
     * @param enabled
     *            inventory configuration is enabled or disabled.
     *
     * @return The {@link InventoryConfiguration} instance.
     */
    public InventoryConfiguration withEnabled(Boolean enabled) {
        setEnabled(enabled);
        return this;
    }

    /**
     * Returns the inventoryFilter used to describe a set of objects
     * to include in inventory results.
     *
     * @return The {@link InventoryFilter} instance.
     */
    public InventoryFilter getInventoryFilter() {
        return inventoryFilter;
    }

    /**
     * Sets the inventoryFilter used to describe a set of objects
     * to include in inventory results.
     *
     * @param inventoryFilter
     *            The {@link InventoryFilter} instance.
     */
    public void setInventoryFilter(InventoryFilter inventoryFilter) {
        this.inventoryFilter = inventoryFilter;
    }

    /**
     * Sets the inventoryFilter used to describe a set of objects
     * to include in inventory results.
     * And returns the {@link InventoryConfiguration} object itself.
     *
     * @param inventoryFilter
     *            The {@link InventoryFilter} instance.
     *
     * @return The {@link InventoryConfiguration} instance.
     */
    public InventoryConfiguration withFilter(InventoryFilter inventoryFilter) {
        setInventoryFilter(inventoryFilter);
        return this;
    }

    /**
     * @return Returns which object version(s) to included in the inventory results.
     */
    public String getIncludedObjectVersions() {
        return includedObjectVersions;
    }

    /**
     * Sets which object version(s) to included in the inventory results.
     *
     * @param includedObjectVersions
     *            included object versions.
     */
    public void setIncludedObjectVersions(String includedObjectVersions) {
        this.includedObjectVersions = includedObjectVersions;
    }

    /**
     * Sets which object version(s) to included in the inventory results
     * And returns the {@link InventoryConfiguration} object itself.
     *
     * @param includedObjectVersions
     *            included object versions.
     *
     * @return The {@link InventoryConfiguration} instance.
     */
    public InventoryConfiguration withIncludedObjectVersions(String includedObjectVersions) {
        setIncludedObjectVersions(includedObjectVersions);
        return this;
    }

    /**
     * Sets which object version(s) to included in the inventory results.
     *
     * @param includedObjectVersions
     *            The {@link InventoryIncludedObjectVersions} instance.
     */
    public void setIncludedObjectVersions(InventoryIncludedObjectVersions includedObjectVersions) {
        setIncludedObjectVersions(includedObjectVersions == null ? (String) null : includedObjectVersions.toString());
    }

    /**
     * Sets which object version(s) to included in the inventory results
     * And returns the {@link InventoryConfiguration} object itself.
     *
     * @param includedObjectVersions
     *            The {@link InventoryIncludedObjectVersions} instance.
     *
     * @return The {@link InventoryConfiguration} instance.
     */
    public InventoryConfiguration withIncludedObjectVersions(InventoryIncludedObjectVersions includedObjectVersions) {
        setIncludedObjectVersions(includedObjectVersions);
        return this;
    }

    /**
     * @return Returns the optional fields that are included in the inventory results.
     */
    public List<String> getOptionalFields() {
        return optionalFields;
    }

    /**
     * Sets the optional fields that are included in the inventory results.
     *
     * @param optionalFields
     *            optional fields.
     */
    public void setOptionalFields(List<String> optionalFields) {
        this.optionalFields = optionalFields;
    }

    /**
     * Sets the optional fields that are included in the inventory results.
     * And returns the {@link InventoryConfiguration} object itself.
     *
     * @param optionalFields
     *            optional fields.
     *
     * @return  The {@link InventoryConfiguration} instance.
     */
    public InventoryConfiguration withOptionalFields(List<String> optionalFields) {
        setOptionalFields(optionalFields);
        return this;
    }

    /**
     * Add a field to the list of optional fields that are included in the inventory results.
     *
     * @param optionalField
     *            optional field.
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
     *
     * @return  The {@link InventorySchedule} instance.
     */
    public InventorySchedule getSchedule() {
        return schedule;
    }

    /**
     * Sets the schedule for generating inventory results.
     *
     * @param schedule
     *            The {@link InventorySchedule} instance.
     */
    public void setSchedule(InventorySchedule schedule) {
        this.schedule = schedule;
    }

    /**
     * Returns the schedule for generating inventory results.
     * And returns the {@link InventoryConfiguration} object itself.
     *
     * @param schedule
     *            The {@link InventorySchedule} instance.
     *
     * @return The {@link InventoryConfiguration} instance.
     */
    public InventoryConfiguration withSchedule(InventorySchedule schedule) {
        setSchedule(schedule);
        return this;
    }
}