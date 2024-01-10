package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.DownloadFileResult;
import org.junit.Test;
import java.io.*;
public class ObjectDownloadFileTest extends TestBase implements Serializable {

    @Test
    public void testObjectDownloadFileCheckPoint() throws IOException {
        String objectName = "testObjectDownloadFileCheckPoint.txt";
        String filePath = "D:\\";

        FileOutputStream fileOut = new FileOutputStream(filePath+objectName+".dcp");
        ObjectOutputStream outStream = new ObjectOutputStream(fileOut);
        outStream.writeObject(this);
        outStream.close();
        fileOut.close();

        File file = createSampleFile(objectName, 1024 * 500);
        ossClient.putObject(bucketName, objectName, new FileInputStream(file));

        DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, objectName);
        downloadFileRequest.setDownloadFile(filePath+objectName);
        downloadFileRequest.setPartSize(4 * 1024 * 1024);
        downloadFileRequest.setTaskNum(1);
        downloadFileRequest.setEnableCheckpoint(true);

        try {
            DownloadFileResult result = ossClient.downloadFile(downloadFileRequest);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
