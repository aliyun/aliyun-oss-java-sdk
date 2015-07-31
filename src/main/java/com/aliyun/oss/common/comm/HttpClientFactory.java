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

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.aliyun.oss.ClientConfiguration;

@SuppressWarnings("deprecation")
class HttpClientFactory {

    public HttpClient createHttpClient(ClientConfiguration config) {
        HttpParams httpClientParams = new BasicHttpParams();
        HttpProtocolParams.setUserAgent(httpClientParams, config.getUserAgent());
        HttpConnectionParams.setConnectionTimeout(httpClientParams, config.getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(httpClientParams, config.getSocketTimeout());
        HttpConnectionParams.setStaleCheckingEnabled(httpClientParams, true);
        HttpConnectionParams.setTcpNoDelay(httpClientParams, true);

        PoolingClientConnectionManager connManager = createConnectionManager(config, httpClientParams);
        DefaultHttpClient httpClient = new DefaultHttpClient(connManager, httpClientParams);

        if (System.getProperty("com.aliyun.oss.disableCertChecking") != null) {
            Scheme sch = new Scheme("https", 443, getSSLSocketFactory());
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
        }

        String proxyHost = config.getProxyHost();
        int proxyPort = config.getProxyPort();

        if (proxyHost != null && proxyPort > 0) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

            String proxyUsername = config.getProxyUsername();
            String proxyPassword = config.getProxyPassword();

            if (proxyUsername != null && proxyPassword != null){
                String proxyDomain = config.getProxyDomain();
                String proxyWorkstation = config.getProxyWorkstation();

                httpClient.getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new NTCredentials(proxyUsername, proxyPassword, proxyWorkstation, proxyDomain));
            }
        }

        return httpClient;
    }
    
    public static PoolingClientConnectionManager createConnectionManager(
    		ClientConfiguration config, HttpParams httpClientParams) {
        PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager(
                SchemeRegistryFactory.createDefault(), config.getConnectionTTL(), TimeUnit.MILLISECONDS);
        connectionManager.setDefaultMaxPerRoute(config.getMaxConnections());
        connectionManager.setMaxTotal(config.getMaxConnections());
        if (config.isUseReaper()) {
            IdleConnectionReaper.registerConnectionManager(connectionManager);
        }
        return connectionManager;
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null; 
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, trustAllCerts, null);
            SSLSocketFactory ssf =
                    new SSLSocketFactory(sslcontext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return ssf;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
