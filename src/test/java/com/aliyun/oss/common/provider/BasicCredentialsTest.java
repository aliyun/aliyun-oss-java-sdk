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

import com.aliyun.oss.common.auth.BasicCredentials;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import org.junit.jupiter.api.*;
import org.junit.Test;

public class BasicCredentialsTest extends TestBase {

    @Test
    public void testBasicTest() {
        try {
            BasicCredentials credentials = new BasicCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, SECURITY_TOKEN)
                    .withExpiredFactor(0.8).withExpiredDuration(900);
            Assertions.assertEquals(credentials.getAccessKeyId(), ACCESS_KEY_ID);
            Assertions.assertEquals(credentials.getSecretAccessKey(), ACCESS_KEY_SECRET);
            Assertions.assertEquals(credentials.getSecurityToken(), SECURITY_TOKEN);
            Assertions.assertTrue(credentials.useSecurityToken());

            credentials = new BasicCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, null).withExpiredFactor(0.8)
                    .withExpiredDuration(900);
            Assertions.assertEquals(credentials.getAccessKeyId(), ACCESS_KEY_ID);
            Assertions.assertEquals(credentials.getSecretAccessKey(), ACCESS_KEY_SECRET);
            Assertions.assertNull(credentials.getSecurityToken());
            Assertions.assertFalse(credentials.useSecurityToken());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testWillSoonExpire() {
        try {
            BasicCredentials credentials = new BasicCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, SECURITY_TOKEN)
                    .withExpiredFactor(1.0).withExpiredDuration(1);
            Thread.sleep(2000);
            Assertions.assertTrue(credentials.willSoonExpire());

            credentials = new BasicCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, SECURITY_TOKEN).withExpiredFactor(1.0)
                    .withExpiredDuration(100);
            Thread.sleep(2000);
            Assertions.assertFalse(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testExpiredFactor() {
        try {
            BasicCredentials credentials = new BasicCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, SECURITY_TOKEN)
                    .withExpiredFactor(1.0).withExpiredDuration(3);
            Thread.sleep(2000);
            Assertions.assertFalse(credentials.willSoonExpire());

            credentials = new BasicCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, SECURITY_TOKEN).withExpiredFactor(0.1)
                    .withExpiredDuration(3);
            Thread.sleep(1000);
            Assertions.assertTrue(credentials.willSoonExpire());

            credentials = new BasicCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, SECURITY_TOKEN).withExpiredFactor(1.0)
                    .withExpiredDuration(1);
            Thread.sleep(1500);
            Assertions.assertTrue(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testDefaultCredentialProvider() {
        DefaultCredentialProvider provider;
        try {
            provider = new DefaultCredentialProvider(null, "");
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            provider = new DefaultCredentialProvider("", "");
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            provider = new DefaultCredentialProvider(ACCESS_KEY_ID, null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            provider = new DefaultCredentialProvider(ACCESS_KEY_ID, "");
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            provider = new DefaultCredentialProvider(ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            Assertions.assertTrue(true);
            provider.setCredentials(null);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    private static final String ACCESS_KEY_ID = "AccessKeyId";
    private static final String ACCESS_KEY_SECRET = "AccessKeySecret";
    private static final String SECURITY_TOKEN = "SecurityToken";

}
