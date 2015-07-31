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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import com.aliyun.oss.common.comm.io.RepeatableFileInputStream;
import com.aliyun.oss.common.comm.io.RepeatableInputStream;
import com.aliyun.oss.internal.OSSConstants;

public class IOUtils {

    public static String readStreamAsString(InputStream in, String charset)
            throws IOException {
        
    	if (in == null) {
        	return "";
        }

        Reader reader = null;
        Writer writer = new StringWriter();
        String result;

        char[] buffer = new char[1024];
        try {
        	int n = -1;
            reader = new BufferedReader(new InputStreamReader(in, charset));
            while((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }

            result = writer.toString();
        } finally {
            in.close();
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
        }

        return result;
    }
    
    public static byte[] readStreamAsByteArray(InputStream in)
        throws IOException {
        if (in == null) {
            return new byte[0];
        }
        
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            output.write(buffer, 0, len);
        }
        output.flush();
        return output.toByteArray();
    }

    public static void safeClose(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {}
        }
    }

    public static void safeClose(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {}
        }
    }
    
    public static boolean checkFile(File file) {
    	if (file == null) {
    		return false;
    	}
    	
    	boolean exists = false;
    	boolean isFile = false;
    	boolean canRead = false;
    	try {
    		exists = file.exists();
    		isFile = file.isFile();
    		canRead = file.canRead();
    	} catch (SecurityException se) {
    		// Swallow the exception and return false directly.
    		return false;
    	}
    	
    	return (exists && isFile && canRead);
    }
    
    @SuppressWarnings("resource")
	public static InputStream newRepeatableInputStream(final InputStream original) throws IOException {
    	InputStream repeatable = null;
    	if (!original.markSupported()) {
    		if (original instanceof FileInputStream) {
    			repeatable = new RepeatableFileInputStream((FileInputStream)original);
    		} else {
    			repeatable = new RepeatableInputStream(original, OSSConstants.DEFAULT_STREAM_BUFFER_SIZE);    			
    		}
    	} else {
    		repeatable = original;
    	}
    	return repeatable;
    }
}
