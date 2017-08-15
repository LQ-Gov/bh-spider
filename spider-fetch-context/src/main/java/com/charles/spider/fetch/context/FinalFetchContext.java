package com.charles.spider.fetch.context;

import com.charles.spider.common.extractor.Document;
import com.charles.spider.common.http.FetchContext;
import com.charles.spider.common.http.Request;
import com.charles.spider.common.http.Response;
import com.charles.spider.fetch.context.doc.DocumentImpl;


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
        return new DocumentImpl(this.response.original());
    }

    @Override
    public Document document(Charset charset) {
        return new DocumentImpl(this.response.original(),charset);
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
    public void scheduler(FetchContext ctx, Request req, boolean local) {

        this.parent.scheduler(ctx,req,local);

    }

    @Override
    public void scheduler(FetchContext ctx, Request req) {
        this.parent.scheduler(ctx,req);
    }

    @Override
    public void scheduler(Request req) {
        this.parent.scheduler(req);
    }

    @Override
    public void scheduler(FetchContext ctx, String url, boolean local) {
        this.parent.scheduler(ctx,url,local);
    }

    @Override
    public void scheduler(FetchContext ctx, String url) {
        this.parent.scheduler(ctx,url);
    }

    @Override
    public void scheduler(String url) {
        this.parent.scheduler(url);
    }

    @Override
    public void cancel() {

    }

}
