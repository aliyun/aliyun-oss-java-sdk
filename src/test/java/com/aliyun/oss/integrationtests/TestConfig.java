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

public final class TestConfig {

    // OSS test configuration
//    public static String OSS_TEST_ENDPOINT = null;
//    public static String OSS_TEST_REGION = null;
//    public static String OSS_TEST_ACCESS_KEY_ID = null;
//    public static String OSS_TEST_ACCESS_KEY_SECRET = null;
//    public static String OSS_TEST_ACCESS_KEY_ID_1 = null;
//    public static String OSS_TEST_ACCESS_KEY_SECRET_1 = null;
  public static String OSS_TEST_ENDPOINT = "http://oss-us-west-1.aliyuncs.com";
  public static String OSS_TEST_REGION = "oss-us-west-1";
  public static String OSS_TEST_ACCESS_KEY_ID = "2NeLUvmJFYbrj2Eb";
  public static String OSS_TEST_ACCESS_KEY_SECRET = "tpKbdpzCavhbYghxHih5urCw5lkBdx";
  public static String OSS_TEST_ACCESS_KEY_ID_1 = OSS_TEST_ACCESS_KEY_ID;
  public static String OSS_TEST_ACCESS_KEY_SECRET_1 = OSS_TEST_ACCESS_KEY_SECRET;
    
    // OSS replication test configuration
    public static String OSS_TEST_REPLICATION_ENDPOINT = null;
    public static String OSS_TEST_REPLICATION_ACCESS_KEY_ID = null;
    public static String OSS_TEST_REPLICATION_ACCESS_KEY_SECRET = null;
   
    // OSS sts test configuration
    public static String STS_TEST_ENDPOINT = null;
    public static String STS_TEST_ROLE = null;
    public static String STS_TEST_BUCKET = null;
    
    // OSS proxy test
    public static String PROXY_HOST = null;
    public static int PROXY_PORT = -1;
    public static String PROXY_USER = null;
    public static String PROXY_PASSWORD = null;
    
}
