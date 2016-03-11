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

import java.io.File;

public final class TestConfig {
    
    // Client Configurations for OSS hangzhou cluster
    public static final String DEFAULT_ENDPOINT = "https://oss-cn-hangzhou.aliyuncs.com";
    public static final String DEFAULT_LOCATION = "oss-cn-hangzhou";
    public static final String DEFAULT_ACCESS_ID_1 = "<valid access id>";
    public static final String DEFAULT_ACCESS_KEY_1 = "<valid access key>";
    public static final String DEFAULT_ACCESS_ID_2 = "<valid access id>";
    public static final String DEFAULT_ACCESS_KEY_2 = "<valid access key>";
    
    // Client Configurations for OSS testing cluster
    public static final String SECOND_ENDPOINT = "http://oss-test.aliyun-inc.com";
    public static final String SECOND_LOCATION = "oss-cn-hangzhou";
    public static final String SECOND_ACCESS_ID = "<valid access id>";
    public static final String SECOND_ACCESS_KEY = "<valid access key>";
    
    public static final String SECOND_REPLICATION_ENDPOINT = "http://10.101.166.37";
    public static final String SECOND_REPLICATION_LOCATION = "oss-cn-qingdao";
    public static final String SECOND_REPLICATION_ACCESS_ID = "<valid access id>";
    public static final String SECOND_REPLICATION_ACCESS_KEY = "<valid access key>";
    
    public static final String INVALID_ENDPOINT = "http://InvalidEndpoint";
    public static final String INVALID_ACCESS_ID = "InvalidAccessId";
    public static final String INVALID_ACCESS_KEY = "InvalidAccessKey";
    
    // Client Configurations for OSS beijing cluster
    public static final String BEIJING_ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
    public static final String BEIJING_LOCATION = "oss-cn-beijing";
    public static final String BEIJING_ACCESS_ID = "<valid access id>";
    public static final String BEIJING_ACCESS_KEY = "<valid access key>";
    
    // Client Configurations for OSS shenzhen cluster
    public static final String SHENZHEN_ENDPOINT = "http://oss-cn-shenzhen.aliyuncs.com";
    public static final String SHENZHEN_LOCATION = "oss-cn-shenzhen";
    public static final String SHENZHEN_ACCESS_ID = "<valid access id>";
    public static final String SHENZHEN_ACCESS_KEY = "<valid access key>";
    
    // Client Configurations for OSS qingdao cluster
    public static final String QINGDAO_ENDPOINT = "http://oss-cn-qingdao.aliyuncs.com";
    public static final String QINGDAO_LOCATION = "oss-cn-qingdao";
    public static final String QINGDAO_ACCESS_ID = "<valid access id>";
    public static final String QINGDAO_ACCESS_KEY = "<valid access key>";
    
    // Client Configurations for OSS hongkong cluster
    public static final String HONGKONG_ENDPOINT = "http://oss-cn-hongkong.aliyuncs.com";
    public static final String HONGKONG_LOCATION = "oss-cn-hongkong";
    public static final String HONGKONG_ACCESS_ID = "<valid access id>";
    public static final String HONGKONG_ACCESS_KEY = "<valid access key>";
    
    // Some miscellaneous configurations.
    public static final String BUCKET_NAME_PREFIX = "oss-java-sdk-";
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String UPLOAD_DIRECOTRY = USER_DIR + File.separator + "upload" + File.separator;
    public static final String DOWNLOAD_DIRECOTRY = USER_DIR + File.separator + "download" + File.separator;
    
    // Configurations for STS.
    public static final String STS_USER = "1287905056319499";
    public static final String STS_HOST = "10.101.88.247";
    public static final int STS_PORT = 8200;
    public static final int STS_DURATION_SECONDS = 3600;
    public static final String STS_GET_TOKEN_URI = "/api/GetFederationToken";
    public static final String STS_VERSION = "1";
    public static final String STS_GRANTEE = "testuser1";
    public static final String POP_USER = "pop";
    public static final String POP_PWD = "poppassword";
    
}
