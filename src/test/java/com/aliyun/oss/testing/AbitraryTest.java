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

package com.aliyun.oss.testing;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.HeadObjectRequest;

public class AbitraryTest {
	
	static final String endpoint = "http://oss-cn-beijing.aliyuncs.com";
	static final String accessId = "jjMaESCoMhUJrLna";
	static final String accessKey = "o4Wq7HuEAi2oQisc7LsMDlGBzzH8zo";
	
	static OSSClient client = new OSSClient(endpoint, accessId, accessKey);
	
	static final String bucketName = "usba";
	static final String key = "a.txt";
	
	public static void main(String[] args) {        
        GenericRequest genericRequest = new GenericRequest(bucketName);
        genericRequest.addHeader("x-oss-source-ip", "10.0.0.0");
        genericRequest.addHeader("x-oss-ssl", "true");
        genericRequest.addParameter("pk0", "pv0");
        //System.out.println(client.getBucketCORSRules(genericRequest));
        System.out.println(client.getBucketAcl(genericRequest));
        //System.out.println(client.getBucketLifecycle(genericRequest));
        System.out.println(client.getBucketLocation(genericRequest));
        System.out.println(client.getBucketLogging(genericRequest));
        System.out.println(client.getBucketReferer(genericRequest));
        //System.out.println(client.getBucketWebsite(genericRequest));
        System.out.println(client.getObjectMetadata(new GenericRequest(bucketName, "a.txt")));
        System.out.println(client.getObjectAcl(new GenericRequest(bucketName, "a.txt")));
        System.out.println(client.doesBucketExist(genericRequest));
        System.out.println(client.doesObjectExist(new HeadObjectRequest(bucketName, "a.txt")));;
        
        client.deleteBucketCORSRules(genericRequest);
        client.deleteBucketLifecycle(genericRequest);
        client.deleteBucketLogging(genericRequest);
        client.deleteBucketWebsite(genericRequest);
        client.deleteObject(new GenericRequest(bucketName, "a.txt"));
        client.deleteBucket(genericRequest);
    }
}
