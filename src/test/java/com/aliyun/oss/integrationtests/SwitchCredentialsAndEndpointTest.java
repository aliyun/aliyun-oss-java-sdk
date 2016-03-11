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

package com.aliyun.oss.integrationtests;

import static com.aliyun.oss.integrationtests.TestConfig.DEFAULT_ACCESS_ID_1;
import static com.aliyun.oss.integrationtests.TestConfig.DEFAULT_ACCESS_ID_2;
import static com.aliyun.oss.integrationtests.TestConfig.DEFAULT_ACCESS_KEY_1;
import static com.aliyun.oss.integrationtests.TestConfig.DEFAULT_ACCESS_KEY_2;
import static com.aliyun.oss.integrationtests.TestConfig.DEFAULT_LOCATION;
import static com.aliyun.oss.integrationtests.TestConfig.INVALID_ACCESS_ID;
import static com.aliyun.oss.integrationtests.TestConfig.INVALID_ACCESS_KEY;
import static com.aliyun.oss.integrationtests.TestConfig.INVALID_ENDPOINT;
import static com.aliyun.oss.integrationtests.TestConfig.SECOND_ACCESS_ID;
import static com.aliyun.oss.integrationtests.TestConfig.SECOND_ACCESS_KEY;
import static com.aliyun.oss.integrationtests.TestConfig.SECOND_ENDPOINT;
import static com.aliyun.oss.integrationtests.TestConfig.SECOND_LOCATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import com.aliyun.oss.ClientErrorCode;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;

@Ignore
public class SwitchCredentialsAndEndpointTest extends TestBase {

    /* Indicate whether credentials switching starts prior to credentials verification */
    private volatile boolean switchStarted = false;
    
    private static final int loopTimes = 100;
    private static final int switchInterval = 50; // unit in milliseconds

    @Ignore
    public void testSwitchValidCredentialsAndEndpoint() {
        CredentialsProvider credsProvider = defaultClient.getCredentialsProvider();
        Credentials defaultCreds = credsProvider.getCredentials();
        assertEquals(DEFAULT_ACCESS_ID_1, defaultCreds.getAccessKeyId());
        assertEquals(DEFAULT_ACCESS_KEY_1, defaultCreds.getSecretAccessKey());

        // Verify default credentials under default endpoint
        try {
            String loc = defaultClient.getBucketLocation(bucketName);
            assertEquals(DEFAULT_LOCATION, loc);
        } catch (OSSException ex) {
            fail("Unable to get bucket location with default credentials.");
        }
        
        // Switch to another default credentials that belongs to the same user acount.
        Credentials defaultCreds2 = new DefaultCredentials(DEFAULT_ACCESS_ID_2, DEFAULT_ACCESS_KEY_2);
        defaultClient.switchCredentials(defaultCreds2);
        defaultCreds2 = credsProvider.getCredentials();
        assertEquals(DEFAULT_ACCESS_ID_2, defaultCreds2.getAccessKeyId());
        assertEquals(DEFAULT_ACCESS_KEY_2, defaultCreds2.getSecretAccessKey());

        // Verify another default credentials under default endpoint
        try {
            String loc = defaultClient.getBucketLocation(bucketName);
            assertEquals(DEFAULT_LOCATION, loc);
        } catch (OSSException ex) {
            restoreDefaultCredentials();
            fail("Unable to get bucket location with another default credentials.");
        }
        
        // Switch to second credentials that belongs to another user acount,
        // Note that the default credentials are only valid under default endpoint 
        // and the second credentials are only valid under second endpoint.
        Credentials secondCreds = new DefaultCredentials(SECOND_ACCESS_ID, SECOND_ACCESS_KEY);
        defaultClient.switchCredentials(secondCreds);
        secondCreds = credsProvider.getCredentials();
        assertEquals(SECOND_ACCESS_ID, secondCreds.getAccessKeyId());
        assertEquals(SECOND_ACCESS_KEY, secondCreds.getSecretAccessKey());
        
        // Verify second credentials under default endpoint
        try {
            defaultClient.getBucketLocation(bucketName);
            fail("Should not be able to get bucket location with second credentials.");
        } catch (OSSException ex) {
            assertEquals(OSSErrorCode.INVALID_ACCESS_KEY_ID, ex.getErrorCode());
        }
        
        // Switch to second endpoint
        defaultClient.setEndpoint(SECOND_ENDPOINT);
        
        // Verify second credentials under second endpoint
        try {
            assertEquals(SECOND_ENDPOINT, defaultClient.getEndpoint().toString());
            String loc = defaultClient.getBucketLocation(bucketName);
            assertEquals(SECOND_LOCATION, loc);
            
            // After switching both credentials and endpoint, the default OSSClient is the same
            // as the second OSSClient actually.
            assertEquals(SECOND_ENDPOINT, secondClient.getEndpoint().toString());
            loc = secondClient.getBucketLocation(bucketName);
            assertEquals(SECOND_LOCATION, loc);
        } catch (OSSException ex) {
            fail("Unable to create bucket with second credentials.");
        } finally {
            restoreDefaultCredentials();
            restoreDefaultEndpoint();
        }
    }
    
    @Test
    public void testSwitchInvalidCredentialsAndEndpoint() {
        CredentialsProvider credsProvider = defaultClient.getCredentialsProvider();
        Credentials defaultCreds = credsProvider.getCredentials();
        assertEquals(DEFAULT_ACCESS_ID_1, defaultCreds.getAccessKeyId());
        assertEquals(DEFAULT_ACCESS_KEY_1, defaultCreds.getSecretAccessKey());
        
        // Switch to invalid credentials
        Credentials invalidCreds = new DefaultCredentials(INVALID_ACCESS_ID, INVALID_ACCESS_KEY);
        defaultClient.switchCredentials(invalidCreds);
        
        // Verify invalid credentials under default endpoint
        try {
            defaultClient.getBucketLocation(bucketName);
            fail("Should not be able to get bucket location with invalid credentials.");
        } catch (OSSException ex) {
            assertEquals(OSSErrorCode.INVALID_ACCESS_KEY_ID, ex.getErrorCode());
        }
        
        // Switch to valid endpoint
        defaultClient.setEndpoint(INVALID_ENDPOINT);
        
        // Verify second credentials under invalid endpoint
        try {
            defaultClient.getBucketLocation(bucketName);
            fail("Should not be able to get bucket location with second credentials.");
        } catch (ClientException ex) {
            assertEquals(ClientErrorCode.UNKNOWN_HOST, ex.getErrorCode());
        } finally {
            restoreDefaultCredentials();
            restoreDefaultEndpoint();
        }
    }
    
    @Test
    public void testSwitchCredentialsSynchronously() throws Exception {
        /* Ensure credentials switching prior to credentials verification at first time */
        final Object ensureSwitchFirst = new Object();
        final Object verifySynchronizer = new Object();
        final Object switchSynchronizer = new Object();
        
        // Verify whether credentials switching work as expected
        Thread verifyThread = new Thread(new Runnable() {
            
            @Override
            public void run() {                
                synchronized (ensureSwitchFirst) {
                    if (!switchStarted) {
                        try {
                            ensureSwitchFirst.wait();
                        } catch (InterruptedException e) { }
                    }
                }
                
                int l = 0;
                do {
                    // Wait for credentials switching completion
                    synchronized (verifySynchronizer) {
                        try {
                            verifySynchronizer.wait();
                        } catch (InterruptedException e) { }
                    }
                    
                    CredentialsProvider credsProvider = defaultClient.getCredentialsProvider();
                    Credentials currentCreds = credsProvider.getCredentials();
                    
                    try {
                        String loc = defaultClient.getBucketLocation(bucketName);
                        assertEquals(DEFAULT_LOCATION, loc);    
                        assertEquals(DEFAULT_ACCESS_ID_1, currentCreds.getAccessKeyId());
                        assertEquals(DEFAULT_ACCESS_KEY_1, currentCreds.getSecretAccessKey());
                    } catch (OSSException ex) {
                        assertEquals(OSSErrorCode.INVALID_ACCESS_KEY_ID, ex.getErrorCode());
                        assertEquals(SECOND_ACCESS_ID, currentCreds.getAccessKeyId());
                        assertEquals(SECOND_ACCESS_KEY, currentCreds.getSecretAccessKey());
                    }
                    
                    // Notify credentials switching
                    synchronized (switchSynchronizer) {
                        switchSynchronizer.notify();
                    }
                    
                } while (++l < loopTimes);
            }
        });
        
        // Switch credentials(including valid and invalid ones) synchronously
        Thread switchThread = new Thread(new Runnable() {
            
            @Override
            public void run() {        
                int l = 0;
                boolean firstSwitch = false;
                do {
                    Credentials secondCreds = new DefaultCredentials(SECOND_ACCESS_ID, SECOND_ACCESS_KEY);
                    defaultClient.switchCredentials(secondCreds);
                    CredentialsProvider credsProvider = defaultClient.getCredentialsProvider();
                    secondCreds = credsProvider.getCredentials();
                    assertEquals(SECOND_ACCESS_ID, secondCreds.getAccessKeyId());
                    assertEquals(SECOND_ACCESS_KEY, secondCreds.getSecretAccessKey());

                    if (!firstSwitch) {
                        synchronized (ensureSwitchFirst) {
                            switchStarted = true;
                            ensureSwitchFirst.notify();
                        }
                        firstSwitch = true;
                    }
                    
                    try {
                        Thread.sleep(switchInterval);
                    } catch (InterruptedException e) { }
                    
                    /* 
                     * Notify credentials verification and wait for next credentials switching.
                     * TODO: The two synchronized clauses below should be combined as atomic operation.
                     */
                    synchronized (verifySynchronizer) {
                        verifySynchronizer.notify();
                    }
                    synchronized (switchSynchronizer) {
                        try {
                            switchSynchronizer.wait();
                        } catch (InterruptedException e) {}
                    }
                } while (++l < loopTimes);
            }
        });
        
        verifyThread.start();
        switchThread.start();
        verifyThread.join();
        switchThread.join();
        
        restoreDefaultCredentials();
    }
    
    @Test
    public void testSwitchEndpointSynchronously() throws Exception {
        /* Ensure endpoint switching prior to endpoint verification at first time */
        final Object ensureSwitchFirst = new Object();
        final Object verifySynchronizer = new Object();
        final Object switchSynchronizer = new Object();
        
        // Verify whether endpoint switching work as expected
        Thread verifyThread = new Thread(new Runnable() {
            
            @Override
            public void run() {                
                synchronized (ensureSwitchFirst) {
                    if (!switchStarted) {
                        try {
                            ensureSwitchFirst.wait();
                        } catch (InterruptedException e) { }
                    }
                }
                
                int l = 0;
                do {
                    // Wait for endpoint switching completion
                    synchronized (verifySynchronizer) {
                        try {
                            verifySynchronizer.wait();
                        } catch (InterruptedException e) { }
                    }
                    
                    CredentialsProvider credsProvider = defaultClient.getCredentialsProvider();
                    Credentials currentCreds = credsProvider.getCredentials();
                    
                    String loc = defaultClient.getBucketLocation(bucketName);
                    assertEquals(SECOND_LOCATION, loc);    
                    assertEquals(SECOND_ACCESS_ID, currentCreds.getAccessKeyId());
                    assertEquals(SECOND_ACCESS_KEY, currentCreds.getSecretAccessKey());
                    
                    /*
                     * Since the default OSSClient is the same as the second OSSClient, let's
                     * do a simple verification. 
                     */
                    String secondLoc = secondClient.getBucketLocation(bucketName);
                    assertEquals(loc, secondLoc);
                    assertEquals(SECOND_LOCATION, secondLoc);
                    CredentialsProvider secondCredsProvider = secondClient.getCredentialsProvider();
                    Credentials secondCreds = secondCredsProvider.getCredentials();
                    assertEquals(SECOND_ACCESS_ID, secondCreds.getAccessKeyId());
                    assertEquals(SECOND_ACCESS_KEY, secondCreds.getSecretAccessKey());
                    
                    // Notify endpoint switching
                    synchronized (switchSynchronizer) {
                        restoreDefaultCredentials();
                        restoreDefaultEndpoint();
                        switchSynchronizer.notify();
                    }
                    
                } while (++l < loopTimes);
            }
        });
        
        // Switch endpoint synchronously
        Thread switchThread = new Thread(new Runnable() {
            
            @Override
            public void run() {        
                int l = 0;
                boolean firstSwitch = false;
                do {
                    /* 
                     * Switch both credentials and endpoint, now the default OSSClient is the same as 
                     * the second OSSClient actually.
                     */
                    Credentials secondCreds = new DefaultCredentials(SECOND_ACCESS_ID, SECOND_ACCESS_KEY);
                    defaultClient.switchCredentials(secondCreds);
                    defaultClient.setEndpoint(SECOND_ENDPOINT);

                    if (!firstSwitch) {
                        synchronized (ensureSwitchFirst) {
                            switchStarted = true;
                            ensureSwitchFirst.notify();
                        }
                        firstSwitch = true;
                    }
                    
                    try {
                        Thread.sleep(switchInterval);
                    } catch (InterruptedException e) { }
                    
                    /* 
                     * Notify credentials verification and wait for next credentials switching.
                     * TODO: The two synchronized clauses below should be combined as atomic operation.
                     */
                    synchronized (verifySynchronizer) {
                        verifySynchronizer.notify();
                    }
                    synchronized (switchSynchronizer) {
                        try {
                            switchSynchronizer.wait();
                        } catch (InterruptedException e) {}
                    }
                } while (++l < loopTimes);
            }
        });
        
        verifyThread.start();
        switchThread.start();
        verifyThread.join();
        switchThread.join();
        
        restoreDefaultCredentials();
        restoreDefaultEndpoint();
    }
    
}
