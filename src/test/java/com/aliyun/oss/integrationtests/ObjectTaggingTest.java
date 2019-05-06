package com.aliyun.oss.integrationtests;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ObjectTaggingTest extends TestBase {
  private static final String TEST_OBJECT_KEY = "test-object-tagging-key";

  @Override
  public void setUp() throws Exception {
    super.setUp();

    InputStream inputStream = TestUtils.genFixedLengthInputStream(1024);
    PutObjectRequest putObjectRequest = new PutObjectRequest(this.bucketName, TEST_OBJECT_KEY, inputStream);

    PutObjectResult putObjectResult = this.ossClient.putObject(putObjectRequest);
  }

  @Test
  public void test_objectTagging() {
    Tag tag1 = new Tag("tag1", "value1");
    Tag tag2 = new Tag("tag2", "value2");
    Tag tag3 = new Tag("tag3", "value3");

    List<Tag> tagSet = new ArrayList<Tag>();
    tagSet.add(tag1);
    tagSet.add(tag2);
    tagSet.add(tag3);

    ObjectTagging objectTagging = new ObjectTagging(tagSet);
    SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(this.bucketName, TEST_OBJECT_KEY, objectTagging);
    this.ossClient.setObjectTagging(setObjectTaggingRequest);

    GenericRequest getObjectTaggingRequest = new GenericRequest(this.bucketName, TEST_OBJECT_KEY);
    ObjectTagging objectTagging1 = this.ossClient.getObjectTagging(getObjectTaggingRequest);

    Assert.assertNotNull(objectTagging1);
    Assert.assertNotNull(objectTagging1.getTagSet());
    Assert.assertTrue(objectTagging1.getTagSet().size() == 3);

    DeleteObjectTaggingRequest deleteObjectTaggingRequest = new DeleteObjectTaggingRequest(this.bucketName, TEST_OBJECT_KEY);

    this.ossClient.deleteObjectTagging(deleteObjectTaggingRequest);

    ObjectTagging objectTagging2 = this.ossClient.getObjectTagging(getObjectTaggingRequest);

    Assert.assertNotNull(objectTagging2);
    Assert.assertNotNull(objectTagging2.getTagSet());
    Assert.assertTrue(objectTagging2.getTagSet().size() == 0);
  }

  @Test
  public void test_Tag() {
    try {
      Tag tag = new Tag("#", "#");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof ClientException);
    }

    try {
      Tag tag = new Tag("!", "!");
    } catch (Exception e) {
      Assert.assertTrue(e instanceof ClientException);
    }
  }


}
