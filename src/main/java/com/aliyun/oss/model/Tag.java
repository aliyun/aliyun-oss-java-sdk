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

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSErrorCode;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a tag on a resource.
 */
public class Tag implements Serializable {
  private String key;
  private String value;

  private final String KEY_REGEX = "^(?=^.{1,128}$)[a-zA-Z0-9+=\\-._: /]{1,128}";
  private final String VALUE_REGEX = "^(?=^.{0,256}$)[a-zA-Z0-9+=\\-._: /]{0,256}";

  /**
   * Constructs an instance of this object.
   *
   * @param key
   *            The tag key.
   * @param value
   *            The tag value.
   */
  public Tag(String key, String value) {
    setKey(key);
    setValue(value);
  }

  /**
   * @return The tag key.
   */
  public String getKey() {
    return key;
  }

  /**
   * Set the tag key.
   *
   * @param key
   *            The tag key.
   */
  public void setKey(String key) throws ClientException {
    Pattern pattern = Pattern.compile(KEY_REGEX);
    Matcher matcher = pattern.matcher(key);

    if (matcher.matches()) {
      this.key = key;
    } else {
      throw new ClientException("your key is invalid", OSSErrorCode.INVALID_ARGUMENT, null);
    }
  }

  /**
   * @return The tag value.
   */
  public String getValue() {
    return value;
  }

  /**
   * Set the tag value.
   *
   * @param value
   *            The tag value.
   */
  public void setValue(String value) throws ClientException {
    Pattern pattern = Pattern.compile(VALUE_REGEX);
    Matcher matcher = pattern.matcher(value);

    if (matcher.matches()) {
      this.value = value;
    } else {
      throw new ClientException("your value is invalid", OSSErrorCode.INVALID_ARGUMENT, null);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Tag tag = (Tag) o;

    if (key != null ? !key.equals(tag.key) : tag.key != null) return false;
    return value != null ? value.equals(tag.value) : tag.value == null;

  }

  @Override
  public int hashCode() {
    int result = key != null ? key.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }
}
