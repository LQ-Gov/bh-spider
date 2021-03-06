package com.bh.spider.scheduler.fetcher;

import com.bh.spider.common.fetch.FetchMethod;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.scheduler.IdGenerator;

import java.net.URL;
import java.util.Date;
import java.util.Map;

public class FetchContent implements Request {
    private Request request;

    private State state;
    private long id;

    public FetchContent(Request request) {
        this(request, request.state());
    }

    public FetchContent(Request request,State state){
        this.id = request.id() == 0 ? IdGenerator.instance.nextId() : request.id();
        this.request = request;
        this.state = state;

    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public URL url() {
        return request.url();
    }

    @Override
    public FetchMethod method() {
        return request.method();
    }

    @Override
    public Map<String, String> headers() {
        return request.headers();
    }

    @Override
    public Map<String, Object> extra() {
        return request.extra();
    }

    @Override
    public String hash() {
        return request.hash();
    }

    @Override
    public Date createTime() {
        return request.createTime();
    }

    @Override
    public State state() {
        return state;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return request.clone();
    }
}
