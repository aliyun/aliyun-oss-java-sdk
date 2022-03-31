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
import com.aliyun.oss.common.auth.InstanceProfileCredentialsFetcher;
import com.aliyun.oss.common.provider.mock.InstanceProfileCredentialsFetcherMock;
import com.aliyun.oss.common.provider.mock.InstanceProfileCredentialsFetcherMock.ResponseCategory;
import com.aliyuncs.exceptions.ClientException;
import org.junit.jupiter.api.*;
import org.junit.Ignore;
import org.junit.Test;

public class InstanceProfileCredentialsFetcherTest extends TestBase {

    @Test
    public void testFetchNormalCredentials() {
        try {
            InstanceProfileCredentialsFetcherMock credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.Normal);

            InstanceProfileCredentials credentials = (InstanceProfileCredentials) credentialsFetcher.fetch(3);
            Assertions.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assertions.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assertions.assertEquals(credentials.getSecurityToken().length(), 536);
            Assertions.assertTrue(credentials.useSecurityToken());
            Assertions.assertFalse(credentials.willSoonExpire());

            credentials = (InstanceProfileCredentials) credentialsFetcher.fetch();
            Assertions.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assertions.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assertions.assertEquals(credentials.getSecurityToken().length(), 536);
            Assertions.assertTrue(credentials.useSecurityToken());
            Assertions.assertFalse(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testFetchExpiredCredentials() {
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.Expired);

            InstanceProfileCredentials credentials = (InstanceProfileCredentials) credentialsFetcher.fetch(3);
            Assertions.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assertions.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assertions.assertEquals(credentials.getSecurityToken().length(), 536);
            Assertions.assertTrue(credentials.useSecurityToken());
            Assertions.assertTrue(credentials.willSoonExpire());

            credentials = (InstanceProfileCredentials) credentialsFetcher.fetch();
            Assertions.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assertions.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assertions.assertEquals(credentials.getSecurityToken().length(), 536);
            Assertions.assertTrue(credentials.useSecurityToken());
            Assertions.assertTrue(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testFetchInvalidCredentials() {
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.FormatInvalid);
            credentialsFetcher.fetch(3);
            Assertions.fail("EcsInstanceCredentialsFetcher.fetch should not be successful.");
        } catch (ClientException e) {
            Assertions.assertEquals(FORMAT_ERROR_MESSAGE, e.getMessage());
        }

        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.FormatInvalid);
            credentialsFetcher.fetch();
            Assertions.fail("EcsInstanceCredentialsFetcher.fetch should not be successful.");
        } catch (ClientException e) {
            Assertions.assertEquals(FORMAT_ERROR_MESSAGE, e.getMessage());
        }
        
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.ServerHalt);
            credentialsFetcher.fetch(3);
            Assertions.fail("EcsInstanceCredentialsFetcher.fetch should not be successful.");
        } catch (ClientException e) {
        }
    }

    @Test
    public void testFetchExceptionalCredentials() {
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.Exceptional);
            credentialsFetcher.fetch(3);
            Assertions.fail("EcsInstanceCredentialsFetcher.fetch should not be successful.");
        } catch (ClientException e) {
        }

        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcherMock()
                    .withResponseCategory(ResponseCategory.Exceptional);
            credentialsFetcher.fetch();
            Assertions.fail("EcsInstanceCredentialsFetcher.fetch should not be successful.");
        } catch (ClientException e) {
        }
    }

    @Test
    public void testSetRoleNameNegative() {
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcher();
            credentialsFetcher.setRoleName(null);
            Assertions.fail("EcsInstanceCredentialsFetcher.setRoleName should not be successful.");
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcher();
            credentialsFetcher.setRoleName("");
            Assertions.fail("EcsInstanceCredentialsFetcher.setRoleName should not be successful.");
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            new InstanceProfileCredentialsFetcher().withRoleName(null);
            Assertions.fail("EcsInstanceCredentialsFetcher.setRoleName should not be successful.");
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof IllegalArgumentException);
        }

        try {
            new InstanceProfileCredentialsFetcher().withRoleName("");
            Assertions.fail("EcsInstanceCredentialsFetcher.setRoleName should not be successful.");
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testGetMetadataNegative() {
        try {
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcher()
                    .withRoleName("NotExistRoleName");
            credentialsFetcher.fetch();
            Assertions.fail("EcsInstanceCredentialsFetcher.getMetadata should not be successful.");
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof ClientException);
        }
    }

    /**
     * NOTE: Run this case on ecs.
     */
    @Ignore
    public void testFetchCredentialsOnEcs() {
        try {
            // TODO: Establish a simulated ECS metadata service
            InstanceProfileCredentialsFetcher credentialsFetcher = new InstanceProfileCredentialsFetcher()
                    .withRoleName(TestConfig.ECS_ROLE_NAME);

            InstanceProfileCredentials credentials = (InstanceProfileCredentials) credentialsFetcher.fetch(3);
            Assertions.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assertions.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assertions.assertEquals(credentials.getSecurityToken().length(), 536);
            Assertions.assertTrue(credentials.useSecurityToken());
            Assertions.assertFalse(credentials.willSoonExpire());

            credentials = (InstanceProfileCredentials) credentialsFetcher.fetch();
            Assertions.assertEquals(credentials.getAccessKeyId().length(), 29);
            Assertions.assertEquals(credentials.getSecretAccessKey().length(), 44);
            Assertions.assertEquals(credentials.getSecurityToken().length(), 536);
            Assertions.assertTrue(credentials.useSecurityToken());
            Assertions.assertFalse(credentials.willSoonExpire());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail(e.getMessage());
        }
    }

    private static final String FORMAT_ERROR_MESSAGE = "Invalid json got from ECS Metadata service.";
    
}
