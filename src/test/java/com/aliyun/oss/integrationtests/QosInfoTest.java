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

import com.aliyun.oss.model.GenericRequest;
import org.junit.Test;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.BucketQosInfo;
import com.aliyun.oss.model.UserQosInfo;
import com.aliyun.oss.model.SetBucketQosInfoRequest;
import org.junit.jupiter.api.*;

public class QosInfoTest extends TestBase {

    @Test
    public void testUserQosInfo() {
        try {
            UserQosInfo userQosInfo = ossClient.getUserQosInfo();
            Assertions.assertEquals(userQosInfo.getRequestId().length(), REQUEST_ID_LEN);
            Assertions.assertNotNull(userQosInfo.getRegion());
            Assertions.assertNotNull(userQosInfo.getTotalUploadBw());
            Assertions.assertNotNull(userQosInfo.getIntranetUploadBw());
            Assertions.assertNotNull(userQosInfo.getExtranetUploadBw());
            Assertions.assertNotNull(userQosInfo.getTotalDownloadBw());
            Assertions.assertNotNull(userQosInfo.getIntranetDownloadBw());
            Assertions.assertNotNull(userQosInfo.getExtranetDownloadBw());
            Assertions.assertNotNull(userQosInfo.getTotalQps());
            Assertions.assertNotNull(userQosInfo.getIntranetQps());
            Assertions.assertNotNull(userQosInfo.getExtranetQps());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testBucketQosInfo() {
        try {
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            bucketQosInfo.setTotalUploadBw(-1);
            bucketQosInfo.setIntranetUploadBw(2);
            bucketQosInfo.setExtranetUploadBw(2);
            bucketQosInfo.setTotalDownloadBw(-1);
            bucketQosInfo.setIntranetDownloadBw(-1);
            bucketQosInfo.setExtranetDownloadBw(-1);
            bucketQosInfo.setTotalQps(-1);
            bucketQosInfo.setIntranetQps(-1);
            bucketQosInfo.setExtranetQps(-1);

            ossClient.setBucketQosInfo(bucketName, bucketQosInfo);

            BucketQosInfo result = ossClient.getBucketQosInfo(bucketName);
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            Assertions.assertEquals(result.getTotalUploadBw(), bucketQosInfo.getTotalUploadBw());
            Assertions.assertEquals(result.getIntranetUploadBw(), bucketQosInfo.getIntranetUploadBw());
            Assertions.assertEquals(result.getExtranetUploadBw(), bucketQosInfo.getExtranetUploadBw());
            Assertions.assertEquals(result.getTotalDownloadBw(), bucketQosInfo.getTotalDownloadBw());
            Assertions.assertEquals(result.getIntranetDownloadBw(), bucketQosInfo.getIntranetDownloadBw());
            Assertions.assertEquals(result.getExtranetDownloadBw(), bucketQosInfo.getExtranetDownloadBw());
            Assertions.assertEquals(result.getIntranetQps(), bucketQosInfo.getIntranetQps());
            Assertions.assertEquals(result.getExtranetQps(), bucketQosInfo.getExtranetQps());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucketQosInfo(bucketName);
        }
    }

    @Test
    public void testBucketQosInfoWithNoneArgs() {
        try {
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            ossClient.setBucketQosInfo(bucketName, bucketQosInfo);

            // Should be return default setting -1.
            BucketQosInfo result = ossClient.getBucketQosInfo(new GenericRequest(bucketName));
            Assertions.assertEquals(result.getRequestId().length(), REQUEST_ID_LEN);
            Assertions.assertEquals(result.getTotalUploadBw().intValue(), -1);
            Assertions.assertEquals(result.getIntranetUploadBw().intValue(), -1);
            Assertions.assertEquals(result.getExtranetUploadBw().intValue(), -1);
            Assertions.assertEquals(result.getTotalDownloadBw().intValue(), -1);
            Assertions.assertEquals(result.getIntranetDownloadBw().intValue(), -1);
            Assertions.assertEquals(result.getExtranetDownloadBw().intValue(), -1);
            Assertions.assertEquals(result.getIntranetQps().intValue(), -1);
            Assertions.assertEquals(result.getExtranetQps().intValue(), -1);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        } finally {
            ossClient.deleteBucketQosInfo(new GenericRequest(bucketName));
        }
    }

    @Test
    public void testPutBucketQosInfoWithIllegalArgs() {
        UserQosInfo userQosInfo = null;
        try {
            userQosInfo = ossClient.getUserQosInfo();
            Assertions.assertEquals(userQosInfo.getRequestId().length(), REQUEST_ID_LEN);
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }

        // BucketQosInfo totalUploadBw > UserQosInfo totalUploadBw, should be failed.
        try {
            Integer totalUploadBw = userQosInfo.getTotalUploadBw() + 1;
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            bucketQosInfo.setTotalUploadBw(totalUploadBw);
            SetBucketQosInfoRequest request = new SetBucketQosInfoRequest(bucketName);
            request.setBucketQosInfo(bucketQosInfo);
            ossClient.setBucketQosInfo(request);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }

        // intranetUploadBw > totalUploadBw, should be failed.
        try {
            Integer totalUploadBw = userQosInfo.getTotalUploadBw();
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            bucketQosInfo.setTotalUploadBw(totalUploadBw);
            bucketQosInfo.setIntranetUploadBw(totalUploadBw + 1);
            ossClient.setBucketQosInfo(bucketName, bucketQosInfo);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }

        // extranetUploadBw > totalUploadBw, should be failed.
        try {
            Integer totalUploadBw = userQosInfo.getTotalUploadBw();
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            bucketQosInfo.setTotalUploadBw(totalUploadBw);
            bucketQosInfo.setExtranetUploadBw(totalUploadBw + 1);
            ossClient.setBucketQosInfo(bucketName, bucketQosInfo);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }

        // BucketQosInfo totalDownloadBw > UserQosInfo totalDownloadBw, should be failed.
        try {
            Integer totalDownloadBw = userQosInfo.getTotalDownloadBw() + 1;
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            bucketQosInfo.setTotalUploadBw(totalDownloadBw);
            ossClient.setBucketQosInfo(bucketName, bucketQosInfo);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }

        // intranetDownloadBw > totalDownloadBw, should be failed.
        try {
            Integer totalDownloadBw = userQosInfo.getTotalDownloadBw();
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            bucketQosInfo.setTotalDownloadBw(totalDownloadBw);
            bucketQosInfo.setIntranetDownloadBw(totalDownloadBw + 1);
            ossClient.setBucketQosInfo(bucketName, bucketQosInfo);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }

        // extranetDownloadBw > totalDownloadBw, should be failed.
        try {
            Integer totalDownloadBw = userQosInfo.getTotalDownloadBw();
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            bucketQosInfo.setTotalDownloadBw(totalDownloadBw);
            bucketQosInfo.setExtranetDownloadBw(totalDownloadBw + 1);
            ossClient.setBucketQosInfo(bucketName, bucketQosInfo);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }

        // BucketQosInfo totalQps > UserQosInfo totalQps, should be failed.
        try {
            Integer totalQps = userQosInfo.getTotalQps() + 1;
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            bucketQosInfo.setTotalUploadBw(totalQps);
            ossClient.setBucketQosInfo(bucketName, bucketQosInfo);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }

        // intranetQps > totalQps, should be failed.
        try {
            Integer totalQps = userQosInfo.getTotalQps();
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            bucketQosInfo.setTotalQps(totalQps);
            bucketQosInfo.setIntranetQps(totalQps + 1);
            ossClient.setBucketQosInfo(bucketName, bucketQosInfo);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }

        // extranetQps > totalQps, should be failed.
        try {
            Integer totalQps = userQosInfo.getTotalQps();
            BucketQosInfo bucketQosInfo = new BucketQosInfo();
            bucketQosInfo.setTotalQps(totalQps);
            bucketQosInfo.setExtranetQps(totalQps + 1);
            ossClient.setBucketQosInfo(bucketName, bucketQosInfo);
        } catch (OSSException e) {
            Assertions.assertEquals(OSSErrorCode.INVALID_ARGUMENT, e.getErrorCode());
        }
    }

}