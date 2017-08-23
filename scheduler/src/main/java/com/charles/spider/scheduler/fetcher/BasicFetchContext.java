package com.charles.spider.scheduler.fetcher;

import com.charles.spider.fetch.*;
import com.charles.spider.doc.Document;
import com.charles.spider.fetch.impl.FetchRequest;
import com.charles.spider.scheduler.BasicScheduler;
import org.apache.http.client.methods.HttpRequestBase;


import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class BasicFetchContext implements FetchContext {

    private HttpRequestBase base;
    private Request original;


    private BasicScheduler sch;

    private Map<String, Object> fields = new HashMap<>();

    public BasicFetchContext(BasicScheduler scheduler, HttpRequestBase req, Request original) {

        this.sch = scheduler;
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
        fields.put(key, value);

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
        if (ctx != null) {
            //格式化 req
        }

        sch.submit(null, (FetchRequest) req);
    }

    @Override
    public void scheduler(FetchContext ctx, Request req) {
        scheduler(ctx, req, false);
    }

    @Override
    public void scheduler(Request req) {
        scheduler(null, req);
    }

    @Override
    public void scheduler(FetchContext ctx, String url, boolean local) throws MalformedURLException {
        scheduler(ctx,new FetchRequest(url),local);
    }

    @Override
    public void scheduler(FetchContext ctx, String url) throws MalformedURLException {
        scheduler(ctx,url,false);
    }

    @Override
    public void scheduler(String url) throws MalformedURLException {
        scheduler(null,url);
    }

    @Override
    public void cancel() throws ExtractorChainException {
        throw new ExtractorChainException(Behaviour.CANCEL);
    }
}
