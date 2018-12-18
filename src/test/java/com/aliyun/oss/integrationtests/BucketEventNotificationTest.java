package com.aliyun.oss.integrationtests;

import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.FunctionComputeConfiguration;
import com.aliyun.oss.model.GenericRequest;
import com.aliyun.oss.model.NotificationConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class BucketEventNotificationTest extends TestBase {
  private static final String TEST_PREFIX = "prefix";
  private static final String TEST_SUFFIX = "suffix";
  private static final String TEST_ACTION = "action";

  private static final String SERVICE_NAME = "service";
  private static final String FUNCTION_NAME = "function";
  private static final String TEST_BUCKET_NAME = "bucket";
  private static final String TEST_BUCKET_HAS_NO_NOTIFICATION_CONF = "no-such-notification-conf-bucket";

  @Test
  public void getBucketEventNotificationTest() throws Exception {
    try {
      GenericRequest genericRequest = new GenericRequest();
      genericRequest.setBucketName(TEST_BUCKET_NAME);

      NotificationConfiguration notificationConfiguration = ossClient.getBucketEventNotification(genericRequest);
      FunctionComputeConfiguration functionComputeConfiguration = notificationConfiguration.getFunctionComputeConfigurations().get(0);
      Map<String, String> result = getFilter(functionComputeConfiguration.getFunction().getArn());

      /**
       * assert length = 1, ARN = /services/TestConfig.serviceName/functions/TestConfig.functionName
       * assert triggerId = xxx(get it from fc according serviceName.functionName.triggerName)
       * assert prefix = TEST_PREFIX, suffix = TEST_SUFFIX
       */
      Assert.assertEquals(result.get("service"), SERVICE_NAME);
      Assert.assertEquals(result.get("function"), FUNCTION_NAME);
      Assert.assertEquals(functionComputeConfiguration.getEvent(), TEST_ACTION);
      Assert.assertEquals(functionComputeConfiguration.getFilter().getKey().getPrefix(), TEST_PREFIX);
      Assert.assertEquals(functionComputeConfiguration.getFilter().getKey().getSuffix(), TEST_SUFFIX);
    } catch (Exception e) {
      junit.framework.Assert.fail(e.getMessage());
    } finally {
      ossClient.deleteBucket(bucketName);
    }
  }

  @Test
  public void NoSuchNotificationConfigurationTest() throws Exception {
    try {
      GenericRequest genericRequest = new GenericRequest();
      genericRequest.setBucketName(TEST_BUCKET_HAS_NO_NOTIFICATION_CONF);

      NotificationConfiguration notificationConfiguration = ossClient.getBucketEventNotification(genericRequest);
    } catch (Exception e) {
      Assert.assertTrue(e instanceof OSSException);
      com.aliyun.oss.OSSException ossClientError = (com.aliyun.oss.OSSException)e;
      String errorCode = ossClientError.getErrorCode();
      Assert.assertTrue(OSSErrorCode.NO_SUCH_NOTIFICATION_CONFIGURATION.equals(errorCode));
    } finally {

    }
  }

  /**
   * get prefix and suffix form arn
   */
  public Map<String, String> getFilter(String arn) {
    Map<String, String> map = new HashMap<String, String>();
    String[] tempResult = arn.split(":");
    String temp = tempResult[tempResult.length - 1];
    String[] output = temp.split("/");
    map.put("service", output[1]);
    map.put("function", output[3]);
    return map;
  }
}
