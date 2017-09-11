package com.bh.spider.fetch.impl;

import com.bh.spider.fetch.Cookie;
import com.bh.spider.fetch.Response;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.*;

public class FetchResponse implements Response {
    private int code;
    private byte[] entity;
    private Map<String, String> headers = new HashMap<>();

    private Map<String, FetchCookie> cookieCache = new HashMap<>();

    private FetchResponse() {
    }

    public FetchResponse(int code, byte[] data, Map<String, String> headers, List<FetchCookie> cookies) {
        this.code = code;
        this.entity = data;
        this.headers = headers;

        if (cookies != null) cookies.forEach(x -> cookieCache.put(x.getName(), x));

    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public byte[] data() {
        return entity;
    }


    public Cookie cookie(String name) {
        return cookieCache.get(name);
    }

    public List<Cookie> cookies() {
        return new ArrayList<>(cookieCache.values());
    }


}
