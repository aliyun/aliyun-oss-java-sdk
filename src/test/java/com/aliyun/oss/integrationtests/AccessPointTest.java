package com.aliyun.oss.integrationtests;

import com.aliyun.oss.*;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import static com.aliyun.oss.integrationtests.TestUtils.genFixedLengthInputStream;

@Ignore
public class AccessPointTest extends TestBase {
    private static OSS ossClient;
    private static String bucketName = genBucketName();
    private static OSS apClientTest;
    private static String arnEndpoint = "http://oss-ap-southeast-2.oss-accesspoint.aliyuncs.com";
    private static String apArn = "";
    private static String alias = "";
    private static String objectName = "test-zxl-object.txt";
    private static String fileUploadName = "test-zxl-object.txt";
    private static String csvObjectName = "test-zxl-csv-object.txt";
    private static String jsonObjectName = "test-zxl-json-object.txt";
    private static String appendObjectName = "test-zxl-append-object.txt";
    private static String multipartObjectName = "test-zxl- multipart-object.txt";
    private static String copyObjectName = "test-zxl-copy-object.txt";
    private static String copyDestObjectName = "test-zxl-copy-dest-object.txt";
    private static String copyMultipartObjectName = "test-zxl-copy-multipart-object.txt";
    private static String postObjectName = "test-zxl-post-object.txt";
    private static int postObjectSize = 500 * 1024;
    private static int streamSize = 128 * 1024; // 128kb
    private static long partSize = 100 * 1024;
    private static String symLink = "test-zxl-sym-link";
    private static String content = "Hello OSS";
    private static String content2 = ",Hello World";
    private static String jsonContent = "{\n\t\"name\": \"Lora Francis\",\n\t\"age\": 27,\n\t\"company\": \"Staples Inc\"\n}\n{\n\t\"name\": \"Eleanor Little\",\n\t\"age\": 43,\n\t\"company\": \"Conectiv, Inc\"\n}\n{\n\t\"name\": \"Rosie Hughes\",\n\t\"age\": 44,\n\t\"company\": \"Western Gas Resources Inc\"\n}\n{\n\t\"name\": \"Lawrence Ross\",\n\t\"age\": 24,\n\t\"company\": \"MetLife Inc.\"\n}";
    private static String jsonResult = "{\t\"name\":\"Eleanor Little\",\n\t\"age\":43,\n\t\"company\":\"Conectiv, Inc\"}\n{\t\"name\":\"Rosie Hughes\",\n\t\"age\":44,\n\t\"company\":\"Western Gas Resources Inc\"}\n";
    private static String csvContent = "name,school,company,age\nLora Francis,School,Staples Inc,27\n#Lora Francis,School,Staples Inc,27\nEleanor Little,School,\"Conectiv, Inc\",43\nRosie Hughes,School,Western Gas Resources Inc,44\nLawrence Ross,School,MetLife Inc.,24\n";
    private static String accessPointName = "test-ap-zxl-jt-01-3";
    private static String networkOrigin = "internet";
    private static String vpcId = "vpc-test-zxl-3";
    private static String networkOrigin2 = "Internet";
    private static String accessPointPolicy = "{\"Version\":\"1\",\"Statement\":[{\"Action\":[\"oss:PutObject\",\"oss:GetObject\"],\"Effect\":\"Deny\",\"Principal\":[\""+ TestConfig.OSS_TEST_USER_ID+"\"],\"Resource\":[\"acs:oss:"+ TestConfig.OSS_TEST_REGION+":"+ TestConfig.OSS_TEST_USER_ID+":accesspoint/"+accessPointName+"\",\"acs:oss:"+ TestConfig.OSS_TEST_REGION+":"+ TestConfig.OSS_TEST_USER_ID+":accesspoint/"+accessPointName+"/object/*\"]}]}";


    @BeforeClass
    public static void init() throws InterruptedException {

        ossClient = new OSSClientBuilder().build(TestConfig.OSS_TEST_ENDPOINT, TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);

        if(!ossClient.doesBucketExist(bucketName)){
            ossClient.createBucket(bucketName);
        }

        CreateAccessPointRequest createAccessPointRequest = new CreateAccessPointRequest(bucketName)
                .withAccessPointName(accessPointName)
                .withNetworkOrigin(networkOrigin)
                .withVpc(new AccessPointVpcConfiguration());
        CreateAccessPointResult createReturn = ossClient.createAccessPoint(createAccessPointRequest);
        Assert.assertEquals(200, createReturn.getResponse().getStatusCode());


        // getAccessPoint
        GetAccessPointRequest getAccessPointRequest = new GetAccessPointRequest(bucketName).withAccessPointName(accessPointName);
        GetAccessPointResult getReturn = ossClient.getAccessPoint(getAccessPointRequest);

        long startTime = System.currentTimeMillis();
        do {
            long endTime = System.currentTimeMillis();
            if(endTime - startTime > 1000 * 60 * 30){
                break;
            }

            Thread.sleep(5 * 1000);
            getReturn = ossClient.getAccessPoint(getAccessPointRequest);
        } while (!"enable".equals(getReturn.getStatus()));

        apArn = getReturn.getAccessPointArn();
        alias = getReturn.getAlias();
        System.out.println(apArn);

        apClientTest = new OSSClientBuilder().build(arnEndpoint, TestConfig.OSS_TEST_ACCESS_KEY_ID, TestConfig.OSS_TEST_ACCESS_KEY_SECRET);
    }


    @AfterClass
    public static void after() throws InterruptedException {
        try{
            ListAccessPointsRequest listAccessPointsRequest = new ListAccessPointsRequest();
            ListAccessPointsResult listReturn = ossClient.listAccessPoints(listAccessPointsRequest);

            long startTime = System.currentTimeMillis();
            boolean flag = true;
            do {
                int count = 0;
                for(AccessPoint ap : listReturn.getAccessPoints()){
                    if(ap.getAccessPointName().startsWith("test-ap-zxl")){
                        count++;
                        if("creating".equals(ap.getStatus())){
                            flag = true;
                        } else if("enable".equals(ap.getStatus())){
                            DeleteAccessPointRequest deleteAccessPointRequest = new DeleteAccessPointRequest(bucketName).withAccessPointName(ap.getAccessPointName());
                            VoidResult delReturn = ossClient.deleteAccessPoint(deleteAccessPointRequest);
                            Assert.assertEquals(204, delReturn.getResponse().getStatusCode());
                        }
                    }
                }
                if(count == 0){
                    break;
                }

                long endTime = System.currentTimeMillis();
                if(endTime - startTime > 1000 * 60 * 30){
                    break;
                }

                Thread.sleep(60 * 1000);
                listReturn = ossClient.listAccessPoints(listAccessPointsRequest);
            } while (flag);
        } catch (OSSException e) {
            e.printStackTrace();
            Assert.fail("ErrorCode:" + e.getErrorCode() + "Message:" +e.getMessage());
        } finally {
            ossClient.deleteBucket(bucketName);

            if(apClientTest != null){
                apClientTest.shutdown();
            }
            if(ossClient != null){
                ossClient.shutdown();
            }
        }
    }

    @Test
    public void testAccessPoint() {

        try {

            GetAccessPointRequest getAccessPointRequest = new GetAccessPointRequest(bucketName).withAccessPointName(accessPointName);
            GetAccessPointResult getReturn = ossClient.getAccessPoint(getAccessPointRequest);
            Assert.assertEquals(200, getReturn.getResponse().getStatusCode());
            Assert.assertEquals(accessPointName, getReturn.getAccessPointName());
            Assert.assertEquals(networkOrigin, getReturn.getNetworkOrigin());
            Assert.assertNotNull(getReturn.getBucket());
            Assert.assertNotNull(getReturn.getAccountId());
            Assert.assertNotNull(getReturn.getAccessPointArn());
            Assert.assertNotNull(getReturn.getCreationDate());
            Assert.assertNotNull(getReturn.getAlias());
            Assert.assertNotNull(getReturn.getStatus());
            Assert.assertNotNull(getReturn.getEndpoints().getPublicEndpoint());
            Assert.assertNotNull(getReturn.getEndpoints().getInternalEndpoint());


            ListBucketAccessPointsRequest listBucketAccessPointsRequest = new ListBucketAccessPointsRequest(bucketName)
                    .withMaxKeys(10);
            ListAccessPointsResult listReturn = ossClient.listBucketAccessPoints(listBucketAccessPointsRequest);
            Assert.assertEquals(200, listReturn.getResponse().getStatusCode());

        } catch (OSSException e) {
            e.printStackTrace();
            Assert.fail("ErrorCode:" + e.getErrorCode() + "Message:" +e.getMessage());
        }
    }

    @Test
    public void testAccessPointWithList() {
        String accessPointName = "test-ap-zxl-list-01-3";
        String accessPointName2 = "test-ap-zxl-list-02-3";
        try {
            CreateAccessPointRequest createAccessPointRequest = new CreateAccessPointRequest(bucketName)
                    .withAccessPointName(accessPointName)
                    .withNetworkOrigin(networkOrigin)
                    .withVpc(new AccessPointVpcConfiguration());
            CreateAccessPointResult createReturn = ossClient.createAccessPoint(createAccessPointRequest);
            Assert.assertEquals(200, createReturn.getResponse().getStatusCode());


            CreateAccessPointRequest createAccessPointRequest2 = new CreateAccessPointRequest(bucketName)
                    .withAccessPointName(accessPointName2)
                    .withNetworkOrigin(networkOrigin2)
                    .withVpc(new AccessPointVpcConfiguration());

            CreateAccessPointResult createReturn2 = ossClient.createAccessPoint(createAccessPointRequest2);
            Assert.assertEquals(200, createReturn2.getResponse().getStatusCode());

            ListAccessPointsRequest listAccessPointsRequest = new ListAccessPointsRequest()
                    .withMaxKeys(10);
            ListAccessPointsResult listReturn = ossClient.listAccessPoints(listAccessPointsRequest);
            Assert.assertEquals(200, listReturn.getResponse().getStatusCode());

            ListAccessPointsRequest listAccessPointsRequest2 = new ListAccessPointsRequest()
                    .withMaxKeys(1);
            ListAccessPointsResult listReturn2 = ossClient.listAccessPoints(listAccessPointsRequest2);
            Assert.assertEquals(200, listReturn2.getResponse().getStatusCode());

            ListAccessPointsRequest listAccessPointsRequest3 = new ListAccessPointsRequest()
                    .withContinuationToken(listReturn2.getNextContinuationToken());
            ListAccessPointsResult listReturn3 = ossClient.listAccessPoints(listAccessPointsRequest3);
            Assert.assertEquals(200, listReturn3.getResponse().getStatusCode());

        } catch (OSSException e) {
            e.printStackTrace();
            Assert.fail("ErrorCode:" + e.getErrorCode() + "Message:" +e.getMessage());
        }
    }

    @Test
    public void testListBucketAccessPoints() {
        String accessPointName = "test-ap-zxl-list-bucket-01-3";
        String accessPointName2 = "test-ap-zxl-list-bucket-02-3";
        try {
            CreateAccessPointRequest createAccessPointRequest = new CreateAccessPointRequest(bucketName)
                    .withAccessPointName(accessPointName)
                    .withNetworkOrigin(networkOrigin)
                    .withVpc(new AccessPointVpcConfiguration());
            CreateAccessPointResult createReturn = ossClient.createAccessPoint(createAccessPointRequest);
            Assert.assertEquals(200, createReturn.getResponse().getStatusCode());


            CreateAccessPointRequest createAccessPointRequest2 = new CreateAccessPointRequest(bucketName)
                    .withAccessPointName(accessPointName2)
                    .withNetworkOrigin(networkOrigin2)
                    .withVpc(new AccessPointVpcConfiguration());

            CreateAccessPointResult createReturn2 = ossClient.createAccessPoint(createAccessPointRequest2);
            Assert.assertEquals(200, createReturn2.getResponse().getStatusCode());

            ListBucketAccessPointsRequest listBucketAccessPointsRequest = new ListBucketAccessPointsRequest(bucketName)
                    .withMaxKeys(10);
            ListAccessPointsResult listReturn = ossClient.listBucketAccessPoints(listBucketAccessPointsRequest);
            Assert.assertEquals(200, listReturn.getResponse().getStatusCode());


            ListBucketAccessPointsRequest listBucketAccessPointsRequest2 = new ListBucketAccessPointsRequest(bucketName)
                    .withMaxKeys(1);
            ListAccessPointsResult listReturn2 = ossClient.listBucketAccessPoints(listBucketAccessPointsRequest2);
            Assert.assertEquals(200, listReturn2.getResponse().getStatusCode());


            ListBucketAccessPointsRequest listBucketAccessPointsRequest3 = new ListBucketAccessPointsRequest(bucketName)
                    .withContinuationToken(listReturn2.getNextContinuationToken());
            ListAccessPointsResult listReturn3 = ossClient.listBucketAccessPoints(listBucketAccessPointsRequest3);
            Assert.assertEquals(200, listReturn3.getResponse().getStatusCode());

        } catch (OSSException e) {
            e.printStackTrace();
            Assert.fail("ErrorCode:" + e.getErrorCode() + "Message:" +e.getMessage());
        }
    }


    @Test
    public void testAccessPointPolicy() {

        try {
            PutAccessPointPolicyRequest putAccessPointPolicyRequest = new PutAccessPointPolicyRequest(bucketName)
                    .withAccessPointName(accessPointName)
                    .withAccessPointPolicy(accessPointPolicy);
            VoidResult createReturn = ossClient.putAccessPointPolicy(putAccessPointPolicyRequest);
            Assert.assertEquals(200, createReturn.getResponse().getStatusCode());


            GetAccessPointPolicyRequest getAccessPointPolicyRequest = new GetAccessPointPolicyRequest(bucketName).withAccessPointName(accessPointName);
            GetAccessPointPolicyResult getReturn = ossClient.getAccessPointPolicy(getAccessPointPolicyRequest);
            Assert.assertEquals(200, getReturn.getResponse().getStatusCode());
            Assert.assertEquals(accessPointPolicy, getReturn.getAccessPointPolicy());
            System.out.println(getReturn.getAccessPointPolicy());

            DeleteAccessPointPolicyRequest deleteAccessPointPolicyRequest = new DeleteAccessPointPolicyRequest(bucketName).withAccessPointName(accessPointName);
            VoidResult delReturn = ossClient.deleteAccessPointPolicy(deleteAccessPointPolicyRequest);
            Assert.assertEquals(204, delReturn.getResponse().getStatusCode());

        } catch (OSSException e) {
            e.printStackTrace();
            Assert.fail("ErrorCode:" + e.getErrorCode() + "Message:" +e.getMessage());
        }
    }

    @Test
    public void testObjectRequestFromAlias() throws Throwable {

        String md5 = BinaryUtil.toBase64String(BinaryUtil.calculateMd5(content.getBytes()));
        try {
            testCommonObjectCase(ossClient, alias, ossClient, md5, false);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("ErrorCode:" + e.getMessage() + "Message:" +e.getMessage());
        } finally {
            // delete object
            List<String> existingKeys = new ArrayList<String>();
            ObjectListing objectListing = ossClient.listObjects( alias, "test-zxl");
            for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
                existingKeys.add(objectSummary.getKey());
            }
            if(existingKeys.size() >0 ){
                DeleteObjectsRequest request = new DeleteObjectsRequest(alias);
                request.setKeys(existingKeys);
                this.ossClient.deleteObjects(request);
            }

            // delete part
            ListMultipartUploadsRequest listMultipartUploadsRequest = new ListMultipartUploadsRequest(alias);
            MultipartUploadListing multipartUploadListing = ossClient.listMultipartUploads(listMultipartUploadsRequest);
            multipartUploadListing.getMultipartUploads();
            for (MultipartUpload upload : multipartUploadListing.getMultipartUploads()) {
                AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(alias, upload.getKey(), upload.getUploadId());
                ossClient.abortMultipartUpload(abortMultipartUploadRequest);
            }
        }
    }

    private void testCommonObjectCase(OSS apClientTest, String apArn, OSS ossClient, String md5, boolean isArn) throws Throwable {
        // put object
        PutObjectRequest putObjectRequest = new PutObjectRequest(apArn, objectName, new ByteArrayInputStream(content.getBytes()));
        PutObjectResult putResult = apClientTest.putObject(putObjectRequest);


        // get object
        OSSObject getObjectResult = apClientTest.getObject(apArn, objectName);
        Assert.assertEquals(200, getObjectResult.getResponse().getStatusCode());
        Assert.assertEquals(getObjectResult.getObjectMetadata().getContentMD5(), md5);


        // head object
        ObjectMetadata headMetadata = apClientTest.headObject(apArn, objectName);
        Assert.assertEquals(headMetadata.getETag(), putResult.getETag());


        // getObjectMetadata
        GenericRequest generirequest = new GenericRequest(apArn, objectName);
        ObjectMetadata objectMetadata = apClientTest.getObjectMetadata(generirequest);
        Assert.assertEquals(objectMetadata.getETag(), putResult.getETag());


        // delete object
        VoidResult delResult = apClientTest.deleteObject(apArn, objectName);
        Assert.assertEquals(204, delResult.getResponse().getStatusCode());


        // sign put
        GeneratePresignedUrlRequest putSignRequest = new GeneratePresignedUrlRequest(apArn, objectName, HttpMethod.PUT);
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 10 );
        putSignRequest.setExpiration(expiration);
        URL signedUrl = apClientTest.generatePresignedUrl(putSignRequest);
        Map<String, String> headers = new HashMap<String, String>();
        apClientTest.putObject(signedUrl, new ByteArrayInputStream(content.getBytes()) , content.length(), headers);


        // sign get
        GeneratePresignedUrlRequest getSignRequest = new GeneratePresignedUrlRequest(apArn, objectName, HttpMethod.GET);
        Date getExpiration = new Date(new Date().getTime() + 1000 * 60 * 10 );
        getSignRequest.setExpiration(getExpiration);
        URL getSignedUrl = apClientTest.generatePresignedUrl(getSignRequest);
        Map<String, String> getHeaders = new HashMap<String, String>();
        apClientTest.getObject(getSignedUrl, getHeaders);

        apClientTest.deleteObject(apArn, objectName);
        Assert.assertFalse(apClientTest.doesObjectExist(apArn, objectName));


        // select object json
        apClientTest.putObject(apArn, jsonObjectName, new ByteArrayInputStream(jsonContent.getBytes()));
        SelectObjectRequest selectObjectRequest =
                new SelectObjectRequest(apArn, jsonObjectName)
                        .withInputSerialization(new InputSerialization()
                                .withCompressionType(CompressionType.NONE)
                                .withJsonInputFormat(new JsonFormat().withParseJsonNumberAsString(true).withJsonType(JsonType.LINES)))
                        .withOutputSerialization(new OutputSerialization()
                                .withOutputRawData(false)
                                .withCrcEnabled(true)
                                .withJsonOutputFormat(new JsonFormat()))
                        .withExpression("select * from ossobject as s where cast(s.age as int) > 40");

        OSSObject ossObject = apClientTest.selectObject(selectObjectRequest);
        byte[] buffer = new byte[1024];
        int bytesRead;
        int off = 0;
        while ((bytesRead = ossObject.getObjectContent().read()) != -1) {
            buffer[off++] = (byte)bytesRead;
        }

        Assert.assertEquals(new String(buffer, 0, off), jsonResult.replace("\t", "").replace(",\n", ","));
        apClientTest.deleteObject(apArn, jsonObjectName);
        Assert.assertFalse(apClientTest.doesObjectExist(apArn, jsonObjectName));


        // select object csv
        apClientTest.putObject(apArn, csvObjectName, new ByteArrayInputStream(csvContent.getBytes()));

        SelectObjectRequest selectCsvObjectRequest =
                new SelectObjectRequest(apArn, csvObjectName)
                        .withSkipPartialDataRecord(false)
                        .withInputSerialization(new InputSerialization()
                                .withCsvInputFormat(
                                        new CSVFormat().withRecordDelimiter("\n")
                                                .withQuoteChar("\"")
                                                .withFieldDelimiter(",")
                                                .withCommentChar("#")
                                                .withHeaderInfo(CSVFormat.Header.Ignore)))
                        .withExpression("select * from ossobject");
        OSSObject ossCsvObject = apClientTest.selectObject(selectCsvObjectRequest);
        byte[] csvBuffer = new byte[1024];
        int csvBytesRead;
        int csvOff = 0;
        while ((csvBytesRead = ossCsvObject.getObjectContent().read()) != -1) {
            csvBuffer[csvOff++] = (byte)csvBytesRead;
        }

        org.junit.Assert.assertEquals(new String(csvBuffer, 0, csvOff), csvContent.substring(csvContent.indexOf("#L") + 1));
        apClientTest.deleteObject(apArn, csvObjectName);
        Assert.assertFalse(apClientTest.doesObjectExist(apArn, csvObjectName));


        // restoreObject
        ObjectMetadata restoreMetadata = new ObjectMetadata();
        restoreMetadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Archive.toString());
        PutObjectRequest putRestoreObjectRequest = new PutObjectRequest(apArn, objectName, new ByteArrayInputStream(content.getBytes()), restoreMetadata);

        PutObjectResult putRestoreResult = apClientTest.putObject(putRestoreObjectRequest);
        restoreMetadata = apClientTest.getObjectMetadata(apArn, objectName);

        try {
            OSSObject restoreObject1 = apClientTest.getObject(apArn, objectName);
        } catch (OSSException e){
            Assert.assertEquals("The operation is not valid for the object's state",e.getErrorMessage());
        }

        StorageClass storageClass = restoreMetadata.getObjectStorageClass();
        if (storageClass == StorageClass.Archive) {
            apClientTest.restoreObject(apArn, objectName);
            do {
                Thread.sleep(1000);
                restoreMetadata = apClientTest.getObjectMetadata(apArn, objectName);
            } while (!restoreMetadata.isRestoreCompleted());
        }

        OSSObject restoreObject = apClientTest.getObject(apArn, objectName);
        Assert.assertEquals(StorageClass.Archive, restoreObject.getObjectMetadata().getObjectStorageClass());


        // copy object
        PutObjectRequest putCopyObjectRequest = new PutObjectRequest(bucketName, copyObjectName, new ByteArrayInputStream(content.getBytes()));
        PutObjectResult putCopyResult = ossClient.putObject(putCopyObjectRequest);
        Assert.assertTrue(ossClient.doesObjectExist(bucketName, copyObjectName));

        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, copyObjectName, apArn, copyDestObjectName);
        CopyObjectResult copyResult = apClientTest.copyObject(copyObjectRequest);
        Assert.assertTrue(apClientTest.doesObjectExist(apArn, copyDestObjectName));


        // UploadPartCopy
        InputStream inStream = genFixedLengthInputStream(streamSize);
        long copyMultipartLength = inStream.available();

        PutObjectRequest putCopyMultipartObjectRequest = new PutObjectRequest(bucketName, copyMultipartObjectName, inStream);
        PutObjectResult putMultipartCopyResult = ossClient.putObject(putCopyMultipartObjectRequest);
        Assert.assertTrue(ossClient.doesObjectExist(bucketName, copyMultipartObjectName));

        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest(apArn, copyMultipartObjectName);
        InitiateMultipartUploadResult initiateMultipartUploadResult = apClientTest.initiateMultipartUpload(initiateMultipartUploadRequest);
        String copyUploadId = initiateMultipartUploadResult.getUploadId();

        int partCount = (int) (copyMultipartLength / partSize);
        if (copyMultipartLength % partSize != 0) {
            partCount++;
        }
        List<PartETag> partETags = new ArrayList<PartETag>();
        for (int i = 0; i < partCount; i++) {
            long skipBytes = partSize * i;
            long size = partSize < copyMultipartLength - skipBytes ? partSize : copyMultipartLength - skipBytes;

            UploadPartCopyRequest uploadPartCopyRequest =
                    new UploadPartCopyRequest(bucketName, copyMultipartObjectName, apArn, copyMultipartObjectName);
            uploadPartCopyRequest.setUploadId(copyUploadId);
            uploadPartCopyRequest.setPartSize(size);
            uploadPartCopyRequest.setBeginIndex(skipBytes);
            uploadPartCopyRequest.setPartNumber(i + 1);
            UploadPartCopyResult uploadPartCopyResult = apClientTest.uploadPartCopy(uploadPartCopyRequest);
            partETags.add(uploadPartCopyResult.getPartETag());
        }

        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(
                apArn, copyMultipartObjectName, copyUploadId, partETags);
        apClientTest.completeMultipartUpload(completeMultipartUploadRequest);

        Assert.assertTrue(apClientTest.doesObjectExist(apArn, copyMultipartObjectName));

        SimplifiedObjectMeta simplifiedObjectMeta = apClientTest.getSimplifiedObjectMeta(apArn, copyMultipartObjectName);
        Assert.assertEquals(streamSize, simplifiedObjectMeta.getSize());


        // delete copy object
        VoidResult ossObjectDelResult = ossClient.deleteObject(bucketName, objectName);
        Assert.assertEquals(204, ossObjectDelResult.getResponse().getStatusCode());

        VoidResult ossCopyDelResult = ossClient.deleteObject(bucketName, copyObjectName);
        Assert.assertEquals(204, ossCopyDelResult.getResponse().getStatusCode());

        VoidResult ossCopyDestDelResult = ossClient.deleteObject(bucketName, copyDestObjectName);
        Assert.assertEquals(204, ossCopyDestDelResult.getResponse().getStatusCode());

        // delete multipart copy object
        VoidResult ossMultipartDelResult2 = ossClient.deleteObject(bucketName, copyMultipartObjectName);
        Assert.assertEquals(204, ossMultipartDelResult2.getResponse().getStatusCode());


        // append object
        AppendObjectRequest appendObjectRequest = new AppendObjectRequest(apArn, appendObjectName, new ByteArrayInputStream(content.getBytes()), null);
        appendObjectRequest.setPosition(0L);
        AppendObjectResult appendObjectResult = apClientTest.appendObject(appendObjectRequest);
        OSSObject o = apClientTest.getObject(apArn, appendObjectName);
        Assert.assertEquals(appendObjectName, o.getKey());
        Assert.assertEquals(content.length(), o.getObjectMetadata().getContentLength());

        AppendObjectRequest appendObjectRequest2 = new AppendObjectRequest(apArn, appendObjectName, new ByteArrayInputStream(content2.getBytes()));
        appendObjectRequest2.setPosition(appendObjectResult.getNextPosition());
        AppendObjectResult appendObjectResult2 = apClientTest.appendObject(appendObjectRequest2);
        OSSObject o2 = apClientTest.getObject(apArn, appendObjectName);
        Assert.assertEquals(content.length()+content2.length(), o2.getObjectMetadata().getContentLength());


        //list object
        ObjectListing objectListing = apClientTest.listObjects(apArn, "test-zxl");
        Assert.assertEquals(1, objectListing.getObjectSummaries().size());

        ListObjectsV2Result listObjectsV2Result = apClientTest.listObjectsV2(apArn, "test-zxl");
        Assert.assertEquals(1, listObjectsV2Result.getObjectSummaries().size());


        // delete objects
        List<String> existingKeys = new ArrayList<String>();

        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            existingKeys.add(objectSummary.getKey());
        }

        DeleteObjectsRequest request = new DeleteObjectsRequest(apArn);
        request.setKeys(existingKeys);
        try {
            DeleteObjectsResult deletesResult = apClientTest.deleteObjects(request);
            List<String> deletedObjects = deletesResult.getDeletedObjects();
            Assert.assertEquals(1, deletedObjects.size());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertFalse(apClientTest.doesObjectExist(apArn, objectName));
        Assert.assertFalse(apClientTest.doesObjectExist(apArn, appendObjectName));
        Assert.assertFalse(apClientTest.doesObjectExist(apArn, copyObjectName));
        Assert.assertFalse(apClientTest.doesObjectExist(apArn, copyMultipartObjectName));


        // uploadFile
        File file = createSampleFile(fileUploadName, 1024 * 500);

        UploadFileRequest uploadFileRequest = new UploadFileRequest(apArn, fileUploadName);
        uploadFileRequest.setUploadFile(file.getAbsolutePath());
        uploadFileRequest.setTaskNum(10);

        uploadFileRequest = new UploadFileRequest(apArn, fileUploadName, file.getAbsolutePath(), (1024 * 100),10);

        UploadFileResult uploadRes = apClientTest.uploadFile(uploadFileRequest);
        Assert.assertEquals(uploadRes.getMultipartUploadResult().getKey(), fileUploadName);

        ObjectMetadata uploadFileObjectMetadata = apClientTest.getObjectMetadata(new GenericRequest(apArn, fileUploadName));
        Assert.assertEquals(file.length(), uploadFileObjectMetadata.getContentLength());

        apClientTest.deleteObject(apArn, fileUploadName);
        Assert.assertFalse(apClientTest.doesObjectExist(apArn, fileUploadName));

        // acl
        PutObjectRequest putAclObject = new PutObjectRequest(apArn, objectName, new ByteArrayInputStream(content.getBytes()));
        PutObjectResult putAclResult = apClientTest.putObject(putAclObject);

        apClientTest.setObjectAcl(apArn, objectName, CannedAccessControlList.Default);
        ObjectAcl returnedAcl = apClientTest.getObjectAcl(apArn, objectName);
        Assert.assertEquals(ObjectPermission.Default, returnedAcl.getPermission());


        // symlink
        ObjectMetadata metadataSymlink = new ObjectMetadata();
        metadataSymlink.setContentType("text/plain");
        metadataSymlink.addUserMetadata("property", "property-value");

        CreateSymlinkRequest createSymlinkRequest = new CreateSymlinkRequest(apArn, symLink, objectName);
        createSymlinkRequest.setMetadata(metadataSymlink);
        apClientTest.createSymlink(createSymlinkRequest);

        OSSSymlink symbolicLink = apClientTest.getSymlink(apArn, symLink);
        Assert.assertEquals(symbolicLink.getSymlink(), symLink);
        Assert.assertEquals(symbolicLink.getTarget(), objectName);
        Assert.assertEquals(symbolicLink.getMetadata().getContentType(), "text/plain");
        Assert.assertEquals(symbolicLink.getMetadata().getUserMetadata().get("property"), "property-value");

        VoidResult delSymlink = apClientTest.deleteObject(apArn, symLink);
        Assert.assertEquals(204, delSymlink.getResponse().getStatusCode());

        // tag
        Map<String, String> tags = new HashMap<String, String>(1);
        tags.put("tag1", "balabala");
        tags.put("tag2", "haha");

        apClientTest.setObjectTagging(apArn, objectName, tags);

        TagSet tagSet = apClientTest.getObjectTagging(apArn, objectName);
        Assert.assertEquals(tagSet.getAllTags().size(), 2);

        VoidResult delTag = apClientTest.deleteObjectTagging(apArn, objectName);
        Assert.assertEquals(204, delTag.getResponse().getStatusCode());

        apClientTest.deleteObject(apArn, objectName);
        Assert.assertFalse(apClientTest.doesObjectExist(apArn, objectName));


        // multipart
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(apArn, multipartObjectName);
        InitiateMultipartUploadResult intiResult = apClientTest.initiateMultipartUpload(initRequest);
        String uploadId = intiResult.getUploadId();

        InputStream instream = genFixedLengthInputStream(streamSize);

        // Upload single part
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(apArn);
        uploadPartRequest.setKey(multipartObjectName);
        uploadPartRequest.setInputStream(instream);
        uploadPartRequest.setPartNumber(2);
        uploadPartRequest.setPartSize(streamSize);
        uploadPartRequest.setUploadId(uploadId);
        apClientTest.uploadPart(uploadPartRequest);


        // List single multipart upload
        ListMultipartUploadsRequest listMultipartUploadsRequest = new ListMultipartUploadsRequest(apArn);
        MultipartUploadListing multipartUploadListing = apClientTest.listMultipartUploads(listMultipartUploadsRequest);

        Assert.assertFalse(multipartUploadListing.isTruncated());
        Assert.assertEquals(multipartObjectName, multipartUploadListing.getNextKeyMarker());
        Assert.assertEquals(uploadId, multipartUploadListing.getNextUploadIdMarker());

        List<MultipartUpload> multipartUploads = multipartUploadListing.getMultipartUploads();
        Assert.assertEquals(1, multipartUploads.size());
        Assert.assertEquals(multipartObjectName, multipartUploads.get(0).getKey());
        Assert.assertEquals(uploadId, multipartUploads.get(0).getUploadId());


        // Lists all parts in a multiple parts upload.
        ListPartsRequest listPartsRequest = new ListPartsRequest(apArn, multipartObjectName, uploadId);
        PartListing partListing = apClientTest.listParts(listPartsRequest);
        Assert.assertEquals(1, partListing.getParts().size());


        // Abort multipart upload
        AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(apArn, multipartObjectName, uploadId);
        apClientTest.abortMultipartUpload(abortMultipartUploadRequest);

        try {
            partListing = apClientTest.listParts(new ListPartsRequest(apArn, multipartObjectName, uploadId));
        } catch (OSSException e){
            Assert.assertEquals("The specified upload does not exist. The upload ID may be invalid, or the upload may have been aborted or completed.",e.getErrorMessage());
        }

        Assert.assertFalse(apClientTest.doesObjectExist(apArn, multipartObjectName));


        // putAccessPointPolicy
        PutAccessPointPolicyRequest putAccessPointPolicyRequest = new PutAccessPointPolicyRequest(apArn)
                .withAccessPointName(accessPointName)
                .withAccessPointPolicy(accessPointPolicy);
        VoidResult createReturn = apClientTest.putAccessPointPolicy(putAccessPointPolicyRequest);
        Assert.assertEquals(200, createReturn.getResponse().getStatusCode());


        GetAccessPointPolicyRequest getAccessPointPolicyRequest = new GetAccessPointPolicyRequest(apArn).withAccessPointName(accessPointName);
        GetAccessPointPolicyResult getReturn = apClientTest.getAccessPointPolicy(getAccessPointPolicyRequest);
        Assert.assertEquals(200, getReturn.getResponse().getStatusCode());
        Assert.assertEquals(accessPointPolicy, getReturn.getAccessPointPolicy());

        DeleteAccessPointPolicyRequest deleteAccessPointPolicyRequest = new DeleteAccessPointPolicyRequest(apArn).withAccessPointName(accessPointName);
        VoidResult delReturn = apClientTest.deleteAccessPointPolicy(deleteAccessPointPolicyRequest);
        Assert.assertEquals(204, delReturn.getResponse().getStatusCode());



        apClientTest.deleteObject(apArn, postObjectName);
        Assert.assertFalse(apClientTest.doesObjectExist(apArn, postObjectName));
    }

    public static File createSampleFile(String fileName, long size) throws IOException {
        File file = File.createTempFile(fileName, ".txt");
        file.deleteOnExit();
        String context = "a\n";

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i < size / context.length(); i++) {
            writer.write(context);
        }
        writer.close();

        return file;
    }


    private static String formUpload(String urlStr, Map<String, String> formFields, File localFile)
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
            conn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // Traverse and read the data in the form Map, and write the data into the output stream.
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
            // Read the file information and write the file to be uploaded into the output stream.
            String filename = localFile.getName();
            String contentType = new MimetypesFileTypeMap().getContentType(localFile);
            if (contentType == null || contentType.equals("")) {
                contentType = "application/octet-stream";
            }
            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(boundary)
                    .append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"file\"; "
                    + "filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type: " + contentType + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());
            DataInputStream in = new DataInputStream(new FileInputStream(localFile));
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
            //Read return data
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