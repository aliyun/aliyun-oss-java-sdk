package com.aliyun.oss.integrationtests;

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

    GenericRequest genericRequest = new GenericRequest(this.bucketName, TEST_OBJECT_KEY);
    ObjectTagging objectTagging1 = this.ossClient.getObjectTagging(genericRequest);

    Assert.assertNotNull(objectTagging1);
    Assert.assertNotNull(objectTagging1.getTagSet());
    Assert.assertTrue(objectTagging1.getTagSet().size() == 3);

    this.ossClient.deleteObjectTagging(genericRequest);

    ObjectTagging objectTagging2 = this.ossClient.getObjectTagging(genericRequest);

    Assert.assertNotNull(objectTagging2);
    Assert.assertNotNull(objectTagging2.getTagSet());
    Assert.assertTrue(objectTagging2.getTagSet().size() == 0);
  }
}
