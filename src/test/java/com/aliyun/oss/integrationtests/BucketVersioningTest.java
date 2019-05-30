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

package com.aliyun.oss.integrationtests;

import junit.framework.Assert;

import org.junit.Test;
import com.aliyun.oss.model.BucketVersioningConfiguration;
import com.aliyun.oss.model.SetBucketVersioningRequest;

public class BucketVersioningTest extends TestBase {

    private static String bucketName = "mingdi-test";

    @Test
    public void testSetBucketVersioning() {

        try {
            // start versioning
            BucketVersioningConfiguration configuration = new BucketVersioningConfiguration();
            configuration.setStatus(BucketVersioningConfiguration.ENABLED);
            SetBucketVersioningRequest request = new SetBucketVersioningRequest(bucketName, configuration);

            ossClient.setBucketVersioning(request);

            BucketVersioningConfiguration versionConfiguration = ossClient.getBucketVersioning(bucketName);
            Assert.assertTrue(versionConfiguration.getStatus().equals(BucketVersioningConfiguration.ENABLED));

            // stop versioning
            configuration.setStatus(BucketVersioningConfiguration.SUSPENDED);
            request = new SetBucketVersioningRequest(bucketName, configuration);

            ossClient.setBucketVersioning(request);

            versionConfiguration = ossClient.getBucketVersioning(bucketName);
            Assert.assertTrue(versionConfiguration.getStatus().equals(BucketVersioningConfiguration.SUSPENDED));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}
