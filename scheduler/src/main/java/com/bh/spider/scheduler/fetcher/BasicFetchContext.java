package com.bh.spider.scheduler.fetcher;

import com.bh.spider.common.fetch.*;
import com.bh.spider.common.fetch.impl.RequestBuilder;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.doc.Document;


import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicFetchContext implements FetchContext {

    private Request original;
    private BasicScheduler sch;

    private Rule rule;

    private Map<String, Object> fields = new HashMap<>();

    public BasicFetchContext(BasicScheduler scheduler, Request original, Rule rule) {

        this.sch = scheduler;
        this.original = original;
        this.rule = rule;

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
    public Rule rule() {
        return rule;
    }

    @Override
    public Cookie cookie(String name) {
        return null;
    }

    @Override
    public List<Cookie> cookies() {
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
    public Object get(String key, Object defaultValue) {
        return fields.getOrDefault(key,defaultValue);
    }

    @Override
    public void scheduler(FetchContext ctx, Request req, boolean local) {
        if (ctx != null) {
            //格式化 req
        }

        sch.submit(null,req);
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
        scheduler(ctx, RequestBuilder.create(url).build(),local);
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
    public void termination() throws ExtractorChainException {

    }
}
