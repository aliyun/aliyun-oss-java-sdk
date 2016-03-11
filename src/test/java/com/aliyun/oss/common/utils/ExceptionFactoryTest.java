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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.junit.Test;

import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;

public class ExceptionFactoryTest {

    @Test
    public void testCreateNetworkException() {
        SocketTimeoutException ste = new SocketTimeoutException();
        ClientException ex = ExceptionFactory.createNetworkException(ste);
        assertEquals(ex.getErrorCode(), ClientErrorCode.SOCKET_TIMEOUT);
        ConnectTimeoutException cte = new ConnectTimeoutException();
        ex = ExceptionFactory.createNetworkException(cte);
        assertEquals(ex.getErrorCode(), ClientErrorCode.CONNECTION_TIMEOUT);
        IOException ioe = new IOException();
        ex = ExceptionFactory.createNetworkException(ioe);
        assertEquals(ex.getErrorCode(), ClientErrorCode.UNKNOWN);
    }
}
