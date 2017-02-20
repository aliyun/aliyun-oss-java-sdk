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

import java.io.InputStream;

/**
 * 上传object操作的返回结果。
 */
public class PutObjectResult extends GenericResult implements CallbackResult {

    // Object的ETag值。
    private String eTag;
    
    // 回调返回的消息体，需要用户close
    private InputStream callbackResponseBody;

    /**
     * 返回新创建的{@link OSSObject}的ETag值。
     * @return 新创建的{@link OSSObject}的ETag值。
     */
    public String getETag() {
        return eTag;
    }

    /**
     * 设置新创建的{@link OSSObject}的ETag值。
     * @param eTag
     *          新创建的{@link OSSObject}的ETag值。
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }
    
    /**
     * 获取回调返回的消息体，需要close，使用this.getResponse().getContent()代替。
     * @return 回调返回的消息体
     */
    @Override
    @Deprecated
    public InputStream getCallbackResponseBody() {
        return callbackResponseBody;
    }
    
    /**
     * 设置回调返回的消息体。
     * @param callbackResponseBody 回调返回的消息体。
     */
    @Override
    public void setCallbackResponseBody(InputStream callbackResponseBody) {
        this.callbackResponseBody = callbackResponseBody;
    }
    
}
