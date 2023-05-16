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

import com.aliyun.oss.model.*;
import junit.framework.Assert;

import org.junit.Test;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.oss.integrationtests.TestUtils.waitForCacheExpiration;

public class BucketProcesTest extends TestBase {

    @Test
    public void testBucketImageProcessConf() {
        try {      
            // get default
            BucketProcess bucketProcess = ossClient.getBucketProcess(bucketName);
            Assert.assertEquals(bucketProcess.getImageProcess().getCompliedHost(), "Both");
            Assert.assertFalse(bucketProcess.getImageProcess().isSourceFileProtect());
            Assert.assertEquals(bucketProcess.getImageProcess().getSourceFileProtectSuffix(), "");
            Assert.assertEquals(bucketProcess.getImageProcess().getStyleDelimiters(), "");
            Assert.assertEquals(bucketProcess.getImageProcess().getVersion().intValue(), 2);
            Assert.assertEquals(bucketProcess.getImageProcess().isSupportAtStyle(), null);
            Assert.assertEquals(bucketProcess.getRequestId().length(), REQUEST_ID_LEN);
            
            // put 1
            ImageProcess imageProcess = new ImageProcess("Img", true, "jpg,png", "/,-");
            SetBucketProcessRequest request = new SetBucketProcessRequest(bucketName, imageProcess);
            request.setImageProcess(imageProcess);
            ossClient.setBucketProcess(request);

            waitForCacheExpiration(2);

            // get 1
            bucketProcess = ossClient.getBucketProcess(new GenericRequest(bucketName));
            Assert.assertEquals(bucketProcess.getImageProcess().getCompliedHost(), "Img");
            Assert.assertTrue(bucketProcess.getImageProcess().isSourceFileProtect());
            Assert.assertEquals(bucketProcess.getImageProcess().getSourceFileProtectSuffix(), "jpg,png");
            Assert.assertEquals(bucketProcess.getImageProcess().getStyleDelimiters(), "-,/");
            Assert.assertEquals(bucketProcess.getImageProcess().getVersion().intValue(), 2);
            Assert.assertEquals(bucketProcess.getImageProcess().isSupportAtStyle(), null);
            Assert.assertEquals(bucketProcess.getRequestId().length(), REQUEST_ID_LEN);
            
            // put 2
            imageProcess = new ImageProcess("Both", false, "gif", "-");
            request = new SetBucketProcessRequest(bucketName, imageProcess);
            ossClient.setBucketProcess(request);

            waitForCacheExpiration(2);

            // get 2
            bucketProcess = ossClient.getBucketProcess(new GenericRequest(bucketName));
            Assert.assertEquals(bucketProcess.getImageProcess().getCompliedHost(), "Both");
            Assert.assertFalse(bucketProcess.getImageProcess().isSourceFileProtect());
            Assert.assertEquals(bucketProcess.getImageProcess().getSourceFileProtectSuffix(), "");
            Assert.assertEquals(bucketProcess.getImageProcess().getStyleDelimiters(), "-");
            Assert.assertEquals(bucketProcess.getImageProcess().getVersion().intValue(), 2);
            Assert.assertEquals(bucketProcess.getImageProcess().isSupportAtStyle(), null);
            Assert.assertEquals(bucketProcess.getRequestId().length(), REQUEST_ID_LEN);
            
            // put 3
            imageProcess = new ImageProcess("Img", true, "*", "/", true);
            request = new SetBucketProcessRequest(bucketName, imageProcess);
            ossClient.setBucketProcess(request);

            waitForCacheExpiration(2);

            // get 3
            bucketProcess = ossClient.getBucketProcess(new GenericRequest(bucketName));
            Assert.assertEquals(bucketProcess.getImageProcess().getCompliedHost(), "Img");
            Assert.assertTrue(bucketProcess.getImageProcess().isSourceFileProtect());
            Assert.assertEquals(bucketProcess.getImageProcess().getSourceFileProtectSuffix(), "*");
            Assert.assertEquals(bucketProcess.getImageProcess().getStyleDelimiters(), "/");
            Assert.assertEquals(bucketProcess.getImageProcess().getVersion().intValue(), 2);
            Assert.assertEquals(bucketProcess.getImageProcess().isSupportAtStyle(), null);
            Assert.assertEquals(bucketProcess.getRequestId().length(), REQUEST_ID_LEN);
            
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testBucketImageProcessConfNegative() {
        // bucket not exist
        try {
            ossClient.getBucketProcess("bucket-not-exist");
            Assert.fail("GetBucketImageProcessConf should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }
        
        try {
            ImageProcess conf = new ImageProcess("img", false, "*", "/,-");
            SetBucketProcessRequest request = new SetBucketProcessRequest("bucket-not-exist", conf);
            ossClient.setBucketProcess(request);
            Assert.fail("PutBucketImageProcessConf should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.NO_SUCH_BUCKET, e.getErrorCode());
        }
        
        // parameter invalid
        try {
            ImageProcess conf = null;
            SetBucketProcessRequest request = new SetBucketProcessRequest(bucketName, conf);
            ossClient.setBucketProcess(request);
            Assert.fail("PutBucketImageProcessConf should not be successful.");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException);
        }
        
        try {
            ImageProcess conf = new ImageProcess(null, false, "*", "/,-");
            SetBucketProcessRequest request = new SetBucketProcessRequest(bucketName, conf);
            ossClient.setBucketProcess(request);
            Assert.fail("PutBucketImageProcessConf should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }
        
        try {
            ImageProcess conf = new ImageProcess("xxx", false, "*", "/,-");
            SetBucketProcessRequest request = new SetBucketProcessRequest(bucketName, conf);
            ossClient.setBucketProcess(request);
            Assert.fail("PutBucketImageProcessConf should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }
    }

    @Test
    public void setImageProcessClassSetter() {
        ImageProcess imageProcess = new ImageProcess(null, false, null, "/,-");
        imageProcess.setCompliedHost("123");
        imageProcess.setSourceFileProtectSuffix("*");
        imageProcess.setStyleDelimiters("_");

        Assert.assertEquals("123", imageProcess.getCompliedHost());
        Assert.assertEquals("*", imageProcess.getSourceFileProtectSuffix());
        Assert.assertEquals("_", imageProcess.getStyleDelimiters());
    }

    @Test
    public void setImageProcessWithBucketChannel() {
        ImageProcess imageProcess = new ImageProcess("img", false, "jpg,png", "/");
        BucketChannelConfig bucketChannelConfig = new BucketChannelConfig();
        bucketChannelConfig.setRuleName("rule-test1");
        bucketChannelConfig.setRuleRegex("L1swLTlhLWZBLUZdezZ9fFswLTlhLWZBLUZdezN9L2c");
        bucketChannelConfig.setCreateTime("Wed, 02 Oct 2019 14:30:18 GMT");
        bucketChannelConfig.setFrontContent("sagweg24524362");
        bucketChannelConfig.setLastModifiedTime("Wed, 02 Oct 2023 14:30:18 GMT");

        BucketChannelConfig bucketChannelConfig2 = new BucketChannelConfig();
        bucketChannelConfig2.setRuleName("rule-test2");
        bucketChannelConfig2.setRuleRegex("LyNbMC05YS1mQS1GXXs2fXxbMC05YS1mQS1GXXszfS9n");
        bucketChannelConfig2.setCreateTime("Wed, 03 Oct 2018 14:30:18 GMT");
        bucketChannelConfig2.setFrontContent("@$#%^%^*)*)+/");
        bucketChannelConfig2.setLastModifiedTime("Wed, 02 Oct 2023 16:32:18 GMT");

        List<BucketChannelConfig> bucketChannelConfigs = new ArrayList<BucketChannelConfig>();
        bucketChannelConfigs.add(bucketChannelConfig);
        bucketChannelConfigs.add(bucketChannelConfig2);
        imageProcess.setBucketChannelConfig(bucketChannelConfigs);
        SetBucketProcessRequest request = new SetBucketProcessRequest(bucketName, imageProcess);

        VoidResult result = ossClient.setBucketProcess(request);
        Assert.assertEquals(200, result.getResponse().getStatusCode());

        BucketProcess getResult = ossClient.getBucketProcess(bucketName);
        Assert.assertEquals("Img", getResult.getImageProcess().getCompliedHost());
//        Assert.assertEquals("jpg,png", getResult.getImageProcess().getSourceFileProtectSuffix());
        Assert.assertEquals(Boolean.FALSE, getResult.getImageProcess().isSourceFileProtect());
        Assert.assertEquals("/", getResult.getImageProcess().getStyleDelimiters());
        Assert.assertEquals("rule-test1", getResult.getImageProcess().getBucketChannelConfig().get(0).getRuleName());
        Assert.assertEquals("L1swLTlhLWZBLUZdezZ9fFswLTlhLWZBLUZdezN9L2c", getResult.getImageProcess().getBucketChannelConfig().get(0).getRuleRegex());
        Assert.assertEquals("sagweg24524362", getResult.getImageProcess().getBucketChannelConfig().get(0).getFrontContent());
        Assert.assertEquals("Wed, 02 Oct 2019 14:30:18 GMT", getResult.getImageProcess().getBucketChannelConfig().get(0).getCreateTime());
        Assert.assertEquals("Wed, 02 Oct 2023 14:30:18 GMT", getResult.getImageProcess().getBucketChannelConfig().get(0).getLastModifiedTime());
        Assert.assertEquals("rule-test2", getResult.getImageProcess().getBucketChannelConfig().get(1).getRuleName());
        Assert.assertEquals("LyNbMC05YS1mQS1GXXs2fXxbMC05YS1mQS1GXXszfS9n", getResult.getImageProcess().getBucketChannelConfig().get(1).getRuleRegex());
        Assert.assertEquals("@$#%^%^*)*)+/", getResult.getImageProcess().getBucketChannelConfig().get(1).getFrontContent());
        Assert.assertEquals("Wed, 03 Oct 2018 14:30:18 GMT", getResult.getImageProcess().getBucketChannelConfig().get(1).getCreateTime());
        Assert.assertEquals("Wed, 02 Oct 2023 16:32:18 GMT", getResult.getImageProcess().getBucketChannelConfig().get(1).getLastModifiedTime());
    }

}
