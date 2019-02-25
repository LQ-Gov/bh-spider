package com.bh.spider.client.context;

import com.bh.spider.common.fetch.*;
import com.bh.spider.common.fetch.impl.RequestBuilder;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.doc.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClientFetchContext implements FetchContext {
    private Request request;

    private Map<String, Object> fields = new HashMap<>();

    public ClientFetchContext(Request request) {
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
        return null;
    }

    @Override
    public Rule rule() {
        return null;
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
        return fields.getOrDefault(key, defaultValue);
    }

    @Override
    public void scheduler(FetchContext ctx, Request req, boolean local) {
        logger.info("submit new request scheduler local:{},url:{}", local, FetchContextUtils.instance().toURL(req));
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
        scheduler(ctx, RequestBuilder.create(url).build(), local);
    }

    @Override
    public void scheduler(FetchContext ctx, String url) throws MalformedURLException {
        scheduler(ctx, url, false);
    }

    @Override
    public void scheduler(String url) throws MalformedURLException {
        scheduler(null, url);
    }
    @Override
    public void termination() throws ExtractorChainException {

    }

}
