package com.bh.spider.scheduler.fetcher;


import com.bh.spider.common.fetch.*;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.doc.Document;
import com.bh.spider.doc.impl.DocumentImpl;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * FinalFetchContext包含了对response和document的操作
 */
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
    public Rule rule() {
        return parent.rule();
    }

    @Override
    public Cookie cookie(String name) {
        return response.cookie(name);
    }

    @Override
    public List<Cookie> cookies() {
        return response.cookies();
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
    public Object get(String key, Object defaultValue) {
        return parent.get(key,defaultValue);
    }

    @Override
    public Map<String, Object> fields() {
        return parent.fields();
    }

    @Override
    public void schedule(FetchContext ctx, Request req, boolean local) throws Exception {

        this.parent.schedule(ctx,req,local);

    }

    @Override
    public void schedule(FetchContext ctx, Request req) throws Exception {
        this.parent.schedule(ctx,req);
    }

    @Override
    public void schedule(Request req) throws Exception {
        this.parent.schedule(req);
    }

    @Override
    public void schedule(FetchContext ctx, String url, boolean local) throws Exception {
        this.parent.schedule(ctx,url,local);
    }

    @Override
    public void schedule(FetchContext ctx, String url) throws Exception {
        this.parent.schedule(ctx,url);
    }

    @Override
    public void schedule(String url) throws Exception {
        this.parent.schedule(url);
    }

    @Override
    public void schedule(FetchContext ctx, List<Request> requests, boolean local) throws Exception {
        this.parent.schedule(ctx,requests,local);
    }

    @Override
    public void schedule(FetchContext ctx, List<String> requests) throws Exception {
        this.parent.schedule(ctx,requests);
    }

    @Override
    public void schedule(List<String> urls) throws Exception {
        this.parent.schedule(urls);
    }


    @Override
    public void termination() throws ExtractorChainException {
        throw new ExtractorChainException(Behaviour.TERMINATION);
    }


}
