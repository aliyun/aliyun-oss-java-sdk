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

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.AccessMonitor;
import com.aliyun.oss.model.AccessMonitorStatus;
import com.aliyun.oss.model.LifecycleRule;
import junit.framework.Assert;
import org.junit.Test;

import java.util.List;

public class BucketAccessMonitorTest extends TestBase {


    @Test
    public void testBucketAccessMonitor() {

        try {
            ossClient.putBucketAccessMonitor(bucketName, AccessMonitorStatus.Enabled);
            AccessMonitor config = ossClient.getBucketAccessMonitor(bucketName);
            Assert.assertEquals("Enabled", config.getStatus());

            ossClient.putBucketAccessMonitor(bucketName, AccessMonitorStatus.Disabled);
            config = ossClient.getBucketAccessMonitor(bucketName);
            Assert.assertEquals("Disabled", config.getStatus());

        } catch (Exception e1) {
            Assert.fail(e1.getMessage());
        }
    }

    @Test
    public void testBucketAccessMonitorException() {

        try {
            ossClient.putBucketAccessMonitor(bucketName, AccessMonitorStatus.Enabled);
            AccessMonitor config = ossClient.getBucketAccessMonitor(bucketName);
            Assert.assertEquals("Enabled", config.getStatus());

            boolean updateFlag = true;
            List<LifecycleRule>  rules = ossClient.getBucketLifecycle(bucketName);
            for(LifecycleRule rule : rules){
                if (rule.getStorageTransition() != null && !rule.getStorageTransition().isEmpty()) {
                    for(LifecycleRule.StorageTransition trans : rule.getStorageTransition()){
                        if("true".equals(trans.getAccessTime())){
                            updateFlag = false;
                        }
                    }
                }
                if (rule.getNoncurrentVersionStorageTransitions() != null && !rule.getNoncurrentVersionStorageTransitions().isEmpty()) {
                    for(LifecycleRule.NoncurrentVersionStorageTransition trans : rule.getNoncurrentVersionStorageTransitions()){
                        if("true".equals(trans.getAccessTime())){
                            updateFlag = false;
                        }
                    }
                }
            }
            if(updateFlag){
                ossClient.putBucketAccessMonitor(bucketName, AccessMonitorStatus.Disabled);
                config = ossClient.getBucketAccessMonitor(bucketName);
                Assert.assertEquals("Disabled", config.getStatus());
            }else{
                try{
                    ossClient.putBucketAccessMonitor(bucketName, AccessMonitorStatus.Disabled);
                } catch (OSSException e){
                    Assert.assertEquals(e.getErrorCode(), OSSErrorCode.OSS_MALFORMED_XML_CODE);
                }
            }
        } catch (Exception e1) {
            Assert.fail(e1.getMessage());
        }
    }
}