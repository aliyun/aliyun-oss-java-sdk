package com.aliyun.oss.model;

/**
 * Created by huaixu on 18/12/10
 */
public class FunctionComputeConfiguration {
  private String id;
  private String event;
  private Filter filter;
  private Function function;

  public FunctionComputeConfiguration(String id, String event, String prefix, String suffix, String arn, String assumeRole) {
    this.id = id;
    this.event = event;
    this.filter = new Filter(new FilterKey(prefix, suffix));
    this.function = new Function(arn, assumeRole);
  }

  public static class Filter {
    private FilterKey key;

    public Filter(FilterKey key) {
      this.key = key;
    }

    public FilterKey getKey() {
      return key;
    }

    public void setKey(FilterKey key) {
      this.key = key;
    }
  }

  public static class FilterKey {
    private String prefix;
    private String suffix;
    public FilterKey(String prefix, String suffix) {
      this.prefix = prefix;
      this.suffix = suffix;
    }

    public String getPrefix() {
      return prefix;
    }

    public void setPrefix(String prefix) {
      this.prefix = prefix;
    }

    public String getSuffix() {
      return suffix;
    }

    public void setSuffix(String suffix) {
      this.suffix = suffix;
    }
  }

  public static class Function {
    private String arn;
    private String assumeRole;

    public Function(String arn, String assumeRole) {
      this.arn = arn;
      this.assumeRole = assumeRole;
    }

    public String getArn() {
      return arn;
    }

    public void setArn(String arn) {
      this.arn = arn;
    }

    public String getAssumeRole() {
      return assumeRole;
    }

    public void setAssumeRole(String assumeRole) {
      this.assumeRole = assumeRole;
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEvent() {
    return event;
  }

  public void setEvent(String event) {
    this.event = event;
  }

  public Filter getFilter() {
    return filter;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  public Function getFunction() {
    return function;
  }

  public void setFunction(Function function) {
    this.function = function;
  }
}
