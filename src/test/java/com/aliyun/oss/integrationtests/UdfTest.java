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

import com.aliyun.oss.model.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.aliyun.oss.common.utils.DateUtil;


public class UdfTest extends TestBase {

    @Test
    public void testUdf() {
        String udf = "udf-go-pingpong-1";
        String desc = "udf-go-pingpong-1";

        try {
            // create udf
            CreateUdfRequest createUdfRequest = new CreateUdfRequest(udf);
            createUdfRequest = new CreateUdfRequest(udf, "", desc);
            Assertions.assertEquals(createUdfRequest.getId(),"");
            createUdfRequest = new CreateUdfRequest(udf, desc);
            createUdfRequest.setDesc("desc");
            createUdfRequest.setId("id");
            Assertions.assertEquals(createUdfRequest.getDesc(),"desc");
            Assertions.assertEquals(createUdfRequest.getId(),"id");
            ossClient.createUdf(createUdfRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            // get udf
            UdfGenericRequest genericRequest = new UdfGenericRequest(udf);
            genericRequest = new UdfGenericRequest();
            genericRequest.setName("name");
            Assertions.assertEquals(genericRequest.getName(), "name");
            UdfInfo ui = ossClient.getUdfInfo(genericRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            Date date = DateUtil.parseRfc822Date("Wed, 15 Mar 2017 03:23:45 GMT");
            UdfImageInfo imageInfo = new UdfImageInfo(1, "status", "desc",
                    "region", date);
            imageInfo.setVersion(2);
            imageInfo.setStatus("new status");
            imageInfo.setDesc("new desc");
            imageInfo.setCanonicalRegion("new region");
            imageInfo.setCreationDate(date);
            Assertions.assertEquals(imageInfo.getVersion(), new Integer(2));
            Assertions.assertEquals(imageInfo.getStatus(), "new status");
            Assertions.assertEquals(imageInfo.getDesc(), "new desc");
            Assertions.assertEquals(imageInfo.getCanonicalRegion(), "new region");
            Assertions.assertEquals(imageInfo.getCreationDate(), date);
            String dump = imageInfo.toString();

            // list image info
            UdfGenericRequest genericRequest = new UdfGenericRequest(udf);
            List<UdfImageInfo> udfImages = ossClient.getUdfImageInfo(genericRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            Date date = DateUtil.parseRfc822Date("Wed, 15 Mar 2017 03:23:45 GMT");
            UdfInfo info = new UdfInfo("name", "owner", "id",
                    "desc", CannedUdfAcl.Private, date);
            info.setName("new name");
            info.setOwner("new owner");
            info.setId("new id");
            info.setDesc("new desc");
            info.setAcl(CannedUdfAcl.parse("public"));
            info.setCreationDate(date);
            Assertions.assertEquals(info.getName(), "new name");
            Assertions.assertEquals(info.getOwner(), "new owner");
            Assertions.assertEquals(info.getId(), "new id");
            Assertions.assertEquals(info.getDesc(), "new desc");
            Assertions.assertEquals(info.getCreationDate(), date);
            Assertions.assertEquals(info.getAcl(), CannedUdfAcl.Public);
            String dump = info.toString();
            // list image info
            List<UdfInfo> udfs = ossClient.listUdfs();
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            UdfGenericRequest genericRequest = new UdfGenericRequest(udf);
            ossClient.deleteUdf(genericRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            CannedUdfAcl.parse("UN");
            Assertions.assertTrue(false);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    @Test
    public void testUdfImage() {
        String udf = "udf-go-pingpong-1";
        String desc = "udf-go-pingpong-1";

        try {
            // upload image
            String content = "Hello OSS";
            InputStream input = new ByteArrayInputStream(content.getBytes());
            UploadUdfImageRequest uploadUdfImageRequest = new UploadUdfImageRequest(udf, null);
            uploadUdfImageRequest = new UploadUdfImageRequest(udf, desc,null);
            uploadUdfImageRequest = new UploadUdfImageRequest(udf, desc,null);
            uploadUdfImageRequest.setUdfImage(input);
            uploadUdfImageRequest.setUdfImageDesc("desc");
            Assertions.assertEquals(uploadUdfImageRequest.getUdfImageDesc(),"desc");
            Assertions.assertEquals(uploadUdfImageRequest.getUdfImage(),input);
            ossClient.uploadUdfImage(uploadUdfImageRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            // upload image
            UdfGenericRequest genericRequest = new UdfGenericRequest(udf);
            ossClient.deleteUdfImage(genericRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }
    
    @Test
    public void testUdfApplication() {
        String udf = "udf-go-pingpong-1";
        String desc = "udf-go-pingpong-1";

        try {
            // list applications
            List<UdfApplicationInfo> appInfos = ossClient.listUdfApplications();
            for (UdfApplicationInfo app : appInfos) {
                System.out.println(app);
            }
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            // create application
            InstanceFlavor flavor = new InstanceFlavor("ecs.n1.middle");
            Assertions.assertEquals(flavor.getInstanceType(), "ecs.n1.middle");
            Assertions.assertEquals(flavor.toString(), "InstanceFlavor [instanceType=ecs.n1.middle]");
            UdfApplicationConfiguration configuration = new UdfApplicationConfiguration(1, 1,flavor);
            configuration = new UdfApplicationConfiguration(1, 1);
            configuration.setImageVersion(2);
            configuration.setInstanceNum(2);
            InstanceFlavor flavor2 = new InstanceFlavor("ecs.n1.big");
            configuration.setFlavor(flavor2);
            Assertions.assertEquals(configuration.getImageVersion(),new Integer(2));
            Assertions.assertEquals(configuration.getInstanceNum(),new Integer(2));
            Assertions.assertEquals(configuration.getFlavor(),flavor2);
            CreateUdfApplicationRequest createUdfApplicationRequest = new CreateUdfApplicationRequest(udf, configuration);
            configuration = createUdfApplicationRequest.getUdfApplicationConfiguration();
            createUdfApplicationRequest.setUdfApplicationConfiguration(configuration);
            ossClient.createUdfApplication(createUdfApplicationRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            //
            Date startTime = DateUtil.parseRfc822Date("Wed, 15 Mar 2017 03:23:45 GMT");
            InstanceFlavor flavor = new InstanceFlavor("ecs.n1.middle");
            UdfApplicationInfo appInfo = new UdfApplicationInfo("name", "id", "region",
                    "status", 1, 2, startTime, flavor);
            appInfo.setName("new name");
            appInfo.setId("new id");
            appInfo.setRegion("new region");
            appInfo.setStatus("new status");
            appInfo.setImageVersion(2);
            appInfo.setInstanceNum(3);
            appInfo.setFlavor(flavor);
            appInfo.setCreationDate(startTime);
            Assertions.assertEquals(appInfo.getName(),"new name");
            Assertions.assertEquals(appInfo.getId(),"new id");
            Assertions.assertEquals(appInfo.getRegion(),"new region");
            Assertions.assertEquals(appInfo.getStatus(),"new status");
            Assertions.assertEquals(appInfo.getImageVersion(),new Integer(2));
            Assertions.assertEquals(appInfo.getInstanceNum(),new Integer(3));
            Assertions.assertEquals(appInfo.getFlavor(),flavor);
            Assertions.assertEquals(appInfo.getCreationDate(),startTime);
            String dump = appInfo.toString();
            // get application info
            UdfGenericRequest genericRequest = new UdfGenericRequest(udf);
            appInfo = ossClient.getUdfApplicationInfo(genericRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            // upgrade application
            UpgradeUdfApplicationRequest upgradeUdfApplicationRequest = new UpgradeUdfApplicationRequest(udf, 2);
            upgradeUdfApplicationRequest.setImageVersion(3);
            Assertions.assertEquals(upgradeUdfApplicationRequest.getImageVersion(),new Integer(3));
            ossClient.upgradeUdfApplication(upgradeUdfApplicationRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            // resize application
            ResizeUdfApplicationRequest resizeUdfApplicationRequest = new ResizeUdfApplicationRequest(udf, 2);
            resizeUdfApplicationRequest.setInstanceNum(3);
            Assertions.assertEquals(resizeUdfApplicationRequest.getInstanceNum(),new Integer(3));
            ossClient.resizeUdfApplication(resizeUdfApplicationRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            // get application log
            GetUdfApplicationLogRequest getUdfApplicationLogRequest = new GetUdfApplicationLogRequest(udf,
                    DateUtil.parseRfc822Date("Wed, 15 Mar 2017 02:23:45 GMT"), 200L);
            Assertions.assertEquals(getUdfApplicationLogRequest.getEndLines(),new Long(200L));
            getUdfApplicationLogRequest = new GetUdfApplicationLogRequest(udf, 100L);
            getUdfApplicationLogRequest = new GetUdfApplicationLogRequest(udf);
            Date startTime = DateUtil.parseRfc822Date("Wed, 15 Mar 2017 03:23:45 GMT");
            getUdfApplicationLogRequest.setStartTime(startTime);
            getUdfApplicationLogRequest.setEndLines(100L);
            Assertions.assertEquals(getUdfApplicationLogRequest.getEndLines(),new Long(100L));
            Assertions.assertEquals(getUdfApplicationLogRequest.getStartTime(),startTime);

            UdfApplicationLog udfApplicationLog = new UdfApplicationLog();
            udfApplicationLog.setUdfName("name");
            udfApplicationLog.setLogContent(null);
            Assertions.assertEquals(udfApplicationLog.getUdfName(),"name");
            Assertions.assertEquals(udfApplicationLog.getLogContent(), null);
            udfApplicationLog.close();
            udfApplicationLog = new UdfApplicationLog("name", new ByteArrayInputStream("".getBytes()));
            udfApplicationLog.close();
            udfApplicationLog = new UdfApplicationLog("name");
            udfApplicationLog = ossClient.getUdfApplicationLog(getUdfApplicationLogRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

        try {
            // delete application
            UdfGenericRequest genericRequest = new UdfGenericRequest(udf);
            ossClient.deleteUdfApplication(genericRequest);
            Assertions.fail("Udf API is removed.");
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    private static void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        int lines = 0;
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            
            lines++;
            System.out.println("    " + line);
        }
        System.out.println("Lines:" + lines);
        
        reader.close();
    }
}
