package com.charles.spider.scheduler.fetcher;

import com.charles.spider.common.extractor.Document;
import com.charles.spider.common.http.FetchContext;
import com.charles.spider.common.http.Request;
import com.charles.spider.common.http.Response;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class BasicFetchContext implements FetchContext {

    private HttpRequestBase base;
    private Request original;

    private Map<String,Object> fields = new HashMap<>();

    public BasicFetchContext(HttpRequestBase req,Request original){
        this.base = req;
        this.original = original;

    }


    @Override
    public URL url() {
        return null;
    }

    @Override
    public Request request() {
        return this.original;
    }

    @Override
    public Response response() {
        return null;

    }

    @Override
    public Document document() {
        return null;
    }

    @Override
    public Document document(Charset charset) {
        return null;
    }


    @Override
    public void set(String key, Object value) {
        fields.put(key,value);

    }

    @Override
    public void set(Map<String, Object> collection) {
        fields.putAll(collection);

    }

    @Override
    public Object get(String key) {
        return fields.get(key);
    }

    @Override
    public void scheduler(FetchContext ctx, Request req, boolean local) {

    }

    @Override
    public void scheduler(FetchContext ctx, Request req) {

    }

    @Override
    public void scheduler(Request req) {

    }

    @Override
    public void scheduler(FetchContext ctx, String url, boolean local) {

    }

    @Override
    public void scheduler(FetchContext ctx, String url) {

    }

    @Override
    public void scheduler(String url) {

    }

    @Override
    public void cancel() {

    }
}
