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

import java.util.Date;

/**
 * Bucket is the namespace in OSS. You could think it's a folder or container
 * under OSS.
 * <p>
 * Bucket name is globally unique in OSS and is immutable. Every object must
 * belong to a bucket. An application such as picture sharing website could be
 * mapped to one or multiple buckets. An OSS account could only create up to 10
 * bucket. And there's no limit on the files count or size under a bucket.
 * </p>
 * <p>
 * Bucket naming rules:
 * <ul>
 * <li>Can only contain low case letter, number or dash(-).</li>
 * <li>Can only start with low case letter or number.</li>
 * <li>The length must be between 3 to 63 bytes.</li>
 * </ul>
 * </p>
 */
public class Bucket extends GenericResult {

    // Bucket name
    private String name;

    // Bucket owner
    private Owner owner;

    // Bucket location
    private String location;

    // Created date.
    private Date creationDate;

    // Storage class (Standard, IA, Archive)
    private StorageClass storageClass = StorageClass.Standard;

    // External endpoint.It could be accessed from anywhere.
    private String extranetEndpoint;

    // Internal endpoint. It could be accessed within AliCloud under the same
    // location.
    private String intranetEndpoint;

    /**
     * Default constructor.
     */
    public Bucket() {
    }

    /**
     * Constructor with the bucket name parameter.
     * 
     * @param name
     *            Bucket name.
     */
    public Bucket(String name) {
        this.name = name;
    }

    public Bucket(String name, String requestId) {
        setName(name);
        setRequestId(requestId);
    }

    /**
     * The override of toString(). Returns the bucket name, creation date, owner
     * and location, with optional storage class.
     */
    @Override
    public String toString() {
        if (storageClass == null) {
            return "OSSBucket [name=" + getName() + ", creationDate=" + getCreationDate() + ", owner=" + getOwner()
                    + ", location=" + getLocation() + "]";
        } else {
            return "OSSBucket [name=" + getName() + ", creationDate=" + getCreationDate() + ", owner=" + getOwner()
                    + ", location=" + getLocation() + ", storageClass=" + getStorageClass() + "]";
        }
    }

    /**
     * Gets the {@link Owner}.
     * 
     * @return The bucket owner or null if the owner is not known.
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Sets the bucket owner (only used by SDK itself).
     * 
     * @param owner
     *            Bucket owner.
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * Gets the bucket's creation time.
     * 
     * @return Bucket's creation time or null if the creation time is unknown.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets teh bucket's creation time.(it's only used by SDK itself).
     * 
     * @param creationDate
     *            Bucket's creation time.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the bucket name
     * 
     * @return Bucket name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the bucket name (should only be used by the SDK itself).
     * 
     * @param name
     *            Bucket name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the bucket location.
     * 
     * @return Bucket location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the bucket location.
     * 
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the storage class
     * 
     * @return storage class
     */
    public StorageClass getStorageClass() {
        return storageClass;
    }

    /**
     * Sets the bucket's storage class
     * 
     * @param storageClass
     */
    public void setStorageClass(StorageClass storageClass) {
        this.storageClass = storageClass;
    }

    /**
     * Gets the external endpoint.
     * 
     * @return external endpoint
     */
    public String getExtranetEndpoint() {
        return extranetEndpoint;
    }

    /**
     * Sets the external endpoint.
     * 
     * @param endpoint
     *            external endpoint
     */
    public void setExtranetEndpoint(String endpoint) {
        this.extranetEndpoint = endpoint;
    }

    /**
     * Gets the internal endpoint.
     * 
     * @return Internal endpoint
     */
    public String getIntranetEndpoint() {
        return intranetEndpoint;
    }

    /**
     * Sets the internal endpoint.
     * 
     * @param endpoint
     *            Internal endpoint
     */
    public void setIntranetEndpoint(String endpoint) {
        this.intranetEndpoint = endpoint;
    }
}
