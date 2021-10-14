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

package com.aliyun.oss.common.comm;

import com.aliyun.oss.common.utils.CommonUtils;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;

public class CommonUtilsTest {
    @Test
    public void testToURI() {
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        String defaultProtocol = "https";

        URI res = CommonUtils.toURI(endpoint, defaultProtocol);
        assertEquals(endpoint, res.toString());
    }

    @Test
    public void testToURI2() {
        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
        String defaultProtocol = "https";

        URI res = CommonUtils.toURI(endpoint, defaultProtocol);
        assertEquals("https://oss-cn-hangzhou.aliyuncs.com", res.toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testToURI3() {
        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
        String defaultProtocol = "";

        URI res = CommonUtils.toURI(endpoint, defaultProtocol);
        assertEquals("https://oss-cn-hangzhou.aliyuncs.com", res.toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testToURI4() {
        String endpoint = null;
        String defaultProtocol = "http";

        URI res = CommonUtils.toURI(endpoint, defaultProtocol);
        assertEquals("https://oss-cn-hangzhou.aliyuncs.com", res.toString());
    }
}