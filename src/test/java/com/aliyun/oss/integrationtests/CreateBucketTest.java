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

import static com.aliyun.oss.integrationtests.TestUtils.*;
import static com.aliyun.oss.model.LocationConstraint.OSS_CN_BEIJING;
import static com.aliyun.oss.model.LocationConstraint.OSS_CN_HANGZHOU;
import static com.aliyun.oss.model.LocationConstraint.OSS_CN_HONGKONG;
import static com.aliyun.oss.model.LocationConstraint.OSS_CN_QINGDAO;
import static com.aliyun.oss.model.LocationConstraint.OSS_CN_SHENZHEN;
import static com.aliyun.oss.integrationtests.TestConstants.INVALID_LOCATION_CONSTRAINT_ERR;
import static com.aliyun.oss.integrationtests.TestConstants.BUCKET_ALREADY_EXIST_ERR;
import static com.aliyun.oss.integrationtests.TestConstants.TOO_MANY_BUCKETS_ERR;
import static com.aliyun.oss.integrationtests.TestConstants.MODIFY_STORAGE_TYPE_ERR;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.AccessControlList;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.BucketList;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.Grant;
import com.aliyun.oss.model.GroupGrantee;
import com.aliyun.oss.model.Permission;
import com.aliyun.oss.model.StorageClass;

public class CreateBucketTest extends TestBase {
    
    private static final int MAX_BUCKETS_ALLOWED = 10;
    
    private static final String DEFAULT_LOCATION = "oss-cn-hangzhou";
    
    private static final String[] ALL_SUPPORTED_LOCATIONS = { 
        OSS_CN_BEIJING, 
        OSS_CN_HANGZHOU, 
        OSS_CN_HONGKONG, 
        OSS_CN_QINGDAO, 
        OSS_CN_SHENZHEN 
    };
    
    @Test
    public void testPutWithDefaultLocation() {
        final String bucketName = "bucket-with-default-location";
        
        try {
            defaultClient.createBucket(bucketName);
            String loc = defaultClient.getBucketLocation(bucketName);
            Assert.assertEquals(DEFAULT_LOCATION, loc);
            
            // Create bucket with the same name again.
            defaultClient.createBucket(bucketName);
            loc = defaultClient.getBucketLocation(bucketName);
            Assert.assertEquals(DEFAULT_LOCATION, loc);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            defaultClient.deleteBucket(bucketName);
        }
    }
    
    @Ignore
    public void testPutWithPerferedLocation() {
        final String bucketName = "bucket-with-prefered-location";
        
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        OSSClient client = null;
        for (String supportedLoc : ALL_SUPPORTED_LOCATIONS) {
            request.setLocationConstraint(supportedLoc);
            client = createClientByLocation(supportedLoc);
            try {
                client.createBucket(request);
                String loc = client.getBucketLocation(bucketName);
                Assert.assertEquals(supportedLoc, loc);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            } finally {
                client.deleteBucket(bucketName);
            }
        }
    }
    
    @Test
    public void testPutWithUnsupportedLocation() {
        final String bucketName = "bucket-with-unsupported-location";
        final String unsupportedLocation = "oss-cn-zhengzhou";
        
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setLocationConstraint(unsupportedLocation);
        try {
            defaultClient.createBucket(request);
            Assert.fail("Create bucket should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.INVALID_LOCATION_CONSTRAINT, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(INVALID_LOCATION_CONSTRAINT_ERR));
        }
    }
    
    @Test
    public void testPutWithInconsistentLocation() {
        final String bucketName = "bucket-with-inconsistent-location";
        
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        // Make location constraint inconsistent with endpoint 
        request.setLocationConstraint(OSS_CN_SHENZHEN);
        try {
            defaultClient.createBucket(request);
            Assert.fail("Create bucket should not be successful.");
        } catch (OSSException e) {
            //TODO: Inconsistent with OSS API, why not IllegalLocationConstraintException(400)?
            Assert.assertEquals(OSSErrorCode.INVALID_LOCATION_CONSTRAINT, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(INVALID_LOCATION_CONSTRAINT_ERR));
        }
    }
    
    @Test
    public void testModifyExistingBucketLocation() {
        final String bucketName = "modify-existing-bucket-location";
        
        try {
            defaultClient.createBucket(bucketName);
            String loc = defaultClient.getBucketLocation(bucketName);
            Assert.assertEquals(DEFAULT_LOCATION, loc);
            
            // Try to modify location of existing bucket
            CreateBucketRequest request = new CreateBucketRequest(bucketName);
            request.setLocationConstraint(OSS_CN_SHENZHEN);
            defaultClient.createBucket(request);
            
            Assert.fail("Create bucket should not be successful.");
        } catch (OSSException e) {
            //TODO: Inconsistent with OSS API, why not Conflict(409)?
            Assert.assertEquals(OSSErrorCode.INVALID_LOCATION_CONSTRAINT, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(INVALID_LOCATION_CONSTRAINT_ERR));
        } finally {
            defaultClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testPutExistingBucketWithoutOwnership() {
        final String bucketWithoutOwnership = "oss";
        
        try {
            defaultClient.createBucket(bucketWithoutOwnership);    
            Assert.fail("Create bucket should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.BUCKET_ALREADY_EXISTS, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(BUCKET_ALREADY_EXIST_ERR));
        }
    }
    
    @Test
    public void testInvalidBucketNames() {
        String[] invalidBucketNames = { "ab", "abcdefjhijklmnopqrstuvwxyz0123456789abcdefjhijklmnopqrstuvwxyz-a",
                "abC", "abc#", "-abc", "#abc", "-abc-", "Abcdefg", "abcdefg-" };
        
        for (String value : invalidBucketNames) {
            boolean created = false;
            try {
                defaultClient.createBucket(value);
                created = true;
                Assert.fail(String.format("Invalid bucket name %s should not be created successfully.", value));
            } catch (Exception ex) {
                Assert.assertTrue(ex instanceof IllegalArgumentException);
            } finally {
                if (created) {
                    defaultClient.deleteBucket(value);
                }
            }
        }
    }
    
    @Ignore
    public void testPutTooManyBuckets() {        
        final String bucketNamePrefix = "too-many-buckets-";
        
        try {
            List<String> existingBuckets = new ArrayList<String>();
            List<Bucket> bucketListing = defaultClient.listBuckets();
            for (Bucket bkt : bucketListing) {
                existingBuckets.add(bkt.getName());
            }
            
            int remaindingAllowed = MAX_BUCKETS_ALLOWED - existingBuckets.size();            
            List<String> newlyBuckets = new ArrayList<String>();
            int i = 0;
            while (i < remaindingAllowed) {
                String bucketName = bucketNamePrefix + i;
                try {
                    defaultClient.createBucket(bucketName);
                    newlyBuckets.add(bucketName);
                    i++;
                    
                    String loc = defaultClient.getBucketLocation(bucketName);
                    Assert.assertEquals(DEFAULT_LOCATION, loc);
                    
                    Thread.sleep(50);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    continue;
                }
            }
            
            // Try to create (MAX_BUCKETS_ALLOWED +1)th bucket
            try {
                defaultClient.createBucket(bucketNamePrefix + MAX_BUCKETS_ALLOWED);    
                Assert.fail("Create bucket should not be successful.");
            } catch (OSSException oe) {
                Assert.assertEquals(OSSErrorCode.TOO_MANY_BUCKETS, oe.getErrorCode());
                Assert.assertTrue(oe.getMessage().startsWith(TOO_MANY_BUCKETS_ERR));
            } finally {
                for (String bkt : newlyBuckets) {
                    try {
                        defaultClient.deleteBucket(bkt);
                    } catch (Exception e) {
                        // Ignore the exception and continue to delete remainding undesired buckets
                    }
                }
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    @Test
    public void testPutWithCannedACL() {
        final String bucketName = "bucket-with-canned-acl";
        
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        try {
            // Create bucket with default(private) acl
            secondClient.createBucket(createBucketRequest);
            AccessControlList returnedAcl = secondClient.getBucketAcl(bucketName);
            Set<Grant> grants = returnedAcl.getGrants();
            Assert.assertEquals(0, grants.size());
            
            // Try to create existing bucket without setting acl
            secondClient.createBucket(bucketName);
            waitForCacheExpiration(5);
            returnedAcl = secondClient.getBucketAcl(bucketName);
            grants = returnedAcl.getGrants();
            Assert.assertEquals(0, grants.size());
            
            // Create bucket with public-read acl
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
            secondClient.createBucket(createBucketRequest);
            returnedAcl = secondClient.getBucketAcl(bucketName);
            grants = returnedAcl.getGrants();
            Assert.assertEquals(1, grants.size());
            Grant grant = (Grant) grants.toArray()[0];
            Assert.assertEquals(GroupGrantee.AllUsers, grant.getGrantee());
            Assert.assertEquals(Permission.Read, grant.getPermission());
            
            // Try to create existing bucket without setting acl
            secondClient.createBucket(bucketName);
            waitForCacheExpiration(5);
            returnedAcl = secondClient.getBucketAcl(bucketName);
            grants = returnedAcl.getGrants();
            Assert.assertEquals(1, grants.size());
            grant = (Grant) grants.toArray()[0];
            Assert.assertEquals(GroupGrantee.AllUsers, grant.getGrantee());
            Assert.assertEquals(Permission.Read, grant.getPermission());

            // Create bucket with public-read-write acl
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicReadWrite);
            secondClient.createBucket(createBucketRequest);
            waitForCacheExpiration(5);
            returnedAcl = secondClient.getBucketAcl(bucketName);
            grants = returnedAcl.getGrants();
            Assert.assertEquals(1, grants.size());
            grant = (Grant) grants.toArray()[0];
            Assert.assertEquals(GroupGrantee.AllUsers, grant.getGrantee());
            Assert.assertEquals(Permission.FullControl, grant.getPermission());
            
            // Try to create existing bucket without setting acl
            secondClient.createBucket(bucketName);
            waitForCacheExpiration(5);
            returnedAcl = secondClient.getBucketAcl(bucketName);
            grants = returnedAcl.getGrants();
            Assert.assertEquals(1, grants.size());
            grant = (Grant) grants.toArray()[0];
            Assert.assertEquals(GroupGrantee.AllUsers, grant.getGrantee());
            Assert.assertEquals(Permission.FullControl, grant.getPermission());
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testPutWithDefaultStorageType() {
        final String bucketName = "bucket-with-default-storage-type";
        
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        try {
            secondClient.createBucket(createBucketRequest);

            BucketList buckets = secondClient.listBuckets(bucketName, "", 100);
            Assert.assertEquals(1, buckets.getBucketList().size());
            Assert.assertEquals(StorageClass.Standard, buckets.getBucketList().get(0).getStorageClass());            
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testPutWithStorageType() {
        final String bucketName = "bucket-with-storage-type";
        
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        createBucketRequest.setStorageClass(StorageClass.Nearline);
        try {
            secondClient.createBucket(createBucketRequest);
            AccessControlList returnedAcl = secondClient.getBucketAcl(bucketName);
            Set<Grant> grants = returnedAcl.getGrants();
            Assert.assertEquals(0, grants.size());
            
            BucketList buckets = secondClient.listBuckets(bucketName, "", 100);
            Assert.assertEquals(1, buckets.getBucketList().size());
            Assert.assertEquals(StorageClass.Nearline, buckets.getBucketList().get(0).getStorageClass());
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testPutWithStorageTypeFunc() {
        final String bucketName = "bucket-with-storage-type-func";
        
        try {
            secondClient.createBucket(new CreateBucketRequest(bucketName).withStorageType(StorageClass.Nearline));
            AccessControlList returnedAcl = secondClient.getBucketAcl(bucketName);
            Set<Grant> grants = returnedAcl.getGrants();
            Assert.assertEquals(0, grants.size());
            
            BucketList buckets = secondClient.listBuckets(bucketName, "", 100);
            Assert.assertEquals(1, buckets.getBucketList().size());
            Assert.assertEquals(StorageClass.Nearline, buckets.getBucketList().get(0).getStorageClass());
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testPutWithStorageTypeAndLocation() {
        final String bucketName = "bucket-with-storage-and-location";
        
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        createBucketRequest.setStorageClass(StorageClass.Standard);
        createBucketRequest.setLocationConstraint(DEFAULT_LOCATION);
        try {
            secondClient.createBucket(createBucketRequest);
            AccessControlList returnedAcl = secondClient.getBucketAcl(bucketName);
            Set<Grant> grants = returnedAcl.getGrants();
            Assert.assertEquals(0, grants.size());
            System.out.println(returnedAcl.toString());
            
            BucketList buckets = secondClient.listBuckets(bucketName, "", 100);
            Assert.assertEquals(1, buckets.getBucketList().size());
            Assert.assertEquals(StorageClass.Standard, buckets.getBucketList().get(0).getStorageClass());
            Assert.assertEquals(DEFAULT_LOCATION, buckets.getBucketList().get(0).getLocation());
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testPutWithStorageTypeModify() {
        final String bucketName = "bucket-with-storage-type-modify";
        
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        try {
            secondClient.createBucket(createBucketRequest);

            BucketList buckets = secondClient.listBuckets(bucketName, "", 100);
            Assert.assertEquals(1, buckets.getBucketList().size());
            Assert.assertEquals(StorageClass.Standard, buckets.getBucketList().get(0).getStorageClass());
           
            try {
                createBucketRequest.setStorageClass(StorageClass.Nearline);
                secondClient.createBucket(createBucketRequest);
                Assert.fail("Create bucket should not be successful.");
            } catch (OSSException oe) {
                Assert.assertEquals(OSSErrorCode.BUCKET_ALREADY_EXISTS, oe.getErrorCode());
                Assert.assertTrue(oe.getMessage().startsWith(MODIFY_STORAGE_TYPE_ERR));
            }
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        } finally {
            secondClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testPutWithWithStorageTypeUnsupportedLocation() {
        final String bucketName = "bucket-with-storage-unsupported-location";
        final String unsupportedLocation = "oss-cn-zhengzhou";
        
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setStorageClass(StorageClass.Standard);
        request.setLocationConstraint(unsupportedLocation);
        try {
            secondClient.createBucket(request);
            Assert.fail("Create bucket should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.INVALID_LOCATION_CONSTRAINT, e.getErrorCode());
            Assert.assertTrue(e.getMessage().startsWith(INVALID_LOCATION_CONSTRAINT_ERR));
        }
    }
    
    @Test
    public void testPutWithStorageTypeCompatibility() {
        final String bucketName = "bucket-with-storage-type-compatibility";
        
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        try {
            defaultClient.createBucket(createBucketRequest);

            BucketList buckets = defaultClient.listBuckets(bucketName, "", 100);
            Assert.assertEquals(1, buckets.getBucketList().size());
            Assert.assertEquals(StorageClass.Standard, buckets.getBucketList().get(0).getStorageClass());
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        } finally {
            defaultClient.deleteBucket(bucketName);
        }
    }
    
    @Test
    public void testPutWithStorageTypeUnsupported() {
        final String bucketName = "bucket-with-storage-type-unsupported";
    
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setStorageClass(StorageClass.Standard);
        try {
            defaultClient.createBucket(request);
            Assert.fail("Create bucket should not be successful.");
        } catch (OSSException e) {
            Assert.assertEquals(OSSErrorCode.MALFORMED_XML, e.getErrorCode());
        }
    }
    
}
