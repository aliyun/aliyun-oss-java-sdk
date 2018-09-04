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

package com.aliyun.oss.common.auth;

/**
 * The interface to compute the signature of the data.
 */
public abstract class ServiceSignature {

    /**
     * Gets the algorithm of signature.
     * 
     * @return The algorithm of the signature.
     */
    public abstract String getAlgorithm();

    /**
     * Gets the algorithm version.
     * 
     * @return The algorithm version.
     */
    public abstract String getVersion();

    /**
     * Computes the signature of the data by the given key.
     * 
     * @param key
     *            The key for the signature.
     * @param data
     *            The data to compute the signature on.
     * @return The signature in string.
     */
    public abstract String computeSignature(String key, String data);

    /**
     *
     * Creates the default <code>ServiceSignature</code> instance which is
     * {@link HmacSignature}.
     * 
     * @return The default <code>ServiceSignature</code> instance
     */
    public static ServiceSignature create() {
        return new HmacSignature();
    }

}