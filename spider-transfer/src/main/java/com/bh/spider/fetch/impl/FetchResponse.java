package com.bh.spider.fetch.impl;

import com.bh.spider.fetch.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FetchResponse implements Response {
    private int code;
    private byte[] entity;
    private Map<String,String> headers = new HashMap<>();

    FetchResponse(){}

    public FetchResponse(int code, byte[] data, Map<String,String> headers) throws IOException {
        this.code = code;
        this.entity = data;
        this.headers = headers;

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
}
