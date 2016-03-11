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

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.SetBucketStorageCapacityRequest;
import com.aliyun.oss.model.UserQos;

public class BucketUserQosTest extends TestBase {

    @Test
    public void testDefaultBucketStorageCapacity() {
        try {
            UserQos userQos = new UserQos();
            
            userQos = secondClient.getBucketStorageCapacity(bucketName);
            Assert.assertEquals(userQos.getStorageCapacity(), -1);

            userQos = defaultClient.getBucketStorageCapacity(new GenericRequest(bucketName));
            Assert.assertEquals(userQos.getStorageCapacity(), -1);
            
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testSetBucketStorageCapacity() {
        try {
            UserQos userQos = new UserQos(-1);
            secondClient.setBucketStorageCapacity(bucketName, userQos);
            
            userQos = secondClient.getBucketStorageCapacity(bucketName);
            Assert.assertEquals(userQos.getStorageCapacity(), -1);
            
            userQos.setStorageCapacity(10000);
            secondClient.setBucketStorageCapacity(new SetBucketStorageCapacityRequest(bucketName).withUserQos(userQos));
            
            userQos = secondClient.getBucketStorageCapacity(bucketName);
            Assert.assertEquals(userQos.getStorageCapacity(), 10000);
            
            userQos.setStorageCapacity(-1);
            defaultClient.setBucketStorageCapacity(new SetBucketStorageCapacityRequest(bucketName).withUserQos(userQos));
     
            userQos = defaultClient.getBucketStorageCapacity(bucketName);
            Assert.assertEquals(userQos.getStorageCapacity(), -1);
            
            userQos.setStorageCapacity(10000);
            defaultClient.setBucketStorageCapacity(bucketName, userQos);
            
            userQos = defaultClient.getBucketStorageCapacity(new GenericRequest(bucketName));
            Assert.assertEquals(userQos.getStorageCapacity(), 10000);

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testSetBucketStorageCapacityNegative() {
        
        try {
            UserQos userQos = new UserQos(-2);
            secondClient.setBucketStorageCapacity(bucketName, userQos);
            Assert.fail("Set bucket storage capacity should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }
        
        try {
            UserQos userQos = new UserQos(-3);
            secondClient.setBucketStorageCapacity(new SetBucketStorageCapacityRequest(bucketName).withUserQos(userQos));
            Assert.fail("Set bucket storage capacity should not be successful");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }
        
    }
    
}
