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

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.internal.OSSConstants;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class TimeoutServiceClientTest {
    @Test
    public void testTimeoutServiceClient() {
        ClientConfiguration config = new ClientConfiguration();
        config.setRequestTimeoutEnabled(true);
        // cover partial codes
        try {
            String content = "test content";
            byte[] contentBytes = content.getBytes(OSSConstants.DEFAULT_CHARSET_NAME);
            ByteArrayInputStream contentStream = new ByteArrayInputStream(contentBytes);

            RequestMessage request = new RequestMessage(null, null);
            request.setEndpoint(new URI("http://localhost"));
            request.setMethod(HttpMethod.GET);
            request.setContent(contentStream);
            request.setContentLength(contentBytes.length);
            ExecutionContext context = new ExecutionContext();

            TimeoutServiceClient client = new TimeoutServiceClient(config);
            client.sendRequest(request, context);
            client.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TimeoutServiceClient client = new TimeoutServiceClient(config);
            client.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testTimeoutServiceClientWithThread() {
        int processors = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(processors * 20, processors * 30, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(processors * 200), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        ClientConfiguration config = new ClientConfiguration();
        config.setRequestTimeoutEnabled(true);
        config.setExecutor(executor);

        // cover partial codes
        try {
            String content = "test content";
            byte[] contentBytes = content.getBytes(OSSConstants.DEFAULT_CHARSET_NAME);
            ByteArrayInputStream contentStream = new ByteArrayInputStream(contentBytes);

            RequestMessage request = new RequestMessage(null, null);
            request.setEndpoint(new URI("http://localhost"));
            request.setMethod(HttpMethod.GET);
            request.setContent(contentStream);
            request.setContentLength(contentBytes.length);
            ExecutionContext context = new ExecutionContext();

            TimeoutServiceClient client = new TimeoutServiceClient(config);
            client.sendRequest(request, context);
            client.shutdown();
        } catch (ClientException e) {
            e.printStackTrace();
            assertEquals(e.getErrorCode(), "SocketException");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            TimeoutServiceClient client = new TimeoutServiceClient(config);
            client.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
