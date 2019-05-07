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

/**
 * Request object for the parameters to set the tags for an object.
 */
public class SetObjectTaggingRequest extends GenericRequest {
  private ObjectTagging tagging;

  private String versionId;

  /**
   * Constructs an instance of this object.
   *
   * @param bucketName
   *            The bucket name.
   * @param key
   *            The object key.
   * @param tagging
   *            The set of tags to set for the specified object.
   */
  public SetObjectTaggingRequest(String bucketName, String key, ObjectTagging tagging) {
    this.setBucketName(bucketName);
    this.setKey(key);
    this.tagging = tagging;
  }

  public SetObjectTaggingRequest(String bucketName, String key, ObjectTagging tagging, String versionId) {
    this.setBucketName(bucketName);
    this.setKey(key);
    this.tagging = tagging;
    setVersionId(versionId);
  }

  /**
   * @return The set of tags to set for the specified object.
   */
  public ObjectTagging getTagging() {
    return tagging;
  }

  /**
   * Set the object tagging.
   *
   * @param tagging The object tagging.
   */
  public void setTagging(ObjectTagging tagging) {
    this.tagging = tagging;
  }

  public String getVersionId() {
    return versionId;
  }

  public void setVersionId(String versionId) {
    this.versionId = versionId;
  }
}