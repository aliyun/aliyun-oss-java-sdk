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

import com.aliyun.oss.common.auth.InstanceProfileCredentials;
import com.aliyun.oss.common.utils.DateUtil;
import org.junit.jupiter.api.*;
import org.junit.Test;

import java.util.Date;

public class InstanceProfileCredentialsTest extends TestBase {

    @Test
    public void testBasicTest() {
        try {
            InstanceProfileCredentials credentials = new InstanceProfileCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, null,
                    "2017-11-03T05:10:02Z");
            Assertions.assertEquals(credentials.getAccessKeyId(), ACCESS_KEY_ID);
            Assertions.assertEquals(credentials.getSecretAccessKey(), ACCESS_KEY_SECRET);
            Assertions.assertEquals(credentials.getSecurityToken(), null);
            Assertions.assertFalse(credentials.useSecurityToken());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testWillSoonExpire() {
        try {
            InstanceProfileCredentials credentials = new InstanceProfileCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, null,
                    "2016-11-11T11:11:11Z");
            Assertions.assertTrue(credentials.willSoonExpire());
            Assertions.assertTrue(credentials.isExpired());
            Assertions.assertTrue(credentials.shouldRefresh());

            long currTime = new Date().getTime() + 100 * 1000;
            credentials = new InstanceProfileCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, null, DateUtil.formatAlternativeIso8601Date(new Date(currTime)));
            Assertions.assertTrue(credentials.willSoonExpire());
            Assertions.assertFalse(credentials.isExpired());
            Assertions.assertTrue(credentials.shouldRefresh());

            credentials.setLastFailedRefreshTime();
            Assertions.assertFalse(credentials.shouldRefresh());
            Thread.sleep(11000);
            Assertions.assertTrue(credentials.shouldRefresh());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testExpiredFactor() {
        try {
            InstanceProfileCredentials credentials = new InstanceProfileCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, null,
                    "2010-11-11T11:11:11Z").withExpiredFactor(10.0);
            Thread.sleep(1000);
            Assertions.assertTrue(credentials.willSoonExpire());

            long currTime = new Date().getTime() + (3600*6*8/10 + 100)*1000;
            credentials = new InstanceProfileCredentials(ACCESS_KEY_ID, ACCESS_KEY_SECRET, null, DateUtil.formatAlternativeIso8601Date(new Date(currTime)))
                    .withExpiredFactor(0.8);
            Thread.sleep(1000);
            Assertions.assertFalse(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    private static final String ACCESS_KEY_ID = "AccessKeyId";
    private static final String ACCESS_KEY_SECRET = "AccessKeySecret";

}
