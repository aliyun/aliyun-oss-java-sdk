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
 * Instance Flavor
 * 
 * Udf Applacation的运行环境，详见请参看ECS。
 * 
 */
public class InstanceFlavor {
    
    public static final String DEFAULT_INSTANCE_TYPE = "ecs.n1.small";
    public static final String DEFAULT_IO_OPTIMIZED = "optimized";
    
    public InstanceFlavor(String instanceType, String ioOptimized) {
        this.instanceType = instanceType;
        this.ioOptimized = ioOptimized;
    }
    
    public String getInstanceType() {
        return instanceType;
    }

    public String getIoOptimized() {
        return ioOptimized;
    }

    public void setIoOptimized(String ioOptimized) {
        this.ioOptimized = ioOptimized;
    }
    
    @Override
    public String toString() {
        return "InstanceFlavor [instanceType=" + instanceType + ", ioOptimized=" + ioOptimized + "]";
    }

    private String instanceType;
    private String ioOptimized;
    
}