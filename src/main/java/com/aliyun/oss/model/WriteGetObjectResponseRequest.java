package com.aliyun.oss.model;

import java.io.File;
import java.io.InputStream;

public class WriteGetObjectResponseRequest extends WebServiceRequest{

    private String route;
    private String token;
    private int status;
    private File file;
    private InputStream inputStream;
    private ObjectMetadata metadata;

    public WriteGetObjectResponseRequest(String route, String token, int status, File file) {
        this(route, token, status, file, null);
    }

    public WriteGetObjectResponseRequest(String route, String token, int status, File file, ObjectMetadata metadata) {
        this.route = route;
        this.token = token;
        this.status = status;
        this.file = file;
        this.metadata = metadata;
    }

    public WriteGetObjectResponseRequest(String route, String token, int status, InputStream input) {
        this(route, token, status, input, null);
    }

    public WriteGetObjectResponseRequest(String route, String token, int status, InputStream input, ObjectMetadata metadata) {
        this.route = route;
        this.token = token;
        this.status = status;
        this.inputStream = input;
        this.metadata = metadata;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public WriteGetObjectResponseRequest withRoute(String route) {
        this.route = route;
        return this;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public WriteGetObjectResponseRequest withToken(String token) {
        this.token = token;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public WriteGetObjectResponseRequest withStatus(int status) {
        this.status = status;
        return this;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public WriteGetObjectResponseRequest withFile(File file) {
        this.file = file;
        return this;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public WriteGetObjectResponseRequest withInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public ObjectMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
    }

    public WriteGetObjectResponseRequest withMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
        return this;
    }
}
