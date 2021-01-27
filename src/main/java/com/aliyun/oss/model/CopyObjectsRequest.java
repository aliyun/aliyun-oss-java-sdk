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

import java.util.ArrayList;
import java.util.List;

/**
 * The request class that is used to copy objects. It wraps all parameters
 * needed to copy objects.
 */
public class CopyObjectsRequest extends WebServiceRequest {
    private String bucketName;
    private List<CopyObjectEntity> copyObjectEntities;

    public CopyObjectsRequest(String bucketName) {
        setBucketName(bucketName);
        this.copyObjectEntities = new ArrayList<CopyObjectEntity>();
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void addCopyObjectEntity(CopyObjectEntity copyObjectEntity) {
        copyObjectEntities.add(copyObjectEntity);
    }

    public List<CopyObjectEntity> getCopyObjectEntities() {
        return copyObjectEntities;
    }
}
