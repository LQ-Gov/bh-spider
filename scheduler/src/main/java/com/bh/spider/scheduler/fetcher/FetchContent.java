package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.FetchMethod;
import com.bh.spider.fetch.Request;
import com.bh.spider.scheduler.IdGenerator;

import java.net.URL;
import java.util.Date;
import java.util.Map;

public class FetchContent implements Request {
    private Request request;

    private long id;


    public FetchContent(Request request) {
        this.request = request;
        id = request.id() == 0 ? IdGenerator.instance.nextId() : request.id();
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
    public Object clone() throws CloneNotSupportedException {
        return request.clone();
    }
}
