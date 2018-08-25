package com.aliyun.oss.integrationtests;

import com.aliyun.oss.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SelectObjectTest extends TestBase {

    @Test
    public void testGetSelectObjectMetadata() {
        final String valid = "get-select-object-metadata-valid";
        final String validContent = "name,school,company,age\n" +
                "Lora Francis,School,Staples Inc,27\n" +
                "Eleanor Little,School,\"Conectiv, Inc\",43\n" +
                "Rosie Hughes,School,Western Gas Resources Inc,44\n" +
                "Lawrence Ross,School,MetLife Inc.,24";
        ossClient.putObject(bucketName, valid, new ByteArrayInputStream(validContent.getBytes()));
        SelectObjectMetadata validSelectObjectMetadata = ossClient.createSelectObjectMetadata(
                new CreateSelectObjectMetadataRequest(bucketName, valid)
                        .withInputSerialization(new InputSerialization().withCsvInputFormat(new CSVFormat())));
        Assert.assertEquals(5, validSelectObjectMetadata.getCsvObjectMetadata().getTotalLines());
        Assert.assertEquals(1, validSelectObjectMetadata.getCsvObjectMetadata().getSplits());

        final String invalid = "get-select-object-metadata-invalid";
        final String invalidContent = "name,school,company,age\n" +
                "Laura Rodriquez,School,Triad Hospitals Inc,39\n" +
                "\",,,44\n" +
                "Nora Cannon,School,Reader's Digest Association Inc.,30\n" +
                "Louisa Weaver,School,Trinity Industries Inc,21\n" +
                "Howard Hart,School,\"EOG Resources, Inc.\",35\n" +
                "\"Ola \"\"\"\"Miller\",School,Trump Hotels & Casino Resorts Inc.,20";
        ossClient.putObject(bucketName, invalid, new ByteArrayInputStream(invalidContent.getBytes()));
        try {
            ossClient.createSelectObjectMetadata(
                    new CreateSelectObjectMetadataRequest(bucketName, invalid)
                            .withInputSerialization(new InputSerialization().withCsvInputFormat(new CSVFormat())));
            Assert.fail("invalid object for get select object metadata");
        } catch (Exception e) {
        }
    }

    @Test
    public void testSelectObject() throws IOException {
        final String key = "get-select-object-metadata-valid";
        final String content = "name,school,company,age\n" +
                "Lora Francis,School,Staples Inc,27\n" +
                "Eleanor Little,School,\"Conectiv, Inc\",43\n" +
                "Rosie Hughes,School,Western Gas Resources Inc,44\n" +
                "Lawrence Ross,School,MetLife Inc.,24\n";
        ossClient.putObject(bucketName, key, new ByteArrayInputStream(content.getBytes()));

        SelectObjectRequest selectObjectRequest =
                new SelectObjectRequest(bucketName, key)
                        .withInputSerialization(new InputSerialization().withCsvInputFormat(
                                new CSVFormat().withHeaderInfo(CSVFormat.Header.Ignore)))
                        .withOutputSerialization(new OutputSerialization().withCsvOutputFormat(new CSVFormat()));
        selectObjectRequest.setExpression("select * from ossobject");
        OSSObject ossObject = ossClient.selectObject(selectObjectRequest);
        byte[] buffer = new byte[1024];
        int bytesRead;
        int off = 0;
        while ((bytesRead = ossObject.getObjectContent().read(buffer, off, 1024 - off)) != -1) {
            off += bytesRead;
        }

        Assert.assertEquals(new String(buffer, 0, off), content.substring(content.indexOf("\n") + 1));

        ossClient.createSelectObjectMetadata(
                new CreateSelectObjectMetadataRequest(bucketName, key)
                        .withInputSerialization(new InputSerialization().withCsvInputFormat(new CSVFormat())));

        selectObjectRequest.setLineRange(1, 3);
        OSSObject rangeOssObject = ossClient.selectObject(selectObjectRequest);
        off = 0;
        while ((bytesRead = rangeOssObject.getObjectContent().read(buffer, off, 1024 - off)) != -1) {
            off += bytesRead;
        }
        Assert.assertEquals(new String(buffer, 0, off),
                "Lora Francis,School,Staples Inc,27\n" +
                "Eleanor Little,School,\"Conectiv, Inc\",43\n" +
                "Rosie Hughes,School,Western Gas Resources Inc,44\n");

        selectObjectRequest.setLineRange(5, 10);
        try {
            ossClient.selectObject(selectObjectRequest);
            Assert.fail("invalid line range for select object request");
        } catch (Exception e) {
        }

        selectObjectRequest.setSplitRange(5, 10);
        try {
            ossClient.selectObject(selectObjectRequest);
            Assert.fail("both split range and line range have been set for select object request");
        } catch (Exception e) {
        }
    }
}
