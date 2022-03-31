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

package com.aliyun.oss.common.provider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.aliyun.oss.common.auth.PublicKey;
import com.aliyun.oss.common.utils.AuthUtils;
import com.aliyuncs.exceptions.ClientException;
import org.junit.jupiter.api.*;
import org.junit.Test;

public class RamUtilsTest extends TestBase {

    @Test
    public void testLoadPrivateKeyFromFile() {
        try {
            String privateKey = AuthUtils.loadPrivateKeyFromFile(TestConfig.PRIVATE_KEY_PATH);
            Assertions.assertNotNull(privateKey);
            Assertions.assertFalse(privateKey.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testLoadPrivateKeyFromFileNegative() {
        try {
            AuthUtils.loadPrivateKeyFromFile("/not/exist/path");
            Assertions.fail("RamUtils.loadPrivateKeyFromFile should not be successful.");
        } catch (IOException e) {
            Assertions.assertTrue(e instanceof FileNotFoundException);
        }
    }

    @Test
    public void testLoadPublicKeyFromFile() {
        try {
            String publicKey = AuthUtils.loadPublicKeyFromFile(TestConfig.PUBLIC_KEY_PATH);
            Assertions.assertNotNull(publicKey);
            Assertions.assertFalse(publicKey.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testLoadPublicKeyFromFileNegative() {
        try {
            AuthUtils.loadPublicKeyFromFile("/not/exist/path");
            Assertions.fail("RamUtils.loadPublicKeyFromFile should not be successful.");
        } catch (IOException e) {
            Assertions.assertTrue(e instanceof FileNotFoundException);
        }
    }

    @Test
    public void testUploadPublicKey() {
        try {
            // upload
            String pubKey = AuthUtils.loadPublicKeyFromFile(TestConfig.PUBLIC_KEY_PATH);
            PublicKey publicKey = AuthUtils.uploadPublicKey(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET, pubKey);
            Assertions.assertNotNull(publicKey);
            Assertions.assertEquals(publicKey.getPublicKeyId().length(), "LTRSA.2Qcjm****XW7M0NO".length());

            // check
            List<PublicKey> publicKeys = AuthUtils.listPublicKeys(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET);
            Assertions.assertEquals(1, publicKeys.size());

            // delete
            AuthUtils.deletePublicKey(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET, publicKey.getPublicKeyId());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testUploadPublicKeyNegative() {
        try {
            String pubKey = "invalid public key.";
            AuthUtils.uploadPublicKey(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET, pubKey);
            Assertions.fail("RamUtils.uploadPublicKey should not be successful.");
        } catch (ClientException e) {
            Assertions.assertEquals(e.getErrCode(), "InvalidParameter.PublicKey.Format");
        }
    }

    @Test
    public void testListPublicKey() {
        try {
            // upload
            String pubKey = AuthUtils.loadPublicKeyFromFile(TestConfig.PUBLIC_KEY_PATH);
            PublicKey publicKey = AuthUtils.uploadPublicKey(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET, pubKey);

            // check
            List<PublicKey> publicKeys = AuthUtils.listPublicKeys(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET);
            Assertions.assertEquals(1, publicKeys.size());
            Assertions.assertEquals(publicKeys.get(0).getPublicKeyId().length(), "LTRSA.2Qcjm****XW7M0NO".length());
            Assertions.assertEquals(publicKeys.get(0).getCreateDate().length(), "2017-11-01T08:00:00Z".length());
            Assertions.assertEquals(publicKeys.get(0).getStatus(), "Active");
            Assertions.assertEquals(publicKeys.get(0).getPublicKeySpec(), null);

            // delete
            AuthUtils.deletePublicKey(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET, publicKey.getPublicKeyId());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testDeletePublicKey() {
        try {
            // upload
            String pubKey = AuthUtils.loadPublicKeyFromFile(TestConfig.PUBLIC_KEY_PATH);
            PublicKey publicKey = AuthUtils.uploadPublicKey(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET, pubKey);
            Assertions.assertNotNull(publicKey);
            Assertions.assertEquals(publicKey.getPublicKeyId().length(), "LTRSA.2Qcjm****XW7M0NO".length());

            publicKey = AuthUtils.uploadPublicKey(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET, pubKey);
            Assertions.assertNotNull(publicKey);
            Assertions.assertEquals(publicKey.getPublicKeyId().length(), "LTRSA.2Qcjm****XW7M0NO".length());

            // check
            List<PublicKey> publicKeys = AuthUtils.listPublicKeys(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET);
            Assertions.assertEquals(2, publicKeys.size());

            // delete
            for (PublicKey pk : publicKeys) {
                AuthUtils.deletePublicKey(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                        TestConfig.ROOT_ACCESS_KEY_SECRET, pk.getPublicKeyId());
            }

            // check
            publicKeys = AuthUtils.listPublicKeys(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET);
            Assertions.assertEquals(0, publicKeys.size());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testKeletePublicKeyNegative() {
        try {
            AuthUtils.deletePublicKey(TestConfig.RAM_REGION_ID, TestConfig.ROOT_ACCESS_KEY_ID,
                    TestConfig.ROOT_ACCESS_KEY_SECRET, "LTRSA.2Qcjm****XW7M0NO");
            Assertions.fail("RamUtils.deletePublicKey should not be successful.");
        } catch (ClientException e) {
            Assertions.assertEquals(e.getErrCode(), "EntityNotExist.User.PublicKey");
        }
    }

}
