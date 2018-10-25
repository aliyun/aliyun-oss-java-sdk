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

public enum ObjectTypeList {

    Normal("Normal"),

    Symlink("Symlink"),

    Multipart("Multipart"),

    FileGroup("FileGroup"),

    ObjectLink("ObjectLink"),

    Appendable("Appendable");

    private String typeString;

    private ObjectTypeList(String typeString) {
        this.typeString = typeString;
    }

    @Override
    public String toString() {
        return this.typeString;
    }

    public static ObjectTypeList parse(String type) {
        for (ObjectTypeList cType : ObjectTypeList.values()) {
            if (cType.toString().equals(type)) {
                return cType;
            }
        }

        throw new IllegalArgumentException("Unable to parse the provided type " + type);
    }
}
