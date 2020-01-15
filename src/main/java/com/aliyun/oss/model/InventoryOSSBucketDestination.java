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
 * Contains the bucket name, file format, bucket owner (optional),
 * prefix (optional) and encryption (optional) where inventory results are published.
 */
public class InventoryOSSBucketDestination implements Serializable {
    private static final long serialVersionUID = 3235296268643849681L;

    private String accountId;

    private String roleArn;

    private String bucket;

    private String format;

    private String prefix;

    private InventoryEncryption encryption;

    /**
     * Returns the account ID that owns the destination bucket.
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * Sets the account ID that owns the destination bucket.
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * Returns the account ID that owns the destination bucket.
     * And returns the {@link InventoryOSSBucketDestination} object itself.
     */
    public InventoryOSSBucketDestination withAccountId(String accountId) {
        setAccountId(accountId);
        return this;
    }

    /**
     * Sets the name of the role arn.
     */
    public String getRoleArn() {
        return roleArn;
    }

    /**
     * Sets the name of the role arn.
     */
    public void setRoleArn(String roleArn) {
        this.roleArn = roleArn;
    }

    /**
     * Sets the name of the role arn.
     * And returns the {@link InventoryOSSBucketDestination} object itself.
     */
    public InventoryOSSBucketDestination withRoleArn(String roleArn) {
        setRoleArn(roleArn);
        return this;
    }

    /**
     * Sets the bucket where the inventory results will be published.
     */
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    /**
     * Gets the bucket where the inventory results will be published.
     */
    public String getBucket() {
        return bucket;
    }

    /**
     * Sets the bucket where the inventory results will be published.
     * And returns the {@link InventoryOSSBucketDestination} object itself.
     */
    public InventoryOSSBucketDestination withBucket(String bucket) {
        setBucket(bucket);
        return this;
    }

    /**
     * Returns the output format of the inventory results.
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the output format of the inventory results.
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Sets the output format of the inventory results.
     */
    public void setFormat(InventoryFormat format) {
        setFormat(format == null ? (String) null : format.toString());
    }

    /**
     * Sets the output format of the inventory results.
     * And returns the {@link InventoryOSSBucketDestination} object itself.
     */
    public InventoryOSSBucketDestination withFormat(String format) {
        setFormat(format);
        return this;
    }

    /**
     * Sets the output format of the inventory results
     * And returns the {@link InventoryOSSBucketDestination} object itself.
     */
    public InventoryOSSBucketDestination withFormat(InventoryFormat format) {
        setFormat(format);
        return this;
    }

    /**
     * Returns the prefix that is prepended to all inventory results.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix that is prepended to all inventory results.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Sets the prefix that is prepended to all inventory results
     * And returns the {@link InventoryOSSBucketDestination} object itself.
     */
    public InventoryOSSBucketDestination withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }

    /**
     * Gets the inventory contents encryption.
     */
    public InventoryEncryption getEncryption() {
        return encryption;
    }

    /**
     * Sets the inventory contents encryption.
     */
    public void setEncryption(InventoryEncryption encryption) {
        this.encryption = encryption;
    }

    /**
     * Sets the inventory contents encryption.
     * And returns the {@link InventoryOSSBucketDestination} object itself.
     */
    public InventoryOSSBucketDestination withEncryption(InventoryEncryption encryption) {
        setEncryption(encryption);
        return this;
    }
}