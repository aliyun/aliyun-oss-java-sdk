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

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.DateUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import junit.framework.Assert;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class PostPolicyTest extends TestBase {

    @Test
    public void testGenPostPolicy() {
        final String bucketName = "gen-post-policy";
        OSSClient client = null;

        try {
            client = new OSSClient(TestConfig.OSS_TEST_ENDPOINT, "AAAAAAAAAAAAAAAA", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
            Date expiration = DateUtil.parseIso8601Date("2020-03-19T03:44:06.476Z");
            
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem("bucket", bucketName);
            // $ must be escaped with backslash.
            policyConds.addConditionItem(MatchMode.Exact, PolicyConditions.COND_KEY, "user/eric/\\${filename}");
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, "user/eric");
            policyConds.addConditionItem(MatchMode.StartWith, "x-oss-meta-tag", "dummy_etag");
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 1, 1024);

            String actualPostPolicy = client.generatePostPolicy(expiration, policyConds);

            String expectedPostPolicy = String.format("{\"expiration\":\"2020-03-19T03:44:06.476Z\",\"conditions\":[{\"bucket\":\"%s\"},"
                    + "[\"eq\",\"$key\",\"user\\/eric\\/\\\\${filename}\"],[\"starts-with\",\"$key\",\"user\\/eric\"],[\"starts-with\",\"$x-oss-meta-tag\","
                    + "\"dummy_etag\"],[\"content-length-range\",1,1024]]}", bucketName);
            Assert.assertEquals(expectedPostPolicy, actualPostPolicy);
            
            byte[] binaryData = actualPostPolicy.getBytes("utf-8");
            String actualEncodedPolicy = BinaryUtil.toBase64String(binaryData);
            String expectedEncodedPolicy = "eyJleHBpcmF0aW9uIjoiMjAyMC0wMy0xOVQwMzo0NDowNi40NzZaIiwiY29uZGl0aW9ucyI6W3s" +
                    "iYnVja2V0IjoiZ2VuLXBvc3QtcG9saWN5In0sWyJlcSIsIiRrZXkiLCJ1c2VyXC9lcmljXC9cXCR7ZmlsZW5hbWV9Il0sWyJzdG" +
                    "FydHMtd2l0aCIsIiRrZXkiLCJ1c2VyXC9lcmljIl0sWyJzdGFydHMtd2l0aCIsIiR4LW9zcy1tZXRhLXRhZyIsImR1bW15X2V0Y" +
                    "WciXSxbImNvbnRlbnQtbGVuZ3RoLXJhbmdlIiwxLDEwMjRdXX0=";
            Assert.assertEquals(expectedEncodedPolicy, actualEncodedPolicy);
            
            String actualPostSignature = client.calculatePostSignature(actualPostPolicy);

            // It has something to do with the local time
            Assert.assertTrue(actualPostSignature.equals("9fs2TRS2W2k5hV8mLsvqZBnlC2M="));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    @Test
    public void testjsonizeError() {
        PolicyConditions conditions = new PolicyConditions();

        try {
            conditions.jsonize();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            conditions.addConditionItem(MatchMode.Exact, "bucket", "bucketName");
            conditions.jsonize();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            conditions.addConditionItem(MatchMode.Unknown, "bucket", "bucketName");
            conditions.jsonize();
            Assert.fail("MatchMode.Unknown, should be failed.");
        } catch (IllegalArgumentException e) {
            // expected exception.
        }
    }


    @Test
    public void testGenPostPolicyWithInAndNotIn() {
        final String bucketName = "gen-post-policy";
        OSSClient client = null;

        try {
            client = new OSSClient(TestConfig.OSS_TEST_ENDPOINT, "AAAAAAAAAAAAAAAA", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
            Date expiration = DateUtil.parseIso8601Date("2020-03-19T03:44:06.476Z");

            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem("bucket", bucketName);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, "user/eric");
            policyConds.addConditionItem(MatchMode.StartWith, "x-oss-meta-tag", "dummy_etag");
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 1, 1024);
            policyConds.addConditionItem(MatchMode.In, PolicyConditions.COND_CONTENT_TYPE, new String[]{"image/jpg","image/png"});
            policyConds.addConditionItem(MatchMode.NotIn, PolicyConditions.COND_CACHE_CONTROL, new String[]{"no-cache"});

            String actualPostPolicy = client.generatePostPolicy(expiration, policyConds);
            String expectedPostPolicy = String.format("{\"expiration\":\"2020-03-19T03:44:06.476Z\",\"conditions\":[{\"bucket\":\"%s\"},"
                    + "[\"starts-with\",\"$key\",\"user\\/eric\"],[\"starts-with\",\"$x-oss-meta-tag\","
                    + "\"dummy_etag\"],[\"content-length-range\",1,1024],[\"in\",\"$content-type\",[\"image\\/jpg\",\"image\\/png\"]],[\"not-in\",\"$cache-control\",[\"no-cache\"]]]}", bucketName);
            Assert.assertEquals(expectedPostPolicy, actualPostPolicy);

            byte[] binaryData = actualPostPolicy.getBytes("utf-8");
            String actualEncodedPolicy = BinaryUtil.toBase64String(binaryData);
            String expectedEncodedPolicy = "eyJleHBpcmF0aW9uIjoiMjAyMC0wMy0xOVQwMzo0NDowNi40NzZaIiwiY29uZGl0aW9ucyI6W3si" +
                    "YnVja2V0IjoiZ2VuLXBvc3QtcG9saWN5In0sWyJzdGFydHMtd2l0aCIsIiRrZXkiLCJ1c2VyXC9lcmljIl0sWyJzdGFydHMtd2l" +
                    "0aCIsIiR4LW9zcy1tZXRhLXRhZyIsImR1bW15X2V0YWciXSxbImNvbnRlbnQtbGVuZ3RoLXJhbmdlIiwxLDEwMjRdLFsiaW4iL" +
                    "CIkY29udGVudC10eXBlIixbImltYWdlXC9qcGciLCJpbWFnZVwvcG5nIl1dLFsibm90LWluIiwiJGNhY2hlLWNvbnRyb2wiLFs" +
                    "ibm8tY2FjaGUiXV1dfQ==";
            Assert.assertEquals(expectedEncodedPolicy, actualEncodedPolicy);

            String actualPostSignature = client.calculatePostSignature(actualPostPolicy);

            Assert.assertTrue(actualPostSignature.equals("/yrk/7alTmdIh9a3YOkkIcKYlbA="));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    @Test
    public void testGenPostPolicyEscape() {
        final String bucketName = "gen-post-policy";
        OSSClient client = null;

        try {
            client = new OSSClient(TestConfig.OSS_TEST_ENDPOINT, "AAAAAAAAAAAAAAAA", "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
            Date expiration = DateUtil.parseIso8601Date("2020-03-19T03:44:06.476Z");

            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem("bucket", bucketName);
            policyConds.addConditionItem("fileName", "1.png\"},{\"key\":\"2.png\"}]}//");
            policyConds.addConditionItem(MatchMode.Exact, PolicyConditions.COND_KEY, "user/eric/\\${filename}//");
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, "user/eric\\//\"");
            policyConds.addConditionItem(MatchMode.StartWith, "x-oss-meta-tag", "dummy_etag");
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 1, 1024);
            policyConds.addConditionItem(MatchMode.NotIn, PolicyConditions.COND_CACHE_CONTROL, new String[]{"1.png\"},{\"key\":\"2.png\"}]}//", "2.png\"},{\"key\":\"3.png\"}]}//", "cache"});
            policyConds.addConditionItem(MatchMode.In, PolicyConditions.COND_CONTENT_TYPE, new String[]{"image/jpg\\//","image/png"});
            String actualPostPolicy = client.generatePostPolicy(expiration, policyConds);
            //System.out.println(actualPostPolicy);

            JSONObject jobject = new JSONObject(actualPostPolicy);

            // expiration
            Assert.assertEquals("2020-03-19T03:44:06.476Z", (String)jobject.get("expiration"));

            // conditions
            JSONArray jarray = (JSONArray) jobject.get("conditions");

            // conditions->bucket
            JSONObject jitem = (JSONObject)jarray.get(0);
            Assert.assertEquals(bucketName, (String)jitem.getString("bucket"));

            // conditions->fileName
            jitem = (JSONObject)jarray.get(1);
            Assert.assertEquals("1.png\"},{\"key\":\"2.png\"}]}//", (String)jitem.getString("fileName"));

            // conditions->exact
            JSONArray jatiem  = (JSONArray)jarray.get(2);
            Assert.assertEquals("user/eric/\\${filename}//", (String)jatiem.get(2));

            // conditions->start-witch
            jatiem  = (JSONArray)jarray.get(3);
            Assert.assertEquals("user/eric\\//\"", (String)jatiem.get(2));

            // conditions->not-in cache-control
            jatiem  = (JSONArray)jarray.get(6);
            jatiem = (JSONArray) jatiem.get(2);
            Assert.assertEquals("1.png\"},{\"key\":\"2.png\"}]}//", jatiem.getString(0));
            Assert.assertEquals("2.png\"},{\"key\":\"3.png\"}]}//", jatiem.getString(1));
            Assert.assertEquals("cache", jatiem.getString(2));

            // conditions->in cache-control
            jatiem  = (JSONArray)jarray.get(7);
            jatiem = (JSONArray) jatiem.get(2);
            Assert.assertEquals("image/jpg\\//", jatiem.getString(0));
            Assert.assertEquals("image/png", jatiem.getString(1));

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    @Test
    public void testUsePostPolicyToUploadData() {
        OSSClient client = null;
        client = new OSSClient(TestConfig.OSS_TEST_ENDPOINT, TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);

        String actualPostPolicy = "";
        String encodePolicy = "";
        String signature = "";

        String fileName = "1.png\"},{\"key\":\"2.png\"}]}//";
        String key = "4.png\"},{\"key\":\"5.png\"}]}//";

        // build policy
        try {
            Date expiration = new Date(new Date().getTime() + 3600 * 1000);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem("bucket", bucketName);
            policyConds.addConditionItem("fileName", fileName);
            policyConds.addConditionItem(MatchMode.Exact, PolicyConditions.COND_KEY, key);
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 1, 1024);
            policyConds.addConditionItem(MatchMode.In, PolicyConditions.COND_CONTENT_TYPE, new String[]{"image/jpg", "image/png"});
            actualPostPolicy = client.generatePostPolicy(expiration, policyConds);
            encodePolicy = new String(Base64.encodeBase64(actualPostPolicy.getBytes()));
            signature = com.aliyun.oss.common.auth.ServiceSignature.create().computeSignature(TestConfig.OSS_TEST_ACCESS_KEY_SECRET, encodePolicy);

        }catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        //upload file
        String endpoint = TestConfig.OSS_TEST_ENDPOINT;
        if (endpoint.startsWith("http://")) {
            endpoint = endpoint.substring(7);
        } else if (endpoint.startsWith("https://")){
            endpoint = endpoint.substring(8);
        }
        String urlStr = "http://" + bucketName + "." + endpoint;

        //upload ok
        try {
            // 设置表单Map。
            Map<String, String> formFields = new LinkedHashMap<String, String>();
            // 设置文件名称。
            formFields.put("key", key);
            formFields.put("fileName", fileName);
            formFields.put("OSSAccessKeyId", TestConfig.OSS_TEST_ACCESS_KEY_ID);
            formFields.put("policy", encodePolicy);
            formFields.put("Signature", signature);
            formUpload(urlStr, formFields, "test.jpg", new ByteArrayInputStream("1234".getBytes()), "image/jpg");
            Assert.assertTrue(true);
        } catch (Exception e) {
            // should not be here
            Assert.fail(e.getMessage());
        }

        // upload with invalid key,will be fail
        try {
            // 设置表单Map。
            Map<String, String> formFields = new LinkedHashMap<String, String>();
            // 设置文件名称。
            formFields.put("key", "2.png");
            formFields.put("fileName", "1.png");
            formFields.put("OSSAccessKeyId", TestConfig.OSS_TEST_ACCESS_KEY_ID);
            formFields.put("policy", encodePolicy);
            formFields.put("Signature", signature);
            formUpload(urlStr, formFields, "test.jpg", new ByteArrayInputStream("1234".getBytes()), "image/jpg");

            // should not be here
            Assert.fail("should not be here");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }


        if (client != null) {
            client.shutdown();
        }
    }

    private static String formUpload(String urlStr, Map<String, String> formFields, String filename, InputStream input, String contentType)
            throws Exception {
        String res = "";
        HttpURLConnection conn = null;
        String boundary = "9431149156168";
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            // 设置MD5值。MD5值由整个Body计算得出。如果希望开启MD5校验，可参考MD5加密设置。
            // conn.setRequestProperty("Content-MD5", contentMD5);
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 遍历读取表单Map中的数据，将数据写入到输出流中。
            if (formFields != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator<Map.Entry<String, String>> iter = formFields.entrySet().iterator();
                int i = 0;
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String inputName = entry.getKey();
                    String inputValue = entry.getValue();
                    if (inputValue == null) {
                        continue;
                    }
                    if (i == 0) {
                        strBuf.append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    } else {
                        strBuf.append("\r\n").append("--").append(boundary).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\""
                                + inputName + "\"\r\n\r\n");
                        strBuf.append(inputValue);
                    }
                    i++;
                }
                out.write(strBuf.toString().getBytes());
            }
            // 将要上传的文件写入到输出流中。
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(boundary)
                    .append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"file\"; "
                    + "filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type: " + contentType + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            DataInputStream in = new DataInputStream(input);
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();
            byte[] endData = ("\r\n--" + boundary + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();
            // 读取返回数据。
            strBuf = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }
            res = strBuf.toString();
            reader.close();
            reader = null;
        } catch (ClientException e) {
            System.err.println("Send post request exception: " + e);
            System.err.println(e.getErrorCode()+" msg="+e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
        return res;
    }
}
