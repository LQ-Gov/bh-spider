package com.charles.scheduler.task;

import io.netty.handler.codec.http.HttpMethod;

import java.util.List;
import java.util.Map;

/**
 * Created by lq on 17-3-17.
 */
public class Task {
    private String id;
    private String name;
    private String url;
    private String timer;
    private Map<String, String> headers;
    private Map<Object, List<String>> handlers;

    private HttpMethod method = HttpMethod.GET;
    private Object data;
    private Object params;
    private int interval;


    public Map<Object, List<String>> getHandlers() {
        return handlers;
    }

    public void setHandlers(Map<Object, List<String>> handlers) {
        this.handlers = handlers;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
