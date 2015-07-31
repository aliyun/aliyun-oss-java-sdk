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

package com.aliyun.oss.common.comm.io;

import java.io.IOException;
import java.io.InputStream;

public class PartialStream extends InputStream {
	private InputStream innerStream;
	private int length;
	private int position;
	private boolean endReached;

	public PartialStream(InputStream innerStream) {
		this(innerStream, -1);
	}
	
	public PartialStream(InputStream innerStream, int length) {
		if (innerStream == null) {
            throw new IllegalArgumentException("Source input stream should not be null");
        }
		
		this.innerStream = innerStream;
		this.length = length;
		this.position = 0;
		this.endReached = false;
	}
	
	@Override
	public int read() throws IOException {
		throw new UnsupportedOperationException("read byte-by-byte not supported.");
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return read(buffer, 0, buffer.length);
	}
	
	@Override
	public int read(byte[] buffer, int offset, int count) throws IOException {
		if (buffer == null) {
            throw new NullPointerException();
        } else if (offset < 0 || count < 0 || count > buffer.length - offset) {
            throw new IndexOutOfBoundsException();
        } else if (count == 0) {
            return 0;
        }
		
		if (endReached) {
			return -1;
		}
		
		int bytesRead = 0;
		if (this.length < 0) {
			bytesRead = innerStream.read(buffer, offset, count);
			if (bytesRead == -1) {
				this.endReached = true;
			}
		} else {
			int remainding = this.length - position;
			int bytesToRead = count;
			if (remainding < count) {
				bytesToRead = remainding;
			}
			
			bytesRead = innerStream.read(buffer, offset, bytesToRead);
			if (bytesRead == -1) {
				endReached = true;
			} else {
				position += bytesRead;
			}
			
			if (position >= length) {
				endReached = true;
			}
		}
		
		return bytesRead;
	}
}
