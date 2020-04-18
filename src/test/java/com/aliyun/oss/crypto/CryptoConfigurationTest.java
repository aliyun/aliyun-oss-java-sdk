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

import java.security.SecureRandom;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import junit.framework.Assert;

public class CryptoConfigurationTest {
    @Test
    public void testConstruction() {
        CryptoConfiguration cryptoConfig = new CryptoConfiguration();
        Assert.assertEquals(ContentCryptoMode.AES_CTR_MODE, cryptoConfig.getContentCryptoMode());
        Assert.assertEquals(CryptoStorageMethod.ObjectMetadata, cryptoConfig.getStorageMethod());
        Assert.assertEquals(SecureRandom.class.getName(), cryptoConfig.getSecureRandom().getClass().getName());
        Assert.assertNull(cryptoConfig.getContentCryptoProvider());

        cryptoConfig = new CryptoConfiguration(
                ContentCryptoMode.AES_CTR_MODE,
                CryptoStorageMethod.ObjectMetadata,
                new SecureRandom(),
                new BouncyCastleProvider());

        Assert.assertEquals(ContentCryptoMode.AES_CTR_MODE, cryptoConfig.getContentCryptoMode());
        Assert.assertEquals(CryptoStorageMethod.ObjectMetadata, cryptoConfig.getStorageMethod());
        Assert.assertEquals(SecureRandom.class.getName(), cryptoConfig.getSecureRandom().getClass().getName());
        Assert.assertEquals("BC", cryptoConfig.getContentCryptoProvider().getName());
    }

    @Test
    public void testRandom() {
        CryptoConfiguration cryptoConfig = new CryptoConfiguration();
        Assert.assertNotNull(cryptoConfig.getSecureRandom());

        cryptoConfig = new CryptoConfiguration().withSecureRandom(null);
        Assert.assertNull(cryptoConfig.getSecureRandom());

        cryptoConfig = new CryptoConfiguration();
        cryptoConfig.setSecureRandom(null);
        Assert.assertNull(cryptoConfig.getSecureRandom());
    }

    @Test
    public void testProvider() {
        CryptoConfiguration cryptoConfig = new CryptoConfiguration();
        Assert.assertNull(cryptoConfig.getContentCryptoProvider());

        cryptoConfig = new CryptoConfiguration().withContentCryptoProvider(new BouncyCastleProvider());
        Assert.assertEquals("BC", cryptoConfig.getContentCryptoProvider().getName());

        cryptoConfig = new CryptoConfiguration();
        cryptoConfig.setContentCryptoProvider(new BouncyCastleProvider());
        Assert.assertEquals("BC", cryptoConfig.getContentCryptoProvider().getName());
    }
}
