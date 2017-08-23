package com.charles.spider.fetch.impl;

import com.charles.spider.fetch.FetchContext;
import com.charles.spider.fetch.Request;
import com.charles.spider.fetch.Response;
import com.charles.spider.doc.Document;
import com.charles.spider.doc.impl.DocumentImpl;


import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

public class FinalFetchContext implements FetchContext {

    private FetchContext parent;
    private Response response;



    public FinalFetchContext(FetchContext parent, Response response) {
        this.parent = parent;
        this.response = response;

    }

    @Override
    public URL url() {
        return request().url();
    }

    @Override
    public Request request() {
        return parent.request();
    }

    @Override
    public Response response() {
        return this.response;
    }

    @Override
    public Document document() {
        return new DocumentImpl(this.response.data());
    }

    @Override
    public Document document(Charset charset) {
        return new DocumentImpl(this.response.data(),charset);
    }

    @Override
    public void set(String key, Object value) {
        this.parent.set(key,value);

    }

    @Override
    public void set(Map<String, Object> collection) {
        this.parent.set(collection);

    }

    @Override
    public Object get(String key) {
        return this.parent.get(key);
    }

    @Override
    public void scheduler(FetchContext ctx, Request req, boolean local) throws Exception {

        this.parent.scheduler(ctx,req,local);

    }

    @Override
    public void scheduler(FetchContext ctx, Request req) throws Exception {
        this.parent.scheduler(ctx,req);
    }

    @Override
    public void scheduler(Request req) throws Exception {
        this.parent.scheduler(req);
    }

    @Override
    public void scheduler(FetchContext ctx, String url, boolean local) throws Exception {
        this.parent.scheduler(ctx,url,local);
    }

    @Override
    public void scheduler(FetchContext ctx, String url) throws Exception {
        this.parent.scheduler(ctx,url);
    }

    @Override
    public void scheduler(String url) throws Exception {
        this.parent.scheduler(url);
    }

    @Override
    public void cancel() {

    }

}
