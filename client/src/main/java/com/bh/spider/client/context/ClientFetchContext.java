package com.bh.spider.client.context;

import com.bh.common.utils.URLUtils;
import com.bh.spider.common.fetch.*;
import com.bh.spider.common.fetch.impl.RequestBuilder;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.doc.Document;
import com.bh.spider.doc.impl.DocumentImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ClientFetchContext implements FetchContext {
    private Request request;

    private Response response;

    private Map<String, Object> fields = new HashMap<>();

    public ClientFetchContext(Request request,Response response) {
        this.request = request;
    }


    private final static Logger logger = LoggerFactory.getLogger(ClientFetchContext.class);

    @Override
    public URL url() {
        return request().url();
    }

    @Override
    public Request request() {
        return request;
    }

    @Override
    public Response response() {
        return response;
    }

    @Override
    public Rule rule() {
        return null;
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
        return fields.getOrDefault(key, defaultValue);
    }

    @Override
    public Map<String, Object> fields() {
        return fields;
    }

    @Override
    public void schedule(FetchContext ctx, Request req, boolean local) {
        logger.info("submit new request scheduler local:{},url:{}", local, FetchContextUtils.instance().toURL(req));
    }

    @Override
    public void schedule(FetchContext ctx, Request req) {
        schedule(ctx, req, false);
    }

    @Override
    public void schedule(Request req) {
        schedule(null, req);
    }

    @Override
    public void schedule(FetchContext ctx, String url, boolean local) throws MalformedURLException {
        schedule(ctx, RequestBuilder.create(url).build(), local);
    }

    @Override
    public void schedule(FetchContext ctx, String url) throws MalformedURLException {
        schedule(ctx, url, false);
    }

    @Override
    public void schedule(String url) throws MalformedURLException {
        schedule(null, url);
    }

    @Override
    public void schedule(FetchContext ctx, List<Request> requests, boolean local) throws Exception {
        for (Request req : requests) {
            schedule(ctx, req, local);
        }
    }

    @Override
    public void schedule(FetchContext ctx, List<String> requests) throws Exception {


        List<Request> objs = new LinkedList<>();
        for (String req : requests) {

            req = URLUtils.format(req, this.url().getProtocol(), this.url().getHost());


            Request obj = RequestBuilder.create(req).build();
            objs.add(obj);
        }

        schedule(ctx, objs, false);
    }

    @Override
    public void schedule(List<String> urls) throws Exception {
        schedule(null, urls);

    }

    @Override
    public void termination() throws ExtractorChainException {

    }

}
