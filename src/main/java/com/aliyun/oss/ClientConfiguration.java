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

package com.aliyun.oss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.aliyun.oss.common.comm.IdleConnectionReaper;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.common.utils.ResourceManager;
import com.aliyun.oss.common.utils.VersionInfoUtils;
import com.aliyun.oss.internal.OSSConstants;

/**
 * Client configurations for accessing to OSS services.
 */
public class ClientConfiguration {

    private static final String DEFAULT_USER_AGENT = VersionInfoUtils.getDefaultUserAgent();
    
    private static final int DEFAULT_MAX_RETRIES = 3;
    
    public static final int DEFAULT_CONNECTION_TIMEOUT = 50 * 1000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 50 * 1000;
    public static final int DEFAULT_MAX_CONNECTIONS = 1024;
    public static final long DEFAULT_CONNECTION_TTL = -1;

    public static final boolean DEFAULT_USE_REAPER = true;
    
    public static final String DEFAULT_CNAME_EXCLUDE_LIST = "aliyuncs.com,aliyun-inc.com,aliyun.com";
    
    private String userAgent = DEFAULT_USER_AGENT;
    private int maxErrorRetry = DEFAULT_MAX_RETRIES;
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int socketTimeout = DEFAULT_CONNECTION_TIMEOUT;
    private int maxConnections = DEFAULT_MAX_CONNECTIONS;
    private long connectionTTL = DEFAULT_CONNECTION_TTL;
    private boolean useReaper = DEFAULT_USE_REAPER;
    private Protocol protocol = Protocol.HTTP;
    
    private String proxyHost = null;
    private int proxyPort = -1;
    private String proxyUsername = null;
    private String proxyPassword = null;
    private String proxyDomain = null;
    private String proxyWorkstation = null;

    private List<String> cnameExcludeList = new ArrayList<String>();
    private Lock rlock = new ReentrantLock();

    /**
     * 构造用户代理。
     * @return 用户代理。
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * 设置用户代理。
     * @param userAgent
     *          用户代理。
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * 返回代理服务器主机地址。
     * @return 代理服务器主机地址。
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * 设置代理服务器主机地址。
     * @param proxyHost
     *          代理服务器主机地址。
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * 返回代理服务器端口。
     * @return 代理服务器端口。
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * 设置代理服务器端口。
     * @param proxyPort 代理服务器端口。
     * @throws ClientException
     */
    public void setProxyPort(int proxyPort) throws ClientException {
    	if (proxyPort <= 0) {
    		throw new ClientException(ResourceManager.getInstance(
    				OSSConstants.RESOURCE_NAME_COMMON).getString("ParameterIsInvalid"), null);
    	}
        this.proxyPort = proxyPort;
    }

    /**
     * 返回代理服务器验证的用户名。
     * @return 用户名。
     */
    public String getProxyUsername() {
        return proxyUsername;
    }

    /**
     * 设置代理服务器验证的用户名。
     * @param proxyUsername
     *          用户名。
     */
    public void setProxyUsername(String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }

    /**
     * 返回代理服务器验证的密码。
     * @return 密码。
     */
    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * 设置代理服务器验证的密码。
     * @param proxyPassword
     *          密码。
     */
    public void setProxyPassword(String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }

    /**
     * 返回访问NTLM验证的代理服务器的Windows域名（可选）。
     * @return 域名。
     */
    public String getProxyDomain() {
        return proxyDomain;
    }

    /**
     * 设置访问NTLM验证的代理服务器的Windows域名（可选）。
     * @param proxyDomain
     *          域名。
     */
    public void setProxyDomain(String proxyDomain) {
        this.proxyDomain = proxyDomain;
    }

    /**
     * 返回NTLM代理服务器的Windows工作站名称。
     * @return NTLM代理服务器的Windows工作站名称。
     */
    public String getProxyWorkstation() {
        return proxyWorkstation;
    }

    /**
     * 设置NTLM代理服务器的Windows工作站名称。
     * （可选，如果代理服务器非NTLM，不需要设置该参数）。
     * @param proxyWorkstation
     *          NTLM代理服务器的Windows工作站名称。
     */
    public void setProxyWorkstation(String proxyWorkstation) {
        this.proxyWorkstation = proxyWorkstation;
    }

    /**
     * 返回允许打开的最大HTTP连接数。
     * @return 最大HTTP连接数。
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * 设置允许打开的最大HTTP连接数。
     * @param maxConnections
     *          最大HTTP连接数。
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 返回通过打开的连接传输数据的超时时间（单位：毫秒）。
     * 0表示无限等待（但不推荐使用）。
     * @return 通过打开的连接传输数据的超时时间（单位：毫秒）。
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * 设置通过打开的连接传输数据的超时时间（单位：毫秒）。
     * 0表示无限等待（但不推荐使用）。
     * @param socketTimeout
     *          通过打开的连接传输数据的超时时间（单位：毫秒）。
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    /**
     * 返回建立连接的超时时间（单位：毫秒）。
     * @return 建立连接的超时时间（单位：毫秒）。
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 设置建立连接的超时时间（单位：毫秒）。
     * @param connectionTimeout
     *          建立连接的超时时间（单位：毫秒）。
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * 返回一个值表示当可重试的请求失败后最大的重试次数。（默认值为3）
     * @return 当可重试的请求失败后最大的重试次数。
     */
    public int getMaxErrorRetry() {
        return maxErrorRetry;
    }

    /**
     * 设置一个值表示当可重试的请求失败后最大的重试次数。（默认值为3）
     * @param maxErrorRetry
     *          当可重试的请求失败后最大的重试次数。
     */
    public void setMaxErrorRetry(int maxErrorRetry) {
        this.maxErrorRetry = maxErrorRetry;
    }
    
    /**
     * 获取连接池中连接过期时间。
     * @return 连接过期时间。
     */
    public long getConnectionTTL() {
		return connectionTTL;
	}

    /**
     * 设置连接池中连接过期时间。
     * @param connectionTTL 连接过期时间（单位为毫秒）。
     */
	public void setConnectionTTL(long connectionTTL) {
		this.connectionTTL = connectionTTL;
	}

	/**
     * 查看是否使用{@link IdleConnectionReaper}管理过期连接。
     */
    public boolean isUseReaper() {
		return useReaper;
	}

    /**
     * 设置是否使用{@link IdleConnectionReaper}管理过期连接。
     */
	public void setUseReaper(boolean useReaper) {
		this.useReaper = useReaper;
	}

	/**
	 * 获取连接OSS所采用的协议（HTTP/HTTPS）。 
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * 设置连接OSS所采用的协议（HTTP/HTTPS）。
	 */
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	/**
     * 获取CNAME排除列表（不可修改），以列表元素作为后缀的域名将不进行CNAME解析。
     * @return CNAME排除列表。
     */
	public List<String> getCnameExcludeList() {
		if (this.cnameExcludeList.size() == 0) {
			rlock.lock();
			if (this.cnameExcludeList.size() == 0) {
				AppendDefaultExcludeList(this.cnameExcludeList);
			}
			rlock.unlock();
		}
		return Collections.unmodifiableList(this.cnameExcludeList);
	}

	/**
	 * 设置CNAME排除列表。
	 * @param cnameExcludeList CNAME排除列表。
	 */
	public void setCnameExcludeList(List<String> cnameExcludeList) {
		if (cnameExcludeList == null) {
			throw new IllegalArgumentException("cname exclude list should not be null.");
		}
		
		this.cnameExcludeList.clear();
		for (String excl : cnameExcludeList) {
			if (!excl.trim().isEmpty()) {
				this.cnameExcludeList.add(excl);
			}
		}
		
		AppendDefaultExcludeList(this.cnameExcludeList);
	}
	
	/**
	 * 添加默认CNAME排除列表至CNAME自定义排除列表。
	 * @param excludeList CNAME自定义排除列表。
	 */
	private static void AppendDefaultExcludeList(List<String> excludeList) {
		String[] excludes = DEFAULT_CNAME_EXCLUDE_LIST.split(",");
		for (String excl : excludes) {
        	if (!excl.trim().isEmpty() && !excludeList.contains(excl)) {
        		excludeList.add(excl.trim().toLowerCase());
        	}
       }
	}
}
