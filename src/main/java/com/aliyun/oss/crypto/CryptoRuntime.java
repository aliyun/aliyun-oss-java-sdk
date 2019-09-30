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

package com.aliyun.oss.crypto;

import java.security.Provider;
import java.security.Security;

public class CryptoRuntime {
    static final String BOUNCY_CASTLE_PROVIDER = "BC";
    private static final String BC_PROVIDER_FQCN = "org.bouncycastle.jce.provider.BouncyCastleProvider";

    /**
     * Checks if the crypto mode is supported by the runtime.
     *
     * @throws UnsupportedOperationException if the necessary security provider
     *                                       cannot be found
     */
    public static void enableBouncyCastle() {
        if (!isBouncyCastleAvailable()) {
            addBouncyCastleProvider();
            if (!isBouncyCastleAvailable()) {
                throw new UnsupportedOperationException("The Bouncy castle library is not found.");
            }
        }
    }

    private static synchronized boolean isBouncyCastleAvailable() {
        return Security.getProvider(BOUNCY_CASTLE_PROVIDER) != null;
    }

    private static synchronized void addBouncyCastleProvider() {
        if (isBouncyCastleAvailable()) {
            return;
        }
        try {
            @SuppressWarnings("unchecked")
            Class<Provider> c = (Class<Provider>) Class.forName(BC_PROVIDER_FQCN);
            Provider provider = c.newInstance();
            Security.addProvider(provider);
        } catch (Exception e) {
        }
    }
}
