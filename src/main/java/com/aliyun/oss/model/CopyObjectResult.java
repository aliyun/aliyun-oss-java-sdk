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
 * 拷贝一个在OSS上已经存在的Object成另外一个Object的请求结果。
 */
public class CopyObjectResult extends GenericResult {

    // 新Object的ETag值。
    private String etag;

    // 新Object的最后修改时间。
    private Date lastModified;

    /**
     * 初始化一个新的{@link CopyObjectResult}实例。
     */
    public CopyObjectResult() {}

    /**
     * 返回新Object的ETag值。
     * @return 新Object的ETag值。
     */
    public String getETag() {
        return etag;
    }

    /**
     * 设置新Object的ETag值。
     * @param etag
     *          新Object的ETag值。
     */
    public void setEtag(String etag) {
        this.etag = etag;
    }

    /**
     * 返回新Object的最后修改时间。
     * @return 新Object的最后修改时间。
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * 设置新Object的最后修改时间。
     * @param lastModified
     *          新Object的最后修改时间。
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

}
