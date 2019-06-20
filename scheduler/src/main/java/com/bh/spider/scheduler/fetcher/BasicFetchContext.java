package com.bh.spider.scheduler.fetcher;

import com.bh.common.utils.URLUtils;
import com.bh.spider.common.fetch.*;
import com.bh.spider.common.fetch.impl.RequestBuilder;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.doc.Document;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.context.LocalContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 实现了对于schedule和request的操作
 */
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
        return original.url();
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
    public Map<String, Object> fields() {
        return fields;
    }

    @Override
    public void schedule(FetchContext ctx, Request req, boolean local) {
        if (ctx != null) {
            //格式化 req
        }

        sch.submit(new LocalContext(sch),req);
    }

    @Override
    public void schedule(FetchContext ctx, Request req) throws Exception {
        schedule(ctx, req, false);
    }

    @Override
    public void schedule(Request req) throws Exception {
        schedule(null, req);
    }

    @Override
    public void schedule(FetchContext ctx, String url, boolean local) throws Exception {
        schedule(ctx, RequestBuilder.create(url).build(),local);
    }

    @Override
    public void schedule(FetchContext ctx, String url) throws Exception {
        schedule(ctx,url,false);
    }

    @Override
    public void schedule(String url) throws Exception {
        schedule(null,url);
    }

    public void schedule(FetchContext ctx,List<Request> requests,boolean local){
        sch.submit(new LocalContext(sch),requests);
    }


    public void schedule(FetchContext ctx,List<String> requests) throws MalformedURLException {
        List<Request> objs = new LinkedList<>();
        for(String req:requests){
            req = URLUtils.format(req,this.url().getProtocol(),this.url().getHost());
            Request obj = RequestBuilder.create(req).build();
            objs.add(obj);
        }

        schedule(ctx,objs,false);

    }


    public void schedule(List<String> urls) throws MalformedURLException {

        schedule(null,urls);

    }



    @Override
    public void termination() throws ExtractorChainException {

    }
}
