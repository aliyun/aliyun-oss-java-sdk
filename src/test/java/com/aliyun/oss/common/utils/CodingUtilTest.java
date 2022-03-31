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

import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.Assertions;

public class CodingUtilTest {
    @Test
    public void testCodingUtils() {

        try {
            CodingUtils.assertStringNotNullOrEmpty("", "");
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            List<String> param = new ArrayList<String>();
            CodingUtils.assertListNotNullOrEmpty(param, "");
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        Assertions.assertEquals(true, CodingUtils.checkParamRange(3, 1, false, 5, false));
        Assertions.assertEquals(false, CodingUtils.checkParamRange(0, 1, false, 5, false));
        Assertions.assertEquals(false, CodingUtils.checkParamRange(8, 1, false, 5, false));

        Assertions.assertEquals(true, CodingUtils.checkParamRange(3, 1, true, 5, false));
        Assertions.assertEquals(false, CodingUtils.checkParamRange(0, 1, true, 5, false));
        Assertions.assertEquals(false, CodingUtils.checkParamRange(8, 1, true, 5, false));

        Assertions.assertEquals(true, CodingUtils.checkParamRange(3, 1, false, 5, true));
        Assertions.assertEquals(false, CodingUtils.checkParamRange(0, 1, false, 5, true));
        Assertions.assertEquals(false, CodingUtils.checkParamRange(8, 1, false, 5, true));

        Assertions.assertEquals(true, CodingUtils.checkParamRange(3, 1, true, 5, true));
        Assertions.assertEquals(false, CodingUtils.checkParamRange(0, 1, true, 5, true));
        Assertions.assertEquals(false, CodingUtils.checkParamRange(8, 1, true, 5, true));
    }

}
