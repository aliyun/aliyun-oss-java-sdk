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
import java.util.List;

/**
 * The tagging for an object.
 */
public class ObjectTagging extends GenericResult implements Serializable {
    private static final long serialVersionUID = 211267925081748283L;
    private List<Tag> tagSet;

    /**
     * Constructs an instance of this object.
     *
     * @param tagSet The tag set.
     */
    public ObjectTagging(List<Tag> tagSet) {
        this.tagSet = tagSet;
    }

    /**
     * @return The tag set.
     */
    public List<Tag> getTagSet() {
        return tagSet;
    }

    /**
     * Set the tag set for the object.
     *
     * @param tagSet
     *            The tag set.
     */
    public void setTagSet(List<Tag> tagSet) {
        this.tagSet = tagSet;
    }
}