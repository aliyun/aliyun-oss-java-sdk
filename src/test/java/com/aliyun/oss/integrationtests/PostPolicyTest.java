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

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;

public class PostPolicyTest extends TestBase {
    
    @Test
    public void testGenPostPolicy() {    
        final String bucketName = "gen-post-policy";
        
        try {            
            Date expiration = DateUtil.parseIso8601Date("2015-03-19T03:44:06.476Z");
            
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem("bucket", bucketName);
            // $ must be escaped with backslash.
            policyConds.addConditionItem(MatchMode.Exact, PolicyConditions.COND_KEY, "user/eric/\\${filename}");
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, "user/eric");
            policyConds.addConditionItem(MatchMode.StartWith, "x-oss-meta-tag", "dummy_etag");
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 1, 1024);

            String actualPostPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            String expectedPostPolicy = String.format("{\"expiration\":\"2015-03-19T03:44:06.476Z\",\"conditions\":[{\"bucket\":\"%s\"},"
                    + "[\"eq\",\"$key\",\"user/eric/\\${filename}\"],[\"starts-with\",\"$key\",\"user/eric\"],[\"starts-with\",\"$x-oss-meta-tag\","
                    + "\"dummy_etag\"],[\"content-length-range\",1,1024]]}", bucketName);
            Assert.assertEquals(expectedPostPolicy, actualPostPolicy);
            
            byte[] binaryData = actualPostPolicy.getBytes("utf-8");
            String actualEncodedPolicy = BinaryUtil.toBase64String(binaryData);
            String expectedEncodedPolicy = "eyJleHBpcmF0aW9uIjoiMjAxNS0wMy0xOVQwMzo0NDowNi40Nz"
                    + "ZaIiwiY29uZGl0aW9ucyI6W3siYnVja2V0IjoiZ2VuLXBvc3QtcG9saWN5In0sWyJlcSIsIiRrZXkiLC"
                    + "J1c2VyL2VyaWMvXCR7ZmlsZW5hbWV9Il0sWyJzdGFydHMtd2l0aCIsIiRrZXkiLCJ1c2VyL2Vya"
                    + "WMiXSxbInN0YXJ0cy13aXRoIiwiJHgtb3NzLW1ldGEtdGFnIiwiZHVtbXlfZXRhZyJdLFsiY29udG"
                    + "VudC1sZW5ndGgtcmFuZ2UiLDEsMTAyNF1dfQ==";
            Assert.assertEquals(expectedEncodedPolicy, actualEncodedPolicy);
            
            String actualPostSignature = ossClient.calculatePostSignature(actualPostPolicy);
            // It has something to do with the local time
            Assert.assertTrue((actualPostSignature.equals("88kD3wGu1W5isVAdWSG765DRPKY=") || 
                    actualPostSignature.equals("KbUYorFeyyqxntffsNlrRcV50Ds=")));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}
