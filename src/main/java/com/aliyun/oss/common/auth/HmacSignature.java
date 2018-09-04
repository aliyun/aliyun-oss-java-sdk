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

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.aliyun.oss.common.utils.BinaryUtil;

/**
 * Used for computing Hmac signature.
 */
public class HmacSignature extends ServiceSignature {

    /* Hmac SHA1 Signature method */
    public static final String SHA1 = "HmacSHA1";

    /* Hmac SHA256 Signature method */
    public static final String SHA256 = "HmacSHA256";

    /* The default encoding. */
    private String encoding = "UTF-8";

    /* Signature method. */
    private String algorithm = SHA1;

    /* Signature version. */
    private static final String VERSION = "1";

    private static final Object LOCK = new Object();

    /* Prototype of the Mac instance. */
    private static Mac macInstance;

    public HmacSignature() {}

    public HmacSignature(String algorithm) {
        this.algorithm = algorithm;
    }

    public HmacSignature(String algorithm, String encoding) {
        this.algorithm = algorithm;
        this.encoding = encoding;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getVersion() {
        return VERSION;
    }

    public String computeSignature(String key, String data) {
        try {
            byte[] signData = sign(key.getBytes(encoding), data.getBytes(encoding));
            return BinaryUtil.toBase64String(signData);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Unsupported algorithm: " + encoding, ex);
        }
    }

    private byte[] sign(byte[] key, byte[] data) {
        try {
            // Because Mac.getInstance(String) calls a synchronized method, it
            // could block on
            // invoked concurrently, so use prototype pattern to improve perf.
            if (macInstance == null) {
                synchronized (LOCK) {
                    if (macInstance == null) {
                        macInstance = Mac.getInstance(algorithm);
                    }
                }
            }

            Mac mac = null;
            try {
                mac = (Mac) macInstance.clone();
            } catch (CloneNotSupportedException e) {
                // If it is not clonable, create a new one.
                mac = Mac.getInstance(algorithm);
            }
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Unsupported algorithm: " + algorithm, ex);
        } catch (InvalidKeyException ex) {
            throw new RuntimeException("Invalid key: " + key, ex);
        }
    }
}