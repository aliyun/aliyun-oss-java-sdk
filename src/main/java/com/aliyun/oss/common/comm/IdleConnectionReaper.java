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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ClientConnectionManager;

/**
 * A daemon thread used to periodically check connection pools for idle connections.
 */
@SuppressWarnings("deprecation")
public final class IdleConnectionReaper extends Thread {

	private static final Log log = LogFactory.getLog(IdleConnectionReaper.class);
    
	private static final int REAP_INTERVAL_MILLISECONDS = 60 * 1000;
    private static final ArrayList<ClientConnectionManager> connectionManagers = new ArrayList<ClientConnectionManager>();

    private static IdleConnectionReaper instance;
    
    private volatile boolean shuttingDown;

    private IdleConnectionReaper() {
        super("idle_connection_reaper");
        setDaemon(true);
    }

    public static synchronized boolean registerConnectionManager(ClientConnectionManager connectionManager) {
        if (instance == null) {
            instance = new IdleConnectionReaper();
            instance.start();
        }
        return connectionManagers.add(connectionManager);
    }

    public static synchronized boolean removeConnectionManager(ClientConnectionManager connectionManager) {
        boolean b = connectionManagers.remove(connectionManager);
        if (connectionManagers.isEmpty())
            shutdown();
        return b;
    }
    
    private void markShuttingDown() {
        shuttingDown = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        while (true) {
            if (shuttingDown) {
                log.debug("Shutting down reaper thread.");
                return;
            }
            try {
                Thread.sleep(REAP_INTERVAL_MILLISECONDS);

                List<ClientConnectionManager> connectionManagers = null;
                synchronized (IdleConnectionReaper.class) {
                    connectionManagers = (List<ClientConnectionManager>)IdleConnectionReaper.connectionManagers.clone();
                }
                for (ClientConnectionManager connectionManager : connectionManagers) {
                    try {
                        connectionManager.closeIdleConnections(60, TimeUnit.SECONDS);
                    } catch (Exception ex) {
                        if (log.isWarnEnabled()) {
                        	log.warn("Unable to close idle connections", ex);
                        }
                    }
                }
            } catch (Throwable t) {
                if (log.isDebugEnabled()) {
                	log.debug("Reaper thread: ",  t);
                }
            }
        }
    }

    public static synchronized boolean shutdown() {
        if (instance != null) {
            instance.markShuttingDown();
            instance.interrupt();
            connectionManagers.clear();
            instance = null;
            return true;
        }
        return false;
    }

    public static synchronized int size() { 
    	return connectionManagers.size(); 
    }
}
