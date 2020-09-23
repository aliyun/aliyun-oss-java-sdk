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

package com.aliyun.oss.common;

import com.aliyun.oss.common.utils.AuthUtils;
import com.aliyun.oss.common.utils.VersionInfoUtils;
import com.aliyun.oss.internal.Mimetypes;
import com.aliyun.oss.internal.OSSConstants;
import com.aliyun.oss.internal.RequestParameters;
import com.aliyun.oss.internal.SignParameters;
import com.aliyun.oss.model.LocationConstraint;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NoCreationClassTest {
    @Test
    public void testNoCreationClass() {
        // update coverage
        RequestParameters requestParameters = new RequestParameters();
        OSSConstants ossConstants = new OSSConstants();
        LocationConstraint locationConstraint = new LocationConstraint();
        SignParameters signParameters = new SignParameters();
        AuthUtils authUtils = new AuthUtils();
        VersionInfoUtils versionInfoUtils = new VersionInfoUtils();
    }

    @Test
    public void testMimetypesClass() {
        String content = "" +
             "xdoc    application/xdoc\n" +
             "#xogg    application/xogg\n" +
             "\n" +
             "xpdf \n" +
             "";
        try {
            Mimetypes mime = Mimetypes.getInstance();
            InputStream input = new ByteArrayInputStream(content.getBytes());
            mime.loadMimetypes(input);
            Assert.assertEquals(mime.getMimetype("test.xdoc"), "application/xdoc");
            Assert.assertEquals(mime.getMimetype("test.xogg"), "application/octet-stream");
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

    }
}
