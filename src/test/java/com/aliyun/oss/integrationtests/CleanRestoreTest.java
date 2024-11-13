package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.util.List;

public class CleanRestoreTest extends TestBase {

    @Test
    public void testCleanRestoreException() {
        String objectNameEXample1 = "testCleanRestore-1.txt";
        String objectNameEXample2 = "testCleanRestore-2.txt";

        try {
            GenericRequest genericRequest = new GenericRequest()
                    .withBucketName(bucketName)
                    .withKey(objectNameEXample1);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectNameEXample1,
                    new ByteArrayInputStream("ColdArchive file".getBytes()));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.ColdArchive.toString());
            putObjectRequest.setMetadata(metadata);
            ossClient.putObject(putObjectRequest);

            metadata = ossClient.getObjectMetadata(bucketName, objectNameEXample1);
            Assert.assertEquals(StorageClass.ColdArchive, metadata.getObjectStorageClass());

            ossClient.cleanRestore(genericRequest);
        } catch (OSSException e1) {
            Assert.assertEquals("ArchiveRestoreFileStale", e1.getErrorCode());
        }


        try {
            GenericRequest genericRequest = new GenericRequest()
                    .withBucketName(bucketName)
                    .withKey(objectNameEXample2);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectNameEXample2,
                    new ByteArrayInputStream("ColdArchive file".getBytes()));

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.ColdArchive.toString());
            putObjectRequest.setMetadata(metadata);
            ossClient.putObject(putObjectRequest);

            metadata = ossClient.getObjectMetadata(bucketName, objectNameEXample2);
            Assert.assertEquals(StorageClass.ColdArchive, metadata.getObjectStorageClass());


            RestoreJobParameters jobParameters = new RestoreJobParameters(RestoreTier.RESTORE_TIER_EXPEDITED);
            RestoreConfiguration configuration = new RestoreConfiguration(1, jobParameters);
            ossClient.restoreObject(bucketName, objectNameEXample2, configuration);

            Thread.sleep(1000);

            ossClient.cleanRestore(genericRequest);
        } catch (OSSException e1) {
            Assert.assertEquals("ArchiveRestoreNotFinished", e1.getErrorCode());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
