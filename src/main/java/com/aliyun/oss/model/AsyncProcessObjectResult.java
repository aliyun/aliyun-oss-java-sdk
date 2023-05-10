package com.aliyun.oss.model;

public class AsyncProcessObjectResult extends GenericResult {

    private String asyncRequestId;
    private String eventId;
    private String taskId;

    public String getAsyncRequestId() {
        return asyncRequestId;
    }

    public void setAsyncRequestId(String asyncRequestId) {
        this.asyncRequestId = asyncRequestId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
