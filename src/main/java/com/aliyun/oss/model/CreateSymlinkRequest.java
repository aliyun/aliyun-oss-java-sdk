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
 * 创建符号链接请求
 * 
 */
public class CreateSymlinkRequest extends GenericRequest {
    
    public CreateSymlinkRequest(String bucketName, String symlink, 
            String target) {
        super(bucketName, symlink);
        this.target = target;
    }
    
    public String getSymlink() {
        return this.getKey();
    }

    public void setSymlink(String symlink) {
        this.setKey(symlink);
    }

    /**
     * 设置链接文件的目标文件
     * @return 链接文件的目标文件
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * 设置链接文件的目标文件
     * @param target 目标文件
     */
    public void setTarget(String target) {
        this.target = target;
    }
    
    /**
     * 返回链接文件的元信息
     * @return 链接文件的元信息
     */
    public ObjectMetadata getMetadata() {
        return metadata;
    }

    /**
     * 设置链接文件的元信息
     * @param metadata 链接文件的元信息
     */
    public void setMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
    }

    // 目标文件
    private String target;
    
    // 链接文件的元信息
    private ObjectMetadata metadata;
}
