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
 * Bucket是OSS上的命名空间。
 * <p>
 * Bucket名在整个 OSS 中具有全局唯一性，且不能修改；存储在OSS上的每个Object必须都包含在某个Bucket中。
 * 一个应用，例如图片分享网站，可以对应一个或多个 Bucket。一个用户最多可创建 10 个Bucket，
 * 但每个Bucket 中存放的Object的数量和大小总和没有限制，用户不需要考虑数据的可扩展性。 
 * </p>
 * <p>
 * Bucket 命名规范
 * <ul>
 *  <li>只能包括小写字母，数字和短横线（-）</li>
 *  <li>必须以小写字母或者数字开头</li>
 *  <li>长度必须在 3-63 字节之间</li>
 * </ul>
 * </p>
 */
public class Bucket {

    // Bucket 名
    private String name;

    // Bucket 所有者
    private Owner owner;
    
    // Bucket 所在地
    private String location;

    // 创建时间
    private Date creationDate;

    /**
     * 构造函数。
     */
    public Bucket() { }
    
    /**
     * 构造函数。
     * @param name
     *      Bucket 名。
     */
    public Bucket(String name) {
        this.name = name;
    }

    /**
     * 返回字符串表示。
     */
    @Override
    public String toString() {
        return "OSSBucket [name=" + getName()
                + ", creationDate=" + getCreationDate()
                + ", owner=" + getOwner()
                + ", location="+ getLocation() + "]";
    }

    /**
     * 返回Bucket的拥有者（{@link Owner}）。
     * @return
     *      Bucket的拥有者。如果拥有者未知，则返回null。
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * 设置Bucket的拥有者。（内部使用）
     * @param owner
     *      Bucket的拥有者。
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    /**
     * 返回Bucket的创建时间。
     * @return Bucket的创建时间。如果创建时间未知，则返回null。
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * 设置Bucket的创建时间。（内部使用）
     * @param creationDate
     *          Bucket的创建时间。
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * 返回Bucket名称。
     * @return Bucket名称。
     */
    public String getName() {
        return name;
    }

    /**
     * 设置Bucket名称。（内部使用）
     * @param name
     *          Bucket名称。
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 返回Bucket所在地
     * @return Bucket所在地
     */
    public String getLocation() {
       return location;
    }

    /**
     * 设置Bucket所在地
     * @param location
     */
    public void setLocation(String location) {
       this.location = location;
    }
}
