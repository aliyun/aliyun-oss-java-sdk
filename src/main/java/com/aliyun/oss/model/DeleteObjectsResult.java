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

/**
 * Successful response for deleting multiple objects.
 */
public class DeleteObjectsResult extends GenericResult {

    /* Successfully deleted objects */
    private final List<DeletedObject> deletedObjects = new ArrayList<DeletedObject>();

    /* User specified encoding method to be applied on the response. */
    private String encodingType;

    public DeleteObjectsResult() {
    }

    public DeleteObjectsResult(List<DeletedObject> deletedObjects) {
        if (deletedObjects != null && deletedObjects.size() > 0) {
            this.deletedObjects.addAll(deletedObjects);
        }
    }

    public List<DeletedObject> getDeletedObjects() {
        return deletedObjects;
    }

    public void setDeletedObjects(List<DeletedObject> deletedObjects) {
        this.deletedObjects.clear();
        this.deletedObjects.addAll(deletedObjects);
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * A successfully deleted object.
     */
    static public class DeletedObject implements Serializable {

        private String key;
        private String versionId;
        private boolean deleteMarker;
        private String deleteMarkerVersionId;

        /**
         * Returns the key that was successfully deleted.
         */
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        /**
         * Returns the version deleted, or null for unversioned objects.
         */
        public String getVersionId() {
            return versionId;
        }

        public void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        /**
         * Returns whether the object deleted was a delete marker.
         */
        public boolean isDeleteMarker() {
            return deleteMarker;
        }

        public void setDeleteMarker(boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
        }

        /**
         * Returns the versionId for the delete marker that was created when
         * doing a non-versioned delete in a versioned bucket.
         */
        public String getDeleteMarkerVersionId() {
            return deleteMarkerVersionId;
        }

        public void setDeleteMarkerVersionId(String deleteMarkerVersionId) {
            this.deleteMarkerVersionId = deleteMarkerVersionId;
        }

    }
}
