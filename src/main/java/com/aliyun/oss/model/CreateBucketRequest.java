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

public class CreateBucketRequest extends GenericRequest {

    private String locationConstraint;
    private CannedAccessControlList cannedACL;
    private StorageClass storageClass;

    public CreateBucketRequest(String bucketName) {
        super(bucketName);
        setLocationConstraint(null);
        setCannedACL(null);
    }

    /**
     * Gets the bucket's datacenter
     * 
     * @return Bucket's datacenter.
     */
    public String getLocationConstraint() {
        return locationConstraint;
    }

    /**
     * Sets the bucket's datacenter.
     * 
     * @param locationConstraint
     *            Bucket's datacenter.
     */
    public void setLocationConstraint(String locationConstraint) {
        this.locationConstraint = locationConstraint;
    }

    /**
     * Creates the instance with datacenter.
     * 
     * @param locationConstraint
     *            Bucket datacenter.
     */
    public CreateBucketRequest withLocationConstraint(String locationConstraint) {
        setLocationConstraint(locationConstraint);
        return this;
    }

    public CannedAccessControlList getCannedACL() {
        return cannedACL;
    }

    public void setCannedACL(CannedAccessControlList cannedACL) {
        this.cannedACL = cannedACL;
    }

    public StorageClass getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(StorageClass storageClass) {
        this.storageClass = storageClass;
    }

    public CreateBucketRequest withCannedACL(CannedAccessControlList cannedACL) {
        setCannedACL(cannedACL);
        return this;
    }

    public CreateBucketRequest withStorageType(StorageClass storageClass) {
        setStorageClass(storageClass);
        return this;
    }
}
