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
import com.aliyun.oss.model.ImageProcessConf;
import com.aliyun.oss.model.PutImageProcessConfRequest;

public class BucketImageProcessConfTest extends TestBase {

    @Test
    public void testBucketImageProcessConf() {
        try {      
            // get default
            ImageProcessConf conf = secondClient.getBucketImageProcessConf(bucketName);
            Assert.assertEquals(conf.getCompliedHost(), "Both");
            Assert.assertFalse(conf.isSourceFileProtect());
            Assert.assertEquals(conf.getSourceFileProtectSuffix(), "");
            Assert.assertEquals(conf.getStyleDelimiters(), "");
            
            // put 1
            conf = new ImageProcessConf("Img", true, "jpg,png", "/,-");
            PutImageProcessConfRequest request = new PutImageProcessConfRequest(bucketName, conf);
            secondClient.putBucketImageProcessConf(request);
            
            // get 1
            conf = secondClient.getBucketImageProcessConf(new GenericRequest(bucketName));
            Assert.assertEquals(conf.getCompliedHost(), "Img");
            Assert.assertTrue(conf.isSourceFileProtect());
            Assert.assertEquals(conf.getSourceFileProtectSuffix(), "jpg,png");
            Assert.assertEquals(conf.getStyleDelimiters(), "-,/");
            
            // put 2
            conf = new ImageProcessConf("Both", false, "gif", "-");
            request = new PutImageProcessConfRequest(bucketName, conf);
            secondClient.putBucketImageProcessConf(request);
            
            // get 2
            conf = secondClient.getBucketImageProcessConf(new GenericRequest(bucketName));
            Assert.assertEquals(conf.getCompliedHost(), "Both");
            Assert.assertFalse(conf.isSourceFileProtect());
            Assert.assertEquals(conf.getSourceFileProtectSuffix(), "");
            Assert.assertEquals(conf.getStyleDelimiters(), "-");
            
            // put 3
            conf = new ImageProcessConf("Img", true, "*", "/");
            request = new PutImageProcessConfRequest(bucketName, conf);
            secondClient.putBucketImageProcessConf(request);
            
            // get 3
            conf = secondClient.getBucketImageProcessConf(new GenericRequest(bucketName));
            Assert.assertEquals(conf.getCompliedHost(), "Img");
            Assert.assertTrue(conf.isSourceFileProtect());
            Assert.assertEquals(conf.getSourceFileProtectSuffix(), "*");
            Assert.assertEquals(conf.getStyleDelimiters(), "/");
            
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testBucketImageProcessConfNegative() {
        // bucket not exist
        try {
            secondClient.getBucketImageProcessConf("bucket-not-exist");
            Assert.fail("GetBucketImageProcessConf should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }
        
        try {
            ImageProcessConf conf = new ImageProcessConf("img", false, "*", "/,-");
            PutImageProcessConfRequest request = new PutImageProcessConfRequest("bucket-not-exist", conf);
            secondClient.putBucketImageProcessConf(request);
            Assert.fail("PutBucketImageProcessConf should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }
        
        // parameter invalid
        try {
            ImageProcessConf conf = null;
            PutImageProcessConfRequest request = new PutImageProcessConfRequest(bucketName, conf);
            secondClient.putBucketImageProcessConf(request);
            Assert.fail("PutBucketImageProcessConf should not be successful.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException);
        }
        
        try {
            ImageProcessConf conf = new ImageProcessConf(null, false, "*", "/,-");
            PutImageProcessConfRequest request = new PutImageProcessConfRequest(bucketName, conf);
            secondClient.putBucketImageProcessConf(request);
            Assert.fail("PutBucketImageProcessConf should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }
        
        try {
            ImageProcessConf conf = new ImageProcessConf("xxx", false, "*", "/,-");
            PutImageProcessConfRequest request = new PutImageProcessConfRequest(bucketName, conf);
            secondClient.putBucketImageProcessConf(request);
            Assert.fail("PutBucketImageProcessConf should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }
    }

}
