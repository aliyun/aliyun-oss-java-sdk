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

import junit.framework.Assert;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.CreateUdfApplicationRequest;
import com.aliyun.oss.model.CreateUdfRequest;
import com.aliyun.oss.model.GetUdfApplicationLogRequest;
import com.aliyun.oss.model.ResizeUdfApplicationRequest;
import com.aliyun.oss.model.UdfApplicationConfiguration;
import com.aliyun.oss.model.UdfApplicationInfo;
import com.aliyun.oss.model.UdfApplicationLog;
import com.aliyun.oss.model.UdfGenericRequest;
import com.aliyun.oss.model.UdfImageInfo;
import com.aliyun.oss.model.UdfInfo;
import com.aliyun.oss.model.UpgradeUdfApplicationRequest;
import com.aliyun.oss.model.UploadUdfImageRequest;

@Ignore
public class UdfTest extends TestBase {

	private static final String UDF_IMG_V1 = "D:\\work\\oss\\udf\\udf-go-pingpong.tar.gz";
	private static final String UDF_IMG_V2 = "D:\\work\\oss\\udf\\udf-go-pingpong-upgrade.tar.gz";

	@Test
	public void testUdf() {
		String udf = "udf-go-pingpong-1";
		String desc = "udf-go-pingpong-1";

		try {
			// create udf
			CreateUdfRequest createUdfRequest = new CreateUdfRequest(udf, desc);
			ossClient.createUdf(createUdfRequest);

			UdfGenericRequest genericRequest = new UdfGenericRequest(udf);
			UdfInfo ui = ossClient.getUdfInfo(genericRequest);
			System.out.println(ui);

			// list image info
			List<UdfImageInfo> udfImages = ossClient.getUdfImageInfo(genericRequest);
			for (UdfImageInfo image : udfImages) {
				System.out.println(image);
			}

			List<UdfInfo> udfs = ossClient.listUdfs();
			for (UdfInfo u : udfs) {
				System.out.println(u);
			}

			// delete udf
			ossClient.deleteUdf(genericRequest);

			udfs = ossClient.listUdfs();
			System.out.println("After delete:");
			for (UdfInfo u : udfs) {
				System.out.println(u);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void testUdfImage() {
		String udf = "udf-go-pingpong-1";
		String desc = "udf-go-pingpong-1";

		try {
			// create udf
			CreateUdfRequest createUdfRequest = new CreateUdfRequest(udf, desc);
			ossClient.createUdf(createUdfRequest);

			UdfGenericRequest genericRequest = new UdfGenericRequest(udf);
			UdfInfo ui = ossClient.getUdfInfo(genericRequest);
			System.out.println(ui);

			// upload image
			InputStream in = new FileInputStream(UDF_IMG_V1);
			UploadUdfImageRequest uploadUdfImageRequest = new UploadUdfImageRequest(udf, desc, in);
			ossClient.uploadUdfImage(uploadUdfImageRequest);

			in = new FileInputStream(UDF_IMG_V2);
			uploadUdfImageRequest = new UploadUdfImageRequest(udf, desc, in);
			ossClient.uploadUdfImage(uploadUdfImageRequest);

			List<UdfImageInfo> udfImages = ossClient.getUdfImageInfo(genericRequest);
			for (UdfImageInfo image : udfImages) {
				System.out.println(image);
			}

			// wait build completed
			for (UdfImageInfo image : udfImages) {
				if (image.getStatus().equals("building")) {
					TestUtils.waitForCacheExpiration(60);
					udfImages = ossClient.getUdfImageInfo(genericRequest);
					continue;
				}
			}

			// delete udf image
			ossClient.deleteUdfImage(genericRequest);

			// wait images deleted
			udfImages = ossClient.getUdfImageInfo(genericRequest);
			for (; udfImages.size() > 0;) {
				TestUtils.waitForCacheExpiration(60);
				udfImages = ossClient.getUdfImageInfo(genericRequest);
			}

			// delete image
			ossClient.deleteUdf(genericRequest);

		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void testUdfApplication() {
		String udf = "udf-go-pingpong-1";
		String desc = "udf-go-pingpong-1";

		try {
			// create udf
			CreateUdfRequest createUdfRequest = new CreateUdfRequest(udf, desc);
			ossClient.createUdf(createUdfRequest);

			UdfGenericRequest genericRequest = new UdfGenericRequest(udf);
			UdfInfo ui = ossClient.getUdfInfo(genericRequest);
			System.out.println(ui);

			// upload image
			InputStream in = new FileInputStream(UDF_IMG_V1);
			UploadUdfImageRequest uploadUdfImageRequest = new UploadUdfImageRequest(udf, desc, in);
			ossClient.uploadUdfImage(uploadUdfImageRequest);

			in = new FileInputStream(UDF_IMG_V2);
			uploadUdfImageRequest = new UploadUdfImageRequest(udf, desc, in);
			ossClient.uploadUdfImage(uploadUdfImageRequest);

			List<UdfImageInfo> udfImages = ossClient.getUdfImageInfo(genericRequest);
			for (UdfImageInfo image : udfImages) {
				System.out.println(image);
			}

			// wait build completed
			for (UdfImageInfo image : udfImages) {
				if (image.getStatus().equals("building")) {
					TestUtils.waitForCacheExpiration(60);
					udfImages = ossClient.getUdfImageInfo(genericRequest);
					continue;
				}
			}

			// list images
			udfImages = ossClient.getUdfImageInfo(genericRequest);
			for (UdfImageInfo image : udfImages) {
				System.out.println(image);
			}

			// list applications
			List<UdfApplicationInfo> appInfos = ossClient.listUdfApplications();
			for (UdfApplicationInfo app : appInfos) {
				System.out.println(app);
			}

			// create application
			UdfApplicationConfiguration configuration = new UdfApplicationConfiguration(1, 1);
			CreateUdfApplicationRequest createUdfApplicationRequest = new CreateUdfApplicationRequest(udf,
					configuration);
			ossClient.createUdfApplication(createUdfApplicationRequest);

			// wait application running
			UdfApplicationInfo appInfo = ossClient.getUdfApplicationInfo(genericRequest);
			System.out.println(appInfo);
			for (; appInfo.getStatus().equals("creating");) {
				TestUtils.waitForCacheExpiration(60);
				appInfo = ossClient.getUdfApplicationInfo(genericRequest);
			}
			System.out.println(appInfo);

			// upgrade application
			UpgradeUdfApplicationRequest UpgradeUdfApplicationRequest = new UpgradeUdfApplicationRequest(udf, 2);
			ossClient.upgradeUdfApplication(UpgradeUdfApplicationRequest);

			appInfo = ossClient.getUdfApplicationInfo(genericRequest);
			System.out.println(appInfo);
			for (; appInfo.getStatus().equals("upgrading");) {
				TestUtils.waitForCacheExpiration(60);
				appInfo = ossClient.getUdfApplicationInfo(genericRequest);
			}
			System.out.println(appInfo);

			// resize application
			ResizeUdfApplicationRequest resizeUdfApplicationRequest = new ResizeUdfApplicationRequest(udf, 2);
			ossClient.resizeUdfApplication(resizeUdfApplicationRequest);

			appInfo = ossClient.getUdfApplicationInfo(genericRequest);
			System.out.println(appInfo);
			for (; appInfo.getStatus().equals("resizing");) {
				TestUtils.waitForCacheExpiration(60);
				appInfo = ossClient.getUdfApplicationInfo(genericRequest);
			}
			System.out.println(appInfo);

			// get application log
			GetUdfApplicationLogRequest getUdfApplicationLogRequest = new GetUdfApplicationLogRequest(udf);
			getUdfApplicationLogRequest.setStartTime(DateUtil.parseRfc822Date("Wed, 15 Mar 2017 03:23:45 GMT"));
			getUdfApplicationLogRequest.setEndLines(100L);
			UdfApplicationLog udfApplicationLog = ossClient.getUdfApplicationLog(getUdfApplicationLogRequest);
			displayTextInputStream(udfApplicationLog.getLogContent());

			// // delete application
			// ossClient.deleteUdfApplication(genericRequest);
			//
			// // wait application deleted
			// appInfos = ossClient.listUdfApplications();
			// for (; appInfos.size() > 0 ; ) {
			// TestUtils.waitForCacheExpiration(60);
			// appInfos = ossClient.listUdfApplications();
			// }
			//
			// // delete udf image
			// ossClient.deleteUdfImage(genericRequest);
			//
			// // wait images deleted
			// udfImages = ossClient.getUdfImageInfo(genericRequest);
			// for (; udfImages.size() > 0;) {
			// TestUtils.waitForCacheExpiration(60);
			// udfImages = ossClient.getUdfImageInfo(genericRequest);
			// }
			//
			// // delete image
			// ossClient.deleteUdf(genericRequest);

		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}

	@Test
	public void testUdfApplicationLog() {
		String udf = "udf-go-pingpong-1";

		try {
			GetUdfApplicationLogRequest getUdfApplicationLogRequest = new GetUdfApplicationLogRequest(udf);
			getUdfApplicationLogRequest.setStartTime(DateUtil.parseRfc822Date("Wed, 15 Mar 2017 03:23:45 GMT"));
			getUdfApplicationLogRequest.setEndLines(10L);
			UdfApplicationLog udfApplicationLog = ossClient.getUdfApplicationLog(getUdfApplicationLogRequest);
			displayTextInputStream(udfApplicationLog.getLogContent());
		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}
	}

	private static void displayTextInputStream(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		int lines = 0;
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;

			lines++;
			System.out.println("    " + line);
		}
		System.out.println("Lines:" + lines);

		reader.close();
	}

}
