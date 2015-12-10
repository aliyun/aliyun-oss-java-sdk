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
 * 包含通过Multipart上传模式上传的Part的摘要信息。
 *
 */
public class PartSummary {

    private int partNumber;

    private Date lastModified;

    private String eTag;

    private long size;
    
    /**
     * 构造函数。
     */
    public PartSummary(){
    }

    /**
     * 返回Part的标识号码。
     * @return Part的标识号码。
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * 设置Part的标识号码。
     * @param partNumber
     *          Part的标识号码。
     */
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * 返回Part的最后修改时间。
     * @return Part的最后修改时间。
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * 设置Part的最后修改时间。
     * @param lastModified
     *          Part的最后修改时间。
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * 返回Part的ETag值。
     * @return Part的ETag值。
     */
    public String getETag() {
        return eTag;
    }

    /**
     * 设置Part的ETag值。
     * @param eTag
     *          Part的ETag值。
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    /**
     * 返回Part数据的字节数。
     * @return Part数据的字节数。
     */
    public long getSize() {
        return size;
    }

    /**
     * 设置Part数据的字节数。
     * @param size
     *          Part数据的字节数。
     */
    public void setSize(long size) {
        this.size = size;
    }

}
