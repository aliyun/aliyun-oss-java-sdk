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
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;

import com.aliyun.oss.common.utils.BinaryUtil;

/**
 * Used for computing Hmac-SHA256 signature.
 */
public class HmacSHA256Signature extends ServiceSignature {

    /* The default encoding. */
    private static final String DEFAULT_ENCODING = "UTF-8";

    /* Signature method. */
    private static final String ALGORITHM = "HmacSHA256";

    /* Signature version. */
    private static final String VERSION = "1";

    private static final Object LOCK = new Object();

    private final ThreadLocal<Mac> reference;

    public HmacSHA256Signature() {
        this.reference = new HmacSHA256Signature.MacThreadLocal();
    }

    public String getAlgorithm() {
        return ALGORITHM;
    }

    public String getVersion() {
        return VERSION;
    }

    public String computeSignature(String key, String data) {
        try {
            byte[] signData = sign(key.getBytes(DEFAULT_ENCODING), data.getBytes(DEFAULT_ENCODING), getMac(),
                    LOCK, ALGORITHM);
            return BinaryUtil.toBase64String(signData);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Unsupported algorithm: " + DEFAULT_ENCODING, ex);
        }
    }

    public byte[] computeHash(byte[] key, byte[] data) {
        return sign(key, data, getMac(), LOCK, ALGORITHM);
    }

    public Mac getMac() {
        return reference.get();
    }

    private static class MacThreadLocal extends ThreadLocal<Mac> {

        @Override
        protected Mac initialValue() {
            try {
                return Mac.getInstance(HmacSHA256Signature.ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Unsupported algorithm: " + HmacSHA256Signature.ALGORITHM, e);
            }
        }
    }

    static {
        try {
            macInstance = Mac.getInstance(ALGORITHM);
        } catch (Throwable e) {
        }
    }
}