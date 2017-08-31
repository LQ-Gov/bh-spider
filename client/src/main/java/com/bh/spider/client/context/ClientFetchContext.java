package com.bh.spider.client.context;

import com.bh.spider.doc.Document;
import com.bh.spider.fetch.FetchContext;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.Response;
import com.bh.spider.fetch.impl.FetchRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;


public class ClientFetchContext implements FetchContext {
    private Request request;

    public ClientFetchContext(Request request){
        this.request = request;
    }


    private final static Logger logger = LoggerFactory.getLogger(ClientFetchContext.class);

    @Override
    public URL url() {
        return null;
    }

    @Override
    public Request request() {
        return null;
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

    }

    @Override
    public void set(Map<String, Object> collection) {

    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void scheduler(FetchContext ctx, Request req, boolean local) {
        logger.info("submit url to {}:{},", local ? "local" : "origin", req.url());
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
        scheduler(ctx, new FetchRequest(url), local);
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
    public void cancel() {

    }
}
