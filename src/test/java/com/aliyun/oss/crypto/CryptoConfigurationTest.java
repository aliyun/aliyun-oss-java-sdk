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
import java.security.SecureRandom;

import org.junit.Test;
import org.junit.jupiter.api.*;

public class CryptoConfigurationTest {
    @Test
    public void testConstruction() {
        CryptoConfiguration cryptoConfig = new CryptoConfiguration();
        Assertions.assertEquals(ContentCryptoMode.AES_CTR_MODE, cryptoConfig.getContentCryptoMode());
        Assertions.assertEquals(CryptoStorageMethod.ObjectMetadata, cryptoConfig.getStorageMethod());
        Assertions.assertEquals(SecureRandom.class.getName(), cryptoConfig.getSecureRandom().getClass().getName());
        Assertions.assertNull(cryptoConfig.getContentCryptoProvider());

        cryptoConfig = new CryptoConfiguration(
                ContentCryptoMode.AES_CTR_MODE,
                CryptoStorageMethod.ObjectMetadata,
                new SecureRandom(),
                getBouncyCastleProvider());

        Assertions.assertEquals(ContentCryptoMode.AES_CTR_MODE, cryptoConfig.getContentCryptoMode());
        Assertions.assertEquals(CryptoStorageMethod.ObjectMetadata, cryptoConfig.getStorageMethod());
        Assertions.assertEquals(SecureRandom.class.getName(), cryptoConfig.getSecureRandom().getClass().getName());
        Assertions.assertEquals("BC", cryptoConfig.getContentCryptoProvider().getName());
    }

    @Test
    public void testRandom() {
        CryptoConfiguration cryptoConfig = new CryptoConfiguration();
        Assertions.assertNotNull(cryptoConfig.getSecureRandom());

        cryptoConfig = new CryptoConfiguration().withSecureRandom(null);
        Assertions.assertNull(cryptoConfig.getSecureRandom());

        cryptoConfig = new CryptoConfiguration();
        cryptoConfig.setSecureRandom(null);
        Assertions.assertNull(cryptoConfig.getSecureRandom());
    }

    @Test
    public void testProvider() {
        CryptoConfiguration cryptoConfig = new CryptoConfiguration();
        Assertions.assertNull(cryptoConfig.getContentCryptoProvider());

        cryptoConfig = new CryptoConfiguration().withContentCryptoProvider(getBouncyCastleProvider());
        Assertions.assertEquals("BC", cryptoConfig.getContentCryptoProvider().getName());

        cryptoConfig = new CryptoConfiguration();
        cryptoConfig.setContentCryptoProvider(getBouncyCastleProvider());
        Assertions.assertEquals("BC", cryptoConfig.getContentCryptoProvider().getName());
    }

    public static Provider getBouncyCastleProvider()
    {
        try {
            Class<?> clz = Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            return (Provider)clz.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
