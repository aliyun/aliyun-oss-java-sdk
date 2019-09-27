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

import com.aliyun.oss.common.comm.io.ChunkedUploadStream;
import com.aliyun.oss.utils.ResourceUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChunkedUploadStreamTest {
    String path = ResourceUtils.getTestFilename("oss/example.jpg");

    @Test
    public void testChunkedUploadStream() {
        try {
            FileInputStream inputFile = new FileInputStream(path);
            ChunkedUploadStream input = new ChunkedUploadStream(inputFile, 10 * 1024);
            byte[] bOut = new byte[20];
            byte[] bOut1 = new byte[10 * 1024];
            int ret;
            ret = input.read();

            ret = input.read(bOut1);
            ret = input.read(bOut1);
            ret = input.read(bOut1);
            ret = input.read();
            assertTrue(true);
            input.close();
        } catch (Exception e) {
            assertTrue(false);
        }

        try {
            ChunkedUploadStream input = new ChunkedUploadStream(null, 10 * 1024);
            assertTrue(false);
        }catch (Exception e) {
            assertTrue(true);
        }

        try {
            String data = "Oss";
            ByteArrayInputStream byteInput = new ByteArrayInputStream(data.getBytes());
            ChunkedUploadStream input = new ChunkedUploadStream(byteInput, 10 * 1024);
            input.read(null, 0, 0);
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}
