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

import com.aliyun.oss.common.utils.AuthUtils;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class RamUtilsTest extends TestBase {

    @Test
    public void testLoadPrivateKeyFromFile() {
        try {
            String privateKey = AuthUtils.loadPrivateKeyFromFile(TestConfig.PRIVATE_KEY_PATH);
            Assert.assertNotNull(privateKey);
            Assert.assertFalse(privateKey.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testLoadPrivateKeyFromFileNegative() {
        try {
            AuthUtils.loadPrivateKeyFromFile("/not/exist/path");
            Assert.fail("RamUtils.loadPrivateKeyFromFile should not be successful.");
        } catch (IOException e) {
            Assert.assertTrue(e instanceof FileNotFoundException);
        }
    }

    @Test
    public void testLoadPublicKeyFromFile() {
        try {
            String publicKey = AuthUtils.loadPublicKeyFromFile(TestConfig.PUBLIC_KEY_PATH);
            Assert.assertNotNull(publicKey);
            Assert.assertFalse(publicKey.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testLoadPublicKeyFromFileNegative() {
        try {
            AuthUtils.loadPublicKeyFromFile("/not/exist/path");
            Assert.fail("RamUtils.loadPublicKeyFromFile should not be successful.");
        } catch (IOException e) {
            Assert.assertTrue(e instanceof FileNotFoundException);
        }
    }


}
