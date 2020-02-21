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

package com.aliyun.oss.event;

import junit.framework.Assert;
import org.junit.Test;

public class ProcessEventTest {
    @Test
    public void testProcessEvent() {
        try {
            ProgressEvent event = new ProgressEvent(null, 20);
            Assert.fail("eventType not be null");
        } catch (Exception e) {
            // expected exception.
        }

        try {
            ProgressEvent event = new ProgressEvent(ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT, -1);
            Assert.fail("bytes should not be non-negative.");
        } catch (Exception e) {
           // expected exception.
        }

        ProgressEvent event = new ProgressEvent(ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT, 10);
        event.toString();
    }


    private  class TestProgressListener implements ProgressListener {
        protected ProgressEvent event = null;
        @Override
        public void progressChanged(ProgressEvent progressEvent) {
            this.event = progressEvent;
        }
    }

    @Test public void testProcessPublisher() {
        ProgressPublisher publisher = new ProgressPublisher();
        TestProgressListener listener = new TestProgressListener();

        try {
            publisher.publishProgress(listener, null);
            Assert.assertNull(listener.event);

            publisher.publishProgress(listener, ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT);
            Assert.assertEquals(ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT, listener.event.getEventType());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        listener.event = null;

        try {
            publisher.publishSelectProgress(listener, null, 100);
            Assert.assertNull(listener.event);

            publisher.publishSelectProgress(listener, ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT, 100);
            Assert.assertEquals(ProgressEventType.REQUEST_BYTE_TRANSFER_EVENT, listener.event.getEventType());
            Assert.assertEquals(100, listener.event.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
