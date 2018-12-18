package com.aliyun.oss.model;

import java.util.List;

/**
 * Created by huaixu on 18/12/10
 */
public class NotificationConfiguration {
  private List<FunctionComputeConfiguration> notificationConfiguration;

  public NotificationConfiguration(List<FunctionComputeConfiguration> notificationConfiguration) {
    this.notificationConfiguration = notificationConfiguration;
  }

  public List<FunctionComputeConfiguration> getFunctionComputeConfigurations() {
    return notificationConfiguration;
  }

  public void setFunctionComputeConfigurations(List<FunctionComputeConfiguration> notificationConfiguration) {
    this.notificationConfiguration = notificationConfiguration;
  }
}
