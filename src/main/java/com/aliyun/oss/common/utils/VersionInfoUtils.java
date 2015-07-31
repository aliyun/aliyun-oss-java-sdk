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

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VersionInfoUtils {

	private static Log log = LogFactory.getLog(VersionInfoUtils.class);
    
	private static final String VERSION_INFO_FILE = "versioninfo.properties";
    
    private static String version = null;
    
    private static String defaultUserAgent = null;

    public static String getVersion() {
        if (version == null) {
            initializeVersion();
        }
        return version;
    }
    
    public static String getDefaultUserAgent() {
        if (defaultUserAgent == null) {
            defaultUserAgent = "aliyun-sdk-java" + "/" + getVersion() + "(" + 
                System.getProperty("os.name") + "/" + System.getProperty("os.version") + "/" +
                System.getProperty("os.arch") + ";" + System.getProperty("java.version") + ")";
        }
        return defaultUserAgent;
    }
    
    private static void initializeVersion() {
        InputStream inputStream = VersionInfoUtils.class.getClassLoader().getResourceAsStream(VERSION_INFO_FILE);
        Properties versionInfoProperties = new Properties();
        try {
            if (inputStream == null) {
            	throw new IllegalArgumentException(VERSION_INFO_FILE + " not found on classpath");
            }
            
            versionInfoProperties.load(inputStream);
            version = versionInfoProperties.getProperty("version");
        } catch (Exception e) {
            log.info("Unable to load version information for the running SDK: " + e.getMessage());
            version = "unknown-version";
        }
    }
}
