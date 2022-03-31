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

package com.aliyun.oss.common.io;

import com.aliyun.oss.common.comm.io.FixedLengthInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;

public class FixedLengthInputStreamTest {
    @Test
    public void testFixedLengthInputStream() {
        String data = "OssService";
        ByteArrayInputStream byteInput = new ByteArrayInputStream(data.getBytes());
        FixedLengthInputStream input = new FixedLengthInputStream(byteInput, data.length());

        Assertions.assertEquals(byteInput, input.getWrappedInputStream());
        Assertions.assertEquals(data.length(), input.getLength());

        input.setLength(data.length());

        input.setWrappedInputStream(null);
        Assertions.assertEquals(null, input.getWrappedInputStream());

        try {
            input.setWrappedInputStream(byteInput);
            input.skip(3);
            int ret = input.read();
            Assertions.assertEquals('S', ret);
            input.reset();
        } catch (IOException e) {
            Assertions.assertTrue(false);
        }

        try {
            input = new FixedLengthInputStream(null, 10);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            input = new FixedLengthInputStream(byteInput, -1);
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }
}
