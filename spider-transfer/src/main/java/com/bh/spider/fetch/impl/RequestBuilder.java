package com.bh.spider.fetch.impl;

import com.bh.spider.fetch.HttpMethod;
import com.bh.spider.fetch.Request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class RequestBuilder {
    private FetchRequest request;

    public RequestBuilder(FetchRequest request) {
        this.request = request;
    }

    public static RequestBuilder create(String url) throws MalformedURLException {
        return create(url, null);
    }

    public static RequestBuilder create(String url, HttpMethod method) throws MalformedURLException {
        return create(new URL(url), method);
    }


    public static RequestBuilder create(URL url) {
        return create(url, null);
    }

    public static RequestBuilder create(URL url, HttpMethod method) {
        return new RequestBuilder(new FetchRequest(url, method));
    }


    public RequestBuilder setId(long id) {
        request.setId(id);
        return this;
    }

    public RequestBuilder setCreateTime(Date date){
        request.setCreateTime(date);
        return this;
    }

    public RequestBuilder setState(FetchState state){
        request.setState(state);
        return this;
    }


    public Request build() {
        return request;
    }
}
