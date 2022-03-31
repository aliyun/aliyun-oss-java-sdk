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

import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.InconsistentException;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.ServiceException;
import com.aliyun.oss.common.utils.ExceptionFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;

import java.net.ConnectException;


public class ExceptionTest {
    @Test
    public void testClientException() {
        ClientException exception = new ClientException();
        Assertions.assertNull(exception.getErrorMessage());

        Throwable cause = new Throwable("test error message");
        exception = new ClientException(cause);
        Assertions.assertNull(exception.getErrorMessage());

        exception = new ClientException("test err msg", "test err code", "test request id");
        Assertions.assertEquals("test err msg", exception.getErrorMessage());
        Assertions.assertEquals("test err code", exception.getErrorCode());
        Assertions.assertEquals("test request id", exception.getRequestId());

        Assertions.assertNotNull(exception.getMessage());
    }

    @Test
    public void testInconsistentException() {
        InconsistentException exception = new InconsistentException(null, null, null);
        exception.setClientChecksum(new Long(123));
        exception.setServerChecksum(new Long(123));
        exception.setRequestId("test request id");

        Assertions.assertEquals(exception.getClientChecksum(), new Long(123));
        Assertions.assertEquals(exception.getServerChecksum(), new Long(123));
        Assertions.assertEquals(exception.getRequestId(), "test request id");
    }

    @Test
    public void testServiceException() {
        ServiceException exception = new ServiceException("test err msg");
        Assertions.assertEquals("test err msg", exception.getErrorMessage());

        exception = new ServiceException(new Throwable("test"));
        Assertions.assertNull(exception.getErrorMessage());

        exception = new ServiceException("test err msg", "test err code", "test request id", "test host id");
        Assertions.assertEquals("test err msg", exception.getErrorMessage());
        Assertions.assertEquals("test err code", exception.getErrorCode());
        Assertions.assertEquals("test request id", exception.getRequestId());
        Assertions.assertEquals("test host id", exception.getHostId());

        exception.setRawResponseError("test response err");
        Assertions.assertEquals("test response err", exception.getRawResponseError());
        Assertions.assertTrue(exception.getRawResponseError().contains("test response err"));
    }

    @Test
    public void testOSSException() {
        OSSException exception = new OSSException("test err msg");
        Assertions.assertEquals("test err msg", exception.getErrorMessage());

        exception = new OSSException("test err msg", new Throwable("test"));
        Assertions.assertEquals("test err msg", exception.getErrorMessage());

       exception = new OSSException("test err msg", "test err code", "test request id", "test host id",
               "test header", "test resource type", "test method", new Throwable("test cause"));
        Assertions.assertEquals("test err msg", exception.getErrorMessage());
        Assertions.assertEquals("test err code", exception.getErrorCode());
        Assertions.assertEquals("test request id", exception.getRequestId());
        Assertions.assertEquals("test host id", exception.getHostId());

        Assertions.assertEquals("test header", exception.getHeader());
        Assertions.assertEquals("test resource type", exception.getResourceType());
        Assertions.assertEquals("test method", exception.getMethod());
    }

    @Test
    public void testExceptionFactory() {
        ExceptionFactory factory = new ExceptionFactory();
        ClientProtocolException protocolException = new ClientProtocolException("");
        ClientException clientException = factory.createNetworkException(protocolException);
        Assertions.assertEquals(ClientErrorCode.UNKNOWN, clientException.getErrorCode());
    }

}
