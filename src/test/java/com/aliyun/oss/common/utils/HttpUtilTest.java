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

package com.aliyun.oss.common.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpUtilTest {
    @Test
    public void testUrlEncodeDecode() {
        assertEquals("", HttpUtil.urlEncode(null, "utf8"));

        try {
            HttpUtil.urlEncode("abcdef", "xxx");
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }

        try {
            HttpUtil.urlDecode("abcdef", "");
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testParamToQueryString() {
        Map<String, String> params = new HashMap<String, String>();
        assertNull(HttpUtil.paramToQueryString(null, "utf8"));
        assertNull(HttpUtil.paramToQueryString(params, "utf8"));
    }

}