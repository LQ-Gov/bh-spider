package com.charles.scheduler.fetcher;

import com.charles.common.task.Task;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by lq on 17-3-18.
 */
public class FetcherContext {
    private String body;
    private HttpRequestBase request = null;
    private Task task = null;

    public FetcherContext(HttpRequestBase req,Task task){
        this.request=req;
        this.task=task;
    }

    public HttpRequestBase getRequest() {
        return request;
    }

    public Task getTask() {
        return task;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
